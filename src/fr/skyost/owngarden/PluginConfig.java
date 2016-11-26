package fr.skyost.owngarden;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import fr.skyost.owngarden.utils.Skyoconfig;

public class PluginConfig extends Skyoconfig {
	
	@ConfigOptions(name = "schematics-directory")
	public String schematicsDirectory;
	
	@ConfigOptions(name = "enable-updater")
	public boolean enableUpdater = true;
	
	@ConfigOptions(name = "enable-metrics")
	public boolean enableMetrics = true;
	
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

	public PluginConfig(final File dataFolder) {
		super(new File(dataFolder, "config.yml"), Arrays.asList("OwnGarden Configuration File"));
		
		schematicsDirectory = new File(dataFolder, "schematics/").getPath();
	}
	
}