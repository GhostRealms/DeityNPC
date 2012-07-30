package com.imdeity.npc.cmds;

import com.imdeity.deityapi.api.DeityCommandHandler;
import com.imdeity.npc.cmds.deitynpc.NPCCreateCommand;
import com.imdeity.npc.cmds.deitynpc.NPCDeleteCommand;
import com.imdeity.npc.cmds.deitynpc.NPCPathCommand;
import com.imdeity.npc.cmds.deitynpc.NPCSpeakCommand;
import com.imdeity.npc.cmds.deitynpc.NPCSpeakToCommand;

public class DeityNPCCommandHandler extends DeityCommandHandler {
    
    public DeityNPCCommandHandler(String pluginName) {
        super(pluginName, "DeityNPC");
    }
    
    @Override
    protected void initRegisteredCommands() {
        this.registerCommand("create", "[npc-name] <npc-color>", "Creates an NPC", new NPCCreateCommand(), "deity.npc.create");
        this.registerCommand("path", "[npc-id]", "Moves an NPC to a location", new NPCPathCommand(), "deity.npc.path");
        this.registerCommand("delete", "[npc-id]", "Deletes an NPC", new NPCDeleteCommand(), "deity.npc.delete");
        this.registerCommand("speak", "[npc-id] [message]", "Talks for an NPC", new NPCSpeakCommand(), "deity.npc.speak");
        this.registerCommand("speak-to", "[npc-id] [player_name][message]", "Talks for an NPC", new NPCSpeakToCommand(), "deity.npc.speaktp");
        
    }
    
}
