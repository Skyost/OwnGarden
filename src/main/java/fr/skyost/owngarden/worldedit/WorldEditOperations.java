package fr.skyost.owngarden.worldedit;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;

import org.bukkit.Bukkit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.skyost.owngarden.OwnGarden;
import fr.skyost.owngarden.config.PluginConfig;

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
	 * Returns the plugin instance.
	 *
	 * @return The plugin instance.
	 */

	public OwnGarden getPlugin() {
		return plugin;
	}

}