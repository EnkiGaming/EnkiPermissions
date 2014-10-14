package com.enkigaming.mcforge.enkipermissions.commandlisteners;

import com.enkigaming.mcforge.enkipermissions.EnkiPerms;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class CmdEnkiperms extends CommandBase
{
    protected static enum HelpOption
    {
        enkiperms("Allows access to miscellaneous EnkiPerms commands.",
                  "Enkiperms <subcommand> <subcommand-specific arguments>",
                  "reloadfiles"),
        
        enkipermsReloadfiles("Overwrites currently loaded data with data in save files.",
                             "Enkiperms reloadfiles",
                             null),
        
        enkipermsHelp("Now you're just taking the piss.",
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
    { return "Enkiperms"; }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    { return "Subcommands: reloadfiles"; }
    
    @Override
    public List getCommandAliases()
    {
        return Arrays.asList("enkiperms",       "ENKIPERMS",       "EnkiPerms",       "enkiPerms",
                             "enkipermissions", "ENKIPERMISSIONS", "EnkiPermissions", "enkiPermissions", "Enkipermissions");
    }

    @Override
    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    { return true; } // Permissions are on a subcommand level.
    
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
    
    protected void handleEnkiperms(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderHelp(sender, HelpOption.enkiperms);
        else if(args.get(0).equalsIgnoreCase("Reloadfiles"))
            handleEnkipermsReloadfiles(sender, args.subList(0, args.size()));
        else if(args.get(0).equalsIgnoreCase("help"))
            handleEnkipermsHelp(sender, args.subList(0, args.size()));
        else
            sendSenderUsage(sender, HelpOption.enkiperms);
    }
    
    protected void handleEnkipermsReloadfiles(ICommandSender sender, List<String> args)
    {
        if(args.size() > 0)
        {
            sendSenderUsage(sender, HelpOption.enkipermsReloadfiles);
            return;
        }
        
        if(!checkPermission(sender, "enkiperms.reloadfiles"))
            return;
        
        EnkiPerms.getInstance().loadData();
    }
    
    protected void handleEnkipermsHelp(ICommandSender sender, List<String> args)
    {
        if(args.size() <= 0)
            sendSenderHelp(sender, HelpOption.enkiperms);
        else if(args.get(1).equalsIgnoreCase("reloadfiles"))
            handleEnkipermsHelpReloadfiles(sender, args.subList(1, args.size()));
        else if(args.get(1).equalsIgnoreCase("help"))
            handleEnkipermsHelpHelp(sender, args.subList(1, args.size()));
        else
            sendSenderUsage(sender, HelpOption.enkipermsHelp);
    }
    
    protected void handleEnkipermsHelpReloadfiles(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.enkipermsReloadfiles); }
    
    protected void handleEnkipermsHelpHelp(ICommandSender sender, List<String> args)
    { sendSenderHelp(sender, HelpOption.enkipermsHelp); }
}