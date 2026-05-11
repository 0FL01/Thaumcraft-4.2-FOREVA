package thaumcraft.api;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemApi {
    public static ItemStack getItem(String itemString, int meta) {
        ItemStack item = null;
        try {
            String itemClass = "thaumcraft.common.config.ConfigItems";
            Object obj = Class.forName(itemClass).getField(itemString).get(null);
            if (obj instanceof Item) {
                item = new ItemStack((Item)obj, 1, meta);
            } else if (obj instanceof ItemStack) {
                item = (ItemStack)obj;
            }
        }
        catch (Exception ex) {
            FMLLog.warning((String)("[Thaumcraft] Could not retrieve item identified by: " + itemString), (Object[])new Object[0]);
        }
        return item;
    }

    public static ItemStack getBlock(String itemString, int meta) {
        ItemStack item = null;
        try {
            String itemClass = "thaumcraft.common.config.ConfigBlocks";
            Object obj = Class.forName(itemClass).getField(itemString).get(null);
            if (obj instanceof Block) {
                item = new ItemStack((Block)obj, 1, meta);
            } else if (obj instanceof ItemStack) {
                item = (ItemStack)obj;
            }
        }
        catch (Exception ex) {
            FMLLog.warning((String)("[Thaumcraft] Could not retrieve block identified by: " + itemString), (Object[])new Object[0]);
        }
        return item;
    }
}

