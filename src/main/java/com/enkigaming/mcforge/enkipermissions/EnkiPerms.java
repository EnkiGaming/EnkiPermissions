package com.enkigaming.mcforge.enkipermissions;

import com.enkigaming.mcforge.enkilib.filehandling.FileHandlerRegistry;
import com.enkigaming.mcforge.enkipermissions.commandlisteners.CmdEnkiperms;
import com.enkigaming.mcforge.enkipermissions.commandlisteners.CmdPermission;
import com.enkigaming.mcforge.enkipermissions.commandlisteners.CmdRank;
import com.enkigaming.mcforge.enkipermissions.eventhandlers.WorldSaveEventHandler;
import com.enkigaming.mcforge.enkipermissions.permissions.PermissionNode;
import com.enkigaming.mcforge.enkipermissions.registry.PermissionsRegistry;
import com.enkigaming.mcforge.enkipermissions.registry.PlayerRankRegistry;
import com.enkigaming.mcforge.enkipermissions.registry.RankRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import java.io.File;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = EnkiPerms.MODID, name = EnkiPerms.MODID, version = EnkiPerms.VERSION, acceptableRemoteVersions = "*")
public class EnkiPerms
{
    public static final String NAME = "EnkiPerms";
    public static final String MODID = "EnkiPerms";
    public static final String VERSION = "B1.0";

    @Instance(EnkiPerms.MODID)
    protected static EnkiPerms instance;
    
    protected PermissionsRegistry permissions;
    protected RankRegistry ranks;
    protected PlayerRankRegistry playerRanks;
    protected FileHandlerRegistry fileHandling;
    protected File saveFolder;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        saveFolder = new File(event.getModConfigurationDirectory().getParentFile(), "plugins/EnkiPerms");
        initialiseRegistries();
        registerFileHandlers();
        loadData();
        registerEvents();
    }
    
    @EventHandler
    public void registerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CmdEnkiperms());
        event.registerServerCommand(new CmdPermission());
        event.registerServerCommand(new CmdRank());
    }
        
    private void initialiseRegistries()
    {
        permissions = new PermissionsRegistry(saveFolder);
        ranks = new RankRegistry(saveFolder);
        playerRanks = new PlayerRankRegistry(saveFolder);
    }
    
    private void registerFileHandlers()
    {
        fileHandling.register(permissions.getFileHandler());
        fileHandling.register(ranks.getFileHandler());
        fileHandling.register(playerRanks.getFileHandler());
    }
    
    private void registerEvents()
    {
        MinecraftForge.EVENT_BUS.register(new WorldSaveEventHandler());
    }
    
    public void loadData()
    { fileHandling.load(); }
    
    public void saveData()
    { fileHandling.save(); }
        
    public static EnkiPerms getInstance()
    { return instance; }
    
    public PermissionsRegistry getPermissions()
    { return permissions; }
    
    public RankRegistry getRanks()
    { return ranks; }
    
    public PlayerRankRegistry getPlayerRanks()
    { return playerRanks; }
    
    public File getSaveFolder()
    { return saveFolder; }
    
    public FileHandlerRegistry getFileHandling()
    { return fileHandling; }
    
    //========== Convenience Methods ==========
    
    public static boolean hasPermission(UUID playerId, String permission)
    { return getInstance().getPermissions().playerHasPermission(playerId, permission); }
    
    public static boolean hasPermission(UUID playerId, PermissionNode permission)
    { return getInstance().getPermissions().playerHasPermission(playerId, permission); }
    
    public static boolean hasPermission(EntityPlayer player, String permission)
    { return getInstance().getPermissions().playerHasPermission(player, permission); }
    
    public static boolean hasPermission(EntityPlayer player, PermissionNode permission)
    { return getInstance().getPermissions().playerHasPermission(player, permission); }
}