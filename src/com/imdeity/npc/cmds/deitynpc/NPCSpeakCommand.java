package com.imdeity.npc.cmds.deitynpc;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.npc.DeityNPCManager;
import com.imdeity.npc.entities.HumanNPC;

public class NPCSpeakCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        args = DeityAPI.getAPI().getUtilAPI().getStringUtils().remFirstArg(args);
        
        if (args.length > 2) {
            npc.say(DeityAPI.getAPI().getUtilAPI().getStringUtils().join(args));
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        args = DeityAPI.getAPI().getUtilAPI().getStringUtils().remFirstArg(args);
        
        if (args.length > 2) {
            npc.say(DeityAPI.getAPI().getUtilAPI().getStringUtils().join(args));
            return true;
        }
        return false;
    }
}
