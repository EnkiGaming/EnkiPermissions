package com.enkigaming.minecraft.forge.enkipermissions.registry;

import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerRankRegistry
{
    public PlayerRankRegistry(File saveFolder)
    { this.saveFolder = saveFolder; }
    
    protected final File saveFolder;
    protected final Map<UUID, String> playerRanks = new HashMap<UUID, String>(); // Map<PlayerId, RankName>
    protected String defaultRank; // synchronize with ranks.
    
    public Map<UUID, String> getPlayerRanks()
    {
        synchronized(playerRanks)
        { return new HashMap<UUID, String>(playerRanks); }
    }
    
    public String getPlayerRank(UUID playerId)
    {
        String rank;
        
        synchronized(playerRanks)
        {
            rank = playerRanks.get(playerId);
            
            if(rank == null)
                return defaultRank;
            
            return rank;
        }
    }
    
    public String getPlayerRank(EntityPlayer player)
    { return getPlayerRank(player.getGameProfile().getId()); }
    
    public Collection<UUID> getPlayersWithRank(String rankName)
    {
        Collection<UUID> players = new ArrayList<UUID>();
        
        synchronized(playerRanks)
        {
            for(Entry<UUID, String> i : playerRanks.entrySet())
                if(rankName.equals(i.getValue()))
                    players.add(i.getKey());
        }
        
        return players;
    }
    
    public String getDefaultRank()
    {
        synchronized(playerRanks)
        { return defaultRank; }
    }
    
    public String setPlayerRank(UUID playerId, String rankName)
    {
        synchronized(playerRanks)
        { return playerRanks.put(playerId, rankName); }
    }
    
    public String setPlayerrank(EntityPlayer player, String rankName)
    { return setPlayerRank(player.getGameProfile().getId(), rankName); }
    
    public String setDefaultRank(String rank)
    {
        synchronized(playerRanks)
        {
            String oldRank = defaultRank;
            defaultRank = rank;
            return oldRank;
        }
    }
    
    public String setDefaultRank(Rank rank)
    { return setDefaultRank(rank.getName()); }
    
    public void save()
    {}
    
    public void load()
    {}
}