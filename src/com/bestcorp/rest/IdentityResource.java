package com.bestcorp.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.annotations.UpdateTimestamp;

import com.bestcorp.models.Identity;
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
    @Path("addNow")
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
    public void createIdentity(String name) throws GeneralException
    {
        service().createIdentity(name);
    }


    @GET
    @Path("get/{id}")
    @AllowAll
    @Produces(MediaType.APPLICATION_JSON)
    public Identity getIdentity(@PathParam("id") int id) throws GeneralException
    {
        return service().readIdentityById(id);
    }


    @GET
    @Path("all/{page}")
    @AllowAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Identity> getAllIdentities(@PathParam("page") int page) throws GeneralException
    {
        return service().readAllIdentities(page);
    }

    @GET
    @Path("all")
    @AllowAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Identity> getAllIdentities() throws GeneralException
    {
        return service().readAllIdentities();
    }

    
    /**
     * todo
     
    @PUT



    @DELETE
    */


}
