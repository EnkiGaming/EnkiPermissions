package com.enkigaming.minecraft.forge.enkipermissions.permissions;

import java.util.Arrays;
import java.util.List;

public class PermissionNode
{
    public PermissionNode(String node)
    {
        boolean nodeRemovesPermission = false;
        boolean nodeAddsPermissionOverriding = false;
        boolean nodeCoversChildren = false;
        
        for(boolean finishedPermissionPrefixes = false; finishedPermissionPrefixes == false; )
        {
            if(node.startsWith("-"))
            {
                nodeRemovesPermission = true;
                node = node.substring(1);
            }
            else if(node.startsWith("+"))
            {
                nodeAddsPermissionOverriding = true;
                node = node.substring(1);
            }
        }
        
        List<String> nodeParts = Arrays.asList(node.split("\\.")); // I have to escape the escape character o-o
        
        if(nodeParts.get(nodeParts.size() - 1).equalsIgnoreCase("*"))
        {
            nodeCoversChildren = true;
            nodeParts.remove(nodeParts.size() - 1);
        }
        
        parts = nodeParts;
        removesPermission = nodeRemovesPermission;
        addsPermissionOverriding = nodeAddsPermissionOverriding;
        coversChildren = nodeCoversChildren;
    }
    
    //immutable class
    
    /**
     * The parts of this permission node, in order where .get(0) is the first.
     */
    protected final List<String> parts;
    
    /**
     * Permission starts in "-". Removes permission.
     */
    protected final boolean removesPermission; // if permission starts in "-"
    
    /**
     * Permission starts in "+". Adds permission even if another permission node removing covers the same node.
     * 
     * Combining both is pointless, and "+-" or "-+" will just be interpreted as "+".
     */
    protected final boolean addsPermissionOverriding;
    
    /**
     * Permission ends in "*". Covers all permissions that start in what this has before the *.
     * 
     * If a permission node is just "*" without anything else, the parts list will be empty and this PermissionNode will cover all permissions.
     */
    protected final boolean coversChildren;
    
    public boolean removesPermission()
    { return removesPermission; }
    
    public boolean addPermissionOverriding()
    { return addsPermissionOverriding; }
    
    public boolean coversChildren()
    { return coversChildren; }
    
    public boolean covers(PermissionNode permission)
    {
        if((coversChildren && parts.size() != permission.parts.size()) || parts.size() < permission.parts.size())
            return false;
        
        for(int i = 0; i < parts.size(); i++)
                if(!parts.get(i).equalsIgnoreCase(permission.parts.get(i)))
                    return false;
        
        return true;
    }
    
    public boolean isCoveredBy(PermissionNode permission)
    { return permission.covers(this); }
    
    @Override
    public String toString()
    {
        String string = "";
        
        if(removesPermission)
            string = "-" + string;
        
        if(addsPermissionOverriding)
            string = "+" + string;
        
        if(parts.size() > 0)
        {
            for(String i : parts)
                string += i + ".";
            
            if(coversChildren)
                string += "*";
            else
                string = string.substring(0, string.length() - 1); // remove fullstop at end.
        }
        else
            string += "*";
        
        return string;
    }
}