package com.imdeity.npc.cmds.deitynpc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.npc.DeityNPCMain;
import com.imdeity.npc.DeityNPCManager;
import com.imdeity.npc.entities.HumanNPC;

public class NPCPathCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        if (args.length == 5) {
            npc.walkTo(new Location(DeityNPCMain.plugin.getServer().getWorld(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4])));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        if (args.length == 1) {
            npc.walkTo(player.getTargetBlock(null, 10).getLocation().add(0, 1, 0));
        } else if (args.length == 5) {
            npc.walkTo(new Location(DeityNPCMain.plugin.getServer().getWorld(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4])));
        } else {
            return false;
        }
        return true;
    }
}
