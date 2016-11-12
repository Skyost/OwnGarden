package fr.skyost.owngarden.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import fr.skyost.owngarden.OwnGarden;
import fr.skyost.owngarden.utils.Schematic;

public class GlobalEvents implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public final void onStructureGrow(final StructureGrowEvent event) {
		if(event.isCancelled()) {
			return;
		}
		final Location location = event.getLocation();
		final Block block = location.getBlock();
		if(block.getType() != Material.SAPLING) {
			return;
		}
		final Random random = new Random();
		String file = null;
		switch(block.getData()) {
		case 8: // Oak
			file = OwnGarden.config.saplingOakSchematics.get(random.nextInt(OwnGarden.config.saplingOakSchematics.size()));
			break;
		case 9: // Spruce
			file = OwnGarden.config.saplingSpruceSchematics.get(random.nextInt(OwnGarden.config.saplingSpruceSchematics.size()));
			break;
		case 10: // Birch
			file = OwnGarden.config.saplingBirchSchematics.get(random.nextInt(OwnGarden.config.saplingBirchSchematics.size()));
			break;
		case 11: // Jungle
			file = OwnGarden.config.saplingJungleSchematics.get(random.nextInt(OwnGarden.config.saplingJungleSchematics.size()));
			break;
		case 12: // Acacia
			file = OwnGarden.config.saplingAcaciaSchematics.get(random.nextInt(OwnGarden.config.saplingAcaciaSchematics.size()));
			break;
		case 13: // Dark Oak
			file = OwnGarden.config.saplingDarkOakSchematics.get(random.nextInt(OwnGarden.config.saplingDarkOakSchematics.size()));
			break;
		}
		try {
			if(file == null) {
				return;
			}
			
			final Object result = OwnGarden.testSchematic(file);
			if(result instanceof Exception) {
				throw (Exception)result;
			}
			else if(result instanceof String) {
				OwnGarden.log(ChatColor.RED, result.toString());
				return;
			}
			
			((Schematic)result).paste(location);
			event.getBlocks().clear();
			event.setCancelled(true);
		}
		catch(final Exception ex) {
			OwnGarden.log(ChatColor.RED, "Unable to load the schematic : \"" + file + "\".");
			ex.printStackTrace();
		}
	}
	
}