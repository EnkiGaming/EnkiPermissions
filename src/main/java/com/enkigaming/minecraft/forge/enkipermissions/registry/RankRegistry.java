package com.enkigaming.minecraft.forge.enkipermissions.registry;

import com.enkigaming.minecraft.forge.enkipermissions.EnkiPerms;
import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;
import com.enkigaming.minecraft.forge.enkipermissions.registry.exceptions.ItemWithNameAlreadyPresentException;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public class RankRegistry
{
    public RankRegistry(File saveFolder)
    { this.saveFolder = saveFolder; }
    
    protected final File saveFolder;
    protected final Map<String, Rank> ranks = new HashMap<String, Rank>();
    
    /**
     * Gets the registered rank with the specified name.
     * @param rankName The name of the rank to get.
     * @return The Rank object with the passed name, ignoring case, or null if no matching rank is found.
     */
    public Rank getRank(String rankName)
    {
        synchronized(ranks)
        {
            for(Rank rank : ranks.values())
                if(rank.getName().equalsIgnoreCase(rankName))
                    return rank;
        }
        
        return null;
    }
    
    /**
     * Registers a pre-made rank.
     * @param rank The rank to register.
     * @throws ItemWithNameAlreadyPresentException If there is already a rank with a matching name.
     */
    public void addRank(Rank rank) throws ItemWithNameAlreadyPresentException
    {
        synchronized(ranks)
        {
            Rank rankAlreadyPresent = ranks.get(rank.getName());
            
            if(rankAlreadyPresent != null)
                throw new ItemWithNameAlreadyPresentException(rank, rankAlreadyPresent, rank.getName());
            
            ranks.put(rank.getName(), rank);
        }
    }
    
    /**
     * Creates and registers a rank.
     * @param rankName The name of the rank.
     * @return The created Rank object.
     * @throws ItemWithNameAlreadyPresentException If there is already a rank with a matching name.
     */
    public Rank createRank(String rankName) throws ItemWithNameAlreadyPresentException
    {
        synchronized(ranks)
        {
            Rank rank = ranks.get(rankName);
            
            if(rank != null)
                throw new ItemWithNameAlreadyPresentException(null, rank, rank.getName());
            
            rank = new Rank(rankName);
            return rank;
        }
    }
    
    /**
     * Removes a rank by its name.
     * @param rankName The name of the rank to remove.
     * @return the removed rank, or null if there was none.
     */
    public Rank removeRank(String rankName)
    {
        synchronized(ranks)
        { return ranks.remove(rankName); }
    }
    
    /**
     * Removes a rank.
     * @param rank The rank to remove.
     * @return The removed rank. If this is different from the passed Rank and not null, it likely indicates a bug. Or null if there was none.
     */
    public Rank removeRank(Rank rank)
    { return removeRank(rank.getName()); }
    
    public void save()
    {}
    
    public void load()
    {}
}