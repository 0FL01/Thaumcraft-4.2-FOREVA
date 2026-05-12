package thaumcraft.common.lib.world.dim;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
        clearHashMap();
        int[] xArr = nbt.getIntArray("xArr");
        int[] zArr = nbt.getIntArray("zArr");
        byte[] dArr = nbt.getByteArray("dArr");
        for (int i = 0; i < xArr.length && i < zArr.length && i < dArr.length; i++) {
            putToHashMapRaw(new CellLoc(xArr[i], zArr[i]), (short)dArr[i]);
        }
    }

    private static NBTTagCompound writeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        int[] xArr = new int[labyrinth.size()];
        int[] zArr = new int[labyrinth.size()];
        byte[] dArr = new byte[labyrinth.size()];
        int i = 0;
        for (java.util.Map.Entry<CellLoc, Short> entry : labyrinth.entrySet()) {
            xArr[i] = entry.getKey().x;
            zArr[i] = entry.getKey().z;
            dArr[i] = entry.getValue().byteValue();
            i++;
        }
        nbt.setIntArray("xArr", xArr);
        nbt.setIntArray("zArr", zArr);
        nbt.setByteArray("dArr", dArr);
        return nbt;
    }

    public static void loadMaze(World world) {
        // Maze persistence via WorldSavedData in Phase 7.4
    }

    public static void saveMaze(World world) {
        // Maze persistence via WorldSavedData in Phase 7.4
    }

    public static boolean mazesInRange(int cx, int cz, int range, int min) {
        int count = 0;
        for (int x = cx - range; x <= cx + range; x++) {
            for (int z = cz - range; z <= cz + range; z++) {
                Cell cell = getFromHashMap(new CellLoc(x, z));
                if (cell != null && cell.feature != 0) {
                    count++;
                    if (count >= min) return true;
                }
            }
        }
        return false;
    }

    public static void generateEldritch(World world, java.util.Random rand, int cx, int cz) {
        // Stub: will generate maze rooms in Phase 7.4
    }
}
