package com.enkigaming.minecraft.forge.enkipermissions.commandlisteners;

import com.enkigaming.minecraft.forge.enkipermissions.EnkiPerms;
import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;
import com.enkigaming.minecraft.forge.enkipermissions.wtfquestionmark.Rank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.NotImplementedException;

public class CmdRank extends CommandBase
{
    
    
//    public void onCommand(ICommandSender ics, String[] args)
//    {
//        EntityPlayer ep = null;
//        
//        if(ics instanceof EntityPlayer)
//            ep = (EntityPlayer)ics;
//        else
//            ep = getPlayer(ics, args[0]);
//        
//        Rank r = Rank.getPlayerRank(LMPlayer.getPlayer(ep));
//        
//        LatCoreMC.printChat(ics, ep.getDisplayName() + "'s rank is " + Rank.getRankName(r));
//    }

    @Override
    public String getCommandName()
    { return "Rank"; }

    @Override
    public String getCommandUsage(ICommandSender sender)
    { return "Subcommands: get, set, prefix, suffix, permission. Type /rank help <subcommand> for help with it."; }
    
    @Override
    public List getCommandAliases()
    { return Arrays.asList("rank", "RANK"); }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        throw new NotImplementedException("Not implemented yet.");
    }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    { return true; } // Permissions are on a subcommand level.
    
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length <= 0)
            return Arrays.asList("get", "set", "prefix", "suffix", "permission");
        else if(args[0].equalsIgnoreCase("get"))
        {
            if(args.length <= 1)
                return getOnlinePlayerNames();
            else
                return new ArrayList<String>();
        }
        else if(args[0].equalsIgnoreCase("set"))
        {
            if(args.length <= 1)
                return getOnlinePlayerNames();
            else if(args.length <= 2)
                return new ArrayList<String>(EnkiPerms.getInstance().getRanks().getRankNames());
            else
                return new ArrayList<String>();
        }
        else if(args[0].equalsIgnoreCase("prefix") || args[0].equalsIgnoreCase("suffix"))
        {
            if(args.length <= 1)
                return Arrays.asList("get", "get");
            else
            {
                if(args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("set"))
                {
                    if(args.length <= 2)
                        return new ArrayList<String>(EnkiPerms.getInstance().getRanks().getRankNames());
                    else
                        return new ArrayList<String>();
                }
                else
                    return new ArrayList<String>();
            }
        }
        else if(args[0].equalsIgnoreCase("permission"))
        {
            if(args.length <= 1)
            {
                return Arrays.asList("give",             "remove",             "removeall",
                                     "cancel",           "check",              "include",
                                     "removeincluder",   "removeallincluders");
            }
            else if(args[1].equalsIgnoreCase("give")
                 || args[1].equalsIgnoreCase("remove")
                 || args[1].equalsIgnoreCase("cancel")
                 || args[1].equalsIgnoreCase("check")
                 || args[1].equalsIgnoreCase("removeall")
                 || args[1].equalsIgnoreCase("removeallincluders")
                 || args[1].equalsIgnoreCase("include"))
            {
                if(args.length <= 2)
                    return new ArrayList<String>(EnkiPerms.getInstance().getRanks().getRankNames());
                else
                    return new ArrayList<String>();
            }
            else if(args[1].equalsIgnoreCase("removeincluder"))
            {
                if(args.length <= 2)
                    return new ArrayList<String>(EnkiPerms.getInstance().getRanks().getRankNames());
                else if(args.length <= 3)
                {
                    Rank rank = EnkiPerms.getInstance().getRanks().getRank(args[2]);
                    
                    if(rank == null)
                        return new ArrayList<String>();
                    
                    return new ArrayList<String>(rank.getDirectPermissionIncluderNames());
                }
                else
                    return new ArrayList<String>();
            }
            else
                return new ArrayList<String>();
        }
        else
            return new ArrayList<String>();
    }
    
    protected List<String> getOnlinePlayerNames()
    {
        throw new NotImplementedException("Not implemented yet.");
    }
}