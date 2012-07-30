package com.imdeity.npc;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NetworkManager;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.records.DatabaseResults;
import com.imdeity.npc.entities.HumanNPC;
import com.imdeity.npc.enums.NPCLoadType;
import com.imdeity.npc.networking.NPCNetworkManager;
import com.imdeity.npc.obj.NPCItems;

public class DeityNPCManager {
    
    private static NPCNetworkManager npcNetworkManager;
    private static Map<NPCLoadType, List<HumanNPC>> npcs = new HashMap<NPCLoadType, List<HumanNPC>>();
    
    public DeityNPCManager() {
        init();
    }
    
    public void init() {
        npcNetworkManager = new NPCNetworkManager();
        npcs.put(NPCLoadType.SPAWNED, new ArrayList<HumanNPC>());
        npcs.put(NPCLoadType.DESPAWNED, new ArrayList<HumanNPC>());
    }
    
    public static NetworkManager getNPCNetworkManager() {
        return npcNetworkManager;
    }
    
    public static boolean spawnNPC(HumanNPC npc) {
        int removeId = -1;
        for (int i = 0; i < npcs.get(NPCLoadType.DESPAWNED).size(); i++) {
            HumanNPC n = npcs.get(NPCLoadType.DESPAWNED).get(i);
            if (n.getDatabaseId() == npc.getDatabaseId()) {
                removeId = i;
            }
        }
        if (removeId != -1) {
            npcs.get(NPCLoadType.DESPAWNED).remove(removeId);
        }
        npcs.get(NPCLoadType.SPAWNED).add(npc);
        return npc.spawn();
    }
    
    public static void despawnNPC(HumanNPC npc) {
        int removeId = -1;
        for (int i = 0; i < npcs.get(NPCLoadType.SPAWNED).size(); i++) {
            HumanNPC n = npcs.get(NPCLoadType.SPAWNED).get(i);
            if (n.getDatabaseId() == npc.getDatabaseId()) {
                removeId = i;
            }
        }
        if (removeId != -1) {
            npcs.get(NPCLoadType.SPAWNED).remove(removeId);
        }
        npcs.get(NPCLoadType.DESPAWNED).add(npc);
        npc.despawn();
    }
    
    public static int spawnNPCs() {
        int numSpawned = 0;
        for (HumanNPC n : npcs.get(NPCLoadType.SPAWNED)) {
            if (n.spawn()) {
                numSpawned++;
            }
        }
        return numSpawned;
    }
    
    public static int spawnAllNPCS() {
        int numSpawned = 0;
        for (HumanNPC n : npcs.get(NPCLoadType.DESPAWNED)) {
            if (spawnNPC(n)) {
                numSpawned++;
            }
        }
        return numSpawned;
    }
    
    public static int despawnAllNPCs() {
        int numDespawned = 0;
        int size = npcs.get(NPCLoadType.SPAWNED).size() - 1;
        while (size > 0) {
            HumanNPC n = npcs.get(NPCLoadType.SPAWNED).get(size);
            despawnNPC(n);
            size--;
            numDespawned++;
        }
        return numDespawned;
    }
    
    public static int loadNPCs() {
        String sql = "SELECT * FROM " + DeityNPCMain.getNPCTableName() + ";";
        DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced(sql);
        int numLoaded = 0;
        if (query != null && query.hasRows()) {
            for (int i = 0; i < query.rowCount(); i++) {
                try {
                    int databaseId = query.getInteger(i, "id");
                    String name = query.getString(i, "name");
                    ChatColor nameColor = null;
                    if (query.getString(i, "name_color") != null) {
                        ChatColor.getByChar(query.getString(i, "name_color").charAt(0));
                    }
                    NPCItems heldItem = null;
                    NPCItems armorHelm = null;
                    NPCItems armorChest = null;
                    NPCItems armorLegging = null;
                    NPCItems armorBoot = null;
                    if (query.getInteger(i, "held_item_row_id") != null) {
                        heldItem = new NPCItems((int) query.getInteger(i, "held_item_row_id"));
                    }
                    
                    if (query.getInteger(i, "armor_helm_row_id") != null) {
                        armorHelm = new NPCItems((int) query.getInteger(i, "armor_helm_row_id"));
                    }
                    if (query.getInteger(i, "armor_chest_row_id") != null) {
                        armorChest = new NPCItems((int) query.getInteger(i, "armor_chest_row_id"));
                    }
                    if (query.getInteger(i, "armor_legging_row_id") != null) {
                        armorLegging = new NPCItems((int) query.getInteger(i, "armor_legging_row_id"));
                    }
                    if (query.getInteger(i, "armor_boot_row_id") != null) {
                        armorBoot = new NPCItems((int) query.getInteger(i, "armor_boot_row_id"));
                    }
                    Location location = new Location(DeityNPCMain.plugin.getServer().getWorld(query.getString(i, "world")), query.getDouble(i, "x_coord"), query.getDouble(i, "y_coord"), query.getDouble(i, "z_coord"), new Float(query.getDouble(i, "yaw")), new Float(query.getDouble(i, "pitch")));
                    
                    HumanNPC npc = new HumanNPC(HumanNPC.createEntity(name, nameColor, location), databaseId, name, nameColor, location, heldItem, armorHelm, armorChest, armorLegging, armorBoot);
                    if (npc.spawn()) {
                        npcs.get(NPCLoadType.SPAWNED).add(npc);
                        numLoaded++;
                    } else {
                        npcs.get(NPCLoadType.DESPAWNED).add(npc);
                    }
                } catch (SQLDataException e) {
                    e.printStackTrace();
                }
            }
        }
        return numLoaded;
    }
    
    public static boolean hasNPC(org.bukkit.entity.Entity entity) {
        for (HumanNPC npc : getLoadedNPCs()) {
            if (npc.getEntityId() == entity.getEntityId()) { return true; }
        }
        return false;
    }
    
    public static HumanNPC getNPC(int databaseId) {
        for (NPCLoadType type : npcs.keySet()) {
            for (int i = 0; i < npcs.get(type).size(); i++) {
                HumanNPC n = npcs.get(type).get(i);
                if (n.getDatabaseId() == databaseId) { return n; }
            }
        }
        return null;
    }
    
    public static HumanNPC getNPCFromDB(int id) {
        String sql = "SELECT * FROM " + DeityNPCMain.getNPCTableName() + " WHERE id = ?;";
        DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced(sql, id);
        if (query != null && query.hasRows()) {
            try {
                int databaseId = id;
                String name = query.getString(0, "name");
                ChatColor nameColor = null;
                if (query.getString(0, "name_color") != null) {
                    ChatColor.getByChar(query.getString(0, "name_color").charAt(0));
                }
                NPCItems heldItem = null;
                NPCItems armorHelm = null;
                NPCItems armorChest = null;
                NPCItems armorLegging = null;
                NPCItems armorBoot = null;
                if (query.getInteger(0, "held_item_row_id") != null) {
                    heldItem = new NPCItems((int) query.getInteger(0, "held_item_row_id"));
                }
                
                if (query.getInteger(0, "armor_helm_row_id") != null) {
                    armorHelm = new NPCItems((int) query.getInteger(0, "armor_helm_row_id"));
                }
                if (query.getInteger(0, "armor_chest_row_id") != null) {
                    armorChest = new NPCItems((int) query.getInteger(0, "armor_chest_row_id"));
                }
                if (query.getInteger(0, "armor_legging_row_id") != null) {
                    armorLegging = new NPCItems((int) query.getInteger(0, "armor_legging_row_id"));
                }
                if (query.getInteger(0, "armor_boot_row_id") != null) {
                    armorBoot = new NPCItems((int) query.getInteger(0, "armor_boot_row_id"));
                }
                Location location = new Location(DeityNPCMain.plugin.getServer().getWorld(query.getString(0, "world")), query.getDouble(0, "x_coord"), query.getDouble(0, "y_coord"), query.getDouble(0, "z_coord"), new Float(query.getDouble(0, "yaw")), new Float(query.getDouble(0, "pitch")));
                HumanNPC npc = new HumanNPC(HumanNPC.createEntity(name, nameColor, location), databaseId, name, nameColor, location, heldItem, armorHelm, armorChest, armorLegging, armorBoot);
                if (npc.spawn()) {
                    npcs.get(NPCLoadType.SPAWNED).add(npc);
                } else {
                    npcs.get(NPCLoadType.DESPAWNED).add(npc);
                }
                return npc;
            } catch (SQLDataException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static HumanNPC getNPC(org.bukkit.entity.Entity entity) {
        for (HumanNPC npc : getLoadedNPCs()) {
            if (npc.getEntityId() == entity.getEntityId()) { return (HumanNPC) npc; }
        }
        return null;
    }
    
    public static List<HumanNPC> getLoadedNPCs() {
        return npcs.get(NPCLoadType.SPAWNED);
    }
    
    public static void resetNPCs() {
        despawnAllNPCs();
        npcs.clear();
    }
    
    public static int unloadChunk(Chunk chunk) {
        int numDespawned = 0;
        int size = npcs.get(NPCLoadType.SPAWNED).size() - 1;
        while (size >= 0) {
            HumanNPC n = npcs.get(NPCLoadType.SPAWNED).get(size);
            if (n.getLocation().getChunk().getX() == chunk.getX() && n.getLocation().getChunk().getZ() == chunk.getZ()) {
                despawnNPC(n);
                numDespawned++;
            }
            size--;
        }
        return numDespawned;
    }
    
    public static int loadChunk(Chunk chunk) {
        int numSpawned = 0;
        int size = npcs.get(NPCLoadType.DESPAWNED).size() - 1;
        while (size >= 0) {
            HumanNPC n = npcs.get(NPCLoadType.DESPAWNED).get(size);
            if (n.chunkX == chunk.getX() && n.chunkZ == chunk.getZ()) {
                spawnNPC(n);
                numSpawned++;
            }
            size--;
        }
        return numSpawned;
    }
    
    public static HumanNPC createNPC(String name, ChatColor color, Location location) {
        location = DeityNPCMain.floorLocation(location);
        String sql = "INSERT INTO " + DeityNPCMain.getNPCTableName() + " (" + "`name`, `name_color`, `world`, `x_coord`, `y_coord`, `z_coord`, `yaw`, `pitch`)VALUES (?,?,?,?,?,?,?,?);";
        DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, name, (color.getChar() + "").toUpperCase(), location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        
        sql = "SELECT id FROM " + DeityNPCMain.getNPCTableName() + " WHERE name = ? AND world = ? AND x_coord = ? AND y_coord = ? AND z_coord = ?;";
        DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced(sql, name, location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
        if (query != null && query.hasRows()) {
            int id = -1;
            try {
                id = query.getInteger(0, "id");
            } catch (SQLDataException e) {
                e.printStackTrace();
            }
            if (id != -1) { return getNPCFromDB(id); }
        }
        return null;
    }
}
