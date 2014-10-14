package com.enkigaming.mcforge.enkipermissions.commandlisteners;

import com.enkigaming.mcforge.enkilib.EnkiLib;
import com.enkigaming.mcforge.enkipermissions.EnkiPerms;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CmdPermission extends CommandBase
{
    protected static enum HelpOption
    {
        permission("Allows administraction of player permissions.",
                   "Permission <subcommand> <subcommand-specific arguments>",
                   "give, remove, removeall, cancel, check"),
        
        permissionGive("Gives a specified permission to the player",
                       "Permission give <LastRecordedPlayerName> <Permission>",
                       null),
        
        permissionRemove("Removes a specified permission from the player",
                         "Permission remove <LastRecordedPlayerName> <Permission>",
                         null),
        
        permissionRemoveall("Removes all permissions from the player",
                            "Permission removeall <LastRecordedPlayerName>",
                            null),
        
        permissionCancel("Gives a permission to the player that cancels out the specified permission.",
                         "Permission cancel <LastRecordedPlayerName> <Permission>",
                         null),
        
        permissionCheck("Checks whether a player has a specified permission.",
                        "Permission check <LastRecordedPlayerName> <Permission>",
                        null),
        
        permissionHelp("Now you're just taking the piss.",
                       "Permission help <Subcommand set to get help with>",
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
    { return "Permission"; }

    @Override
    public String getCommandUsage(ICommandSender sender)
    { return "Subcommands: give, remove, removeall, cancel, check"; }
    
    @Override
    public List getCommandAliases()
    {
        return Arrays.asList("permission",  "PERMISSION",
                             "permissions", "PERMISSIONS", "Permissions",
                             "perms",       "PERMS",       "Perms",
                             "perm",        "PERM",        "Perm");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    { handlePermission(sender, new ArrayList<String>(Arrays.asList(args))); }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    { return true; } // Permissions are on a subcommand level.
    
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length <= 0)
            return Arrays.asList("give", "remove", "removeall", "cancel", "check");
        else if(args[0].equalsIgnoreCase("give")
             || args[0].equalsIgnoreCase("remove")
             || args[0].equalsIgnoreCase("removeall")
             || args[0].equalsIgnoreCase("cancel")
             || args[0].equalsIgnoreCase("check"))
        {
            if(args.length <= 1)
                return Arrays.<String>asList(MinecraftServer.getServer().getConfigurationManager().getAllUsernames());
            else
                return new ArrayList<String>();
        }
        else
            return new ArrayList<String>();
    }
    
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
    
    protected void handlePermission(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderUsage(sender, HelpOption.permission);
        else if(args.get(0).equalsIgnoreCase("give"))
            handlePermissionGive(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("remove"))
            handlePermissionRemove(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("removeall"))
            handlePermissionRemoveall(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("cancel"))
            handlePermissionCancel(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("check"))
            handlePermissionCheck(sender, args.subList(1, args.size()));
        else if(args.get(0).equalsIgnoreCase("help"))
            handlePermissionHelp(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.permission);
    }
    
    protected void handlePermissionGive(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.permissionGive);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.player"))
            return;
        
        UUID playerId = EnkiLib.getInstance().getUsernameCache().getLastRecordedUUIDForName(args.get(0));

        if(playerId == null)
        {
            sender.addChatMessage(new ChatComponentText("There isn't a player with that name recorded."));
            sendSenderUsage(sender, HelpOption.permissionGive);
            return;
        }
        
        EnkiPerms.getInstance().getPermissions().givePlayerPermission(playerId, args.get(1));
        sender.addChatMessage(new ChatComponentText("Permission given!"));
    }
    
    protected void handlePermissionRemove(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.permissionRemove);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.player"))
            return;
        
        UUID playerId = EnkiLib.getInstance().getUsernameCache().getLastRecordedUUIDForName(args.get(0));

        if(playerId == null)
        {
            sender.addChatMessage(new ChatComponentText("There isn't a player with that name recorded."));
            sendSenderUsage(sender, HelpOption.permissionRemove);
            return;
        }
        
        if(EnkiPerms.getInstance().getPermissions().removePlayerPermission(playerId, args.get(1)))
            sender.addChatMessage(new ChatComponentText("Permission removed!"));
        else
            sender.addChatMessage(new ChatComponentText("Permission not found for player."));
    }
    
    protected void handlePermissionRemoveall(ICommandSender sender, List<String> args)
    {
        if(args.size() != 1)
        {
            sendSenderUsage(sender, HelpOption.permissionRemoveall);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.player"))
            return;
        
        UUID playerId = EnkiLib.getInstance().getUsernameCache().getLastRecordedUUIDForName(args.get(0));

        if(playerId == null)
        {
            sender.addChatMessage(new ChatComponentText("There isn't a player with that name recorded."));
            sendSenderUsage(sender, HelpOption.permissionRemoveall);
            return;
        }
        
        EnkiPerms.getInstance().getPermissions().removePlayerPermissions(playerId);
        sender.addChatMessage(new ChatComponentText("Permissions removed!"));
    }
    
    protected void handlePermissionCancel(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.permissionCancel);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.player"))
            return;
        
        UUID playerId = EnkiLib.getInstance().getUsernameCache().getLastRecordedUUIDForName(args.get(0));

        if(playerId == null)
        {
            sender.addChatMessage(new ChatComponentText("There isn't a player with that name recorded."));
            sendSenderUsage(sender, HelpOption.permissionCancel);
            return;
        }
        
        if(EnkiPerms.getInstance().getPermissions().givePlayerPermission(playerId, "-" + args.get(1))) // if player already had the permission
            sender.addChatMessage(new ChatComponentText("Player already had cancelling permission."));
        else
            sender.addChatMessage(new ChatComponentText("Player permission cancelled!"));
    }
    
    protected void handlePermissionCheck(ICommandSender sender, List<String> args)
    {
        if(args.size() != 2)
        {
            sendSenderUsage(sender, HelpOption.permissionCheck);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.permission.modify.player"))
            return;
        
        UUID playerId = EnkiLib.getInstance().getUsernameCache().getLastRecordedUUIDForName(args.get(0));

        if(playerId == null)
        {
            sender.addChatMessage(new ChatComponentText("There isn't a player with that name recorded."));
            sendSenderUsage(sender, HelpOption.permissionCheck);
            return;
        }
        
        if(EnkiPerms.hasPermission(playerId, args.get(1)))
            sender.addChatMessage(new ChatComponentText("Player has that permission!"));
        else
            sender.addChatMessage(new ChatComponentText("Player does not have that permission!"));
    }
    
    protected void handlePermissionHelp(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderHelp(sender, HelpOption.permission);
        else if(args.get(1).equalsIgnoreCase("give"))
            handlePermissionHelpGive(sender, args.subList(1, args.size()));
        else if(args.get(1).equalsIgnoreCase("remove"))
            handlePermissionHelpRemove(sender, args.subList(1, args.size()));
        else if(args.get(1).equalsIgnoreCase("removeall"))
            handlePermissionHelpRemoveall(sender, args.subList(1, args.size()));
        else if(args.get(1).equalsIgnoreCase("cancel"))
            handlePermissionHelpCancel(sender, args.subList(1, args.size()));
        else if(args.get(1).equalsIgnoreCase("check"))
            handlePermissionHelpCheck(sender, args.subList(1, args.size()));
        else if(args.get(1).equalsIgnoreCase("help"))
            handlePermissionHelpHelp(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.permissionHelp);
    }
    
    protected void handlePermissionHelpGive(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.permissionGive); }
    
    protected void handlePermissionHelpRemove(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.permissionRemove); }
    
    protected void handlePermissionHelpRemoveall(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.permissionRemoveall); }
    
    protected void handlePermissionHelpCancel(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.permissionCancel); }
    
    protected void handlePermissionHelpCheck(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.permissionCheck); }
    
    protected void handlePermissionHelpHelp(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.permissionHelp); }
}