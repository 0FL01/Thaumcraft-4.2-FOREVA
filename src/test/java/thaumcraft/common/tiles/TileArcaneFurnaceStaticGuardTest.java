package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileArcaneFurnaceStaticGuardTest {

    @Test
    public void tileArcaneFurnaceShouldKeepCoreSmeltEjectAndSpeedContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneFurnace.java");

        assertTrue(source.contains("private final ItemStack[] furnaceItemStacks = new ItemStack[32];"));
        assertTrue(source.contains("public int furnaceCookTime = 0;"));
        assertTrue(source.contains("public int furnaceMaxCookTime = 0;"));
        assertTrue(source.contains("public int speedyTime = 0;"));
        assertTrue(source.contains("public int facingX = -5;"));
        assertTrue(source.contains("public int facingZ = -5;"));
        assertTrue(source.contains("this.speedyTime = VisNetHandler.drainVis("));
        assertTrue(source.contains("Aspect.FIRE, 5"));
        assertTrue(source.contains("this.ejectItem(smelt.copy(), source);"));
        assertTrue(source.contains("this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 3, 0);"));
        assertTrue(source.contains("private int calcCookTime()"));
        assertTrue(source.contains("(this.speedyTime > 0 ? 80 : 140) - 20 * this.getBellows()"));
        assertTrue(source.contains("ItemStack bonus = ThaumcraftApi.getSmeltingBonus(furnaceItemStack);"));
        assertTrue(source.contains("EntityXPOrb.getXPSplit(xpAmount)"));
    }

    @Test
    public void tileArcaneFurnaceShouldKeepNozzleFacingAndNbtContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneFurnace.java");

        assertTrue(source.contains("this.world.getBlockState(this.pos.west()).getBlock() == ConfigBlocks.blockArcaneFurnace"));
        assertTrue(source.contains("ConfigBlocks.blockArcaneFurnace.getMetaFromState(this.world.getBlockState(this.pos.west())) == 10"));
        assertTrue(source.contains("this.world.getBlockState(this.pos.east()).getBlock() == ConfigBlocks.blockArcaneFurnace"));
        assertTrue(source.contains("ConfigBlocks.blockArcaneFurnace.getMetaFromState(this.world.getBlockState(this.pos.east())) == 10"));
        assertTrue(source.contains("this.world.getBlockState(this.pos.north()).getBlock() == ConfigBlocks.blockArcaneFurnace"));
        assertTrue(source.contains("ConfigBlocks.blockArcaneFurnace.getMetaFromState(this.world.getBlockState(this.pos.north())) == 10 ? -1 : 1;"));
        assertTrue(source.contains("NBTTagList list = nbt.getTagList(\"Items\", Constants.NBT.TAG_COMPOUND);"));
        assertTrue(source.contains("nbt.setShort(\"CookTime\", (short) this.furnaceCookTime);"));
        assertTrue(source.contains("nbt.setShort(\"SpeedyTime\", (short) this.speedyTime);"));
        assertTrue(source.contains("entry.setByte(\"Slot\", (byte) i);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
