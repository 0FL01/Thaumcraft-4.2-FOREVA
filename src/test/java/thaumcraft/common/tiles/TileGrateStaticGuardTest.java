package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileGrateStaticGuardTest {

    @Test
    public void tileGrateShouldKeepSingleSlotSidedInputContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileGrate.java");

        assertTrue(source.contains("public class TileGrate extends TileThaumcraft implements IInventory, ISidedInventory"));
        assertTrue(source.contains("public int getSizeInventory()"));
        assertTrue(source.contains("return 1;"));
        assertTrue(source.contains("return ItemStack.EMPTY;"));
        assertTrue(source.contains("if (isOpenInputMeta() && side == EnumFacing.UP)"));
        assertTrue(source.contains("return new int[]{0};"));
        assertTrue(source.contains("return isOpenInputMeta() && direction == EnumFacing.UP;"));
        assertTrue(source.contains("return this.world.getBlockState(this.pos).getValue(BlockMetalDevice.TYPE) == 5;"));
    }

    @Test
    public void tileGrateShouldKeepEntityItemGrateSpawnFlow() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileGrate.java");

        assertTrue(source.contains("EntityItemGrate item = new EntityItemGrate("));
        assertTrue(source.contains("this.pos.getX() + 0.5D"));
        assertTrue(source.contains("this.pos.getY() + 0.6D"));
        assertTrue(source.contains("this.pos.getZ() + 0.5D"));
        assertTrue(source.contains("stack.copy()"));
        assertTrue(source.contains("item.motionY = -0.1D;"));
        assertTrue(source.contains("item.motionX = 0.0D;"));
        assertTrue(source.contains("item.motionZ = 0.0D;"));
    }

    @Test
    public void entityItemGrateShouldKeepReferenceConstructors() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/EntityItemGrate.java");

        assertTrue(source.contains("public EntityItemGrate(World world)"));
        assertTrue(source.contains("public EntityItemGrate(World world, double x, double y, double z, ItemStack stack)"));
        assertTrue(source.contains("super(world, x, y, z, stack);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
