package fr.reizam.mlockets.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import lombok.Getter;

@SerializableAs("Cuboid")
public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {

	protected final String worldName;
	protected @Getter Location loc1;
	protected @Getter Location loc2;
	protected final int x1;
	protected final int y1;
	protected final int z1;
	protected final int x2;
	protected final int y2;
	protected final int z2;

	public Cuboid(Location location1, Location location2) {

		if (!location1.getWorld().equals(location2.getWorld())) {
			Logger.getLogger("Les locations doivent être dans un même monde.");
		}
		this.worldName = location1.getWorld().getName();
		this.x1 = Math.min(location1.getBlockX(), location2.getBlockX());
		this.y1 = Math.min(location1.getBlockY(), location2.getBlockY());
		this.z1 = Math.min(location1.getBlockZ(), location2.getBlockZ());
		this.x2 = Math.max(location1.getBlockX(), location2.getBlockX());
		this.y2 = Math.max(location1.getBlockY(), location2.getBlockY());
		this.z2 = Math.max(location1.getBlockZ(), location2.getBlockZ());
		this.loc1 = location1;
		this.loc2 = location2;
	}

	public Cuboid(Location location) {
		this(location, location);
	}

	public Cuboid(Cuboid cuboid) {
		this(cuboid.getWorld().getName(), cuboid.x1, cuboid.y1, cuboid.z1, cuboid.x2, cuboid.y2, cuboid.z2);
	}

	private Cuboid(String string, int n, int n2, int n3, int n4, int n5, int n6) {
		this.worldName = string;
		this.x1 = Math.min(n, n4);
		this.x2 = Math.max(n, n4);
		this.y1 = Math.min(n2, n5);
		this.y2 = Math.max(n2, n5);
		this.z1 = Math.min(n3, n6);
		this.z2 = Math.max(n3, n6);
	}

	public Cuboid(Map<String, Object> map) {
		this.worldName = (String) map.get("worldName");
		this.x1 = (Integer) map.get("x1");
		this.x2 = (Integer) map.get("x2");
		this.y1 = (Integer) map.get("y1");
		this.y2 = (Integer) map.get("y2");
		this.z1 = (Integer) map.get("z1");
		this.z2 = (Integer) map.get("z2");
	}

	public Map<String, Object> serialize() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("worldName", this.worldName);
		hashMap.put("x1", this.x1);
		hashMap.put("y1", this.y1);
		hashMap.put("z1", this.z1);
		hashMap.put("x2", this.x2);
		hashMap.put("y2", this.y2);
		hashMap.put("z2", this.z2);
		return hashMap;
	}

	public Location getLowerNE() {
		return new Location(this.getWorld(), (double) this.x1, (double) this.y1, (double) this.z1);
	}

	public Location getUpperSW() {
		return new Location(this.getWorld(), (double) this.x2, (double) this.y2, (double) this.z2);
	}

	public List<Block> getBlocks() {
		Iterator<Block> iterator = this.iterator();
		ArrayList<Block> arrayList = new ArrayList<Block>();
		while (iterator.hasNext()) {
			arrayList.add(iterator.next());
		}
		return arrayList;
	}

	public Location getCenter() {

		int i = getUpperX() + 1;
		int j = getUpperY() + 1;
		int k = getUpperZ() + 1;
		return new Location(getWorld(), getLowerX() + (i - getLowerX()) / 2.0D, getLowerY() + (j - getLowerY()) / 2.0D,
				getLowerZ() + (k - getLowerZ()) / 2.0D);
	}

	public World getWorld() {

		World world = Bukkit.getWorld(this.worldName);

		if (world == null) {
			Logger.getLogger("Le monde " + worldName + "n'est pas chargé !");
		}
		return world;
	}

	public int getSizeX() {
		return this.x2 - this.x1 + 1;
	}

	public int getSizeY() {
		return this.y2 - this.y1 + 1;
	}

	public int getSizeZ() {
		return this.z2 - this.z1 + 1;
	}

	public int getLowerX() {
		return this.x1;
	}

	public int getLowerY() {
		return this.y1;
	}

	public int getLowerZ() {
		return this.z1;
	}

	public int getUpperX() {
		return this.x2;
	}

	public int getUpperY() {
		return this.y2;
	}

	public int getUpperZ() {
		return this.z2;
	}
	
	@SuppressWarnings("deprecation")
	public List<Player> getPlayersInside() {
		List<Player> list = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (this.contains(player.getLocation())) {
				list.add(player);
			}
		}
		return list;
	}
	
	public List<LivingEntity> getLivingEntityInside() {
		List<LivingEntity> list = new ArrayList<>();
		for (LivingEntity entity : this.getWorld().getLivingEntities()) {
			if (this.contains(entity.getLocation())) {
				list.add(entity);
			}
		}
		return list;
	}
	
	public List<LivingEntity> getLivingEntityInside(EntityType type) {
		List<LivingEntity> list = new ArrayList<>();
		for (LivingEntity entity : this.getWorld().getLivingEntities()) {
			if (this.contains(entity.getLocation()) && entity.getType() == type) {
				list.add(entity);
			}
		}
		return list;
	}
	
	public boolean isInside(Player player) {
		return this.contains(player.getLocation());
	}
	
    public Integer replaceBlocks(Location loc1, Location loc2, Material material) {
        int BlockCount = 0;
        List<Block> temp = getBlockList(loc1, loc2);
        for (Block b : temp) {
            if (b.getType() != material) {
                b.setType(material);
                BlockCount++;
            }
        }
        return Integer.valueOf(BlockCount);
    }
 
    public Integer replaceBlocksByBlock(Location loc1, Location loc2, Material toReplace, Material New) {
        int BlockCount = 0;
        List<Block> temp = getBlockList(loc1, loc2);
        for (Block b : temp) {
            if ((b.getType() == toReplace) && (b.getType() != New)) {
                b.setType(New);
                BlockCount++;
            }
        }
        return Integer.valueOf(BlockCount);
    }
 
    public Integer replaceWalls(Location loc1, Location loc2, Material material) {
        return generateWalls(loc1, loc2, material);
    }
 
    public Integer replaceBlocksInWallsByBlock(Location loc1, Location loc2, Material toReplace, Material New) {
        int BlockCount = 0;
        List<Block> temp = getBlockList(loc1, loc2);
        for (Block b : temp) {
            if ((b.getType() == toReplace) && (b.getType() != New)) {
                b.setType(New);
                BlockCount++;
            }
        }
        return Integer.valueOf(BlockCount);
    }
 
    
    public Integer generateWalls(Location loc1, Location loc2, Material material) {
        int BlockCount = 0;
 
        int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                if (loc1.getWorld().getBlockAt(x, y, zMin).getType() != material) {
                    loc1.getWorld().getBlockAt(x, y, zMin).setType(material);
                    BlockCount++;
                }
                if (loc1.getWorld().getBlockAt(x, y, zMax).getType() != material) {
                    loc1.getWorld().getBlockAt(x, y, zMax).setType(material);
                    BlockCount++;
                }
            }
            BlockCount++;
        }
        for (int y = yMin; y <= yMax; y++) {
            for (int z = zMin; z <= zMax; z++) {
                if (loc1.getWorld().getBlockAt(xMin, y, z).getType() != material) {
                    loc1.getWorld().getBlockAt(xMin, y, z).setType(material);
                    BlockCount++;
                }
                if (loc1.getWorld().getBlockAt(xMax, y, z).getType() != material) {
                    loc1.getWorld().getBlockAt(xMax, y, z).setType(material);
                    BlockCount++;
                }
            }
        }
        return Integer.valueOf(BlockCount);
    }
 
    public void clearCube(Location loc1, Location loc2) {
        List<Block> b = getBlockList(loc1, loc2);
        for (Block bl : b) {
            if (!getWalls(loc1, loc2).contains(bl)) {
                bl.setType(Material.AIR);
            }
        }
    }
 
    public void cutBlock(Location loc1, Location loc2) {
        List<Block> b = getBlockList(loc1, loc2);
        for (Block bl : b) {
            bl.setType(Material.AIR);
        }
    }
 
    public List<Block> getWalls(Location loc1, Location loc2) {
        List<Block> b = new ArrayList<>();
 
        int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                b.add(loc1.getWorld().getBlockAt(x, y, zMin));
                b.add(loc1.getWorld().getBlockAt(x, y, zMax));
            }
        }
        for (int y = yMin; y <= yMax; y++) {
            for (int z = zMin; z <= zMax; z++) {
                b.add(loc1.getWorld().getBlockAt(xMin, y, z));
                b.add(loc1.getWorld().getBlockAt(xMax, y, z));
            }
        }
        return b;
    }

    public List<Block> getBlockList(Location loc1, Location loc2) {
        List<Block> bL = new ArrayList<>();
 
        int xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Block b = loc1.getWorld().getBlockAt(x, y, z);
                    bL.add(b);
                }
            }
        }
        return bL;
    }
    
    
	public Block[] corners() {

		Block[] arrayOfBlock = new Block[8];
		World localWorld = getWorld();
		arrayOfBlock[0] = localWorld.getBlockAt(this.x1, this.y1, this.z1);
		arrayOfBlock[1] = localWorld.getBlockAt(this.x1, this.y1, this.z2);
		arrayOfBlock[2] = localWorld.getBlockAt(this.x1, this.y2, this.z1);
		arrayOfBlock[3] = localWorld.getBlockAt(this.x1, this.y2, this.z2);
		arrayOfBlock[4] = localWorld.getBlockAt(this.x2, this.y1, this.z1);
		arrayOfBlock[5] = localWorld.getBlockAt(this.x2, this.y1, this.z2);
		arrayOfBlock[6] = localWorld.getBlockAt(this.x2, this.y2, this.z1);
		arrayOfBlock[7] = localWorld.getBlockAt(this.x2, this.y2, this.z2);
		return arrayOfBlock;
	}

	public Cuboid expand(CuboidDirection paramCuboidDirection, int paramInt) {

		switch (paramCuboidDirection) {

		case Both:
			return new Cuboid(this.worldName, this.x1 - paramInt, this.y1, this.z1, this.x2, this.y2, this.z2);
		case East:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + paramInt, this.y2, this.z2);
		case Down:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - paramInt, this.x2, this.y2, this.z2);
		case Horizontal:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + paramInt);
		case South:
			return new Cuboid(this.worldName, this.x1, this.y1 - paramInt, this.z1, this.x2, this.y2, this.z2);
		case North:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + paramInt, this.z2);
		default:
			break;
		}
		throw new IllegalArgumentException("Invalid direction " + paramCuboidDirection);
	}

	public Cuboid shift(CuboidDirection paramCuboidDirection, int paramInt) {

		return expand(paramCuboidDirection, paramInt).expand(paramCuboidDirection.opposite(), -paramInt);
	}

	public Cuboid outset(CuboidDirection paramCuboidDirection, int paramInt) {

		Cuboid localCuboid;
		switch (paramCuboidDirection) {

		case Unknown:
			localCuboid = expand(CuboidDirection.North, paramInt).expand(CuboidDirection.South, paramInt)
					.expand(CuboidDirection.East, paramInt).expand(CuboidDirection.West, paramInt);
			break;
		case Up:
			localCuboid = expand(CuboidDirection.Down, paramInt).expand(CuboidDirection.Up, paramInt);
			break;
		case Vertical:
			localCuboid = outset(CuboidDirection.Horizontal, paramInt).outset(CuboidDirection.Vertical, paramInt);
			break;
		default:
			throw new IllegalArgumentException("Invalid direction " + paramCuboidDirection);
		}
		return localCuboid;
	}

	public Cuboid inset(CuboidDirection cuboidDirection, int n) {
		return this.outset(cuboidDirection, -n);
	}

	public boolean contains(int n, int n2, int n3) {
		if (n >= this.x1 && n <= this.x2 && n2 >= this.y1 && n2 <= this.y2 && n3 >= this.z1 && n3 <= this.z2) {
			return true;
		}
		return false;
	}

	public boolean contains(Block block) {
		return this.contains(block.getLocation());
	}

	public boolean contains(Location location) {
		if (!this.worldName.equals(location.getWorld().getName())) {
			return false;
		}
		return this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public int getVolume() {
		return this.getSizeX() * this.getSizeY() * this.getSizeZ();
	}

	public byte getAverageLightLevel() {

		long l = 0L;
		int i = 0;
		for (Block localBlock : this) {
			if (localBlock.isEmpty()) {

				l += localBlock.getLightLevel();
				i++;
			}
		}
		return i > 0 ? (byte) (int) (l / i) : 0;
	}

	public Cuboid contract() {

		return contract(CuboidDirection.Down).contract(CuboidDirection.South).contract(CuboidDirection.East)
				.contract(CuboidDirection.Up).contract(CuboidDirection.North).contract(CuboidDirection.West);
	}

	public Cuboid contract(CuboidDirection paramCuboidDirection) {

		Cuboid localCuboid = getFace(paramCuboidDirection.opposite());
		switch (paramCuboidDirection) {
		case South:
			while ((localCuboid.containsOnly(0)) && (localCuboid.getLowerY() > getLowerY())) {
				localCuboid = localCuboid.shift(CuboidDirection.Down, 1);
			}
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, localCuboid.getUpperY(), this.z2);
		case North:
			while ((localCuboid.containsOnly(0)) && (localCuboid.getUpperY() < getUpperY())) {
				localCuboid = localCuboid.shift(CuboidDirection.Up, 1);
			}
			return new Cuboid(this.worldName, this.x1, localCuboid.getLowerY(), this.z1, this.x2, this.y2, this.z2);
		case Both:
			while ((localCuboid.containsOnly(0)) && (localCuboid.getLowerX() > getLowerX())) {
				localCuboid = localCuboid.shift(CuboidDirection.North, 1);
			}
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, localCuboid.getUpperX(), this.y2, this.z2);
		case East:
			while ((localCuboid.containsOnly(0)) && (localCuboid.getUpperX() < getUpperX())) {
				localCuboid = localCuboid.shift(CuboidDirection.South, 1);
			}
			return new Cuboid(this.worldName, localCuboid.getLowerX(), this.y1, this.z1, this.x2, this.y2, this.z2);
		case Down:
			while ((localCuboid.containsOnly(0)) && (localCuboid.getLowerZ() > getLowerZ())) {
				localCuboid = localCuboid.shift(CuboidDirection.East, 1);
			}
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, localCuboid.getUpperZ());
		case Horizontal:
			while ((localCuboid.containsOnly(0)) && (localCuboid.getUpperZ() < getUpperZ())) {
				localCuboid = localCuboid.shift(CuboidDirection.West, 1);
			}
			return new Cuboid(this.worldName, this.x1, this.y1, localCuboid.getLowerZ(), this.x2, this.y2, this.z2);
		default:
			break;
		}
		throw new IllegalArgumentException("Invalid direction " + paramCuboidDirection);
	}

	public Cuboid getFace(CuboidDirection paramCuboidDirection) {

		switch (paramCuboidDirection) {

		case South:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
		case North:
			return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
		case Both:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
		case East:
			return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
		case Down:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
		case Horizontal:
			return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
		default:
			break;
		}
		throw new IllegalArgumentException("Invalid direction " + paramCuboidDirection);
	}

	@SuppressWarnings("deprecation")
	public boolean containsOnly(int paramInt) {

		for (Block block : this) {
			if (block.getTypeId() != paramInt) {
				return false;
			}
		}
		return true;
	}

	public Cuboid getBoundingCuboid(Cuboid paramCuboid) {

		if (paramCuboid == null) {
			return this;
		}
		int i = Math.min(getLowerX(), paramCuboid.getLowerX());
		int j = Math.min(getLowerY(), paramCuboid.getLowerY());
		int k = Math.min(getLowerZ(), paramCuboid.getLowerZ());
		int m = Math.max(getUpperX(), paramCuboid.getUpperX());
		int n = Math.max(getUpperY(), paramCuboid.getUpperY());
		int i1 = Math.max(getUpperZ(), paramCuboid.getUpperZ());

		return new Cuboid(this.worldName, i, j, k, m, n, i1);
	}

	public Block getRelativeBlock(int n, int n2, int n3) {
		return this.getWorld().getBlockAt(this.x1 + n, this.y1 + n2, this.z1 + n3);
	}

	public Block getRelativeBlock(World world, int n, int n2, int n3) {
		return world.getBlockAt(this.x1 + n, this.y1 + n2, this.z1 + n3);
	}

	public List<Chunk> getChunks() {
		ArrayList<Chunk> arrayList = new ArrayList<Chunk>();

		World world = getWorld();
		int i = getLowerX() & -16;
		int j = getUpperX() & -16;
		int k = getLowerZ() & -16;
		int m = getUpperZ() & -16;
		for (int n = i; n <= j; n += 16) {
			for (int i1 = k; i1 <= m; i1 += 16) {
				arrayList.add(world.getChunkAt(n >> 4, i1 >> 4));
			}
		}
		return arrayList;
	}

	public Iterator<Block> iterator() {

		return new CuboidIterator(getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
	}

	public Cuboid clone() {
		return new Cuboid(this);
	}

	public String toString() {
		return new String("Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2
				+ "," + this.y2 + "," + this.z2);
	}

	public class CuboidIterator implements Iterator<Block> {
		private World w;
		private int baseX;
		private int baseY;
		private int baseZ;
		private int x;
		private int y;
		private int z;
		private int sizeX;
		private int sizeY;
		private int sizeZ;

		public CuboidIterator(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4,
				int paramInt5, int paramInt6) {

			this.w = paramWorld;
			this.baseX = paramInt1;
			this.baseY = paramInt2;
			this.baseZ = paramInt3;
			this.sizeX = (Math.abs(paramInt4 - paramInt1) + 1);
			this.sizeY = (Math.abs(paramInt5 - paramInt2) + 1);
			this.sizeZ = (Math.abs(paramInt6 - paramInt3) + 1);
			this.x = (this.y = this.z = 0);
		}

		public boolean hasNext() {

			return (this.x < this.sizeX) && (this.y < this.sizeY) && (this.z < this.sizeZ);
		}

		@Override
		public Block next() {
			Block block = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
			if (++this.x >= this.sizeX) {
				this.x = 0;
				if (++this.y >= this.sizeY) {
					this.y = 0;
					++this.z;
				}
			}
			return block;
		}

		@Override
		public void remove() {
		}
	}

	public enum CuboidDirection {

		North, East, South, West, Up, Down, Horizontal, Vertical, Both, Unknown;

		public CuboidDirection opposite() {
			switch (this) {

			case Both:
				return South;
			case Down:
				return West;
			case East:
				return North;
			case Horizontal:
				return East;
			case Unknown:
				return Vertical;
			case Up:
				return Horizontal;
			case North:
				return Down;
			case South:
				return Up;
			case Vertical:
				return Both;
			default:
				break;
			}
			return Unknown;
		}
	}
}
