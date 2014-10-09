package com.enkigaming.minecraft.forge.enkipermissions.commandlisteners;

import latmod.core.LatCoreMC;
import latmod.enkigaming.core.cmd.CmdEnki;
import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;
import net.minecraft.command.ICommandSender;

public class CmdReload extends CmdEnki
{
	public CmdReload()
	{ super("reloadPerms"); }
	
	public void onCommand(ICommandSender ics, String[] args)
	{
		Rank.reload();
		LatCoreMC.printChat(ics, "Permissions reloaded!");
	}
}