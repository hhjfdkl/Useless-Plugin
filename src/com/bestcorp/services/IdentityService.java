package com.bestcorp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bestcorp.models.Identity;

import sailpoint.plugin.PluginBaseHelper;
import sailpoint.plugin.PluginContext;
import sailpoint.server.Environment;
import sailpoint.tools.GeneralException;
import sailpoint.tools.IOUtil;

/**
 * This should scrape the identityIQ database to grab the IDENTITY info
 * 
 * Info as of right now is...
 * 1. Sailpoint's ID for the identity
 * 2. Sailpoint's name for the identity
 * - POTENTIALLY we could add the display name, but we'll keep it simple for now
 * 
 * THEN it constructs an Identity object to store in our database, BUT
 * it uses unix time (or some other method) to make sure the name of the identity is unique
 * 
 * Basically, we're grabbing sp ID and sp name + unix time
 * 
 * We will likely need a "crawler" value to move through the sp database with 
 * since this will be automated by our IdentityGetAutoService
 */

public class IdentityService 
{
    private PluginContext context;
    //private static int crawl = 0; 
    private static int position = 0;    //tracks where we are
    private static int quantity = 15;   //the number of records we will pull from db
    private static int identityQty = 0; //this will be set by count method (total number of identities in SailPoint)
    private static int iterations = 0;  //counts the number of times we've execute. This resets every 5 iterations to recalculate identityQty

    public IdentityService(PluginContext context)
    {
        this.context = context;
    }



//This section concerns pulling data from SailPoint and assigning values in our plugin's db
    private void createIdentities(List<Identity> identities) throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = context.getConnection();
            for(Identity identity : identities)
            {
                statement = PluginBaseHelper.prepareStatement(
                    connection
                    , "INSERT INTO ep_plugin_useless (spt_id, spt_name) VALUES (?, ?)"
                    , identity.getId()
                    , identity.getSptName());

                statement.executeUpdate();
            }
            
        }
        catch(SQLException e)
        {
            throw new GeneralException(e);
        }
        finally
        {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    /**
     * Crawls the sailpoint identity database
     * pulls a list of identities
     * iterates through the list of identities and calls createIdentity for each
     * 
     * Appends System.currentTimeMillis()/1000L to the end of sptName
     * 
     * @throws GeneralException
     */
    public void getSpIdentities() throws GeneralException
    {
        if(iterations%5==0)
        {
            setIdentityQty();
            iterations = 0;
        }

        if(position > identityQty)
        {
            position = 0;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try 
        {
            connection = Environment.getEnvironment().getSpringDataSource().getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, "USE identityiq;");
            statement.execute();
            statement = PluginBaseHelper.prepareStatement(
                  connection
                , "SELECT id, name FROM spt_identity LIMIT ? OFFSET ?;"
                , quantity
                , position
            );
            
            ResultSet result = statement.executeQuery();
            List<Identity> identities = new ArrayList<>();
            while(result.next())
            {
                identities.add(
                    new Identity(
                          result.getString("id")
                        , result.getString("name") 
                        + (System.currentTimeMillis()/1000L)
                        )
                    );
            }
            createIdentities(identities);

            position += quantity;
            iterations++;
        }
        catch(SQLException e)
        {
            throw new GeneralException(e);
        }
        finally
        {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }

    //checks the total identities in the SailPoint database
    public void setIdentityQty() throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = Environment.getEnvironment().getSpringDataSource().getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, "USE identityiq;");
            statement.execute();
            statement = PluginBaseHelper.prepareStatement(connection, "SELECT COUNT(id) FROM spt_identity;");
            ResultSet result = statement.executeQuery();

            if(result.next())
                identityQty = result.getInt("COUNT(id)");
        }
        catch(SQLException e)
        {
            throw new GeneralException(e);
        }
        finally
        {
            IOUtil.closeQuietly(statement);
            IOUtil.closeQuietly(connection);
        }
    }
//END SailPoint section



    /**
     * This plugin should function as a standalone application,
     * so this section will just have boilerplate CRUD functionality
     */

    //CREATE


    //READ


    //UPDATE


    //DELETE



}