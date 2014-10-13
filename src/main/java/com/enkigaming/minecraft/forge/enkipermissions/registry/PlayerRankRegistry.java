package com.enkigaming.minecraft.forge.enkipermissions.registry;

import com.enkigaming.minecraft.forge.enkilib.EnkiLib;
import com.enkigaming.minecraft.forge.enkilib.filehandling.CSVFileHandler;
import com.enkigaming.minecraft.forge.enkilib.filehandling.CSVFileHandler.CSVRowMember;
import com.enkigaming.minecraft.forge.enkipermissions.ranks.Rank;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerRankRegistry
{
    public PlayerRankRegistry(File saveFolder)
    { this.saveFolder = saveFolder; }
    
    protected final File saveFolder;
    protected final Map<UUID, String> playerRanks = new HashMap<UUID, String>(); // Map<PlayerId, RankName>
    protected String defaultRank; // synchronize with ranks.
    
    protected Lock playerRanksLock = new ReentrantLock();
    
    protected CSVFileHandler makeFileHandler(File saveFolder)
    {
        return new CSVFileHandler("PlayerRankRegistry", saveFolder, "Not all rows could be read from playerranks.csv - as many as could be were.")
        {
            List<Map.Entry<UUID, String>> ranksList;
            
            @Override
            protected void onNoFileToInterpret()
            {}

            @Override
            protected List<String> getColumnNames()
            { return Arrays.asList("Player ID", "Last recorded player name", "Rank"); }

            @Override
            protected void preInterpretation()
            { playerRanksLock.lock(); }

            @Override
            protected boolean interpretRow(List<String> list)
            {
                if(list.size() != 3)
                    return false;
                
                UUID playerId;
                
                try
                { playerId = UUID.fromString(list.get(0)); }
                catch(IllegalArgumentException e)
                {
                    System.out.println("Could not interpret row for " + list.get(1));
                    return false;
                }
                
                String rank = list.get(2);
                
                playerRanks.put(playerId, rank);
                return true;
            }

            @Override
            protected void postInterpretation()
            { playerRanksLock.unlock(); }

            @Override
            protected void preSave()
            {
                playerRanksLock.lock();
                
                ranksList = new ArrayList<Map.Entry<UUID, String>>(playerRanks.entrySet());
            }

            @Override
            protected List<CSVRowMember> getRow(int i)
            {
                String lastRecordedUsername = EnkiLib.getInstance().getUsernameCache().getLastRecordedNameOf(ranksList.get(i).getKey());
                
                if(lastRecordedUsername == null)
                    lastRecordedUsername = "";
                
                return Arrays.asList(new CSVRowMember[]{new CSVRowMember(ranksList.get(i).getKey().toString(), false),
                                                        new CSVRowMember(lastRecordedUsername,                 !lastRecordedUsername.isEmpty()),
                                                        new CSVRowMember(ranksList.get(i).getValue(),          true)});
            }

            @Override
            protected void postSave()
            { playerRanksLock.unlock(); }
        };
    }
    
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
}