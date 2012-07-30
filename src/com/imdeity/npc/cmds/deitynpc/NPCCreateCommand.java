package com.imdeity.npc.cmds.deitynpc;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.npc.DeityNPCMain;
import com.imdeity.npc.DeityNPCManager;
import com.imdeity.npc.entities.HumanNPC;

public class NPCCreateCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        if (args.length < 1) { return false; }
        String name = args[0];
        ChatColor color = ChatColor.getByChar(args[1].charAt(0));
        Location location = new Location(DeityNPCMain.plugin.getServer().getWorld(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[4]));
        HumanNPC npc = DeityNPCManager.createNPC(name, color, location);
        DeityNPCMain.plugin.chat.out("Created the npc named " + npc.getName());
        return true;
    }
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("4")) {
                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 10; y++) {
                        Random rand = new Random();
                        String name = DeityNPCMain.plugin.getServer().getOnlinePlayers()[rand.nextInt(DeityNPCMain.plugin.getServer().getOnlinePlayers().length)].getName();
                        DeityNPCManager.createNPC(name, ChatColor.WHITE, player.getLocation().add(x, y, 0));
                    }
                }
                return true;
            }
            for (HumanNPC npc : DeityNPCManager.getLoadedNPCs()) {
                switch (Integer.parseInt(args[0])) {
                    case 0:
                        npc.setSneaking(true);
                        break;
                    case 1:
                        npc.hide();
                        break;
                    case 2:
                        npc.unhide();
                        break;
                    case 3:
                        npc.putInBed(new Location(npc.getWorld(), -6, 9, -3));
                        break;
                    default:
                        npc.getOutOfBed();
                        npc.setSneaking(false);
                        npc.setLocation(npc.getLocation());
                }
            }
            return true;
        }
        String name = args[0];
        ChatColor color = ChatColor.GREEN;
        if (args.length > 1) {
            color = ChatColor.getByChar(args[1].charAt(0));
        }
        HumanNPC npc = DeityNPCManager.createNPC(name, color, player.getLocation());
        DeityNPCMain.plugin.chat.sendPlayerMessage(player, "Created your npc named " + npc.getName());
        return true;
    }
}
