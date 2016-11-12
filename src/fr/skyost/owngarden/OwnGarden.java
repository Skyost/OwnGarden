package fr.skyost.owngarden;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Joiner;

import fr.skyost.owngarden.commands.OwnGardenCommand;
import fr.skyost.owngarden.listeners.GlobalEvents;
import fr.skyost.owngarden.utils.Schematic;
import fr.skyost.owngarden.utils.Skyupdater;

public class OwnGarden extends JavaPlugin {
	
	public static PluginConfig config;
	
	@Override
	public final void onEnable() {
		try {
			/* CONFIGURATION : */
			
			log(ChatColor.GOLD, "Loading the configuration...");
			config = new PluginConfig(this.getDataFolder());
			config.load();
			
			if(config.enableUpdater) {
				new Skyupdater(this, 103296, this.getFile(), true, true);
			}
			log(ChatColor.GOLD, "Configuration loaded !");
			
			/* EXTRACTING DEFAULT SCHEMATICS IF NEEDED : */
			
			final File shematicsDirectory = new File(config.schematicsDirectory);
			if(!shematicsDirectory.exists() || !shematicsDirectory.isDirectory()) {
				shematicsDirectory.mkdir();
			}
			if(shematicsDirectory.list().length == 0) {
				log(ChatColor.GOLD, "Extracting samples schematics...");
				extractSamples(shematicsDirectory);
				log(ChatColor.GOLD, "Done !");
			}
			
			/* TESTING SCHEMATICS : */
			
			log(ChatColor.GOLD, "Testing schematics...");
			final String[] invalidSchematics = testSchematics();
			if(invalidSchematics.length > 0) {
				log(ChatColor.RED, "There are some invalid schematics :");
				for(final String invalidSchematic : invalidSchematics) {
					log(ChatColor.RED, invalidSchematic);
					
					if(config.saplingOakSchematics.contains(invalidSchematic)) {
						config.saplingOakSchematics.remove(invalidSchematic);
					}
					else if(config.saplingSpruceSchematics.contains(invalidSchematic)) {
						config.saplingSpruceSchematics.remove(invalidSchematic);
					}
					else if(config.saplingBirchSchematics.contains(invalidSchematic)) {
						config.saplingBirchSchematics.remove(invalidSchematic);
					}
					else if(config.saplingJungleSchematics.contains(invalidSchematic)) {
						config.saplingJungleSchematics.remove(invalidSchematic);
					}
					else if(config.saplingAcaciaSchematics.contains(invalidSchematic)) {
						config.saplingAcaciaSchematics.remove(invalidSchematic);
					}
					else if(config.saplingDarkOakSchematics.contains(invalidSchematic)) {
						config.saplingDarkOakSchematics.remove(invalidSchematic);
					}
				}
				log(ChatColor.RED, "They are not going to be used by the plugin. Please fix them and restart your server.");
			}
			else {
				log(ChatColor.GOLD, "Done, no error.");
			}
			
			/* REGISTERING EVENTS : */
			
			Bukkit.getPluginManager().registerEvents(new GlobalEvents(), this);
			
			/* REGISTERING COMMANDS : */
			
			this.getCommand("owngarden").setExecutor(new OwnGardenCommand());
			
			final PluginDescriptionFile description = this.getDescription();
			log(ChatColor.RESET, "Enabled " + ChatColor.GREEN + this.getName() + " v" + description.getVersion() + ChatColor.GOLD + " by " + Joiner.on(' ').join(description.getAuthors()) + ChatColor.RESET + " !");
		}
		catch(final Exception ex) {
			log(ChatColor.RED, "Unable to start the plugin !");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Log a message to the console.
	 * 
	 * @param color The color (after the [plugin-name]).
	 * @param message The message.
	 */
	
	public static final void log(final ChatColor color, final String message) {
		log(color, message, Bukkit.getConsoleSender());
	}
	
	/**
	 * Log a message to a CommandSender.
	 * 
	 * @param color The color (after the [plugin-name]).
	 * @param message The message.
	 * @param sender The sender.
	 */
	
	public static final void log(final ChatColor color, final String message, final CommandSender sender) {
		sender.sendMessage("[" + Bukkit.getPluginManager().getPlugin("OwnGarden").getName() + "] " + color + message);
	}
	
	/**
	 * Extracts the samples to the specified directory.
	 * 
	 * @param schematicsDirectory The schematics directory.
	 * 
	 * @throws IOException If an exception occurs when trying to extract the samples.
	 */
	
	public final void extractSamples(final File schematicsDirectory) throws IOException {
		final JarFile jar = new JarFile(this.getFile());
		final Enumeration<JarEntry> enumEntries = jar.entries();
		
		while(enumEntries.hasMoreElements()) {
			final JarEntry entry = enumEntries.nextElement();
			final String name = entry.getName();
			
			if(!name.startsWith("schematics/") || name.equals("schematics/")) {
				continue;
			}
			
			final File file = new File(schematicsDirectory, name.split("schematics/")[1]);
			file.getParentFile().mkdirs();
			
			final InputStream is = jar.getInputStream(entry);
			final FileOutputStream fos = new FileOutputStream(file);
			while(is.available() > 0) {
				fos.write(is.read());
			}
			fos.close();
			is.close();
		}
		jar.close();
	}
	
	/**
	 * Tests if each schematic is valid.
	 * 
	 * @return An array containing the invalid schematics.
	 */
	
	public static final String[] testSchematics() {
		final List<String> schematics = new ArrayList<String>();
		schematics.addAll(config.saplingOakSchematics);
		schematics.addAll(config.saplingSpruceSchematics);
		schematics.addAll(config.saplingBirchSchematics);
		schematics.addAll(config.saplingJungleSchematics);
		schematics.addAll(config.saplingAcaciaSchematics);
		schematics.addAll(config.saplingDarkOakSchematics);
		
		final List<String> invalidSchematics = new ArrayList<String>();
		for(final String schematic : schematics) {
			if(!(testSchematic(schematic) instanceof Schematic)) {
				invalidSchematics.add(schematic);
			}
		}
		return invalidSchematics.toArray(new String[invalidSchematics.size()]);
	}
	
	/**
	 * Test if a schematic is valid.
	 * 
	 * @param schematic The schematic (must be in the schematics directory).
	 * 
	 * @return An exception if an exception occurs while loading the schematic,
	 * <br>A string for any other error,
	 * <br>Or a Schematic if no error occurs.
	 */
	
	public static final Object testSchematic(final String schematic) {
		final File file = new File(config.schematicsDirectory, schematic);
		if(!file.exists()) {
			return "Schematic file not found : \"" + file + "\".";
		}
		try {
			final Schematic result = Schematic.loadSchematic(file);
			return result;
		}
		catch(final Exception ex) {
			return ex;
		}
	}
	
}