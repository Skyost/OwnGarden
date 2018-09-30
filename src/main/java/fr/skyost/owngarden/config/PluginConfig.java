package fr.skyost.owngarden.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.skyost.owngarden.util.Skyoconfig;

/**
 * The plugin configuration.
 */

public class PluginConfig extends Skyoconfig {

	@ConfigOptions(name = "enable.updater")
	public boolean enableUpdater = true;

	@ConfigOptions(name = "enable.metrics")
	public boolean enableMetrics = true;
	
	@ConfigOptions(name = "schematics.directory")
	public String schematicsDirectory;

	@ConfigOptions(name = "schematics.random-rotation")
	public boolean schematicsRandomRotation = true;
	
	@ConfigOptions(name = "sapling.oak")
	public List<String> saplingOakSchematics = Arrays.asList("oak/1.schematic", "oak/2.schematic", "oak/3.schematic");
	
	@ConfigOptions(name = "sapling.spruce")
	public List<String> saplingSpruceSchematics = Arrays.asList("spruce/1.schematic", "spruce/2.schematic", "spruce/3.schematic");
	
	@ConfigOptions(name = "sapling.birch")
	public List<String> saplingBirchSchematics = Arrays.asList("birch/1.schematic", "birch/2.schematic", "birch/3.schematic");
	
	@ConfigOptions(name = "sapling.jungle")
	public List<String> saplingJungleSchematics = Arrays.asList("jungle/1.schematic", "jungle/2.schematic", "jungle/3.schematic");
	
	@ConfigOptions(name = "sapling.acacia")
	public List<String> saplingAcaciaSchematics = Arrays.asList("acacia/1.schematic", "acacia/2.schematic", "acacia/3.schematic");
	
	@ConfigOptions(name = "sapling.dark-oak")
	public List<String> saplingDarkOakSchematics = Arrays.asList("dark_oak/1.schematic", "dark_oak/2.schematic", "dark_oak/3.schematic");

	@ConfigOptions(name = "mushroom.brown")
	public List<String> mushroomBrownSchematics = Arrays.asList("brown_mushroom/1.schem", "brown_mushroom/2.schem", "brown_mushroom/3.schem");

	@ConfigOptions(name = "mushroom.red")
	public List<String> mushroomRedSchematics = Arrays.asList("red_mushroom/1.schem", "red_mushroom/2.schem", "red_mushroom/3.schem");

	/**
	 * Creates a new plugin config instance.
	 *
	 * @param dataFolder The plugin data folder.
	 */

	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Collections.singletonList("OwnGarden Configuration File"));
		
		schematicsDirectory = new File(dataFolder, "schematics/").getPath();
	}
	
}