package org.mcteam.ancientgates.listeners;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.types.WorldCoord;
import org.mcteam.ancientgates.util.BlockUtil;

public class PluginBlockListener implements Listener {
	
    public Plugin plugin;
    
    public PluginBlockListener(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		
		// Ok so a player breaks a portal/frame block
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromAll(blockCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		
		// Ok so a frame block is burning
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		
		// Ok so a frame block ignites
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		
		// Ok so a frame block fire spreads
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		
		// Ok so a player places near a portal block
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromPortalAndSurround(blockCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
    }
    
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled()) return;

		for (Block block : event.getBlocks()) {
    		// Ok so a block is pushed into a frame block
    		// Find the nearest gate!
			WorldCoord blockCoord = new WorldCoord(block);
			Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);
			
			if (nearestGate != null) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled()) return;

		Block block = event.getRetractLocation().getBlock();
    		
		// Ok so a block is pulled from a frame block
    	// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);
			
		if (nearestGate != null) {
			event.setCancelled(true);
			return;
		}
	}
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlockClicked();
		Block blockUp = block.getRelative(BlockFace.UP);
				
		// Ok so a player empties a bucket near on portal/frame block
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		WorldCoord blockCoordUp = new WorldCoord(blockUp);
		Gate nearestGate = Gates.gateFromAll(blockCoord);
		Gate nearestGateUp = Gates.gateFromAll(blockCoordUp);
				
		if (nearestGate != null || nearestGateUp != null) {
			event.setCancelled(true);
		}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlockClicked();
		Block blockUp = block.getRelative(BlockFace.UP);

		// Ok so a player fills a bucket on a portal block
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		WorldCoord blockCoordUp = new WorldCoord(blockUp);
		Gate nearestGate = Gates.gateFromPortal(blockCoord);
		Gate nearestGateUp = Gates.gateFromPortal(blockCoordUp);
				
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
            
        	// Ok so an explosion happens near a portal block
        	// Find the nearest gate!
    		WorldCoord blockCoord = new WorldCoord(block);
    		Gate nearestGate = Gates.gateFromAll(blockCoord);
            	
            //If a gate block, remove from explosion
            if (nearestGate != null) {
            	it.remove();
            }
        }
    }
    
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		WorldCoord coord = new WorldCoord(block);
		
		// Stop portal blocks from breaking
		if (BlockUtil.isStandableGateMaterial(block.getType()) && Gates.gateFromPortal(coord) != null) {
			event.setCancelled(true);
		}
		
		// Stop sand falling when part of the frame
		if(block.getType() == Material.SAND && Gates.gateFromFrame(coord) != null) {
			event.setCancelled(true);
		}
		
		return;
	}
    
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {
		if (event.isCancelled()) return;

		Block block = event.getBlock();

		if (block.getTypeId() != 9 && block.getTypeId() != 11) return;

		// Ok so water/lava starts flowing within a portal frame
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromPortal(blockCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockForm(BlockFormEvent event) {
		if (event.isCancelled()) return;

		Block block = event.getBlock();

		// Ok so blocks starts forming within a portal frame
		// Find the nearest gate!
		WorldCoord blockCoord = new WorldCoord(block);
		Gate nearestGate = Gates.gateFromPortal(blockCoord);
		
		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockGrow(BlockGrowEvent event) {
		if (event.isCancelled()) return;
		
		Block block = event.getNewState().getBlock();
		WorldCoord coord = new WorldCoord(block);
		
		// Stop sugarcane blocks from growing
		if (BlockUtil.isStandableGateMaterial(event.getNewState().getType()) && Gates.gateFromPortalSurround(coord) != null) {
			event.setCancelled(true);
		}
	}
	
}
