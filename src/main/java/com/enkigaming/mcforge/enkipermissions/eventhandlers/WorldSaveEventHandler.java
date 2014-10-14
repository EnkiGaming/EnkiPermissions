package com.enkigaming.mcforge.enkipermissions.eventhandlers;

import com.enkigaming.mcforge.enkipermissions.EnkiPerms;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class WorldSaveEventHandler
{
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event)
    { EnkiPerms.getInstance().saveData(); }
}