package com.bestcorp.server;

import com.bestcorp.services.IdentityService;

import sailpoint.api.SailPointContext;
import sailpoint.server.BasePluginService;
import sailpoint.tools.GeneralException;

/**
 * This class automates the IdentityService's getSpIdentities method
 * 
 * In the future, we will also automate pulling additional data from SailPoint to store in our plugin
 */
public class IdentityGetAutoService extends BasePluginService
{

    private IdentityService service;
    
    public IdentityGetAutoService()
    {
        service = new IdentityService(this);
    }

    @Override
    public void execute(SailPointContext context) throws GeneralException
    {
        service.getSpIdentities();
    }

    @Override
    public String getPluginName() 
    {
        return "Useless";
    }
    
}
