package fr.skyost.owngarden;

import com.google.common.base.Joiner;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

import fr.skyost.owngarden.command.OwnGardenCommand;
import fr.skyost.owngarden.config.PluginConfig;
import fr.skyost.owngarden.listener.GlobalEvents;
import fr.skyost.owngarden.util.Skyupdater;
import fr.skyost.owngarden.worldedit.WorldEditOperations;

/**
 * The OwnGarden plugin class.
 */

public class OwnGarden extends JavaPlugin {

	/**
	 * The plugin config.
	 */
	
	private PluginConfig config;

	/**
	 * The WorldEdit operations.
	 */

	private WorldEditOperations worldEditOperations;
	
	@Override
	public final void onEnable() {
		try {
			/* WORLDEDIT HOOK : */

			if(Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
				log(ChatColor.RED, "WorldEdit must be installed on your server !");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}

			worldEditOperations = new WorldEditOperations(this);
			if(!worldEditOperations.checkWorldEditVersion()) {
				log(ChatColor.RED, "Incorrect WorldEdit version. Current accepted ones are : " + Joiner.on(", ").join(WorldEditOperations.WORLDEDIT_VERSIONS) + ".");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}

			/* CONFIGURATION : */
			
			log(ChatColor.GOLD, "Loading the configuration...");
			config = new PluginConfig(this.getDataFolder());
			config.load();
			
			if(config.enableUpdater) {
				new Skyupdater(this, 103296, this.getFile(), true, true);
			}
			if(config.enableMetrics) {
				new MetricsLite(this);
			}
			log(ChatColor.GOLD, "Configuration loaded !");
			
			/* EXTRACTING DEFAULT SCHEMATICS IF NEEDED : */
			
			final File shematicsDirectory = new File(config.schematicsDirectory);
			if(!shematicsDirectory.exists() || !shematicsDirectory.isDirectory()) {
				shematicsDirectory.mkdirs();
			}
			if(shematicsDirectory.list().length == 0) {
				log(ChatColor.GOLD, "Extracting samples schematics...");
				extractSamples(shematicsDirectory);
				log(ChatColor.GOLD, "Done !");
			}
			
			/* TESTING SCHEMATICS : */
			
			log(ChatColor.GOLD, "Testing schematics...");
			final String[] invalidSchematics = worldEditOperations.testSchematics();
			if(invalidSchematics.length > 0) {
				log(ChatColor.RED, "There are some invalid schematics :");
				for(final String invalidSchematic : invalidSchematics) {
					log(ChatColor.RED, invalidSchematic);

					config.saplingOakSchematics.remove(invalidSchematic);
					config.saplingSpruceSchematics.remove(invalidSchematic);
					config.saplingBirchSchematics.remove(invalidSchematic);
					config.saplingJungleSchematics.remove(invalidSchematic);
					config.saplingAcaciaSchematics.remove(invalidSchematic);
					config.saplingDarkOakSchematics.remove(invalidSchematic);
					config.mushroomBrownSchematics.remove(invalidSchematic);
					config.mushroomRedSchematics.remove(invalidSchematic);
				}
				log(ChatColor.RED, "They are not going to be used by the plugin. Please fix them and restart your server.");
			}
			else {
				log(ChatColor.GOLD, "Done, no error.");
			}
			
			/* REGISTERING EVENTS : */
			
			Bukkit.getPluginManager().registerEvents(new GlobalEvents(this), this);
			
			/* REGISTERING COMMANDS : */
			
			this.getCommand("owngarden").setExecutor(new OwnGardenCommand(this));
			
			final PluginDescriptionFile description = this.getDescription();
			log(ChatColor.RESET, "Enabled " + ChatColor.GREEN + this.getName() + " v" + description.getVersion() + ChatColor.GOLD + " by " + Joiner.on(' ').join(description.getAuthors()) + ChatColor.RESET + " !");
		}
		catch(final Exception ex) {
			log(ChatColor.RED, "Unable to start the plugin !");
			ex.printStackTrace();
		}
	}

	/**
	 * Returns the plugin config.
	 *
	 * @return The plugin config.
	 */

	public PluginConfig getPluginConfig() {
		return config;
	}
	
	/**
	 * Logs a message to the console.
	 * 
	 * @param color The color (after the [plugin-name]).
	 * @param message The message.
	 */
	
	public static void log(final ChatColor color, final String message) {
		log(color, message, Bukkit.getConsoleSender());
	}
	
	/**
	 * Logs a message to a CommandSender.
	 * 
	 * @param color The color (after the [plugin-name]).
	 * @param message The message.
	 * @param sender The sender.
	 */
	
	public static void log(final ChatColor color, final String message, final CommandSender sender) {
		sender.sendMessage("[" + Bukkit.getPluginManager().getPlugin("OwnGarden").getName() + "] " + color + message);
	}
	
	/**
	 * Extracts the samples to the specified directory.
	 * 
	 * @param schematicsDirectory The schematics directory.
	 */
	
	public void extractSamples(final File schematicsDirectory) {
		ZipUtil.unpack(getFile(), schematicsDirectory, name -> name.startsWith("schematics/") ? name.replaceFirst("schematics/", "") : null);
	}

	/**
	 * Returns the available WorldEdit operations.
	 *
	 * @return The available WorldEdit operations.
	 */

	public WorldEditOperations getWorldEditOperations() {
		return worldEditOperations;
	}
	
}