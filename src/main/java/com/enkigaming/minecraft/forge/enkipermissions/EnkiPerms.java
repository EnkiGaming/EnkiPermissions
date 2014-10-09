package com.enkigaming.minecraft.forge.enkipermissions;

import com.enkigaming.minecraft.forge.enkipermissions.commandlisteners.CmdSetRank;
import com.enkigaming.minecraft.forge.enkipermissions.commandlisteners.CmdReload;
import com.enkigaming.minecraft.forge.enkipermissions.commandlisteners.CmdGetRank;
import latmod.enkigaming.perms.cmd.*;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import com.enkigaming.minecraft.forge.enkipermissions.eventhandlers.EnkiPermsEventHandler;
import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;

@Mod(modid = EnkiPerms.MODID, name = "EnkiPerms", version = "1.0", acceptableRemoteVersions = "*", dependencies = "required-after:EnkiCore")
public class EnkiPerms
{
	public static final String MODID = "EnkiPerms";
	
	@Mod.Instance(EnkiPerms.MODID)
	public static EnkiPerms inst;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(EnkiPermsEventHandler.instance);
		
		Rank.reload();
	}
	
	@Mod.EventHandler
	public void preInit(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdReload());
		e.registerServerCommand(new CmdSetRank());
		e.registerServerCommand(new CmdGetRank());
	}
}