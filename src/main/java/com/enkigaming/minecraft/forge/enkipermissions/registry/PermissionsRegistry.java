package com.enkigaming.minecraft.forge.enkipermissions.registry;

import com.enkigaming.minecraft.forge.enkipermissions.permissions.PermissionNode;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public class PermissionsRegistry
{
    public PermissionsRegistry(File saveFolder)
    {}
    
    final protected File saveFolder;
    
    final protected Multimap<UUID, PermissionNode> playerPermission;
    
    public boolean playerHasPermission(UUID playerId, String permission)
    {}
    
    public boolean playerHasPermission(EntityPlayer player, String permission)
    {}
    
    public boolean playerHasPermission(UUID playerId, PermissionNode permission)
    {}
    
    public boolean playerHasPermission(EntityPlayer player, PermissionNode permission)
    {}
    
    public boolean givePlayerPermission(UUID playerId, String permission)
    {}
    
    public boolean givePlayerPermission(EntityPlayer player, String permission)
    {}
    
    public boolean givePlayerPermission(UUID playerId, PermissionNode permission)
    {}
    
    public boolean givePlayerPermission(EntityPlayer player, PermissionNode permission)
    {}
    
    public boolean removePlayerPermission(UUID playerId, String permission)
    {}
    
    public boolean removePlayerPermission(EntityPlayer player, String permission)
    {}
    
    public boolean removePlayerPermission(UUID playerId, PermissionNode permission)
    {}
    
    public boolean removePlayerPermission(EntityPlayer player, PermissionNode permission)
    {}
    
    public Collection<String> getPlayerPermissionsAsStrings(UUID playerId)
    {}
    
    public Collection<String> getPlayerPermissionsAsStrings(EntityPlayer player)
    {}
    
    public Collection<PermissionNode> getPlayerPermissions(UUID playerId)
    {}
    
    public Collection<PermissionNode> getPlayerPermissions(EntityPlayer player)
    {}
    
    public void save()
    {}
    
    public void load()
    {}
}