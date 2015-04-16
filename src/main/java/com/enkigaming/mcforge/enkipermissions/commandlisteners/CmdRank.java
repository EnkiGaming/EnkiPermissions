package com.enkigaming.mcforge.enkipermissions.commandlisteners;

import com.enkigaming.mcforge.lib.EnkiLib;
import com.enkigaming.mcforge.enkipermissions.EnkiPerms;
import com.enkigaming.mcforge.enkipermissions.ranks.Rank;
import com.enkigaming.mcforge.enkipermissions.registry.exceptions.ItemWithNameAlreadyPresentException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CmdRank extends CommandBase
{
    protected static enum HelpOption
    {
        rank("Base command for rank-related commands.",
             "Rank <subcommand and additional, subcommand-specific arguments>",
             "get, set, prefix, suffix, permission, help"),
        
        rankGet("Gets the rank of the player with the passed name last recorded.",
                "Rank get <LastRecordedPlayerName>",
                null),
        
        rankSet("Sets the rank of the player with the passed name last recorded.",
                "Rank set <LastRecordedPlayerName> <NameOfNewRank>",
                null),
        
        rankSetDefault("Sets the rank players without ranks assigned get.",
                       "Rank setdefault <RankName>",
                       null),
        
        rankCreate("Creates a rank using the given name",
                   "Rank create <NewRankName>",
                   null),
        
        rankDelete("Deletes a rank with the given name and removes it as a permission includer",
                   "Rank delete <RankName>",
                   null),
        
        rankPrefix("Allows access to the rank's chat prefix.",
                   "Rank prefix <set/get> <RankName> <NewPrefix>",
                   "get, set"),
        
        rankPrefixGet("Shows the current chat prefix of a rank.",
                      "Rank prefix get <RankName>",
                      null),
        
        rankPrefixSet("Changes the rank's chat prefix.",
                      "Rank prefix set <RankName> <NewPrefix>",
                      null),
        
        rankSuffix("Allows access to the rank's chat suffix.",
                   "Rank suffixfix <set/get> <RankName> <NewPrefix>",
                   "get, set"),
        
        rankSuffixGet("Shows the current chat suffix of a rank.",
                      "Rank suffix get <RankName>",
                      null),
        
        rankSuffixSet("Changes the rank's chat suffix.",
                      "Rank suffix set <RankName> <NewSuffix>",
                      null),
        
        rankPermission("Allows access to the rank's associated permissions",
                       "Rank permission <subcommand and addition, subcommand-specific arguments>",
                       "give, remove, removeall, cancel, check, include, removeincluder, removeallincluders"),
        
        rankPermissionGive("Gives a permission to the rank.",
                           "Rank permission give <RankName> <PermissionToGive>",
                           null),
        
        rankPermissionRemove("Removes a permission from the rank",
                             "Rank permission remove <RankName> <PermissionToRemove>",
                             null),
        
        rankPermissionRemoveall("Removes all directly associated permissions from a rank",
                                "Rank permission removeall",
                                null),
        
        rankPermissionCancel("Gives a rank a permission that cancels out the passed permission",
                             "Rank permission cancel <RankName> <PermissionToCancelOut>",
                             null),
        
        rankPermissionCheck("Checks whether a rank has a given permission",
                            "Rank permission check <RankName> <PermissionToCheckFor>",
                            null),
        
        rankPermissionInclude("Includes one rank's permissions in another.",
                              "Rank permission include <RankNameToGetPermissions> <RankWithPermissionsToInclude>",
                              null),
        
        rankPermissionRemoveincluder("Removes a rank as a permission includer from another.",
                                     "Rank permission removeincluder <RankNameToLosePermissions> <RankNameToStopIncluding>",
                                     null),
        
        rankPermissionRemoveallincluders("Removes all reanks as permission includers from the specified one.",
                                         "Rank permission removeallincluders <RankName>",
                                         null),
        
        rankHelp("Now you're just taking the piss.",
                 "Rank help <Subcommand set to get help with>",
                 null);
        
        HelpOption(String description, String usage, String subcommands)
        {
            this.description = description;
            this.usage = usage;
            this.subcommands = subcommands;
        }
        
        String description;
        String usage;
        String subcommands;
        
        public String getDescription()
        { return description; }
        
        public String getUsage()
        { return usage; }
        
        public String getSubcommands()
        { return subcommands; }
    }
    
    final String usageText = "Usage: ";
    final String subcommandsText = "Subcommands: ";

    @Override
    public String getCommandName()
    { return "Rank"; }

    @Override
    public String getCommandUsage(ICommandSender sender)
    { return "Subcommands: get, set, setdefault, create, delete, prefix, suffix, permission, help. Type /rank help <subcommand> for help with it."; }
    
    @Override
    public List getCommandAliases()
    { return Arrays.asList("rank", "RANK"); }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    { handleRank(sender, new ArrayList<String>(Arrays.asList(args))); }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    { return true; } // Permissions are on a subcommand level.
    
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length <= 0)
            return Arrays.asList("get", "set", "setdefault", "create", "delete" ,"prefix", "suffix", "permission");
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
        else if(args[0].equalsIgnoreCase("create"))
        {
            return new ArrayList<String>();
        }
        else if(args[0].equalsIgnoreCase("setdefault") || args[0].equalsIgnoreCase("delete"))
        {
            if(args.length <= 1)
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
    { return Arrays.<String>asList(MinecraftServer.getServer().getConfigurationManager().getAllUsernames()); }
    
    protected void sendSenderUsage(ICommandSender sender, HelpOption help)
    {
        String usage = help.getUsage();
        String subcommands = help.getSubcommands();
        
        if(usage != null)
            sender.addChatMessage(new ChatComponentText(usageText + usage));
        
        if(subcommands != null)
            sender.addChatMessage(new ChatComponentText(subcommandsText + subcommands));
    }
    
    protected void sendSenderHelp(ICommandSender sender, HelpOption help)
    {
        String description = help.getDescription();
        
        if(description != null)
            sender.addChatMessage(new ChatComponentText(description));
        
        sendSenderUsage(sender, help);
    }
    
    protected boolean checkPermission(ICommandSender sender, String permission)
    {
        if(sender instanceof EntityPlayer)
            if(!EnkiPerms.hasPermission((EntityPlayer)sender, permission))
            {
                sender.addChatMessage(new ChatComponentText("You don't have permission to do that."));
                return false;
            }
        
        return true;
    }
    
    // ========================================================================================================
    // ===============Below this are the handle methods, which handle every possible subcommand.===============
    // ========================================================================================================
    //
    // To do: I can create methods for some repeating parts in the handlers.
    
    protected void handleRank(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderUsage(sender, HelpOption.rank);
        else if(args.get(0).equalsIgnoreCase("get"))
            handleRankGet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("set"))
            handleRankSet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("setDefault"))
            handleRankSet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("create"))
            handleRankCreate(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("delete"))
            handleRankDelete(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("prefix"))
            handleRankPrefix(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("suffix"))
            handleRankSuffix(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("permission"))
            handleRankPermission(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("help"))
            handleRankGet(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.rank);
    }
    
    protected void handleRankGet(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankGet);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.getplayerrank"))
            return;
        
        UUID playerId = EnkiLib.getLastRecordedIDForName(args.get(0));

        if(playerId == null)
        {
            sender.addChatMessage(new ChatComponentText("There isn't a player with the name \"" + args.get(0)
                                                        + "\" recorded."));
            sendSenderUsage(sender, HelpOption.rankGet);
            return;
        }

        Rank rank = EnkiPerms.getInstance().getPlayerRanks().getPlayerRank(playerId);

        if(rank == null)
             sender.addChatMessage(new ChatComponentText("Could not get player rank. Have you set a default?"));

        sender.addChatMessage(new ChatComponentText("Player has the rank: " + rank.getName()));
    }
    
    protected void handleRankSet(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankSet);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.setplayerrank"))
            return;
        
        UUID playerId = EnkiLib.getLastRecordedIDForName(args.get(0));
        
        if(playerId == null)
        {
            sender.addChatMessage(new ChatComponentText("There isn't a player with the name \"" + args.get(0)
                                                        + "\" recorded."));
            sendSenderUsage(sender, HelpOption.rankSet);
            return;
        }
        
        try
        {
            EnkiPerms.getInstance().getPlayerRanks().setPlayerRank(playerId, args.get(1));
            sender.addChatMessage(new ChatComponentText("Rank set!"));
        }
        catch(IllegalArgumentException exception)
        {
            sender.addChatMessage(new ChatComponentText("There is no rank by that name."));
            sendSenderUsage(sender, HelpOption.rankSet);
        }
    }
    
    protected void handleRankSetdefault(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankSetDefault);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.setplayerrank"))
            return;
        
        try
        {
            EnkiPerms.getInstance().getRanks().setDefaultRank(args.get(0));
            sender.addChatMessage(new ChatComponentText("Default rank set!"));
        }
        catch(IllegalArgumentException exception)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankSetDefault);
        }
    }
    
    protected void handleRankCreate(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankCreate);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.create"))
            return;
        
        try
        {
            EnkiPerms.getInstance().getRanks().createRank(args.get(0));
            sender.addChatMessage(new ChatComponentText("Rank created!"));
        }
        catch(ItemWithNameAlreadyPresentException exception)
        {
            sender.addChatMessage(new ChatComponentText("A rank already exists with that name."));
            sendSenderUsage(sender, HelpOption.rankCreate);
        }
    }
    
    protected void handleRankDelete(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankDelete);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.delete"))
            return;
        
        boolean deleted = EnkiPerms.getInstance().getRanks().removeRank(args.get(0)) != null;
        
        if(deleted)
            sender.addChatMessage(new ChatComponentText("Rank deleted!"));
        else
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankDelete);
        }
    }
    
    protected void handleRankPrefix(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderUsage(sender, HelpOption.rankPrefix);
        else if(args.get(0).equalsIgnoreCase("get"))
            handleRankPrefixGet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("set"))
            handleRankPrefixSet(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.rankPrefix);
    }
    
    protected void handleRankPrefixGet(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankPrefixGet);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.prefix.get"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPrefixGet);
            return;
        }
        
        sender.addChatMessage(new ChatComponentText("Prefix: " + rank.getUsernamePrefix()));
    }
    
    protected void handleRankPrefixSet(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankPrefixSet);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.prefix.set"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPrefixSet);
            return;
        }
        
        rank.setUsernamePrefix(args.get(0));
        sender.addChatMessage(new ChatComponentText("Prefix set!"));
    }
    
    protected void handleRankSuffix(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderUsage(sender, HelpOption.rankSuffix);
        else if(args.get(0).equalsIgnoreCase("get"))
            handleRankPrefixGet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("set"))
            handleRankPrefixSet(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.rankSuffix);
    }
    
    protected void handleRankSuffixGet(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankSuffixGet);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.suffix.get"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankSuffixGet);
            return;
        }
        
        sender.addChatMessage(new ChatComponentText("Suffix: " + rank.getUsernameSuffix()));
    }
    
    protected void handleRankSuffixSet(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankSuffixSet);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.rank.suffix.set"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankSuffixSet);
            return;
        }
        
        rank.setUsernameSuffix(args.get(0));
        sender.addChatMessage(new ChatComponentText("Suffix set!"));
    }
    
    protected void handleRankPermission(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderUsage(sender, HelpOption.rankPermission);
        else if(args.get(0).equalsIgnoreCase("give"))
            handleRankPermissionGive(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("remove"))
            handleRankPermissionRemove(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("removeall"))
            handleRankPermissionRemoveall(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("cancel"))
            handleRankPermissionCancel(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("check"))
            handleRankPermissionCheck(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("include"))
            handleRankPermissionInclude(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("removeincluder"))
            handleRankPermissionRemoveincluder(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("removeallincluders"))
            handleRankPermissionRemoveallincluders(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.rankPermission);
    }
    
    protected void handleRankPermissionGive(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionGive);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPermissionGive);
            return;
        }
        
        rank.givePermission(args.get(1));
        sender.addChatMessage(new ChatComponentText("Permission given!"));
    }
    
    protected void handleRankPermissionRemove(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionRemove);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPermissionRemove);
            return;
        }
        
        if(rank.removePermission(args.get(1)))
            sender.addChatMessage(new ChatComponentText("Permission removed!"));
        else
            sender.addChatMessage(new ChatComponentText("Permission not found."));
    }
    
    protected void handleRankPermissionRemoveall(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionRemoveall);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPermissionRemoveall);
            return;
        }
        
        rank.removeAllPermissions();
        sender.addChatMessage(new ChatComponentText("Permissions removed!"));
    }
    
    protected void handleRankPermissionCancel(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionCancel);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPermissionCancel);
            return;
        }
        
        rank.givePermission("-" + args.get(1));
        sender.addChatMessage(new ChatComponentText("Permission cancelled out!"));
    }
    
    protected void handleRankPermissionCheck(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionCheck);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.check.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPermissionCheck);
            return;
        }
        
        if(rank.hasPermission(args.get(1)))
            sender.addChatMessage(new ChatComponentText("Rank has that permission!"));
        else
            sender.addChatMessage(new ChatComponentText("Rank does not have that permission!"));
    }
    
    protected void handleRankPermissionInclude(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionInclude);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modifyincluders.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        Rank includer = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by the name " + args.get(0) + " found."));
            sendSenderUsage(sender, HelpOption.rankPermissionInclude);
            return;
        }
        
        if(includer == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by the name " + args.get(1) + " found."));
            sendSenderUsage(sender, HelpOption.rankPermissionInclude);
            return;
        }
        
        if(rank.addPermissionIncluder(includer))
            sender.addChatMessage(new ChatComponentText("Permissions included!"));
        else
            sender.addChatMessage(new ChatComponentText("Permissions already included."));
    }
    
    protected void handleRankPermissionRemoveincluder(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionInclude);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modifyincluders.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        Rank includer = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by the name " + args.get(0) + " found."));
            sendSenderUsage(sender, HelpOption.rankPermissionRemoveincluder);
            return;
        }
        
        if(includer == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by the name " + args.get(1) + " found."));
            sendSenderUsage(sender, HelpOption.rankPermissionRemoveincluder);
            return;
        }
        
        if(rank.removePermissionIncluder(includer))
            sender.addChatMessage(new ChatComponentText("Permissions includer removed!"));
        else
            sender.addChatMessage(new ChatComponentText("Permissions includer not present."));
    }
    
    protected void handleRankPermissionRemoveallincluders(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.rankPermissionRemoveallincluders);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modifyincluders.rank"))
            return;
        
        Rank rank = EnkiPerms.getInstance().getRanks().getRank(args.get(0));
        
        if(rank == null)
        {
            sender.addChatMessage(new ChatComponentText("No rank by that name found."));
            sendSenderUsage(sender, HelpOption.rankPermissionRemoveallincluders);
            return;
        }
        
        rank.removeAllPermissionIncluders();
        sender.addChatMessage(new ChatComponentText("Permissions includers removed!"));
    }
    
    protected void handleRankHelp(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderHelp(sender, HelpOption.rank);
        else if(args.get(0).equalsIgnoreCase("get"))
            handleRankHelpGet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("set"))
            handleRankHelpSet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("create"))
            handleRankHelpCreate(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("delete"))
            handleRankHelpDelete(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("prefix"))
            handleRankHelpPrefix(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("suffix"))
            handleRankHelpSuffix(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("permission"))
            handleRankHelpPermission(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("help"))
            handleRankHelpHelp(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.rankHelp);
    }
    
    protected void handleRankHelpGet(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankGet); }
    
    protected void handleRankHelpSet(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankSet); }
    
    protected void handleRankHelpCreate(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankCreate); }
    
    protected void handleRankHelpDelete(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankDelete); }
    
    protected void handleRankHelpPrefix(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderHelp(sender, HelpOption.rankPrefix);
        else if(args.get(0).equalsIgnoreCase("get"))
            handleRankHelpPrefixGet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("set"))
            handleRankHelpPrefixSet(sender, args.subList(1, args.size()));
        else
            sendSenderHelp(sender, HelpOption.rankPrefix);
    }
    
    protected void handleRankHelpPrefixGet(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPrefixGet); }
    
    protected void handleRankHelpPrefixSet(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPrefixSet); }
    
    protected void handleRankHelpSuffix(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderHelp(sender, HelpOption.rankSuffix);
        else if(args.get(0).equalsIgnoreCase("get"))
            handleRankHelpSuffixGet(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("set"))
            handleRankHelpSuffixSet(sender, args.subList(1, args.size()));
        else
            sendSenderHelp(sender, HelpOption.rankSuffix);
    }
    
    protected void handleRankHelpSuffixGet(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankSuffixGet); }
    
    protected void handleRankHelpSuffixSet(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankSuffixSet); }
    
    protected void handleRankHelpPermission(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderHelp(sender, HelpOption.rankPermission);
        else if(args.get(0).equalsIgnoreCase("give"))
            handleRankHelpPermissionGive(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("remove"))
            handleRankHelpPermissionRemove(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("removeall"))
            handleRankHelpPermissionRemoveall(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("cancel"))
            handleRankHelpPermissionCancel(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("check"))
            handleRankHelpPermissionCheck(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("include"))
            handleRankHelpPermissionInclude(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("removeincluder"))
            handleRankHelpPermissionRemoveincluder(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("removeallincluders"))
            handleRankHelpPermissionRemoveallincluders(sender, args.subList(1, args.size()));
        else
            sendSenderHelp(sender, HelpOption.rankPermission);
    }
    
    protected void handleRankHelpPermissionGive(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionGive); }
    
    protected void handleRankHelpPermissionRemove(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionRemove); }
    
    protected void handleRankHelpPermissionRemoveall(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionRemoveall); }
    
    protected void handleRankHelpPermissionCancel(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionCancel); }
    
    protected void handleRankHelpPermissionCheck(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionCheck); }
    
    protected void handleRankHelpPermissionInclude(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionInclude); }
    
    protected void handleRankHelpPermissionRemoveincluder(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionRemoveincluder); }
    
    protected void handleRankHelpPermissionRemoveallincluders(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankPermissionRemoveallincluders); }
    
    protected void handleRankHelpHelp(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.rankHelp); }
}