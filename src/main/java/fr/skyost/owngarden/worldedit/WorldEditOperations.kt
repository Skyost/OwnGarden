package fr.skyost.owngarden.worldedit

import com.sk89q.jnbt.*
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.math.transform.AffineTransform
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.util.io.Closer
import fr.skyost.owngarden.OwnGarden
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import java.io.*
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Represents available WorldEdit operations.
 */
class WorldEditOperations(private val plugin: OwnGarden) {
    /**
     * Returns whether the current WorldEdit version should be accepted.
     *
     * @return Whether the current WorldEdit version should be accepted.
     */
    fun checkWorldEditVersion(): Boolean {
        val version = Bukkit.getPluginManager().getPlugin("WorldEdit")!!.description.version
        for (prefix in WORLDEDIT_VERSIONS) {
            if (version.startsWith(prefix)) {
                return true
            }
        }
        return false
    }

    /**
     * Tests if each schematic is valid.
     *
     * @return An array containing the invalid schematics.
     */
    fun testSchematics(): Array<String> {
        val config = plugin.pluginConfig
        val schematics: MutableList<String> = ArrayList()
        schematics.addAll(config!!.saplingOakSchematics)
        schematics.addAll(config.saplingSpruceSchematics)
        schematics.addAll(config.saplingBirchSchematics)
        schematics.addAll(config.saplingJungleSchematics)
        schematics.addAll(config.saplingAcaciaSchematics)
        schematics.addAll(config.saplingDarkOakSchematics)
        val removeWorldEditMetaData = plugin.pluginConfig!!.schematicsRemoveWorldEditMetaData
        val invalidSchematics: MutableList<String> = ArrayList()
        for (schematic in schematics) {
            try {
                loadSchematic(schematic)
                if (!removeWorldEditMetaData) {
                    continue
                }
                val file = getFile(schematic)
                val format = ClipboardFormats.findByFile(file)
                format?.let { removeWorldEditMetaData(it, file) }
            } catch (ex: Exception) {
                ex.printStackTrace()
                invalidSchematics.add(schematic)
            }
        }
        return invalidSchematics.toTypedArray()
    }

    /**
     * Returns the file associated with the given schematic.
     *
     * @param schematic The schematic.
     *
     * @return The file.
     */
    private fun getFile(schematic: String): File {
        return File(plugin.pluginConfig!!.schematicsDirectory, schematic)
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
    @Throws(IOException::class)
    fun loadSchematic(schematic: String): ClipboardHolder? {
        val file = getFile(schematic)
        if (!file.exists()) {
            throw FileNotFoundException("Schematic not found : $schematic.")
        }
        val format = ClipboardFormats.findByFile(file)
                ?: throw IllegalArgumentException("Unknown schematic format.")
        val closer = Closer.create()
        try {
            val fileInputStream = closer.register(FileInputStream(file))
            val bufferedInputStream = closer.register(BufferedInputStream(fileInputStream))
            val reader = closer.register(format.getReader(bufferedInputStream))
            return ClipboardHolder(reader.read())
        } catch (ex: Exception) {
            closer.rethrow(ex)
        } finally {
            closer.close()
        }
        return null
    }

    /**
     * Grows a tree at the specified location.
     *
     * @param schematics The schematics list.
     * @param location The location.
     *
     * @return Whether the operation has been a success.
     */
    fun growTree(schematics: List<String>?, location: Location): Boolean {
        if (schematics == null || schematics.isEmpty()) {
            return false
        }
        val file = schematics[Random().nextInt(schematics.size)]
        try {
            location.block.type = Material.AIR
            val holder = loadSchematic(file)
            if (plugin.pluginConfig!!.schematicsRandomRotation) {
                val degrees = Random().nextInt(4) * 90
                if (degrees != 0) {
                    val transform = AffineTransform()
                    transform.rotateY(degrees.toDouble())
                    holder!!.transform = transform
                }
            }
            val dimensions = holder!!.clipboard.dimensions
            if (plugin.pluginConfig!!.schematicsCheckHeight && !checkHeight(dimensions, location)) {
                return false
            }
            holder.clipboard.origin = BlockVector3.at(dimensions.x / 2, 0, dimensions.z / 2)
            val session = WorldEdit.getInstance().newEditSession(BukkitWorld(location.world))
            val operation = holder
                    .createPaste(session)
                    .to(BlockVector3.at(location.blockX, location.blockY, location.blockZ))
                    .ignoreAirBlocks(true)
                    .build()
            Operations.completeLegacy(operation)
            session.close()

            return true
        } catch (ex: Exception) {
            plugin.log(ChatColor.RED, "Unable to load the schematic : \"$file\".")
            ex.printStackTrace()
        }
        return false
    }

    /**
     * Checks if a there is no floor above the tree.
     *
     * @param dimensions Tree dimensions.
     * @param location Tree location.
     *
     * @return Whether there is a floor above the tree.
     */
    private fun checkHeight(dimensions: BlockVector3, location: Location): Boolean {
        val world = location.world
        val blockX = location.blockX
        val blockY = location.blockY
        val blockZ = location.blockZ
        for (x in blockX until blockX + dimensions.blockX) {
            for (z in blockZ until blockZ + dimensions.blockZ) {
                for (y in blockY + 1 until blockY + dimensions.blockY) {
                    if (world!!.getBlockAt(x, y, z).type != Material.AIR) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Removes all WorldEdit metadata (if needed).
     *
     * @param format The clipboard format.
     * @param file The file.
     *
     * @throws IOException If any I/O exception occurs.
     */
    @Throws(IOException::class)
    private fun removeWorldEditMetaData(format: ClipboardFormat, file: File) {
        val input = NBTInputStream(GZIPInputStream(FileInputStream(file)))
        var root = input.readNamedTag().tag as CompoundTag
        var target = root
        val isSponge = format.primaryFileExtension == BuiltInClipboardFormat.SPONGE_SCHEMATIC.primaryFileExtension
        if (isSponge) {
            target = target.value.getOrDefault("Metadata", CompoundTag(HashMap())) as CompoundTag
        }
        val value: MutableMap<String, Tag> = HashMap(target.value)
        value.remove("WEOriginX")
        value.remove("WEOriginY")
        value.remove("WEOriginZ")
        value.remove("WEOffsetX")
        value.remove("WEOffsetY")
        value.remove("WEOffsetZ")
        target = target.setValue(value)
        if (isSponge) {
            val rootValue: MutableMap<String, Tag> = HashMap(root.value)
            rootValue["Metadata"] = target
            rootValue["Offset"] = IntArrayTag(intArrayOf(0, 0, 0))
            root = root.setValue(rootValue)
        } else {
            root = target
        }
        val output = NBTOutputStream(GZIPOutputStream(FileOutputStream(file)))
        output.writeNamedTag("Schematic", root)
        output.close()
    }

    companion object {
        /**
         * Accepted WorldEdit versions.
         */
        val WORLDEDIT_VERSIONS = arrayOf("7.2")
    }
}