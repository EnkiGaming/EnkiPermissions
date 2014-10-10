package com.enkigaming.minecraft.forge.enkipermissions.ranks;

import com.enkigaming.minecraft.forge.enkipermissions.permissions.PermissionNode;
import java.util.Collection;
import java.util.UUID;

public class Rank
{
    public Rank(String name)
    {}
    
    protected String name;
    protected String usernamePrefix;
    protected String usernameSuffix;
    
    /**
     * Other ranks which this rank includes the permissions of.
     */
    protected Collection<UUID> permissionIncluders;
    protected Collection<PermissionNode> permissions;
    
    public String getName()
    {}
    
    public String getUsernamePrefix()
    {}
    
    public String getUsernameSuffix()
    {}
    
    public String setName()
    {}
    
    public String setUsernamePrefix()
    {}
    
    public String setUsernameSuffix()
    {}
    
    public boolean hasPermission(PermissionNode permission)
    {}
    
    public boolean hasPermission(String permission)
    {}
    
    public boolean hasPermissionDirectly(PermissionNode permission)
    {}
    
    public boolean hasPermissionDirectly(String permission)
    {}
}