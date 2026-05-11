package thaumcraft.common.config;

import net.minecraft.block.Block;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.blocks.BlockCrystal;
import thaumcraft.common.blocks.BlockTable;
import thaumcraft.common.blocks.BlockStoneDevice;

public class ConfigBlocks {

    // Block instances
    public static BlockJar blockJar;
    public static BlockCrystal blockCrystal;
    public static BlockTable blockTable;
    public static BlockStoneDevice blockStoneDevice;

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
    }

    public static Block[] getAllBlocks() {
        return new Block[]{
                blockJar,
                blockCrystal,
                blockTable,
                blockStoneDevice
        };
    }
}
