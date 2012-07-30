package com.imdeity.npc.enums;

import net.minecraft.server.Entity;

import com.imdeity.npc.entities.NPCHuman;

public enum NPCType {
    
    HUMAN(NPCHuman.class);
    
    private Class<? extends Entity> clazz;
    
    private NPCType(Class<? extends Entity> clazz) {
        this.clazz = clazz;
    }
    
    public Class<? extends Entity> getNPCClass() {
        return this.clazz;
    }
    
    public static NPCType getByName(String name) {
        for (NPCType type : values()) {
            if (type.name().equalsIgnoreCase(name)) return type;
        }
        
        return null;
    }
    
}
