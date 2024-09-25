package com.bestcorp.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hasher 
{
    

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
}


