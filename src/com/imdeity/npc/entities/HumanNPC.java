package com.imdeity.npc.entities;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet22Collect;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.npc.DeityNPCMain;
import com.imdeity.npc.obj.NPCItems;
import com.imdeity.npc.pathing.NPCPath;
import com.imdeity.npc.pathing.NPCPathFinder;
import com.imdeity.npc.pathing.Node;
import com.imdeity.npc.pathing.PathReturn;

public class HumanNPC extends CraftHumanEntity implements NPC {
    
    private NPCPathFinder path;
    private Iterator<Node> pathIterator;
    private Node last;
    private NPCPath runningPath;
    private int taskid;
    protected int databaseId;
    protected String name;
    protected Location location;
    protected ChatColor nameColor = ChatColor.GREEN;
    public int chunkX, chunkZ;
    
    public NPCItems heldItem;
    public NPCItems armorHelm;
    public NPCItems armorChest;
    public NPCItems armorLegging;
    public NPCItems armorBoot;
    protected boolean hasUpdated = false;
    
    public HumanNPC(NPCHuman entity, int databaseId, String name, ChatColor nameColor, Location location, NPCItems heldItem, NPCItems armorHelm, NPCItems armorChest, NPCItems armorLegging, NPCItems armorBoot) {
        super((CraftServer) Bukkit.getServer(), entity);
        this.databaseId = databaseId;
        this.name = name;
        this.nameColor = nameColor;
        this.location = location;
        chunkX = location.getChunk().getX();
        chunkZ = location.getChunk().getZ();
        this.heldItem = heldItem;
        this.armorHelm = armorHelm;
        this.armorChest = armorChest;
        this.armorLegging = armorLegging;
        this.armorBoot = armorBoot;
    }
    
    public int getEntityId() {
        return getHandle().getBukkitEntity().getEntityId();
    }
    
    public int getDatabaseId() {
        return databaseId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void removeFromWorld() {
        try {
            getHandle().getBukkitEntity().remove();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void pathFindTo(Location l, PathReturn callback) {
        pathFindTo(l, 3000, callback);
    }
    
    public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
        if (path != null) {
            path.cancel = true;
        }
        path = new NPCPathFinder(this.getLocation(), l, maxIterations, callback);
        path.start();
    }
    
    public void walkTo(Location l) {
        walkTo(l, 5000);
    }
    
    public void walkTo(final Location l, final int maxIterations) {
        pathFindTo(l, maxIterations, new PathReturn() {
            public void run(NPCPath path) {
                usePath(path, new Runnable() {
                    
                    public void run() {
                        walkTo(l, maxIterations);
                    }
                });
            }
        });
    }
    
    public void usePath(NPCPath path) {
        usePath(path, new Runnable() {
            public void run() {
                walkTo(runningPath.getEnd(), 5000);
            }
        });
    }
    
    public void usePath(NPCPath path, Runnable onFail) {
        if (taskid == 0) {
            taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(DeityNPCMain.plugin, new Runnable() {
                
                public void run() {
                    pathStep();
                }
                
            }, 8L, 8L);
        }
        pathIterator = path.getPath().iterator();
        runningPath = path;
    }
    
    private void pathStep() {
        if (pathIterator.hasNext()) {
            Node n = pathIterator.next();
            Block b = null;
            if (last == null || runningPath.checkPath(n, last, true)) {
                b = n.b;
                if (last != null) {
                    this.lookAtPoint(last.b.getLocation(), b.getLocation());
                }
                getHandle().setPosition(b.getX(), b.getY(), b.getZ());
            }
            last = n;
        } else {
            getHandle().setPositionRotation(runningPath.getEnd().getX(), runningPath.getEnd().getY(), runningPath.getEnd().getZ(), runningPath.getEnd().getYaw(), runningPath.getEnd().getPitch());
            Bukkit.getServer().getScheduler().cancelTask(taskid);
            taskid = 0;
        }
        location = new Location(getHandle().world.getWorld(), getHandle().locX, getHandle().locY, getHandle().locZ);
    }
    
    public void say(String message) {
        Bukkit.getServer().broadcastMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
    }
    
    public void say(String message, Player player) {
        player.sendMessage(this.nameColor + this.getName() + ChatColor.WHITE + ": " + message);
    }
    
    public void say(String message, List<Player> players) {
        for (Player player : players) {
            say(message, player);
        }
    }
    
    public void say(String message, int distance) {
        Player players[] = Bukkit.getServer().getOnlinePlayers();
        for (Player player : players) {
            if (player.getLocation().distanceSquared(this.getLocation()) <= distance) {
                say(message, player);
            }
        }
    }
    
    public void pickupItem(Item item) {
        ((CraftServer) DeityNPCMain.plugin.getServer()).getServer().getTracker(((CraftWorld) this.getWorld()).getHandle().dimension).a(entity, new Packet22Collect(item.getEntityId(), this.getEntityId()));
        item.remove();
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
        chunkX = location.getChunk().getX();
        chunkZ = location.getChunk().getZ();
        this.hasUpdated = true;
    }
    
    public void lookAtPoint(Location origLocation, Location newLocation) {
        if (this.getWorld() != newLocation.getWorld()) { return; }
        double xDiff = newLocation.getX() - origLocation.getX();
        double yDiff = newLocation.getY() - origLocation.getY();
        double zDiff = newLocation.getZ() - origLocation.getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double newYaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
        double newPitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
        
        if (zDiff < 0.0) {
            newYaw = newYaw + (Math.abs(180 - newYaw) * 2);
        }
        
        getHandle().yaw = (float) (newYaw - 90);
        getHandle().pitch = (float) newPitch;
        getHandle().X = (float) (newYaw - 90);
        getHandle().setPositionRotation(getHandle().locX, getHandle().locY, getHandle().locZ, (float) (newYaw - 90), (float) newPitch);
    }
    
    public void setNameColor(ChatColor color) {
        this.nameColor = color;
        this.hasUpdated = true;
    }
    
    public ChatColor getNameColor() {
        return this.nameColor;
    }
    
    public void setName(String name) {
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }
        this.name = name;
        this.hasUpdated = true;
    }
    
    public boolean spawn() {
        if (location.getChunk().isLoaded()) {
            NPCHuman entity = (NPCHuman) getHandle();
            getHandle().setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            if (heldItem != null) {
                entity.getBukkitEntity().setItemInHand(heldItem.getItemStack());
            }
            if (armorHelm != null) {
                entity.getBukkitEntity().getInventory().setHelmet(armorHelm.getItemStack());
            }
            if (armorChest != null) {
                entity.getBukkitEntity().getInventory().setChestplate(armorChest.getItemStack());
            }
            if (armorLegging != null) {
                entity.getBukkitEntity().getInventory().setLeggings(armorLegging.getItemStack());
            }
            if (armorBoot != null) {
                entity.getBukkitEntity().getInventory().setBoots(armorBoot.getItemStack());
            }
            ((CraftWorld) location.getWorld()).getHandle().addEntity(entity);
            return true;
        }
        return false;
    }
    
    public void despawn() {
        this.removeFromWorld();
    }
    
    public static NPCHuman createEntity(String name, ChatColor nameColor, Location location) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        if (nameColor == null) {
            return new NPCHuman(world, name);
        } else {
            return new NPCHuman(world, nameColor + name);
        }
    }
    
    public void animateArmSwing() {
        ((WorldServer) getHandle().world).tracker.a(getHandle(), new Packet18ArmAnimation(entity, 1));
    }
    
    public void actAsHurt() {
        ((WorldServer) getHandle().world).tracker.a(getHandle(), new Packet18ArmAnimation(getHandle(), 2));
    }
    
    public void hide() {
        for (Player player : getHandle().world.getWorld().getPlayers()) {
            ((CraftPlayer) player).hidePlayer(((NPCHuman) getHandle()).getBukkitEntity());
        }
    }
    
    public void unhide() {
        for (Player player : getHandle().world.getWorld().getPlayers()) {
            ((CraftPlayer) player).showPlayer(((NPCHuman) getHandle()).getBukkitEntity());
        }
    }
    
    public void putInBed(Location bed) {
        getHandle().setPosition(bed.getX(), bed.getY(), bed.getZ());
        getHandle().a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
    }
    
    public void getOutOfBed() {
        ((NPCHuman) getHandle()).a(true, true, true);
    }
    
    public void setSneaking(boolean sneak) {
        getHandle().setSneak(sneak);
    }
    
    public void save() {
        if (hasUpdated) {
            hasUpdated = false;
            String sql = "UPDATE " + DeityNPCMain.getNPCTableName() + " SET name = ?, name_color = ?, x_coord = ?, y_coord = ?, z_coord = ?, yaw = ?, pitch = ?, held_item_row_id = ?, armor_helm_row_id = ?, armor_chest_row_id = ?, armor_legging_row_id = ?, armor_boot_row_id = ? WHERE id = ?;";
            DeityAPI.getAPI().getDataAPI().getMySQL()
                    .write(sql, name, (nameColor.getChar() + "").toUpperCase(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch(), heldItem.getId(), armorHelm.getId(), armorChest.getId(), armorLegging.getId(), armorBoot.getId(), databaseId);
        }
    }
    
    public void remove() {
        DeityAPI.getAPI().getDataAPI().getMySQL().write("DELETE FROM " + DeityNPCMain.getNPCTableName() + " WHERE id = ?;", databaseId);
    }
}