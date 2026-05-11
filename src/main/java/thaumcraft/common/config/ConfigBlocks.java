package thaumcraft.common.config;

import net.minecraft.block.Block;
import thaumcraft.common.blocks.*;

public class ConfigBlocks {

    // Block instances
    public static BlockJar blockJar;
    public static BlockCrystal blockCrystal;
    public static BlockTable blockTable;
    public static BlockStoneDevice blockStoneDevice;
    public static BlockWoodenDevice blockWoodenDevice;
    public static BlockMetalDevice blockMetalDevice;
    public static BlockMagicalLog blockMagicalLog;

    public static void init() {
        blockJar = (BlockJar) new BlockJar()
                .setRegistryName("thaumcraft", "jar")
                .setTranslationKey("thaumcraft.jar");

        blockCrystal = (BlockCrystal) new BlockCrystal()
                .setRegistryName("thaumcraft", "crystal")
                .setTranslationKey("thaumcraft.crystal");

        blockTable = (BlockTable) new BlockTable()
                .setRegistryName("thaumcraft", "table")
                .setTranslationKey("thaumcraft.table");

        blockStoneDevice = (BlockStoneDevice) new BlockStoneDevice()
                .setRegistryName("thaumcraft", "stone_device")
                .setTranslationKey("thaumcraft.stone_device");

        blockWoodenDevice = (BlockWoodenDevice) new BlockWoodenDevice()
                .setRegistryName("thaumcraft", "wooden_device")
                .setTranslationKey("thaumcraft.wooden_device");

        blockMetalDevice = (BlockMetalDevice) new BlockMetalDevice()
                .setRegistryName("thaumcraft", "metal_device")
                .setTranslationKey("thaumcraft.metal_device");

        blockMagicalLog = (BlockMagicalLog) new BlockMagicalLog()
                .setRegistryName("thaumcraft", "magical_log")
                .setTranslationKey("thaumcraft.magical_log");
    }

    public static Block[] getAllBlocks() {
        return new Block[]{
                blockJar,
                blockCrystal,
                blockTable,
                blockStoneDevice,
                blockWoodenDevice,
                blockMetalDevice,
                blockMagicalLog
        };
    }
}
