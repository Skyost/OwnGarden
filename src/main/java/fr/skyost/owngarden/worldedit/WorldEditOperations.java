package fr.skyost.owngarden.worldedit;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import fr.skyost.owngarden.OwnGarden;
import fr.skyost.owngarden.config.PluginConfig;
import org.bukkit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents available WorldEdit operations.
 */

public class WorldEditOperations {

	/**
	 * Accepted WorldEdit versions.
	 */

	public static final String[] WORLDEDIT_VERSIONS = new String[]{"7.0"};

	/**
	 * The plugin instance.
	 */

	private final OwnGarden plugin;

	/**
	 * Creates a new WorldEdit operations instance.
	 *
	 * @param plugin The plugin instance.
	 */

	public WorldEditOperations(final OwnGarden plugin) {
		this.plugin = plugin;
	}

	/**
	 * Returns whether the current WorldEdit version should be accepted.
	 *
	 * @return Whether the current WorldEdit version should be accepted.
	 */

	public boolean checkWorldEditVersion() {
		final String version = Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
		for(final String prefix : WORLDEDIT_VERSIONS) {
			if(version.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests if each schematic is valid.
	 *
	 * @return An array containing the invalid schematics.
	 */

	public String[] testSchematics() {
		final PluginConfig config = plugin.getPluginConfig();
		final List<String> schematics = new ArrayList<>();
		schematics.addAll(config.saplingOakSchematics);
		schematics.addAll(config.saplingSpruceSchematics);
		schematics.addAll(config.saplingBirchSchematics);
		schematics.addAll(config.saplingJungleSchematics);
		schematics.addAll(config.saplingAcaciaSchematics);
		schematics.addAll(config.saplingDarkOakSchematics);

		final List<String> invalidSchematics = new ArrayList<>();
		for(final String schematic : schematics) {
			try {
				loadSchematic(schematic);
			}
			catch(final Exception ex) {
				invalidSchematics.add(schematic);
			}
		}
		return invalidSchematics.toArray(new String[0]);
	}

	/**
	 * Loads a schematic.
	 *
	 * @param schematic The schematic file (must be in the schematics directory).
	 *
	 * @return The WorldEdit clipboard holder.
	 *
	 * @throws IOException If any I/O exception occurs.
	 */

	public ClipboardHolder loadSchematic(final String schematic) throws IOException {
		final File file = new File(plugin.getPluginConfig().schematicsDirectory, schematic);
		if(!file.exists()) {
			throw new FileNotFoundException("Schematic not found : " + schematic + ".");
		}

		final ClipboardFormat format = ClipboardFormats.findByFile(file);
		if(format == null) {
			throw new IllegalArgumentException("Unknown schematic format.");
		}

		final Closer closer = Closer.create();
		final FileInputStream fileInputStream = closer.register(new FileInputStream(file));
		final BufferedInputStream bufferedInputStream = closer.register(new BufferedInputStream(fileInputStream));
		final ClipboardReader reader = closer.register(format.getReader(bufferedInputStream));

		return new ClipboardHolder(reader.read());
	}

	/**
	 * Grows a tree at the specified location.
	 *
	 * @param schematics The schematics list.
	 * @param location The location.
	 *
	 * @return Whether the operation has been a success.
	 */

	public boolean growTree(final List<String> schematics, final Location location) {
		if(schematics == null || schematics.isEmpty()) {
			return false;
		}

		final String file = schematics.get(new Random().nextInt(schematics.size()));
		try {
			location.getBlock().setType(Material.AIR);
			final ClipboardHolder holder = loadSchematic(file);

			if(plugin.getPluginConfig().schematicsRandomRotation) {
				final int degrees = new Random().nextInt(4) * 90;
				if(degrees != 0) {
					final AffineTransform transform = new AffineTransform();
					transform.rotateY(degrees);
					holder.setTransform(transform);
				}
			}

			final BlockVector3 dimensions = holder.getClipboard().getDimensions();
			if(plugin.getPluginConfig().schematicsCheckHeight && !checkHeight(dimensions, location)) {
				return false;
			}

			holder.getClipboard().setOrigin(BlockVector3.at(dimensions.getX() / 2, 0, dimensions.getZ() / 2));
			Operations.completeLegacy(holder
					.createPaste(WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(location.getWorld()), WorldEdit.getInstance().getConfiguration().maxChangeLimit))
					.to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
					.ignoreAirBlocks(true)
					.build()
			);
			return true;
		}
		catch(final Exception ex) {
			plugin.log(ChatColor.RED, "Unable to load the schematic : \"" + file + "\".");
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 * Checks if a there is no floor above the tree.
	 *
	 * @param dimensions Tree dimensions.
	 * @param location Tree location.
	 *
	 * @return Whether there is a floor above the tree.
	 */

	private boolean checkHeight(final BlockVector3 dimensions, final Location location) {
		final World world = location.getWorld();
		final int blockX = location.getBlockX();
		final int blockY = location.getBlockY();
		final int blockZ = location.getBlockZ();
		for(int x = blockX; x < blockX + dimensions.getBlockX(); x++) {
			for(int z = blockZ; z < blockZ + dimensions.getBlockZ(); z++) {
				for(int y = blockY + 1; y < blockY + dimensions.getBlockY(); y++) {
					if(world.getBlockAt(x, y, z).getType() != Material.AIR) {
						return false;
					}
				}
			}
		}

		return true;
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