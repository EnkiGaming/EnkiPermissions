package latmod.enkigaming.perms;

import latmod.enkigaming.perms.json.Rank;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = EnkiPerms.MODID, name = "EnkiPerms", version = "1.0", acceptableRemoteVersions = "*", dependencies = "required-after:EnkiCore")
public class EnkiPerms
{
	public static final String MODID = "EnkiPerms";
	
	@Mod.Instance(EnkiPerms.MODID)
	public static EnkiPerms inst;
	
	public Rank[] ranks;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
	}
}