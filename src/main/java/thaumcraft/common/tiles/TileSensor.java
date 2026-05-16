package thaumcraft.common.tiles;

import net.minecraft.world.WorldServer;
import thaumcraft.api.TileThaumcraft;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class TileSensor extends TileThaumcraft {
    public static final WeakHashMap<WorldServer, ArrayList<Integer[]>> noteBlockEvents = new WeakHashMap<>();
}
