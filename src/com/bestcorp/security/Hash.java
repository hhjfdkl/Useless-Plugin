package com.bestcorp.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sailpoint.tools.GeneralException;

/**
 * More of a test class
 * 
 * Built with ChatGPT alone - This needs to be reviewed for clarity later (lazy rn)
 * 
 * Note that SailPoint uses 32-char Strings for its ID
 * 
 * TODO:
 * There should be a simple and clean way to select the type of hashing method we want
 * For now we're just going to have both methods hash
 */
public class Hash 
{
    //64-char string returns
    public String getSHA256Hash(String input) throws GeneralException
    {
        try 
        {
            // Create a message digest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Perform the hash computation
            byte[] hashBytes = digest.digest(input.getBytes());
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) 
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) 
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralException("SHA-256 algorithm not found", e);
        }
    }
    
    //32-char string returns
    public String getMD5Hash(String input) throws GeneralException
    {
        try 
        {
            // Create a message digest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // Perform the hash computation
            byte[] hashBytes = digest.digest(input.getBytes());
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) 
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) 
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new GeneralException("MD5 algorithm not found", e);
        }
    }
}
