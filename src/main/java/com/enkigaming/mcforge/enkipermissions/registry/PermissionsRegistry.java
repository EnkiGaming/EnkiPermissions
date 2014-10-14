package com.enkigaming.mcforge.enkipermissions.registry;

import com.enkigaming.mcforge.enkilib.EnkiLib;
import com.enkigaming.mcforge.enkilib.filehandling.FileHandler;
import com.enkigaming.mcforge.enkilib.filehandling.TreeFileHandler;
import com.enkigaming.mcforge.enkilib.filehandling.TreeFileHandler.TreeMember;
import com.enkigaming.mcforge.enkipermissions.permissions.PermissionNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.entity.player.EntityPlayer;

public class PermissionsRegistry
{
    public PermissionsRegistry(File saveFolder)
    { fileHandler = makeFileHandler(saveFolder); }
    
    final protected FileHandler fileHandler;
    
    protected Multimap<UUID, PermissionNode> playerPermissions = ArrayListMultimap.<UUID, PermissionNode>create();
    
    protected Lock playerPermissionsLock = new ReentrantLock();
    
    protected FileHandler makeFileHandler(File saveFolder)
    {
        return new TreeFileHandler("PermissionsRegistry", new File(saveFolder, "PlayerPermissions.txt"))
        {
            Collection<Map.Entry<UUID, PermissionNode>> permissionEntries;
            String idNameSeparator = " = ";
            
            @Override
            protected void preSave()
            {
                playerPermissionsLock.lock();
                permissionEntries = playerPermissions.entries();
            }

            @Override
            protected List<TreeMember> getTreeStructureOfSaveData()
            {
                Map<String, TreeMember> members = new HashMap<String, TreeMember>();
                
                for(Map.Entry<UUID, PermissionNode> entry : permissionEntries)
                {
                    String idAsString = entry.getKey().toString();
                    
                    TreeMember treeMember = members.get(idAsString);
                    
                    if(treeMember == null)
                    {
                        treeMember = new TreeMember(idAsString);
                        members.put(treeMember.getName(), treeMember);
                    }
                    
                    treeMember.addMember(new TreeMember(entry.getValue().toString()));
                }
                
                for(TreeMember treeMember : members.values())
                {
                    UUID id = UUID.fromString(treeMember.getName());
                    String lastRecordedUsername = EnkiLib.getInstance().getUsernameCache().getLastRecordedNameOf(id);
                    
                    if(lastRecordedUsername != null)
                        treeMember.setName(treeMember.getName() + idNameSeparator + lastRecordedUsername);
                }
                
                return new ArrayList<TreeMember>(members.values());
            }

            @Override
            protected void postSave()
            { playerPermissionsLock.unlock(); }

            @Override
            protected void preInterpretation()
            { playerPermissionsLock.lock(); }

            @Override
            protected boolean interpretTree(List<TreeMember> list)
            {
                Multimap<UUID, PermissionNode> permissionsMap = ArrayListMultimap.<UUID, PermissionNode>create();
                
                for(TreeMember playerMember : list)
                {
                    if(!playerMember.getName().isEmpty())
                    {
                        String currentUserId = playerMember.getName();
                        String[] parts = playerMember.getName().split(idNameSeparator);

                        if(parts.length > 2)
                            return false;

                        if(parts.length == 2)
                            currentUserId = parts[0];

                        UUID id;
                        
                        try
                        { id = UUID.fromString(currentUserId); }
                        catch(IllegalArgumentException exception)
                        { return false; }
                        
                        for(TreeMember permissionMember : playerMember.getMembers())
                            permissionsMap.put(id, new PermissionNode(permissionMember.getName()));
                    }
                }
                
                playerPermissions = permissionsMap;
                return true;
            }

            @Override
            protected void postInterpretation()
            { playerPermissionsLock.unlock(); }

            @Override
            protected void onNoFileToInterpret()
            { playerPermissions.clear(); }
        };
    }
    
    public FileHandler getFileHandler()
    { return fileHandler; }
    
    public boolean playerHasPermission(UUID playerId, String permission)
    { return playerHasPermission(playerId, new PermissionNode(permission)); }
    
    public boolean playerHasPermission(EntityPlayer player, String permission)
    { return playerHasPermission(player.getGameProfile().getId(), new PermissionNode(permission)); }
    
    public boolean playerHasPermission(UUID playerId, PermissionNode permission)
    {
        boolean hasPermission = false;
        boolean hasPermissionCanceller = false; // -permission
        boolean hasPermissionEnsurer = false; // +permission
        
        playerPermissionsLock.lock();
        
        try
        {
            for(PermissionNode current : playerPermissions.get(playerId))
            {
                if(current.covers(permission))
                {
                    hasPermission = true;
                    
                    if(current.removesPermission())
                        hasPermissionCanceller = true;
                    
                    if(current.addPermissionOverriding())
                        hasPermissionEnsurer = true;
                }
            }
        }
        finally
        { playerPermissionsLock.unlock(); }
        
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
        playerPermissionsLock.lock();
        
        try
        {
            if(playerHasPermission(playerId, permission))
                return true;
            
            playerPermissions.put(playerId, permission);
            return false;
        }
        finally
        { playerPermissionsLock.unlock(); }
    }
    
    public boolean givePlayerPermission(EntityPlayer player, PermissionNode permission)
    { return givePlayerPermission(player.getGameProfile().getId(), permission); }
    
    public boolean removePlayerPermission(UUID playerId, String permission)
    { return removePlayerPermission(playerId, new PermissionNode(permission)); }
    
    public boolean removePlayerPermission(EntityPlayer player, String permission)
    { return removePlayerPermission(player.getGameProfile().getId(), new PermissionNode(permission)); }
    
    public boolean removePlayerPermission(UUID playerId, PermissionNode permission)
    {
        playerPermissionsLock.lock();
        
        try
        { return playerPermissions.remove(playerId, permission); }
        finally
        { playerPermissionsLock.unlock(); }
    }
    
    public boolean removePlayerPermission(EntityPlayer player, PermissionNode permission)
    { return removePlayerPermission(player.getGameProfile().getId(), permission); }
    
    public void removePlayerPermissions(EntityPlayer player)
    { removePlayerPermissions(player.getGameProfile().getId()); }
    
    public void removePlayerPermissions(UUID playerId)
    {
        playerPermissionsLock.lock();
        
        try
        { playerPermissions.removeAll(playerId); }
        finally
        { playerPermissionsLock.unlock(); }
    }
    
    public Collection<PermissionNode> getPlayerPermissions(UUID playerId)
    {
        playerPermissionsLock.lock();
        
        try
        { return new ArrayList<PermissionNode>(playerPermissions.get(playerId)); }
        finally
        { playerPermissionsLock.unlock(); }
    }
    
    public Collection<PermissionNode> getPlayerPermissions(EntityPlayer player)
    { return getPlayerPermissions(player.getGameProfile().getId()); }
}