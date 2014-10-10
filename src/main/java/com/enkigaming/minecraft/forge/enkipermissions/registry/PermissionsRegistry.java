package com.enkigaming.minecraft.forge.enkipermissions.registry;

import com.enkigaming.minecraft.forge.enkipermissions.permissions.PermissionNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public class PermissionsRegistry
{
    public PermissionsRegistry(File saveFolder)
    {
        this.saveFolder = saveFolder;
    }
    
    final protected File saveFolder;
    
    final protected Multimap<UUID, PermissionNode> playerPermissions = ArrayListMultimap.<UUID, PermissionNode>create();
    
    public boolean playerHasPermission(UUID playerId, String permission)
    { return playerHasPermission(playerId, new PermissionNode(permission)); }
    
    public boolean playerHasPermission(EntityPlayer player, String permission)
    { return playerHasPermission(player.getGameProfile().getId(), new PermissionNode(permission)); }
    
    public boolean playerHasPermission(UUID playerId, PermissionNode permission)
    {
        boolean hasPermission = false;
        boolean hasPermissionCanceller = false; // -permission
        boolean hasPermissionEnsurer = false; // +permission
        
        synchronized(playerPermissions)
        {
            for(PermissionNode current : playerPermissions.get(playerId))
            {
                if(current.covers(permission))
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
    
    public boolean playerHasPermission(EntityPlayer player, PermissionNode permission)
    { return playerHasPermission(player.getGameProfile().getId(), permission); }
    
    public boolean givePlayerPermission(UUID playerId, String permission)
    { return givePlayerPermission(playerId, new PermissionNode(permission)); }
    
    public boolean givePlayerPermission(EntityPlayer player, String permission)
    { return givePlayerPermission(player.getGameProfile().getId(), new PermissionNode(permission)); }
    
    public boolean givePlayerPermission(UUID playerId, PermissionNode permission)
    {
        synchronized(playerPermissions)
        {
            if(playerHasPermission(playerId, permission))
                return true;
            
            playerPermissions.put(playerId, permission);
            return false;
        }
    }
    
    public boolean givePlayerPermission(EntityPlayer player, PermissionNode permission)
    { return givePlayerPermission(player.getGameProfile().getId(), permission); }
    
    public boolean removePlayerPermission(UUID playerId, String permission)
    { return removePlayerPermission(playerId, new PermissionNode(permission)); }
    
    public boolean removePlayerPermission(EntityPlayer player, String permission)
    { return removePlayerPermission(player.getGameProfile().getId(), new PermissionNode(permission)); }
    
    public boolean removePlayerPermission(UUID playerId, PermissionNode permission)
    {
        synchronized(playerPermissions)
        { return playerPermissions.remove(playerId, permission); }
    }
    
    public boolean removePlayerPermission(EntityPlayer player, PermissionNode permission)
    { return removePlayerPermission(player.getGameProfile().getId(), permission); }
    
    public Collection<PermissionNode> getPlayerPermissions(UUID playerId)
    {
        synchronized(playerPermissions)
        { return new ArrayList<PermissionNode>(playerPermissions.get(playerId)); }
    }
    
    public Collection<PermissionNode> getPlayerPermissions(EntityPlayer player)
    { return getPlayerPermissions(player.getGameProfile().getId()); }
    
    public void save()
    {}
    
    public void load()
    {}
}