package org.mcteam.ancientgates.listeners;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
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
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.util.BlockUtil;
import org.mcteam.ancientgates.util.XMaterial;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class PluginBlockListener implements Listener {

	public Plugin plugin;

	public PluginBlockListener(final Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event) {
		final Block block = event.getBlock();

		// Ok so a player breaks a portal/frame block
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromAll(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(final BlockBurnEvent event) {
		final Block block = event.getBlock();

		// Ok so a frame block is burning
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event) {
		final Block block = event.getBlock();

		// Ok so a frame block ignites
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockSpread(final BlockSpreadEvent event) {
		final Block block = event.getBlock();

		// Ok so a frame block fire spreads
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {
		final Block block = event.getBlock();

		// Ok so a player places near a portal block
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromPortalAndSurround(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
		for (final Block block : event.getBlocks()) {
			// Ok so a block is pushed into a frame block
			// Find the nearest gate!
			final WorldCoord blockCoord = new WorldCoord(block);
			final Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);

			if (nearestGate != null) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
		final Block block = event.getRetractLocation().getBlock();

		// Ok so a block is pulled from a frame block
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromFrameAndSurround(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
		final Block block = event.getBlockClicked();
		final Block blockUp = block.getRelative(BlockFace.UP);

		// Ok so a player empties a bucket near on portal/frame block
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final WorldCoord blockCoordUp = new WorldCoord(blockUp);
		final Gate nearestGate = Gates.gateFromAll(blockCoord);
		final Gate nearestGateUp = Gates.gateFromAll(blockCoordUp);

		if (nearestGate != null || nearestGateUp != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
		final Block block = event.getBlockClicked();
		final Block blockUp = block.getRelative(BlockFace.UP);

		// Ok so a player fills a bucket on a portal block
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final WorldCoord blockCoordUp = new WorldCoord(blockUp);
		final Gate nearestGate = Gates.gateFromPortal(blockCoord);
		final Gate nearestGateUp = Gates.gateFromPortal(blockCoordUp);

		if (nearestGate != null || nearestGateUp != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(final EntityExplodeEvent event) {
		final List<Block> destroyed = event.blockList();
		final Iterator<Block> it = destroyed.iterator();

		while (it.hasNext()) {
			final Block block = it.next();

			// Ok so an explosion happens near a portal block
			// Find the nearest gate!
			final WorldCoord blockCoord = new WorldCoord(block);
			final Gate nearestGate = Gates.gateFromAll(blockCoord);

			// If a gate block, remove from explosion
			if (nearestGate != null) {
				it.remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPhysics(final BlockPhysicsEvent event) {
		final Block block = event.getBlock();
		// Avoid calling getType twice since it's an heavy call on 1.13+ while being in legacy mode
		final Material type = block.getType();
		final WorldCoord coord = new WorldCoord(block);

		// Stop portal blocks from breaking
		if (BlockUtil.isStandableGateMaterial(type) && Gates.gateFromPortal(coord) != null) {
			event.setCancelled(true);
		}

		// Stop sand falling when part of the frame
		if (type == Material.SAND && Gates.gateFromFrame(coord) != null) {
			event.setCancelled(true);
		}

		return;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockFromTo(final BlockFromToEvent event) {
		final Block block = event.getBlock();

		// Fixes leaking water on 1.13 and doesn't seem to cause issues <1.13
		if (!block.getType().equals(Material.WATER) && !block.getType().equals(Material.LAVA))
			return;

		// Ok so water/lava starts flowing within a portal frame
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromPortal(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockForm(final BlockFormEvent event) {
		final Block block = event.getBlock();

		// Ok so blocks starts forming within a portal frame
		// Find the nearest gate!
		final WorldCoord blockCoord = new WorldCoord(block);
		final Gate nearestGate = Gates.gateFromPortal(blockCoord);

		if (nearestGate != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockGrow(final BlockGrowEvent event) {
		final Block block = event.getNewState().getBlock();
		final WorldCoord coord = new WorldCoord(block);

		// Stop sugarcane blocks from growing
		if (BlockUtil.isStandableGateMaterial(event.getNewState().getType()) && Gates.gateFromPortalSurround(coord) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		if (block == null)
			return;

		final WorldCoord coord = new WorldCoord(block);

		// Stop piston moving pieces (false air) disappearing
		if (BlockUtil.isStandableGateMaterial(event.getClickedBlock().getType()) && Gates.gateFromPortal(coord) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onItemSpawn(final ItemSpawnEvent event) {
		final Entity item = event.getEntity();
		if (item.getType() != EntityType.DROPPED_ITEM || ((Item) item).getItemStack().getType() != Material.SUGAR_CANE)
			return;

		// Stop sugarcane block from decaying (workaround for lack of 1.7.2-R0.1 physics support)
		final WorldCoord coord = new WorldCoord((Item) item);
		final Gate nearestGate = Gates.gateFromPortal(coord);

		if (nearestGate != null) {
			if (nearestGate.getMaterial() != XMaterial.SUGAR_CANE.parseMaterial())
				return;

			event.getEntity().remove();
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.instance, new Runnable() {
				@Override
				public void run() {
					coord.getBlock().setType(XMaterial.SUGAR_CANE.parseMaterial());
				}
			}, 1);
		}
	}

}
