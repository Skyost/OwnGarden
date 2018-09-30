package fr.skyost.owngarden.listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;

import org.bukkit.ChatColor;
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
import java.util.Random;

import fr.skyost.owngarden.OwnGarden;

/**
 * Global events handled by the plugin.
 */

public class GlobalEvents implements Listener {

	/**
	 * The plugin instance.
	 */

	private final OwnGarden plugin;

	/**
	 * Creates a new global events listener instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public GlobalEvents(final OwnGarden plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onStructureGrow(final StructureGrowEvent event) {
		if(event.isCancelled()) {
			return;
		}

		boolean grown = false;
		final Location location = event.getLocation();
		switch(location.getBlock().getType()) {
		case OAK_SAPLING: // Oak
			grown = growBlock(plugin.getPluginConfig().saplingOakSchematics, location);
			break;
		case SPRUCE_SAPLING: // Spruce
			grown = growBlock(plugin.getPluginConfig().saplingSpruceSchematics, location);
			break;
		case BIRCH_SAPLING: // Birch
			grown = growBlock(plugin.getPluginConfig().saplingBirchSchematics, location);
			break;
		case JUNGLE_SAPLING: // Jungle
			grown = growBlock(plugin.getPluginConfig().saplingJungleSchematics, location);
			break;
		case ACACIA_SAPLING: // Acacia
			grown = growBlock(plugin.getPluginConfig().saplingAcaciaSchematics, location);
			break;
		case DARK_OAK_SAPLING: // Dark Oak
			grown = growBlock(plugin.getPluginConfig().saplingDarkOakSchematics, location);
			if(grown) {
				final Block current = location.getBlock();
				for(final BlockFace blockFace : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
					final Block relative = current.getRelative(blockFace);
					if(relative.getType() == Material.DARK_OAK_SAPLING) {
						relative.setType(Material.AIR);
					}
				}
			}
			break;
		case RED_MUSHROOM: // Red mushroom
			grown = growBlock(plugin.getPluginConfig().mushroomRedSchematics, location);
			break;
		case BROWN_MUSHROOM: // Brown mushroom
			grown = growBlock(plugin.getPluginConfig().mushroomBrownSchematics, location);
			break;
		}

		if(grown) {
			event.getBlocks().clear();
			event.setCancelled(true);
		}
	}

	/**
	 * Grows a block.
	 *
	 * @param schematics The schematics list.
	 * @param location The location.
	 *
	 * @return Whether the operation has been a success.
	 */

	private boolean growBlock(final List<String> schematics, final Location location) {
		if(schematics.isEmpty()) {
			return false;
		}

		final String file = schematics.get(new Random().nextInt(schematics.size()));
		try {
			location.getBlock().setType(Material.AIR);
			final ClipboardHolder holder = plugin.getWorldEditOperations().loadSchematic(file);

			if(plugin.getPluginConfig().schematicsRandomRotation) {
				final int degrees = new Random().nextInt(4) * 90;
				if(degrees != 0) {
					final AffineTransform transform = new AffineTransform();
					transform.rotateY(degrees);
					holder.setTransform(transform);
				}
			}

			final Vector dimensions = holder.getClipboard().getDimensions();
			holder.getClipboard().setOrigin(new Vector(dimensions.getX() / 2, 0, dimensions.getZ() / 2));
			Operations.completeLegacy(holder
					.createPaste(WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), WorldEdit.getInstance().getConfiguration().maxChangeLimit))
					.to(new Vector(location.getX(), location.getY(), location.getZ()))
					.ignoreAirBlocks(true)
					.build()
			);
			return true;
		}
		catch(final Exception ex) {
			OwnGarden.log(ChatColor.RED, "Unable to load the schematic : \"" + file + "\".");
			ex.printStackTrace();
		}

		return false;
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