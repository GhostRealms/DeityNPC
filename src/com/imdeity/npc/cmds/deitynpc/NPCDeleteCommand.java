package com.imdeity.npc.cmds.deitynpc;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.npc.DeityNPCMain;
import com.imdeity.npc.DeityNPCManager;
import com.imdeity.npc.entities.HumanNPC;

public class NPCDeleteCommand extends DeityCommandReceiver {
    
    @Override
    public boolean onConsoleRunCommand(String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        DeityNPCManager.despawnNPC(npc);
        npc.remove();
        DeityNPCMain.plugin.chat.out("Removed " + npc.getName());
        return false;
    }
    
    @Override
    public boolean onPlayerRunCommand(Player player, String[] args) {
        HumanNPC npc = DeityNPCManager.getNPC(Integer.parseInt(args[0]));
        DeityNPCManager.despawnNPC(npc);
        npc.remove();
        DeityNPCMain.plugin.chat.sendPlayerMessage(player, "Removed " + npc.getName());
        return false;
    }
}
