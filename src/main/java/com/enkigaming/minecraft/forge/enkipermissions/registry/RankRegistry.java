package com.enkigaming.minecraft.forge.enkipermissions.registry;

import com.enkigaming.minecraft.forge.enkilib.filehandling.FileHandler;
import com.enkigaming.minecraft.forge.enkilib.filehandling.TreeFileHandler;
import com.enkigaming.minecraft.forge.enkilib.filehandling.TreeFileHandler.TreeMember;
import com.enkigaming.minecraft.forge.enkipermissions.permissions.PermissionNode;
import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;
import com.enkigaming.minecraft.forge.enkipermissions.registry.exceptions.ItemWithNameAlreadyPresentException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RankRegistry
{
    public RankRegistry(File saveFolder)
    {
        fileHandler = makeFileHandler(saveFolder);
    }

    protected final Map<String, Rank> ranks = new HashMap<String, Rank>();
    protected final FileHandler fileHandler;
    
    protected final Lock ranksLock = new ReentrantLock();
    
    protected FileHandler makeFileHandler(File saveFolder)
    {
        return new TreeFileHandler("RankRegistry", new File(saveFolder, "Ranks.txt"))
        {
            final String prefixTag = "Prefix: ";
            final String suffixTag = "Suffix: ";
            final String permissionIncludersTag = "Permission Includers:";
            final String permissionsTag = "Permissions:";
            final String blankSpace = "";
            
            @Override
            protected void preSave()
            {}

            @Override
            protected List<TreeMember> getTreeStructureOfSaveData()
            {
                ranksLock.lock();
                
                try
                {
                    List<TreeMember> rankTrees = new ArrayList<TreeMember>();

                    for(Rank rank : ranks.values())
                    {
                        TreeMember rankTree = new TreeMember(rank.getName());

                        rankTree.addMember(new TreeMember(prefixTag + rank.getUsernamePrefix()));
                        rankTree.addMember(new TreeMember(suffixTag + rank.getUsernameSuffix()));
                        rankTree.addMember(new TreeMember(blankSpace));

                        TreeMember permissionIncludersTree = new TreeMember(permissionIncludersTag);

                        for(Rank permissionIncluder : rank.getDirectPermissionIncluders())
                            permissionIncludersTree.addMember(new TreeMember(permissionIncluder.getName()));

                        TreeMember permissionTree = new TreeMember(permissionsTag);

                        for(PermissionNode permission : rank.getPermissions())
                            permissionTree.addMember(new TreeMember(permission.toString()));

                        rankTree.addMember(permissionIncludersTree);
                        rankTree.addMember(new TreeMember(blankSpace));
                        rankTree.addMember(permissionTree);
                        rankTree.addMember(new TreeMember(blankSpace));

                        rankTrees.add(rankTree);
                    }

                    return rankTrees;
                }
                finally
                { ranksLock.unlock(); }
            }

            @Override
            protected void postSave()
            {}

            @Override
            protected void preInterpretation()
            {}

            @Override
            protected boolean interpretTree(List<TreeMember> list)
            {
                Map<String, Rank> ranksLoaded = new HashMap<String, Rank>();
                Multimap<String, Rank> permissionIncludersToBeAdded = HashMultimap.<String, Rank>create();
                // Multimap<Includer rank name to be added, ranks to be added to>
                
                for(TreeMember rankTree : list)
                {
                    String rankName = rankTree.getName();
                    
                    if(!rankName.trim().isEmpty())
                    {
                        Rank rank = new Rank(rankName);
                        
                        for(TreeMember rankValue : rankTree.getMembers())
                        {
                            if(rankValue.getName().equalsIgnoreCase(prefixTag))
                                rank.setUsernamePrefix(rankValue.getName().substring(prefixTag.length()));
                            else if(rankValue.getName().equalsIgnoreCase(suffixTag))
                            {
                                rank.setUsernameSuffix(rankValue.getName().substring(suffixTag.length()));
                            }
                            else if(rankValue.getName().equalsIgnoreCase(permissionIncludersTag))
                            {
                                for(TreeMember permissionIncluderValue : rankValue.getMembers())
                                {
                                    if(ranksLoaded.containsKey(permissionIncluderValue.getName()))
                                        rank.addPermissionIncluder(ranksLoaded.get(permissionIncluderValue.getName()));
                                    else
                                        permissionIncludersToBeAdded.put(permissionIncluderValue.getName(), rank);
                                }
                            }
                            else if(rankValue.getName().equalsIgnoreCase(permissionsTag))
                            {
                                for(TreeMember permissionValue : rankValue.getMembers())
                                    if(!permissionValue.getName().isEmpty())
                                        rank.givePermission(permissionValue.getName());
                            }
                        }
                        
                        if(permissionIncludersToBeAdded.containsKey(rankName))
                            for(Rank toAddToAsIncluder : permissionIncludersToBeAdded.get(rankName))
                                toAddToAsIncluder.addPermissionIncluder(rank);
                    }
                }
                
                ranksLock.lock();
                
                try
                {
                    ranks.clear();
                    ranks.putAll(ranksLoaded);
                }
                finally
                { ranksLock.unlock(); }
                
                return true;
            }

            @Override
            protected void postInterpretation()
            {}

            @Override
            protected void onNoFileToInterpret()
            {
                
            }
        };
    }
    
    public FileHandler getFileHandler()
    { return fileHandler; }
    
    /**
     * Gets the registered rank with the specified name.
     * @param rankName The name of the rank to get.
     * @return The Rank object with the passed name, ignoring case, or null if no matching rank is found.
     */
    public Rank getRank(String rankName)
    {
        ranksLock.lock();
        
        try
        {
            for(Rank rank : ranks.values())
                if(rank.getName().equalsIgnoreCase(rankName))
                    return rank;
        }
        finally
        { ranksLock.unlock(); }
        
        return null;
    }
    
    public Collection<Rank> getRanks()
    {
        ranksLock.lock();
        
        try
        { return new ArrayList<Rank>(ranks.values()); }
        finally
        { ranksLock.unlock(); }
    }
    
    public Collection<String> getRankNames()
    {
        Collection<String> rankNames = new ArrayList<String>();
        
        for(Rank rank : getRanks())
            rankNames.add(rank.getName());
        
        return rankNames;
    }
    
    /**
     * Registers a pre-made rank.
     * @param rank The rank to register.
     * @throws ItemWithNameAlreadyPresentException If there is already a rank with a matching name.
     */
    public void addRank(Rank rank) throws ItemWithNameAlreadyPresentException
    {
        ranksLock.lock();
        
        try
        {
            Rank rankAlreadyPresent = ranks.get(rank.getName());
            
            if(rankAlreadyPresent != null)
                throw new ItemWithNameAlreadyPresentException(rank, rankAlreadyPresent, rank.getName());
            
            ranks.put(rank.getName(), rank);
        }
        finally
        { ranksLock.unlock(); }
    }
    
    /**
     * Creates and registers a rank.
     * @param rankName The name of the rank.
     * @return The created Rank object.
     * @throws ItemWithNameAlreadyPresentException If there is already a rank with a matching name.
     */
    public Rank createRank(String rankName) throws ItemWithNameAlreadyPresentException
    {
        ranksLock.lock();
        
        try
        {
            Rank rank = ranks.get(rankName);
            
            if(rank != null)
                throw new ItemWithNameAlreadyPresentException(null, rank, rank.getName());
            
            rank = new Rank(rankName);
            return rank;
        }
        finally
        { ranksLock.unlock(); }
    }
    
    /**
     * Removes a rank by its name.
     * @param rankName The name of the rank to remove.
     * @return the removed rank, or null if there was none.
     */
    public Rank removeRank(String rankName)
    {
        ranksLock.lock();
        
        try
        { return ranks.remove(rankName); }
        finally
        { ranksLock.unlock(); }
    }
    
    /**
     * Removes a rank.
     * @param rank The rank to remove.
     * @return The removed rank. If this is different from the passed Rank and not null, it likely indicates a bug. Or null if there was none.
     */
    public Rank removeRank(Rank rank)
    { return removeRank(rank.getName()); }
}