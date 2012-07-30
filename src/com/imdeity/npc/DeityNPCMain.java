package com.imdeity.npc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.MinecraftServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityPlugin;
import com.imdeity.npc.cmds.DeityNPCCommandHandler;
import com.imdeity.npc.tasks.NPCDespawnTask;

public class DeityNPCMain extends DeityPlugin {
    private static final int FLOOD_PROTECTION_TIME = 1500;
    public static DeityNPCMain plugin;
    private static HashMap<String, Long> npcFloodProtection = new HashMap<String, Long>();
    
    @Override
    public void onDisable() {
        this.chat.out(DeityNPCManager.despawnAllNPCs() + " npcs despawned");
        DeityNPCManager.resetNPCs();
        super.onDisable();
    }
    
    @Override
    protected void initCmds() {
        this.registerCommand(new DeityNPCCommandHandler("DeityNPC"));
    }
    
    @Override
    protected void initConfig() {
        // None
    }
    
    @Override
    protected void initDatabase() {
        DeityAPI.getAPI().getDataAPI().getMySQL()
                .write("CREATE TABLE IF NOT EXISTS " + getNPCItemsTableName() + " (" + "`id` INT( 16 ) NOT NULL AUTO_INCREMENT PRIMARY KEY ," + "`item_id_value` INT( 16 ) NOT NULL DEFAULT '0' ," + "`item_damage_value` INT( 16 ) NOT NULL DEFAULT '0'" + ") ENGINE = MYISAM ;");
        
        DeityAPI.getAPI()
                .getDataAPI()
                .getMySQL()
                .write("CREATE TABLE IF NOT EXISTS " + getNPCTableName() + " (" + "`id` INT( 16 ) NOT NULL AUTO_INCREMENT PRIMARY KEY ," + "`name` VARCHAR( 32 ) NOT NULL ," + "`name_color` VARCHAR( 1 ) NULL DEFAULT NULL ," + "`held_item_row_id` INT( 16 ) NULL ,"
                        + "`armor_helm_row_id` INT( 16 ) NULL ," + "`armor_chest_row_id` INT( 16 ) NULL ," + "`armor_legging_row_id` INT( 16 )  NULL ," + "`armor_boot_row_id` INT( 16 ) NULL ," + "`world` VARCHAR( 64 ) NOT NULL ," + "`x_coord` DOUBLE NOT NULL ," + "`y_coord` DOUBLE NOT NULL ,"
                        + "`z_coord` DOUBLE NOT NULL ," + "`yaw` DOUBLE NOT NULL ," + "`pitch` DOUBLE NOT NULL" + ") ENGINE = MYISAM ;");
        
    };
    
    @Override
    protected void initInternalDatamembers() {
        DeityNPCMain.plugin.chat.out(DeityNPCManager.loadNPCs() + " npcs loaded");
        DeityNPCMain.plugin.chat.out(DeityNPCManager.spawnNPCs() + " npcs spawned");
    }
    
    @Override
    protected void initLanguage() {
        
    }
    
    @Override
    protected void initListeners() {
        this.registerListener(new DeityNPCListener());
    }
    
    @Override
    protected void initPlugin() {
        plugin = this;
        new DeityNPCManager();
    }
    
    @Override
    protected void initTasks() {
        this.registerTask(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(DeityNPCMain.plugin, new NPCDespawnTask(getLocations()), 90 * 20, 90 * 20));
    }
    
    private List<Location> getLocations() {
        List<Location> locations = new ArrayList<Location>();
        for (Player p : getServer().getOnlinePlayers()) {
            locations.add(p.getLocation());
        }
        return locations;
    }
    
    public static String getNPCItemsTableName() {
        return DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_", "npc_items");
    }
    
    public static String getNPCTableName() {
        return DeityAPI.getAPI().getDataAPI().getMySQL().tableName("deity_", "npcs");
    }
    
    public static MinecraftServer getMinecraftServer() {
        return ((CraftServer) plugin.getServer()).getServer();
    }
    
    public static Location floorLocation(Location location) {
        DecimalFormat doubleFormat = new DecimalFormat("#.#");
        double x = Double.valueOf(doubleFormat.format(location.getX()));
        double y = Double.valueOf(doubleFormat.format(location.getY()));
        double z = Double.valueOf(doubleFormat.format(location.getZ()));
        float yaw = Float.valueOf(doubleFormat.format(location.getYaw()));
        float pitch = Float.valueOf(doubleFormat.format(location.getPitch()));
        
        return new Location(location.getWorld(), x, y, z, yaw, pitch);
    }
    
    public static boolean canUseNPC(Player player) {
        if (npcFloodProtection.containsKey(player.getName())) {
            long timeInMilliseconds = System.currentTimeMillis() - npcFloodProtection.get(player.getName());
            if (timeInMilliseconds < FLOOD_PROTECTION_TIME) { return false; }
        }
        npcFloodProtection.put(player.getName(), System.currentTimeMillis());
        return true;
    }
    
}
