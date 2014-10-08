package latmod.enkigaming.perms;

import net.minecraftforge.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EnkiPermsEventHandler
{
	public static final EnkiPermsEventHandler instance = new EnkiPermsEventHandler();
	
	@SubscribeEvent
	public void nameEvent(net.minecraftforge.event.entity.player.PlayerEvent.NameFormat e)
	{
	}
	
	@SubscribeEvent
	public void chatEvent(ServerChatEvent e)
	{
		//e.component = new ChatComponentTranslation();
	}
	
	@SubscribeEvent
	public void commandEvent(CommandEvent e)
	{
		if(e.command.getCommandName().equalsIgnoreCase("help"))
			e.setCanceled(true);
	}
}