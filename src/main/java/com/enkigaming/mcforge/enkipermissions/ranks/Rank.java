package com.enkigaming.mcforge.enkipermissions.ranks;

import com.enkigaming.mcforge.enkipermissions.permissions.PermissionNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class Rank
{
    public Rank(String name)
    {
        this.name = name;
        usernamePrefix = "";
        usernameSuffix = "";
    }
    
    protected final String name;
    protected String usernamePrefix;
    protected String usernameSuffix;
    
    protected final Object usernamePrefixBusy = new Object();
    protected final Object usernameSuffixBusy = new Object();
    
    /**
     * Other ranks which this rank includes the permissions of.
     */
    protected final Collection<Rank> permissionIncluders = new ArrayList<Rank>();
    protected final Collection<PermissionNode> permissions = new ArrayList<PermissionNode>();
    
    public String getName()
    { return name; }
    
    public String getUsernamePrefix()
    {
        synchronized(usernamePrefixBusy)
        { return usernamePrefix; }
    }
    
    public String getUsernameSuffix()
    {
        synchronized(usernameSuffixBusy)
        { return usernameSuffix; }
    }
    
    public String setUsernamePrefix(String newPrefix)
    {
        synchronized(usernamePrefixBusy)
        {
            String oldPrefix = usernamePrefix;
            usernamePrefix = newPrefix;
            return oldPrefix;
        }
    }
    
    public String setUsernameSuffix(String newSuffix)
    {
        synchronized(usernameSuffixBusy)
        {
            String oldSuffix = usernameSuffix;
            usernameSuffix = newSuffix;
            return oldSuffix;
        }
    }
    
    public boolean hasPermission(PermissionNode permission)
    {
        boolean hasPermission = false;
        boolean hasPermissionCanceller = false;
        boolean hasPermissionEnsurer = false;
        
        Collection<Rank> ranksToCheck = getPermissionIncluders();
        
        CheckAllRanks:
        for(Rank currentRank : ranksToCheck)
        {
            synchronized(currentRank.permissions)
            {
                for(PermissionNode currentPermission : currentRank.permissions)
                {
                    if(currentPermission.covers(permission))
                    {
                        hasPermission = true;
                        
                        if(currentPermission.removesPermission())
                            hasPermissionCanceller = true;
                        
                        if(currentPermission.addPermissionOverriding())
                            hasPermissionEnsurer = true;
                    }
                    
                    // No point in continuing to check once all possible states of a permission node have been derived.
                    if(hasPermission && hasPermissionCanceller && hasPermissionEnsurer)
                        break CheckAllRanks;
                }
            }
        }
        
        if(hasPermissionEnsurer)
            return true;
        
        if(hasPermissionCanceller)
            return false;
        
        if(hasPermission)
            return true;
        
        return false;
    }
    
    public boolean hasPermission(String permission)
    { return hasPermission(new PermissionNode(permission)); }
    
    public boolean hasPermissionDirectly(PermissionNode permission)
    {
        boolean hasPermission = false;
        boolean hasPermissionCanceller = false;
        boolean hasPermissionEnsurer = false;
        
        synchronized(permissions)
        {
            for(PermissionNode i : permissions)
            {
                if(i.covers(permission))
                {
                    hasPermission = true;
                    
                    if(permission.removesPermission())
                        hasPermissionCanceller = true;
                    
                    if(permission.addPermissionOverriding())
                        hasPermissionEnsurer = true;
                }
            }
        }
        
        if(hasPermissionEnsurer)
            return true;
        
        if(hasPermissionCanceller)
            return false;
        
        if(hasPermission)
            return true;
        
        return false;
    }
    
    public boolean hasPermissionDirectly(String permission)
    { return hasPermissionDirectly(new PermissionNode(permission)); }
    
    public Collection<PermissionNode> getPermissions()
    {
        HashSet<PermissionNode> permissionsInRanks = new HashSet<PermissionNode>();
        
        for(Rank rank : getPermissionIncluders())
            permissionsInRanks.addAll(rank.getPermissionsDirectOnly());
        
        return permissionsInRanks;
    }
    
    public Collection<PermissionNode> getPermissionsDirectOnly()
    {
        synchronized(permissions)
        { return new ArrayList<PermissionNode>(permissions); }
    }
    
    public boolean givePermission(PermissionNode permission)
    {
        synchronized(permissions)
        {
            if(permissions.contains(permission))
                return false;
            else
            {
                permissions.add(permission);
                return true;
            }
        }
    }
    
    public boolean givePermission(String permission)
    { return givePermission(new PermissionNode(permission)); }
    
    public boolean removePermission(PermissionNode permission)
    {
        synchronized(permissions)
        { return permissions.remove(permission); }
    }
    
    public boolean removePermission(String permission)
    { return removePermission(new PermissionNode(permission)); }
    
    public void removeAllPermissions()
    {
        synchronized(permissions)
        { permissions.clear(); }
    }
    
    public Collection<Rank> getPermissionIncluders()
    {
        // To do: Add detection for circular permissions inclusion.
        
        Collection<Rank> includers = new HashSet<Rank>();
        Collection<Rank> newIncluders;
        
        newIncluders = new HashSet<Rank>(Arrays.asList(new Rank[]{this}));
        
        while(newIncluders.size() > 0)
        {
            Collection<Rank> nextIncluders = new HashSet<Rank>();
            
            for(Rank includer : newIncluders)
                nextIncluders.addAll(includer.getDirectPermissionIncluders());
            
            includers.addAll(newIncluders);
            newIncluders = nextIncluders;
        }
        
        return includers;
    }
    
    public Collection<String> getDirectPermissionIncluderNames()
    {
        Collection<String> names = new ArrayList<String>();
        
        synchronized(permissionIncluders)
        {
            for(Rank includer : permissionIncluders)
                names.add(includer.getName());
        }
        
        return names;
    }
    
    public Collection<Rank> getDirectPermissionIncluders()
    {
        synchronized(permissionIncluders)
        { return new ArrayList<Rank>(permissionIncluders); }
    }
    
    public boolean addPermissionIncluder(Rank includer)
    {
        synchronized(permissionIncluders)
        { return permissionIncluders.add(includer); }
    }
    
    public boolean removePermissionIncluder(Rank includer)
    {
        synchronized(permissionIncluders)
        { return permissionIncluders.remove(includer); }
    }
    
    public void removeAllPermissionIncluders()
    {
        synchronized(permissionIncluders)
        { permissionIncluders.clear(); }
    }
}