package thaumcraft.common.lib.utils;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.ArrayList;
import java.util.List;

public class CropUtils {
    public static ArrayList<String> standardCrops = new ArrayList<>();
    public static ArrayList<String> clickableCrops = new ArrayList<>();
    public static ArrayList<String> stackedCrops = new ArrayList<>();
    public static ArrayList<String> lampBlacklist = new ArrayList<>();

    public static void addStandardCrop(ItemStack seed, int maxMeta) {
        Block block = getCropBlock(seed);
        addStandardCrop(block, maxMeta);
    }

    public static void addStandardCrop(Block block, int maxMeta) {
        addCrop(standardCrops, block, maxMeta);
    }

    public static void addClickableCrop(ItemStack seed, int maxMeta) {
        addCrop(clickableCrops, getCropBlock(seed), maxMeta);
    }

    public static void addStackedCrop(ItemStack seed, int maxMeta) {
        addStackedCrop(getCropBlock(seed), maxMeta);
    }

    public static void addStackedCrop(Block block, int maxMeta) {
        addCrop(stackedCrops, block, maxMeta);
    }

    private static void addCrop(ArrayList<String> crops, Block block, int maxMeta) {
        if (block == null || block == Blocks.AIR) return;
        if (maxMeta == Short.MAX_VALUE) {
            for (int a = 0; a < 16; a++) crops.add(block.getTranslationKey() + a);
        } else {
            crops.add(block.getTranslationKey() + maxMeta);
        }
        if (block instanceof BlockCrops && maxMeta != 7) crops.add(block.getTranslationKey() + "7");
    }

    private static Block getCropBlock(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        return Block.getBlockFromItem(stack.getItem());
    }

    public static void processIMC(List<FMLInterModComms.IMCMessage> messages) {
        if (messages == null) return;
        for (FMLInterModComms.IMCMessage message : messages) {
            if (message == null || !message.isItemStackMessage()) continue;
            ItemStack crop = message.getItemStackValue();
            if (crop == null || crop.isEmpty()) continue;
            if ("harvestStandardCrop".equals(message.key)) {
                addStandardCrop(crop, crop.getMetadata());
            } else if ("harvestClickableCrop".equals(message.key)) {
                addClickableCrop(crop, crop.getMetadata());
            } else if ("harvestStackedCrop".equals(message.key)) {
                addStackedCrop(crop, crop.getMetadata());
            }
        }
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

        if (block instanceof BlockStem) return false;
        if (isStackedCrop(state)) {
            return world.getBlockState(pos.down()).getBlock() == block;
        }

        // Vanilla crops. Do not assume every BlockCrops implementation uses age 7:
        // beetroot and many addon crops use a smaller maximum age.
        if (block instanceof BlockCrops && ((BlockCrops) block).isMaxAge(state)) return true;
        if (block instanceof IGrowable && !((IGrowable) block).canGrow(world, pos, state, world.isRemote)) {
            return true;
        }
        if (block == Blocks.NETHER_WART) return meta >= 3;
        if (block == Blocks.COCOA) return (meta & 0xC) >> 2 >= 2;

        if (isStandardCrop(state) || isClickableCrop(state)) return true;
        return false;
    }

    public static boolean isStandardCrop(IBlockState state) {
        return state != null && standardCrops.contains(cropKey(state));
    }

    public static boolean isClickableCrop(IBlockState state) {
        return state != null && clickableCrops.contains(cropKey(state));
    }

    public static boolean isStackedCrop(IBlockState state) {
        return state != null && stackedCrops.contains(cropKey(state));
    }

    private static String cropKey(IBlockState state) {
        Block block = state.getBlock();
        return block.getTranslationKey() + block.getMetaFromState(state);
    }

    public static boolean doesLampGrow(World world, BlockPos pos) {
        if (world.isAirBlock(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        return !lampBlacklist.contains(block.getTranslationKey() + meta);
    }
}
