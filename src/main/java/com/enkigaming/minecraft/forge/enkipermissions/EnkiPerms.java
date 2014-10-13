package com.enkigaming.minecraft.forge.enkipermissions;

import com.enkigaming.minecraft.forge.enkilib.filehandling.FileHandlerRegistry;
import com.enkigaming.minecraft.forge.enkipermissions.registry.PermissionsRegistry;
import com.enkigaming.minecraft.forge.enkipermissions.registry.PlayerRankRegistry;
import com.enkigaming.minecraft.forge.enkipermissions.registry.RankRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import java.io.File;
import org.apache.commons.lang3.NotImplementedException;

@Mod(modid = EnkiPerms.MODID, name = EnkiPerms.MODID, version = EnkiPerms.VERSION, acceptableRemoteVersions = "*")
public class EnkiPerms
{
    public static final String NAME = "EnkiPerms";
    public static final String MODID = "EnkiPerms";
    public static final String VERSION = "1.0";

    @Instance(EnkiPerms.MODID)
    protected static EnkiPerms instance;
    
    protected PermissionsRegistry permissions;
    protected RankRegistry ranks;
    protected PlayerRankRegistry playerRanks;
    protected FileHandlerRegistry fileHandling;
    protected File saveFolder;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        initialiseRegistries();
        
        loadData();
        registerEvents();
    }
    
    @EventHandler
    public void registerCommands(FMLServerStartingEvent e)
    {
        throw new NotImplementedException("To be implemented");
    }
        
    private void initialiseRegistries()
    {
        throw new NotImplementedException("To be implemented");
    }
    
    private void registerFileHandlers()
    {
        throw new NotImplementedException("To be implemented");
    }
    
    private void registerEvents()
    {
        throw new NotImplementedException("To be implemented");
    }
    
    public void loadData()
    {
        throw new NotImplementedException("To be implemented");
    }
    
    public void saveData()
    {
        throw new NotImplementedException("To be implemented");
    }
        
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
}