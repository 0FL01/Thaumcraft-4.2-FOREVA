package thaumcraft.common.config;

import net.minecraft.block.Block;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.blocks.BlockCrystal;

public class ConfigBlocks {

    // Block instances
    public static BlockJar blockJar;
    public static BlockCrystal blockCrystal;

    public static void init() {
        blockJar = (BlockJar) new BlockJar()
                .setRegistryName("thaumcraft", "jar")
                .setTranslationKey("thaumcraft.jar");

        blockCrystal = (BlockCrystal) new BlockCrystal()
                .setRegistryName("thaumcraft", "crystal")
                .setTranslationKey("thaumcraft.crystal");
    }

    public static Block[] getAllBlocks() {
        return new Block[]{
                blockJar,
                blockCrystal
        };
    }
}
