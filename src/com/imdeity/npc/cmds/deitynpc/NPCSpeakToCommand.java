package com.imdeity.npc.cmds.deitynpc;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.npc.DeityNPCMain;
import com.imdeity.npc.DeityNPCManager;
import com.imdeity.npc.entities.HumanNPC;

public class NPCSpeakToCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        
        if (args.length > 3 && DeityNPCMain.plugin.getServer().getPlayer(args[1]) != null) {
            Player player = DeityNPCMain.plugin.getServer().getPlayer(args[1]);
            args = DeityAPI.getAPI().getUtilAPI().getStringUtils().remArgs(args, 2);
            npc.say(DeityAPI.getAPI().getUtilAPI().getStringUtils().join(args), player);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        
        if (args.length > 3 && DeityNPCMain.plugin.getServer().getPlayer(args[1]) != null) {
            Player pReceiver = DeityNPCMain.plugin.getServer().getPlayer(args[1]);
            args = DeityAPI.getAPI().getUtilAPI().getStringUtils().remArgs(args, 2);
            npc.say(DeityAPI.getAPI().getUtilAPI().getStringUtils().join(args), pReceiver);
            return true;
        }
        return false;
    }
}
