package com.enkigaming.minecraft.forge.enkipermissions.registry;

import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerRankRegistry
{
    public PlayerRankRegistry(File saveFolder)
    {}
    
    protected final File saveFolder;
    protected final Map<UUID, String> playerRanks; // Map<PlayerId, RankName>
    protected String defaultRank;
    
    public Map<UUID, String> getPlayerRanks()
    {}
    
    public String getPlayerRank(UUID playerId)
    {}
    
    public String getPlayerRank(EntityPlayer player)
    {}
    
    public String setPlayerRank(UUID playerId)
    {}
    
    public String setPlayerrank(EntityPlayer player)
    {}
    
    public void save()
    {}
    
    public void load()
    {}
}