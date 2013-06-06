package org.mcteam.ancientgates.commands;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.util.BlockUtil;
import org.mcteam.ancientgates.util.TextUtil;

public class CommandSetMaterial extends BaseCommand {
	
	public CommandSetMaterial() {
		aliases.add("setmaterial");
		
		requiredParameters.add("id");
		requiredParameters.add("material");
		
		requiredPermission = "ancientgates.setmaterial";
		
		senderMustBePlayer = false;
		
		helpDescription = "Set portal \"material\" of the gate.";
	}
	
	public void perform() {	
		String material = parameters.get(1).toUpperCase();
		
		if (BlockUtil.asSpawnableGateMaterial(material) == null) {
			sendMessage("This is not a valid gate material. Valid materials:");
			sendMessage(TextUtil.implode(BlockUtil.spawnableGateMaterials, Conf.colorSystem+", "));
			return;
		}
        
		Gates.close(gate);
		gate.setMaterial(material);
		sendMessage("Portal material for gate \""+gate.getId()+"\" is now "+material+".");
		
		Gate.save();
	}
        
}