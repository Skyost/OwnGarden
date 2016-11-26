package fr.skyost.owngarden.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

/*
*
*    This class is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This class is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this class.  If not, see <http://www.gnu.org/licenses/>.
*
*/

/**
*
* @author Max
*/

public class Schematic {
	
	
	private byte[] blocks;
	private byte[] data;
	private short width;
	private short length;
	private short height;
	
	public Schematic(byte[] blocks, byte[] data, short width, short length, short height) {
		this.blocks = blocks;
		this.data = data;
		this.width = width;
		this.length = length;
		this.height = height;
	}
	
	/**
	* @return the blocks
	*/
	
	public byte[] getBlocks() {
		return blocks;
	}
	
	/**
	* @return the data
	*/
	
	public byte[] getData() {
		return data;
	}
	
	/**
	* @return the width
	*/
	
	public short getWidth() {
		return width;
	}
	
	/**
	* @return the lenght
	*/
	
	public short getLength() {
		return length;
	}
	
	/**
	* @return the height
	*/
	
	public short getHeight() {
		return height;
	}
	
	public final List<Location> paste(Location loc) {
		return paste(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public final List<Location> paste(final World locWorld, final double locX, final double locY, final double locZ) {
		final List<Location> locations = new ArrayList<Location>();
		final int[] root = getRoot();
		
		final double xOrigin = locX - root[0];
		final double zOrigin = locZ - root[1];
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				for(int z = 0; z < length; ++z) {
					int index = y * width * length + z * width + x;
					int b = blocks[index] & 0xFF; // Make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted.
					final Material material = Material.getMaterial(b);
					if(material == Material.AIR) {
						continue;
					}
					
					final Location location = new Location(locWorld, xOrigin + x, locY + y, zOrigin + z);
					final Block block = location.getBlock();
					block.setTypeIdAndData(material.getId(), data[index], true);
					
					locations.add(location);
				}
			}
		}
		return locations;
	}
	
	public static Schematic loadSchematic(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		NBTInputStream nbtStream = new NBTInputStream(stream);
		
		CompoundTag schematicTag = (CompoundTag)nbtStream.readTag();
		nbtStream.close();
		if(!schematicTag.getName().equals("Schematic")) {
			throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
		}
		
		Map<String, Tag> schematic = schematicTag.getValue();
		if(!schematic.containsKey("Blocks")) {
			throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
		}
		
		short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
		short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
		short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
		
		String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
		if(!materials.equals("Alpha")) {
			throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
		}
		
		byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
		byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
		return new Schematic(blocks, blockData, width, length, height);
	}
	
	/**
	* Get child tag of a NBT structure.
	*
	* @param items The parent tag map
	* @param key The name of the tag to get
	* @param expected The expected type of the tag
	* @return child tag casted to the expected type
	* @throws DataException if the tag does not exist or the tag is not of the
	* expected type
	*/
	
	private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException {
		if(!items.containsKey(key)) {
			throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
		}
		Tag tag = items.get(key);
		if(!expected.isInstance(tag)) {
			throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
		}
		return expected.cast(tag);
	}
	
	private final int[] getRoot() {
		final int y = 0;
		for(int x = 0; x < width; ++x) {
			for(int z = 0; z < length; ++z) {
				int index = y * width * length + z * width + x;
				int b = blocks[index] & 0xFF; // make the block unsigned, so that blocks with an id over 127, like quartz and emerald, can be pasted
				final Material material = Material.getMaterial(b);
				if(material == Material.AIR) {
					continue;
				}
				
				return new int[]{x, z};
			}
		}
		return new int[]{width / 2, length / 2};
	}
	
}