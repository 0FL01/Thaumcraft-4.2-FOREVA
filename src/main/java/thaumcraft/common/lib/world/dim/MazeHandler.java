package thaumcraft.common.lib.world.dim;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MazeHandler {
    public static ConcurrentHashMap<CellLoc, Short> labyrinth = new ConcurrentHashMap<>();

    public static synchronized void putToHashMap(CellLoc loc, Cell cell) {
        labyrinth.put(loc, cell.pack());
    }

    public static synchronized void putToHashMapRaw(CellLoc loc, short data) {
        labyrinth.put(loc, data);
    }

    public static synchronized Cell getFromHashMap(CellLoc loc) {
        Short data = labyrinth.get(loc);
        if (data == null) return null;
        return new Cell(data);
    }

    public static synchronized void removeFromHashMap(CellLoc loc) {
        labyrinth.remove(loc);
    }

    public static synchronized short getFromHashMapRaw(CellLoc loc) {
        Short data = labyrinth.get(loc);
        return data == null ? 0 : data;
    }

    public static synchronized void clearHashMap() {
        labyrinth.clear();
    }

    private static void readNBT(NBTTagCompound nbt) {
        NBTTagList tagList = nbt.getTagList("cells", 10);
        for (int a = 0; a < tagList.tagCount(); a++) {
            NBTTagCompound cell = tagList.getCompoundTagAt(a);
            int x = cell.getInteger("x");
            int z = cell.getInteger("z");
            short v = cell.getShort("cell");
            putToHashMapRaw(new CellLoc(x, z), v);
        }
    }

    private static NBTTagCompound writeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        for (CellLoc loc : labyrinth.keySet()) {
            short v = getFromHashMapRaw(loc);
            if (v <= 0) continue;
            NBTTagCompound cell = new NBTTagCompound();
            cell.setInteger("x", loc.x);
            cell.setInteger("z", loc.z);
            cell.setShort("cell", v);
            tagList.appendTag(cell);
        }
        nbt.setTag("cells", tagList);
        return nbt;
    }

    public static void loadMaze(World world) {
        clearHashMap();
        File file1 = new File(world.getSaveHandler().getWorldDirectory(), "labyrinth.dat");
        if (file1.exists()) {
            try (FileInputStream stream = new FileInputStream(file1)) {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(stream);
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                readNBT(nbttagcompound1);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Try backup
        File file2 = new File(world.getSaveHandler().getWorldDirectory(), "labyrinth.dat_old");
        if (file2.exists()) {
            try (FileInputStream stream = new FileInputStream(file2)) {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(stream);
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                readNBT(nbttagcompound1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveMaze(World world) {
        NBTTagCompound nbttagcompound = writeNBT();
        NBTTagCompound wrapper = new NBTTagCompound();
        wrapper.setTag("Data", nbttagcompound);
        try {
            File dir = world.getSaveHandler().getWorldDirectory();
            File fileNew = new File(dir, "labyrinth.dat_new");
            File fileOld = new File(dir, "labyrinth.dat_old");
            File fileCur = new File(dir, "labyrinth.dat");

            try (FileOutputStream stream = new FileOutputStream(fileNew)) {
                CompressedStreamTools.writeCompressed(wrapper, stream);
            }

            if (fileOld.exists()) fileOld.delete();
            if (fileCur.exists()) fileCur.renameTo(fileOld);
            if (fileCur.exists()) fileCur.delete();
            if (!fileNew.renameTo(fileCur)) {
                try (FileOutputStream stream = new FileOutputStream(fileCur)) {
                    CompressedStreamTools.writeCompressed(wrapper, stream);
                }
                if (fileNew.exists()) {
                    fileNew.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean mazesInRange(int chunkX, int chunkZ, int w, int h) {
        for (int x = -w; x <= w; ++x) {
            for (int z = -h; z <= h; ++z) {
                if (getFromHashMap(new CellLoc(chunkX + x, chunkZ + z)) != null) return true;
            }
        }
        return false;
    }

    public static void generateEldritch(World world, Random random, int cx, int cz) {
        CellLoc loc = new CellLoc(cx, cz);
        Cell cell = getFromHashMap(loc);
        if (cell != null) {
            switch (cell.feature) {
                case 1:
                    GenPortal.generatePortal(world, random, cx, cz, 50, cell);
                    break;
                case 2: case 3: case 4: case 5:
                    GenBossRoom.generateRoom(world, random, cx, cz, 50, cell);
                    break;
                case 6:
                    GenKeyRoom.generateRoom(world, random, cx, cz, 50, cell);
                    break;
                case 7:
                    GenNestRoom.generateRoom(world, random, cx, cz, 50, cell);
                    break;
                case 8:
                    GenLibraryRoom.generateRoom(world, random, cx, cz, 50, cell);
                    break;
                default:
                    GenPassage.generateDefaultPassage(world, random, cx, cz, 50, cell);
                    break;
            }
            GenCommon.processDecorations(world);
        }
    }
}
