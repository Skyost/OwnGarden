package fr.skyost.owngarden.listener;

import fr.skyost.owngarden.OwnGarden;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.Material.DARK_OAK_SAPLING;

/**
 * Global events handled by the plugin.
 */

public class GlobalEventsListener implements Listener {

	private static final List<BlockFace> FACES = Arrays.asList(
			BlockFace.NORTH,
			BlockFace.NORTH_EAST,
			BlockFace.EAST,
			BlockFace.SOUTH_EAST,
			BlockFace.SOUTH,
			BlockFace.SOUTH_WEST,
			BlockFace.WEST,
			BlockFace.NORTH_WEST
	);

	/**
	 * The plugin instance.
	 */

	private final OwnGarden plugin;

	/**
	 * Creates a new global events listener instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public GlobalEventsListener(final OwnGarden plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onStructureGrow(final StructureGrowEvent event) {
		if(event.isCancelled()) {
			return;
		}

		final Location location = event.getLocation();
		final List<String> schematics = plugin.getPluginConfig().getSchematics(location.getBlock().getType());
		if(plugin.getWorldEditOperations().growTree(schematics, location)) {
			if(schematics == plugin.getPluginConfig().saplingDarkOakSchematics) {
				final Block current = location.getBlock();
				for(final BlockFace blockFace : FACES) {
					final Block relative = current.getRelative(blockFace);
					if(relative.getType() == DARK_OAK_SAPLING) {
						relative.setType(Material.AIR);
					}
				}
			}

			//event.getBlocks().clear();
			event.setCancelled(true);
		}
	}

	/**
	 * Returns the plugin instance.
	 *
	 * @return The plugin instance.
	 */

	public OwnGarden getPlugin() {
		return plugin;
	}
	
}