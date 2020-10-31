package fr.skyost.owngarden.listener

import fr.skyost.owngarden.OwnGarden
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.StructureGrowEvent

/**
 * Global events handled by the plugin.
 */
class GlobalEventsListener(private val plugin: OwnGarden) : Listener {
    /**
     * Returns the plugin instance.
     *
     * @return The plugin instance.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun onStructureGrow(event: StructureGrowEvent) {
        if (event.isCancelled) {
            return
        }
        val location = event.location
        val schematics = plugin.pluginConfig!!.getSchematics(location.block.type)
        if (plugin.worldEditOperations!!.growTree(schematics, location)) {
            if (schematics === plugin.pluginConfig!!.saplingDarkOakSchematics) {
                val current = location.block
                for (blockFace in FACES) {
                    val relative = current.getRelative(blockFace)
                    if (relative.type == Material.DARK_OAK_SAPLING) {
                        relative.type = Material.AIR
                    }
                }
            }

            //event.getBlocks().clear();
            event.isCancelled = true
        }
    }

    companion object {
        private val FACES = listOf(
                BlockFace.NORTH,
                BlockFace.NORTH_EAST,
                BlockFace.EAST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH,
                BlockFace.SOUTH_WEST,
                BlockFace.WEST,
                BlockFace.NORTH_WEST
        )
    }
}