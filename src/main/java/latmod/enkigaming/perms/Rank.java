package latmod.enkigaming.perms;

import java.io.File;
import java.util.*;

import net.minecraft.command.ICommand;
import latmod.core.LatCoreMC;
import latmod.core.mod.LMPlayer;
import latmod.core.util.*;
import latmod.enkigaming.core.EnkiCore;

import com.google.gson.annotations.Expose;

public class Rank
{
	@Expose public Boolean isDefault;
	@Expose public String prefix;
	@Expose public String suffix;
	@Expose public List<String> include;
	@Expose public List<String> permissions;
	
	public void setDefaults()
	{
		isDefault = null;
		prefix = "";
		suffix = "";
		include = new ArrayList<String>();
		permissions = new ArrayList<String>();
	}
	
	public boolean allowCommand(ICommand c)
	{
		return true;
	}
	
	// Static //
	
	private static Rank defaultRank = null;
	public static Map<String, Rank> ranks;
	public static Map<String, List<String>> players = new HashMap<String, List<String>>();
	private static final FastMap<String, Rank> cachedRanks = new FastMap<String, Rank>();
	
	public static void reload()
	{
		cachedRanks.clear();
		
		File f = new File(EnkiCore.pluginsFolder, "EnkiPerms/Ranks.txt");
		
		String col = "c_";
		
		if(!f.exists())
		{
			f = LatCore.newFile(f);
			
			ranks = new HashMap<String, Rank>();
			
			{
				Rank r = new Rank();
				r.setDefaults();
				r.isDefault = true;
				r.prefix = col + "7";
				r.permissions.add("+help");
				r.permissions.add("+motd");
				r.permissions.add("+rules");
				r.permissions.add("+afk");
				r.permissions.add("+list");
				r.permissions.add("+mail");
				r.permissions.add("+msg");
				r.permissions.add("+tell");
				r.permissions.add("+gc");
				ranks.put("Player", r);
			}
			
			{
				Rank r = new Rank();
				r.setDefaults();
				r.prefix = col + "f";
				r.include.add("Player");
				r.permissions.add("+claim");
				r.permissions.add("+getrank");
				ranks.put("Member", r);
			}
			
			{
				Rank r = new Rank();
				r.setDefaults();
				r.prefix = col + "b[VIP]";
				r.include.add("Member");
				r.permissions.add("+sethome");
				r.permissions.add("+home");
				r.permissions.add("+spawn");
				r.permissions.add("+back");
				ranks.put("VIP", r);
			}
			
			{
				Rank r = new Rank();
				r.setDefaults();
				r.prefix = col + "2[Mod]";
				r.include.add("VIP");
				r.permissions.add("+kick");
				r.permissions.add("+whitelist");
		        r.permissions.add("+ban");
		        r.permissions.add("+ban-ip");
		        r.permissions.add("+pardon");
		        r.permissions.add("+pardon-ip");
		        r.permissions.add("+seed");
		        r.permissions.add("+worldedit");
		        r.permissions.add("+setrank");
		        r.permissions.add("+reloadPerms");
				ranks.put("Mod", r);
			}
			
			{
				Rank r = new Rank();
				r.setDefaults();
				r.prefix = col + "4[Admin]";
				r.include.add("Mod");
				r.permissions.add("*");
				
				ranks.put("Admin", r);
			}
			
			LatCoreMC.toJsonFile(new File(EnkiCore.pluginsFolder, "EnkiPerms/Ranks.txt"), ranks);
		}
		
		ranks = LatCoreMC.fromJsonFromFile(f, LatCoreMC.getListType(Rank.class));
		
		defaultRank = null;
		for(Rank r : ranks.values())
		{ if(r.isDefault != null && r.isDefault.booleanValue())
		{ defaultRank = r; break; }}
		
		File f1 = new File(EnkiCore.pluginsFolder, "EnkiPerms/Players.txt");
		
		if(!f1.exists())
		{
			f1 = LatCore.newFile(f1);
			
			players = new HashMap<String, List<String>>();
			
			for(String s : ranks.keySet())
				players.put(s, new ArrayList<String>());
			
			players.get("Admin").add("Baphometis");
			players.get("Mod").add("LatvianModder");
			players.get("Mod").add("HaniiPuppy");
			players.get("VIP").add("Stickyricky24");
			
			saveRanks();
		}
		
		players = LatCoreMC.fromJsonFromFile(f1, LatCoreMC.getMapType(String.class, LatCoreMC.getListType(String.class)));
	}
	
	public static void saveRanks()
	{ LatCoreMC.toJsonFile(new File(EnkiCore.pluginsFolder, "EnkiPerms/Players.txt"), players); }
	
	public static Rank getPlayerRank(LMPlayer ep)
	{
		Rank rank = cachedRanks.get(ep.username);
		if(rank != null) return rank;
		
		Iterator<String> keys = players.keySet().iterator();
		Iterator<List<String>> values = players.values().iterator();
		
		while(keys.hasNext())
		{
			String k = keys.next();
			List<String> v = values.next();
			
			for(String s : v)
			{
				if(s.equalsIgnoreCase(ep.username))
				{
					rank = getRank(k);
					cachedRanks.put(ep.username, rank);
					return rank;
				}
			}
		}
		
		return defaultRank;
	}
	
	public static Rank getRank(String s)
	{
		Rank r = ranks.get(s);
		if(r != null) return r;
		return defaultRank;
	}

	public static String getRankName(Rank r)
	{
		Iterator<String> keys = ranks.keySet().iterator();
		Iterator<Rank> values = ranks.values().iterator();
		
		while(keys.hasNext())
		{
			String s = keys.next();
			Rank r1 = values.next();
			if(r == r1) return s;
		}
		
		return "None";
	}
}