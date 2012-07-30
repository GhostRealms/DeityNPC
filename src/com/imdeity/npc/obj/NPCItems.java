package com.imdeity.npc.obj;

import java.sql.SQLDataException;

import org.bukkit.inventory.ItemStack;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.records.DatabaseResults;
import com.imdeity.npc.DeityNPCMain;

public class NPCItems {
    
    private int id;
    private int itemId;
    private int itemDamage;
    private boolean hasUpdated = false;
    
    public NPCItems(int id) {
        String sql = "SELECT * FROM " + DeityNPCMain.getNPCItemsTableName() + " WHERE id = ?;";
        DatabaseResults query = DeityAPI.getAPI().getDataAPI().getMySQL().readEnhanced(sql, id);
        if (query != null && query.hasRows()) {
            int itemId = 0;
            int itemDamage = 0;
            try {
                itemId = query.getInteger(0, "item_id_value");
                itemDamage = query.getInteger(0, "item_damage_value");
            } catch (SQLDataException e) {
                e.printStackTrace();
            }
            setAll(id, itemId, itemDamage);
        }
    }
    
    public NPCItems(int id, int itemId, int itemDamage) {
        setAll(id, itemId, itemDamage);
    }
    
    public void setAll(int id, int itemId, int itemDamage) {
        this.id = id;
        this.itemId = itemId;
        this.itemDamage = itemDamage;
    }
    
    public int getId() {
        return id;
    }
    
    public ItemStack getItemStack() {
        return new ItemStack(itemId, 1, (short) itemDamage);
    }
    
    public void setItem(ItemStack item) {
        this.itemId = item.getTypeId();
        this.itemDamage = item.getDurability();
        hasUpdated = true;
    }
    
    public void save() {
        if (hasUpdated) {
            String sql = "UPDATE " + DeityNPCMain.getNPCItemsTableName() + " SET item_id_value = ?, item_damage_value = ? WHERE id = ?;";
            DeityAPI.getAPI().getDataAPI().getMySQL().write(sql, itemId, itemDamage, id);
            hasUpdated = false;
        }
    }
    
    public void remove() {
        DeityAPI.getAPI().getDataAPI().getMySQL().write("DELETE FROM " + DeityNPCMain.getNPCItemsTableName() + " WHERE id = ?", id);
    }
}
