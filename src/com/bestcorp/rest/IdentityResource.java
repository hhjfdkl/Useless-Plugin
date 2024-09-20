package com.bestcorp.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.annotations.UpdateTimestamp;

import com.bestcorp.services.IdentityService;

import sailpoint.rest.plugin.AllowAll;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.tools.GeneralException;


@Path("identity")
public class IdentityResource extends BasePluginResource
{
    private IdentityService service()
    {
        return new IdentityService(this);
    }

    @POST
    @Path("getNow")
    @AllowAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addIdentities() throws GeneralException
    {
        //we'll add a request body for this, but for now we'll just have it add 3 identities
        service().getSpIdentities(3);

    }

    @Override
    public String getPluginName() 
    {
        return "Useless";
    }



    @POST
    @Path("add")
    @AllowAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void createIdentity()
    {
        //once service has create method we can put it here
    }

    /**
     * Todo - needs refactoring so we aren't pulling hundreds of identities
     * @return
     * @throws GeneralException
     
    @GET
    @Path("all")
    @AllowAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Identity> getAllIdentities() throws GeneralException
    {
        //This will return 15 based on page - refactor
        return service.getAllIdentities
    }
    */

    /**
     * todo
     
    @PUT



    @DELETE
    */


}
