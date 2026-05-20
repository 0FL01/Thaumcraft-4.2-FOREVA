package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileArcaneLampGrowthStaticGuardTest {

    @Test
    public void tileArcaneLampGrowthShouldKeepEssentiaReserveAndPlantUpdateContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneLampGrowth.java");

        assertTrue(source.contains("public class TileArcaneLampGrowth extends TileThaumcraft implements ITickable, IEssentiaTransport"));
        assertTrue(source.contains("private boolean reserve = false;"));
        assertTrue(source.contains("public int charges = -1;"));
        assertTrue(source.contains("if (this.charges <= 0)"));
        assertTrue(source.contains("if (this.reserve)"));
        assertTrue(source.contains("this.charges = 100;"));
        assertTrue(source.contains("if (!this.reserve && this.drawEssentia())"));
        assertTrue(source.contains("if (this.charges == 0)"));
        assertTrue(source.contains("if (this.charges > 0)"));
        assertTrue(source.contains("this.updatePlant();"));
        assertTrue(source.contains("Collections.shuffle(this.checklist, this.world.rand);"));
        assertTrue(source.contains("CropUtils.isGrownCrop(this.world, target)"));
        assertTrue(source.contains("CropUtils.doesLampGrow(this.world, target)"));
        assertTrue(source.contains("this.world.scheduleUpdate(target, this.world.getBlockState(target).getBlock(), 1);"));
        assertTrue(source.contains("return face == this.facing && (!this.reserve || this.charges <= 0) ? 128 : 0;"));
    }

    @Test
    public void tileArcaneLampGrowthShouldKeepOrientationAndEssentiaDrawContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneLampGrowth.java");

        assertTrue(source.contains("this.facing = EnumFacing.byIndex(nbt.getInteger(\"orientation\"));"));
        assertTrue(source.contains("nbt.setInteger(\"orientation\", this.facing.getIndex());"));
        assertTrue(source.contains("nbt.setBoolean(\"reserve\", this.reserve);"));
        assertTrue(source.contains("nbt.setInteger(\"charges\", this.charges);"));
        assertTrue(source.contains("if (++this.drawDelay % 5 != 0)"));
        assertTrue(source.contains("ThaumcraftApiHelper.getConnectableTile(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.facing)"));
        assertTrue(source.contains("ic.takeEssentia(Aspect.PLANT, 1, this.facing.getOpposite()) == 1"));
    }

    @Test
    public void blockMetalDeviceShouldKeepArcaneLampGrowthSupportCheck() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");

        assertTrue(source.contains("if (meta == 8)"));
        assertTrue(source.contains("te instanceof TileArcaneLampGrowth"));
        assertTrue(source.contains("TileArcaneLampGrowth lamp = (TileArcaneLampGrowth) te;"));
        assertTrue(source.contains("worldIn.isAirBlock(pos.offset(lamp.facing))"));
        assertTrue(source.contains("worldIn.destroyBlock(pos, true);"));
    }

    @Test
    public void cropUtilsShouldKeepLampBlacklistContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/utils/CropUtils.java");

        assertTrue(source.contains("public static ArrayList<String> lampBlacklist = new ArrayList<>();"));
        assertTrue(source.contains("public static void blacklistLamp(ItemStack stack, int meta)"));
        assertTrue(source.contains("public static boolean doesLampGrow(World world, BlockPos pos)"));
        assertTrue(source.contains("return !lampBlacklist.contains(block.getTranslationKey() + meta);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
