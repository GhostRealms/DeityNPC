package com.imdeity.npc.entities;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.entity.EntityTargetEvent;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.npc.DeityNPCMain;
import com.imdeity.npc.networking.NPCEntityTargetEvent;
import com.imdeity.npc.networking.NPCNetHandler;

public class NPCHuman extends EntityPlayer {
    
    private int lastTargetId;
    private long lastBounceTick;
    private int lastBounceId;
    
    public NPCHuman(World world, String name) {
        super(DeityNPCMain.getMinecraftServer(), world, formatName(name), new ItemInWorldManager(world));
        
        itemInWorldManager.b(1);
        
        this.netServerHandler = new NPCNetHandler(this);
        this.lastTargetId = -1;
        this.lastBounceId = -1;
        this.lastBounceTick = 0;
    }
    
    public static String formatName(String name) {
        return DeityAPI.getAPI().getUtilAPI().getChatUtils().formatMessage(name, true);
    }
    
    public void setBukkitEntity(org.bukkit.craftbukkit.entity.CraftPlayer entity) {
        this.bukkitEntity = entity;
    }
    
    @Override
    public boolean b(EntityHuman entity) {
        EntityTargetEvent event = new NPCEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NPCEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
        CraftServer server = ((WorldServer) this.world).getServer();
        server.getPluginManager().callEvent(event);
        
        return super.b(entity);
    }
    
    @Override
    public void a_(EntityHuman entity) {
        if (lastTargetId == -1 || lastTargetId != entity.id) {
            EntityTargetEvent event = new NPCEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NPCEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);
        }
        lastTargetId = entity.id;
        
        super.a_(entity);
    }
    
    @Override
    public void c(Entity entity) {
        if (lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000) {
            EntityTargetEvent event = new NPCEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NPCEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
            CraftServer server = ((WorldServer) this.world).getServer();
            server.getPluginManager().callEvent(event);
            
            lastBounceTick = System.currentTimeMillis();
        }
        
        lastBounceId = entity.id;
        
        super.c(entity);
    }
    
    @Override
    public void move(double x, double y, double z) {
        setPosition(x, y, z);
    }
}