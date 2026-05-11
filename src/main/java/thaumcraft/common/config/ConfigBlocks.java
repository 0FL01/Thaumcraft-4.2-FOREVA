package thaumcraft.common.config;

import net.minecraft.block.Block;
import thaumcraft.common.blocks.BlockJar;

public class ConfigBlocks {

    // Block instances
    public static BlockJar blockJar;

    public static void init() {
        blockJar = (BlockJar) new BlockJar()
                .setRegistryName("thaumcraft", "jar")
                .setTranslationKey("thaumcraft.jar");
    }

    public static Block[] getAllBlocks() {
        return new Block[]{
                blockJar
        };
    }
}
