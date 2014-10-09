package latmod.enkigaming.perms;

import latmod.core.LatCoreMC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.*;
import cpw.mods.fml.common.eventhandler.*;

public class EnkiPermsEventHandler
{
	public static final EnkiPermsEventHandler instance = new EnkiPermsEventHandler();
	
	public void playerLoggedIn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent e)
	{
		Rank.saveRanks();
	}
	
	public void nameEvent(net.minecraftforge.event.entity.player.PlayerEvent.NameFormat e)
	{
	}
	
	@SubscribeEvent
	public void commandEvent(CommandEvent e)
	{
		if(e.sender instanceof EntityPlayer)
		{
			if(e.command.getCommandName().equalsIgnoreCase("mail"))
			{
				LatCoreMC.printChat(e.sender, EnumChatFormatting.RED + "You don't have permissions to use this command!");
				e.setCanceled(true);
			}
		}
	}
}