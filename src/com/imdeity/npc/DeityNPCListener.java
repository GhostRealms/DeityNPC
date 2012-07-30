package com.imdeity.npc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.imdeity.deityapi.api.DeityListener;
import com.imdeity.npc.entities.HumanNPC;

public class DeityNPCListener extends DeityListener {
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        int numUnloaded = DeityNPCManager.unloadChunk(event.getChunk());
        if (numUnloaded > 1) {
            DeityNPCMain.plugin.chat.out("Unloaded " + numUnloaded + " NPCs");
        } else if (numUnloaded > 0) {
            DeityNPCMain.plugin.chat.out("Unloaded " + numUnloaded + " NPC");
        }
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        int numLoaded = DeityNPCManager.loadChunk(event.getChunk());
        if (numLoaded > 1) {
            DeityNPCMain.plugin.chat.out("Loaded " + numLoaded + " NPCs");
        } else if (numLoaded > 0) {
            DeityNPCMain.plugin.chat.out("Loaded " + numLoaded + " NPC");
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer() != null) {
            Player player = event.getPlayer();
            if (DeityNPCManager.hasNPC(event.getRightClicked())) {
                HumanNPC npc = DeityNPCManager.getNPC(event.getRightClicked());
                if (npc != null) {
                    npc.lookAtPoint(npc.getHandle().getBukkitEntity().getEyeLocation(), player.getEyeLocation());
                    npc.animateArmSwing();
                    if (DeityNPCMain.canUseNPC(player)) {
                        DeityNPCMain.plugin.chat.out("DeityNPC:RIGHTCLICK:" + npc.getDatabaseId() + ":" + player.getName());
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() != null && event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (DeityNPCManager.hasNPC(event.getEntity())) {
                HumanNPC npc = DeityNPCManager.getNPC(event.getEntity());
                if (npc != null) {
                    npc.lookAtPoint(npc.getHandle().getBukkitEntity().getEyeLocation(), player.getEyeLocation());
                    npc.animateArmSwing();
                    if (DeityNPCMain.canUseNPC(player)) {
                        DeityNPCMain.plugin.chat.out("DeityNPC:LEFTCLICK:" + npc.getDatabaseId() + ":" + player.getName());
                    }
                }
            }
        }
    }
}
