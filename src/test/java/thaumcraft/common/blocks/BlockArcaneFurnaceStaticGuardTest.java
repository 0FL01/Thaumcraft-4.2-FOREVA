package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockArcaneFurnaceStaticGuardTest {

    @Test
    public void blockArcaneFurnaceShouldKeepMetadataTileAndCollisionContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockArcaneFurnace.java");

        assertTrue(source.contains("public static final PropertyInteger TYPE = PropertyInteger.create(\"type\", 0, 10);"));
        assertTrue(source.contains("return meta == 0 || meta == 2 || meta == 4 || meta == 5 || meta == 6 || meta == 8;"));
        assertTrue(source.contains("if (meta == 0) {"));
        assertTrue(source.contains("return new TileArcaneFurnace();"));
        assertTrue(source.contains("return new TileArcaneFurnaceNozzle();"));
        assertTrue(source.contains("if (meta == 0 || meta == 10) {"));
        assertTrue(source.contains("return 13;"));
        assertTrue(source.contains("if (meta == 0) {"));
        assertTrue(source.contains("pos.getY() + 0.25D"));
        assertTrue(source.contains("entityIn instanceof EntityItem"));
        assertTrue(source.contains("((TileArcaneFurnace) tile).addItemsToInventory(stack.copy())"));
        assertTrue(source.contains("entityIn.attackEntityFrom(net.minecraft.util.DamageSource.HOT_FLOOR, 3.0F);"));
        assertTrue(source.contains("entityIn.setFire(10);"));
    }

    @Test
    public void configBlocksShouldRegisterBlockArcaneFurnaceAndItem() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");

        assertTrue(source.contains("public static BlockArcaneFurnace blockArcaneFurnace;"));
        assertTrue(source.contains("blockArcaneFurnace = (BlockArcaneFurnace) new BlockArcaneFurnace()"));
        assertTrue(source.contains("legacyPath(\"blockArcaneFurnace\")"));
        assertTrue(source.contains("blockArcaneFurnace,"));
        assertTrue(source.contains("new BlockMetadataItem(blockArcaneFurnace)"));
        assertTrue(source.contains("setRegistryName(blockArcaneFurnace.getRegistryName())"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
