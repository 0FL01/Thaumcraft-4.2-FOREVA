package thaumcraft.common.lib.utils;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

public class CropUtils {
    public static ArrayList<String> standardCrops = new ArrayList<>();
    public static ArrayList<String> clickableCrops = new ArrayList<>();
    public static ArrayList<String> stackedCrops = new ArrayList<>();
    public static ArrayList<String> lampBlacklist = new ArrayList<>();

    public static void addStandardCrop(ItemStack seed, int maxMeta) {
        Block block = Block.getBlockFromItem(seed.getItem());
        addStandardCrop(block, maxMeta);
    }

    public static void addStandardCrop(Block block, int maxMeta) {
        if (block == null) return;
        if (maxMeta == Short.MAX_VALUE) {
            for (int a = 0; a < 16; a++) standardCrops.add(block.getTranslationKey() + a);
        } else {
            standardCrops.add(block.getTranslationKey() + maxMeta);
        }
        if (block instanceof BlockCrops && maxMeta != 7) standardCrops.add(block.getTranslationKey() + "7");
    }

    public static void addStackedCrop(Object blockOrItem, int maxMeta) {
        Block block = null;
        if (blockOrItem instanceof Block) block = (Block) blockOrItem;
        else if (blockOrItem instanceof ItemStack) block = Block.getBlockFromItem(((ItemStack) blockOrItem).getItem());
        if (block == null) return;
        if (maxMeta == Short.MAX_VALUE) {
            for (int a = 0; a < 16; a++) stackedCrops.add(block.getTranslationKey() + a);
        } else {
            stackedCrops.add(block.getTranslationKey() + maxMeta);
        }
        if (block instanceof BlockCrops && maxMeta != 7) stackedCrops.add(block.getTranslationKey() + "7");
    }

    public static void blacklistLamp(ItemStack stack, int meta) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null) return;
        if (meta == Short.MAX_VALUE) {
            for (int a = 0; a < 16; ++a) {
                lampBlacklist.add(block.getTranslationKey() + a);
            }
        } else {
            lampBlacklist.add(block.getTranslationKey() + meta);
        }
    }

    public static boolean isGrownCrop(World world, BlockPos pos) {
        if (world.isAirBlock(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);

        // Vanilla crops
        if (block instanceof BlockCrops) return meta >= 7;
        if (block instanceof BlockStem) {
            // Check if stem is mature (connected to a fruit)
            for (EnumFacing f : net.minecraft.util.EnumFacing.VALUES) {
                if (f.getAxis() == net.minecraft.util.EnumFacing.Axis.Y) continue;
                BlockPos p = pos.offset(f);
                IBlockState s = world.getBlockState(p);
                if (s.getBlock() instanceof BlockMelon || s.getBlock() instanceof BlockPumpkin) return true;
            }
            return false;
        }
        if (block instanceof IGrowable) return !((IGrowable) block).canGrow(world, pos, state, world.isRemote);
        if (block == Blocks.NETHER_WART) return meta >= 3;
        if (block == Blocks.COCOA) return (meta & 0xC) >> 2 >= 2;

        // Check registered crops
        String key = block.getTranslationKey() + meta;
        return standardCrops.contains(key) || clickableCrops.contains(key) || stackedCrops.contains(key);
    }

    public static boolean doesLampGrow(World world, BlockPos pos) {
        if (world.isAirBlock(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        return !lampBlacklist.contains(block.getTranslationKey() + meta);
    }
}
