package org.mcteam.ancientgates.listeners;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.GateUtil;

public class PluginBlockListener implements Listener {
	
    public Plugin plugin;
    
    public PluginBlockListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlock();
		
		if (block.getType() != Material.PORTAL && !isBlockAdjacentToPortal(block)) {
			return;
		}
		
		// Ok so a player breaks a portal block
		// Find the nearest gate!
		Location blockLocation = block.getLocation();
		Gate nearestGate = GateUtil.nearestGate(blockLocation, false);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlock();
		
		if (!isBlockAdjacentToPortal(block)) {
			return;
		}
		
		// Ok so a player breaks a portal block
		// Find the nearest gate!
		Location blockLocation = block.getLocation();
		Gate nearestGate = GateUtil.nearestGate(blockLocation, false);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlock();
		
		if (!isBlockInPortal(block) && !isBlockAdjacentToPortal(block)) {
			return;
		}
		
		// Ok so a player places near a portal block
		// Find the nearest gate!
		Location blockLocation = block.getLocation();
		Gate nearestGate = GateUtil.nearestGate(blockLocation, false);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlockClicked();
		Block blockUp = block.getRelative(BlockFace.UP);
		
		if (!isBlockInPortal(block) && !isBlockAdjacentToPortal(block) &&
				!isBlockInPortal(blockUp) && !isBlockAdjacentToPortal(blockUp)) {
			return;
		}
				
		// Ok so a player empties a bucket near a portal block
		// Find the nearest gate!
		Location blockLocation = block.getLocation();
		Location blockUpLocation = blockUp.getLocation();
		Gate nearestGate = GateUtil.nearestGate(blockLocation, false);
		Gate nearestGateUp = GateUtil.nearestGate(blockUpLocation, false);
				
		if (nearestGate != null || nearestGateUp != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
    	List<Block> destroyed = event.blockList();
    	Iterator<Block> it = destroyed.iterator();
        
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getType() == Material.PORTAL || isBlockAdjacentToPortal(block)) {
            
        		// Ok so an explosion happens near a portal block
        		// Find the nearest gate!
            	Location blockLocation = block.getLocation();
            	Gate nearestGate = GateUtil.nearestGate(blockLocation, false);	
            	
            	//If a gate block, remove from explosion
            	if (nearestGate != null) {
            		it.remove();
            	}
            }
        }
    }
    
    @EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (event.getBlock().getType() == Material.PORTAL) {
			if (isBlockInPortal(event.getBlock())) {
				event.setCancelled(true);
			}
		}
		
		if(event.getBlock().getType() == Material.SAND) {
			if (isBlockAdjacentToPortal(event.getBlock())) {
				event.setCancelled(true);
			}
		}
		
		return;
	}
	
	public boolean isBlockInPortal(Block block) {
		if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
			return false;
		}
		
		if (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
			return false;
		}
		
		if ( block.getRelative(BlockFace.NORTH).getType() != Material.AIR && block.getRelative(BlockFace.SOUTH).getType() != Material.AIR ) {
			return true;
		}
		
		if ( block.getRelative(BlockFace.WEST).getType() != Material.AIR && block.getRelative(BlockFace.EAST).getType() != Material.AIR ) {
			return true;
		}
		
		return false;
	}
	
	public boolean isBlockAdjacentToPortal(Block block) {
		if (block.getRelative(BlockFace.UP).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.DOWN).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.NORTH).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.SOUTH).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.WEST).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.EAST).getType() == Material.PORTAL) return true;
		
		if (block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST).getType() == Material.PORTAL) return true;
		if (block.getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST).getType() == Material.PORTAL) return true;
		
		return false;
	}
	
}
