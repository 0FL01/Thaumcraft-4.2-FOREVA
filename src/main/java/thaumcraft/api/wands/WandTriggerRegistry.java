package thaumcraft.api.wands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.wands.IWandTriggerManager;

public class WandTriggerRegistry {
    private static HashMap<String, HashMap<List, List>> triggers = new HashMap();
    private static final String DEFAULT = "default";

    public static void registerWandBlockTrigger(IWandTriggerManager manager, int event, Block block, int meta, String modid) {
        if (!triggers.containsKey(modid)) {
            triggers.put(modid, new HashMap());
        }
        HashMap<List, List> temp = triggers.get(modid);
        temp.put(Arrays.asList(block, meta), Arrays.asList(manager, event));
        triggers.put(modid, temp);
    }

    public static void registerWandBlockTrigger(IWandTriggerManager manager, int event, Block block, int meta) {
        WandTriggerRegistry.registerWandBlockTrigger(manager, event, block, meta, DEFAULT);
    }

    public static boolean hasTrigger(Block block, int meta) {
        for (String modid : triggers.keySet()) {
            HashMap<List, List> temp = triggers.get(modid);
            if (!temp.containsKey(Arrays.asList(block, meta)) && !temp.containsKey(Arrays.asList(block, -1))) continue;
            return true;
        }
        return false;
    }

    public static boolean hasTrigger(Block block, int meta, String modid) {
        if (!triggers.containsKey(modid)) {
            return false;
        }
        HashMap<List, List> temp = triggers.get(modid);
        return temp.containsKey(Arrays.asList(block, meta)) || temp.containsKey(Arrays.asList(block, -1));
    }

    public static boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side, Block block, int meta) {
        for (String modid : triggers.keySet()) {
            int event;
            IWandTriggerManager manager;
            boolean result;
            HashMap<List, List> temp = triggers.get(modid);
            List l = temp.get(Arrays.asList(block, meta));
            if (l == null) {
                l = temp.get(Arrays.asList(block, -1));
            }
            if (l == null || !(result = (manager = (IWandTriggerManager)l.get(0)).performTrigger(world, wand, player, x, y, z, side, event = ((Integer)l.get(1)).intValue()))) continue;
            return true;
        }
        return false;
    }

    public static boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side, Block block, int meta, String modid) {
        if (!triggers.containsKey(modid)) {
            return false;
        }
        HashMap<List, List> temp = triggers.get(modid);
        List l = temp.get(Arrays.asList(block, meta));
        if (l == null) {
            l = temp.get(Arrays.asList(block, -1));
        }
        if (l == null) {
            return false;
        }
        IWandTriggerManager manager = (IWandTriggerManager)l.get(0);
        int event = (Integer)l.get(1);
        return manager.performTrigger(world, wand, player, x, y, z, side, event);
    }
}

