package com.enkigaming.mcforge.enkipermissions.registry;

import com.enkigaming.mcforge.enkilib.filehandling.FileHandler;
import com.enkigaming.mcforge.enkilib.filehandling.TreeFileHandler;
import com.enkigaming.mcforge.enkipermissions.EnkiPerms;
import com.enkigaming.mcforge.enkipermissions.permissions.PermissionNode;
import com.enkigaming.mcforge.enkipermissions.ranks.Rank;
import com.enkigaming.mcforge.enkipermissions.registry.exceptions.ItemWithNameAlreadyPresentException;
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
    { fileHandler = makeFileHandler(saveFolder); }

    protected final Map<String, Rank> ranks = new HashMap<String, Rank>();
    protected final FileHandler fileHandler;
    protected Rank defaultRank; // synchronise with ranks.
    
    protected final Lock ranksLock = new ReentrantLock();
    
    protected FileHandler makeFileHandler(File saveFolder)
    {
        return new TreeFileHandler("RankRegistry", new File(saveFolder, "Ranks.txt"))
        {
            final String prefixTag = "Prefix: ";
            final String suffixTag = "Suffix: ";
            final String permissionIncludersTag = "Permission Includers:";
            final String permissionsTag = "Permissions:";
            final String defaultRankTag = "Default Rank";
            final String blankSpace = "";
            
            @Override
            protected void preSave()
            {}

            @Override
            protected List<TreeNode> getTreeStructureOfSaveData()
            {
                ranksLock.lock();
                
                try
                {
                    List<TreeNode> rankTrees = new ArrayList<TreeNode>();

                    for(Rank rank : ranks.values())
                    {
                        TreeNode rankTree = new TreeNode(rank.getName());

                        rankTree.addChild(new TreeNode(prefixTag + rank.getUsernamePrefix()));
                        rankTree.addChild(new TreeNode(suffixTag + rank.getUsernameSuffix()));
                        rankTree.addChild(new TreeNode(blankSpace));

                        TreeNode permissionIncludersTree = new TreeNode(permissionIncludersTag);

                        for(Rank permissionIncluder : rank.getDirectPermissionIncluders())
                            permissionIncludersTree.addChild(new TreeNode(permissionIncluder.getName()));

                        TreeNode permissionTree = new TreeNode(permissionsTag);

                        for(PermissionNode permission : rank.getPermissions())
                            permissionTree.addChild(new TreeNode(permission.toString()));
                        
                        if(rank.equals(defaultRank))
                            rankTree.addChild(new TreeNode(defaultRankTag));

                        rankTree.addChild(permissionIncludersTree);
                        rankTree.addChild(new TreeNode(blankSpace));
                        rankTree.addChild(permissionTree);
                        rankTree.addChild(new TreeNode(blankSpace));

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
            protected boolean interpretTree(List<TreeNode> list)
            {
                Map<String, Rank> ranksLoaded = new HashMap<String, Rank>();
                Multimap<String, Rank> permissionIncludersToBeAdded = HashMultimap.<String, Rank>create();
                // Multimap<Includer rank name to be added, ranks to be added to>
                
                Rank loadedDefaultRank = null;
                boolean multipleDefaultsDetected = false;
                
                for(TreeNode rankTree : list)
                {
                    String rankName = rankTree.getName();
                    Rank rank = new Rank(rankName);
                    
                    for(TreeNode rankPropertyTree : rankTree.getChildren())
                    {
                        if(rankPropertyTree.getName().startsWith(prefixTag))
                            rank.setUsernamePrefix(rankPropertyTree.getName().substring(prefixTag.length()));
                        else if(rankPropertyTree.getName().startsWith(suffixTag))
                            rank.setUsernameSuffix(rankPropertyTree.getName().substring(suffixTag.length()));
                        else if(rankPropertyTree.getName().startsWith(permissionIncludersTag))
                        {
                            for(TreeNode permissionIncluder : rankPropertyTree.getChildren())
                            {
                                if(ranksLoaded.containsKey(permissionIncluder.getName()))
                                    rank.addPermissionIncluder(ranksLoaded.get(permissionIncluder.getName()));
                                else
                                    permissionIncludersToBeAdded.put(permissionIncluder.getName(), rank);
                            }
                        }
                        else if(rankPropertyTree.getName().startsWith(permissionsTag))
                        {
                            for(TreeNode permission : rankPropertyTree.getChildren())
                                if(!permission.getName().isEmpty())
                                    rank.givePermission(permission.getName());
                        }
                        else if(rankPropertyTree.getName().startsWith(defaultRankTag))
                        {
                            if(loadedDefaultRank == null)
                                loadedDefaultRank = rank;
                            else
                                multipleDefaultsDetected = true;
                        }
                        
                        if(permissionIncludersToBeAdded.containsKey(rankName))
                            for(Rank rankToAddToAsIncluder : permissionIncludersToBeAdded.get(rankName))
                                rankToAddToAsIncluder.addPermissionIncluder(rank);
                    }
                    ranksLoaded.put(rankName, rank);
                }
                
                ranksLock.lock();
                
                try
                {
                    ranks.clear();
                    ranks.putAll(ranksLoaded);
                    defaultRank = loadedDefaultRank;
                    
                    if(multipleDefaultsDetected)
                        System.out.println("Multiple default ranks detected. Default rank chosen: " + defaultRank.getName());
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
                ranksLock.lock();
                
                try
                {
                    ranks.clear();

                    Rank adminRank = new Rank("Admin");
                    adminRank.givePermission("*");

                    Rank memberRank = new Rank("Member");
                    memberRank.givePermission("enkiperms.rank.getplayerrank");
                    memberRank.givePermission("enkiperms.rank.prefix.get");
                    memberRank.givePermission("enkiperms.rank.suffix.get");
                    memberRank.givePermission("enkiperms.permission.check.*");

                    ranks.put("Admin", adminRank);
                    ranks.put("Member", memberRank);
                    
                    defaultRank = memberRank;
                    
                    System.out.println("No ranks file found, loaded default ranks.");
                }
                finally
                { ranksLock.unlock(); }
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
    
    /**
     * Gets the default rank.
     * @return The default rank, or null if none is set.
     */
    public Rank getDefaultRank()
    {
        ranksLock.lock();
        
        try
        { return defaultRank; }
        finally
        { ranksLock.unlock(); }
    }
    
    /**
     * Gets the name of the default rank.
     * @return The name of the default rank, or null if none is set.
     */
    public String getDefaultRankName()
    {
        ranksLock.lock();
        
        try
        { return defaultRank.getName(); }
        finally
        { ranksLock.unlock(); }
    }

    /**
     * Sets the default rank.
     * @param rank The rank to make the default rank.
     * @return The previous default rank, or null if there was none.
     */
    public Rank setDefaultRank(Rank rank)
    {
        ranksLock.lock();
        
        try
        {
            Rank old = defaultRank;
            defaultRank = rank;
            return old;
        }
        finally
        { ranksLock.unlock(); }
    }
    
    /**
     * Sets the default rank.
     * @param rankName The name of the registered rank to make the default.
     * @return The previous default rank.
     * @throws IllegalArgumentException If a rank by the given name is not found.
     */
    public Rank setDefaultRank(String rankName) throws IllegalArgumentException
    {
        ranksLock.lock();
        
        try
        {
            Rank newRank = null;
            
            FindNewRank:
            for(Rank rank : ranks.values())
                if(rank.getName().equalsIgnoreCase(rankName))
                {
                    newRank = rank;
                    break FindNewRank;
                }
            
            if(newRank == null)
                throw new IllegalArgumentException("Rank does not exist");
            
            Rank oldRank = defaultRank;
            defaultRank = newRank;
            return oldRank;
        }
        finally
        { ranksLock.unlock(); }
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
        {
            Collection<String> toRemove = new ArrayList<String>();
            
            for(String key : ranks.keySet())
                if(key.equalsIgnoreCase(rankName))
                    toRemove.add(key);
            
            Rank removed = null;
            
            for(String remove : toRemove)
                removed = ranks.remove(remove);
            
            if(removed != null)
            {
                for(Rank rank : ranks.values())
                    rank.removePermissionIncluder(removed);
                
                if(defaultRank == removed)
                    defaultRank = null;
            }
            
            EnkiPerms.getInstance().getPlayerRanks().loseRank(rankName);
            
            return removed;
        }
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
    
    public boolean containsRank(String rankName)
    {
        ranksLock.lock();
        
        try
        {
            for(Rank rank : ranks.values())
                if(rank.getName().equalsIgnoreCase(rankName))
                    return true;
            
            return false;
        }
        finally
        { ranksLock.unlock(); }
    }
}