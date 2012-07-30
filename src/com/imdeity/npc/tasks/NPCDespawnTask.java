package com.imdeity.npc.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.imdeity.npc.DeityNPCManager;
import com.imdeity.npc.entities.HumanNPC;

public class NPCDespawnTask implements Runnable {
    
    public List<Location> playerLocations;
    
    public NPCDespawnTask(List<Location> playerLocations) {
        this.playerLocations = playerLocations;
    }
    
    public void run() {
        List<Chunk> chunksToUnload = new ArrayList<Chunk>();
        
        npcLoop: for (int i = 0; i < DeityNPCManager.getLoadedNPCs().size(); i++) {
            HumanNPC npc = DeityNPCManager.getLoadedNPCs().get(i);
            org.bukkit.Chunk chunk = npc.getLocation().getChunk();
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();
            boolean shouldUnload = true;
            if (!chunk.isLoaded()) {
                DeityNPCManager.despawnNPC(npc);
                chunksToUnload.add(chunk);
                continue npcLoop;
            }
            playerLoop: for (Location playerLocation : playerLocations) {
                if (!chunk.isLoaded()) {
                    break playerLoop;
                }
                if (!npc.getLocation().getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName())) {
                    continue playerLoop;
                }
                if ((chunkX + 4 >= playerLocation.getChunk().getX() && chunkZ + 4 >= playerLocation.getChunk().getZ()) && (chunkX - 4 <= playerLocation.getChunk().getX() && chunkZ - 4 <= playerLocation.getChunk().getZ())) {
                    continue npcLoop;
                }
            }
            if (shouldUnload) {
                DeityNPCManager.despawnNPC(npc);
                chunksToUnload.add(chunk);
                break npcLoop;
            }
        }
        
        for (Chunk c : chunksToUnload) {
            c.getWorld().unloadChunkRequest(c.getX(), c.getZ());
        }
    }
}
