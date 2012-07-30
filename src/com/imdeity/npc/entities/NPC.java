package com.imdeity.npc.entities;

import java.util.List;

import net.minecraft.server.EntityLiving;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.imdeity.npc.pathing.NPCPath;
import com.imdeity.npc.pathing.PathReturn;

public interface NPC extends org.bukkit.entity.LivingEntity {
    
    public String getName();
    
    public void removeFromWorld();
    
    public void pathFindTo(Location l, PathReturn callback);
    
    public void pathFindTo(Location l, int maxIterations, PathReturn callback);
    
    public void walkTo(Location l);
    
    public void walkTo(final Location l, final int maxIterations);
    
    public void usePath(NPCPath path);
    
    public void usePath(NPCPath path, Runnable onFail);
    
    public void say(String message);
    
    public void say(String message, Player player);
    
    public void say(String message, List<Player> players);
    
    public void say(String message, int distance);
    
    public ChatColor getNameColor();
    
    public void setNameColor(ChatColor color);
    
    public EntityLiving getHandle();
    
    public void setName(String name);
}