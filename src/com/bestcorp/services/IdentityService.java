package com.bestcorp.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bestcorp.models.Identity;
import com.bestcorp.security.Hash;

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
    // private static int quantity = 15; //commented this out since we're passing it in via executor  //the number of records we will pull from db
    private static int identityQty = 0; //this will be set by count method (total number of identities in SailPoint)
    private static int iterations = 0;  //counts the number of times we've execute. This resets every 5 iterations to recalculate identityQty

    public IdentityService(PluginContext context)
    {
        this.context = context;
    }



//This section concerns pulling data from SailPoint and assigning values in our plugin's db
    private void createIdentities(List<Identity> identities) throws GeneralException
    {
        System.out.println("Received " + identities.size() + " identities");
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
                    , identity.getSptId()
                    , identity.getSptName());

                statement.executeUpdate();
            }
            System.out.println("Created " + identities.size() + " identities");
            
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
    public void getSpIdentities(int quantity) throws GeneralException
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
            System.out.println("Got " + identities.size() + " identities");
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
    public void createIdentity(String name) throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;
        Hash h = new Hash();    //is used to create a mock sp id

        try
        {
            name += (System.currentTimeMillis()/1000L);
            connection = context.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection
                , "INSERT INTO ep_plugin_useless (spt_id, spt_name) VALUES (?, ?);"
                , h.getMD5Hash(name)
                , name);
            statement.executeUpdate();
            System.out.println("Identity created");
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

    //READ
    public Identity readIdentityById(int id) throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = context.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, "SELECT id, spt_id, spt_name FROM ep_plugin_useless WHERE id = ?", id);
            ResultSet result = statement.executeQuery();
            Identity identity = null;
            if(result.next())
            {
                identity = new Identity(
                      result.getInt("id")
                    , result.getString("spt_id")
                    , result.getString("spt_name")
                    );
            }
            return identity;
            
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
    
    //May implement method for reading identity by name, but unecessary at this time


    public List<Identity> readAllIdentities(int page) throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            int limit = 15;
            int offset = (limit * page) - limit;
            System.out.println("Page: " + page + ", Limit: " + limit + ", Offset: " + offset);
            connection = context.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, "SELECT id, spt_id, spt_name FROM ep_plugin_useless LIMIT ? OFFSET ?", limit, offset);
            ResultSet result = statement.executeQuery();
            List<Identity> identities = new ArrayList<>();
            while(result.next())
            {
                identities.add
                (
                    new Identity
                    (
                          result.getInt("id")
                        , result.getString("spt_id")
                        , result.getString("spt_name")
                    )
                );
                
            }

            

            return identities;
            
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
    
    
    public List<Identity> readAllIdentities() throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = context.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, "SELECT id, spt_id, spt_name FROM ep_plugin_useless");
            ResultSet result = statement.executeQuery();
            List<Identity> identities = new ArrayList<>();
            while(result.next())
            {
                identities.add
                (
                    new Identity
                    (
                          result.getInt("id")
                        , result.getString("spt_id")
                        , result.getString("spt_name")
                    )
                );
                
            }
            return identities;
            
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

    

    //UPDATE
    public void updateIdentity(Identity identity) throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        //TODO: check db to see if our passed int identity is different than the original
        //also MD5 hash the new spt_id if that is different

        try
        {
            

            connection = context.getConnection();
            statement = PluginBaseHelper.prepareStatement
            (     connection
                , "UPDATE ep_plugin_useless SET spt_id = ?, spt_name = ? WHERE id = ?"
                , identity.getSptId()
                , identity.getSptName()
                , identity.getId()
            );
            statement.executeUpdate();
            
            
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


    //DELETE
    public void deleteIdentity(int id) throws GeneralException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = context.getConnection();
            statement = PluginBaseHelper.prepareStatement(connection, "DELETE FROM ep_plugin_useless WHERE id = ?", id);
            statement.executeUpdate();
            
            
            
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


}