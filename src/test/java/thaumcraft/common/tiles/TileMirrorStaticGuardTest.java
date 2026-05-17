package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileMirrorStaticGuardTest {

    @Test
    public void tileMirrorShouldKeepLinkRestoreTransportAndInstabilityContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileMirror.java");

        assertTrue(source.contains("public boolean linked = false;"));
        assertTrue(source.contains("public int instability;"));
        assertTrue(source.contains("private final ArrayList<ItemStack> outputStacks = new ArrayList<>();"));
        assertTrue(source.contains("if (!this.isLinkValidSimple())"));
        assertTrue(source.contains("this.restoreLink();"));
        assertTrue(source.contains("this.checkInstability();"));
        assertTrue(source.contains("this.eject();"));
        assertTrue(source.contains("((TileMirror) target).addStack(items.copy());"));
        assertTrue(source.contains("this.addInstability(null, items.getCount());"));
        assertTrue(source.contains("this.world.addBlockEvent(this.pos, ConfigBlocks.blockMirror, 1, 0);"));
        assertTrue(source.contains("VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), Aspect.ORDER, Math.min(this.instability, 1))"));
        assertTrue(source.contains("nbt.setBoolean(\"linked\", this.linked);"));
        assertTrue(source.contains("nbt.setInteger(\"linkDim\", this.linkDim);"));
        assertTrue(source.contains("nbt.setInteger(\"instability\", this.instability);"));
        assertTrue(source.contains("NBTTagList list = nbt.getTagList(\"Items\", 10);"));
    }

    @Test
    public void tileMirrorShouldKeepFacingBasedSpawnVectorAndInventorySinkContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileMirror.java");

        assertTrue(source.contains("EnumFacing face = this.getFacing();"));
        assertTrue(source.contains("this.pos.getX() + 0.5D - face.getXOffset() * 0.3D"));
        assertTrue(source.contains("this.pos.getY() + 0.5D - face.getYOffset() * 0.3D"));
        assertTrue(source.contains("this.pos.getZ() + 0.5D - face.getZOffset() * 0.3D"));
        assertTrue(source.contains("ie2.motionX = face.getXOffset() * 0.15F;"));
        assertTrue(source.contains("ie2.motionY = face.getYOffset() * 0.15F;"));
        assertTrue(source.contains("ie2.motionZ = face.getZOffset() * 0.15F;"));
        assertTrue(source.contains("public void setInventorySlotContents(int index, ItemStack stack)"));
        assertTrue(source.contains("target instanceof TileMirror"));
        assertTrue(source.contains("this.spawnItem(stack.copy());"));
    }

    @Test
    public void blockMirrorShouldKeepLinkedDropAndCollisionContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockMirror.java");

        assertTrue(source.contains("if (tile instanceof TileMirror)"));
        assertTrue(source.contains("((TileMirror) tile).invalidateLink();"));
        assertTrue(source.contains("if (state.getValue(TYPE) < 6 && entityIn instanceof EntityItem"));
        assertTrue(source.contains("((TileMirror) tile).transport((EntityItem) entityIn);"));
        assertTrue(source.contains("drop.setItemDamage(1);"));
        assertTrue(source.contains("drop.setItemDamage(7);"));
        assertTrue(source.contains("tag.setInteger(\"linkX\", mirror.linkX);"));
        assertTrue(source.contains("tag.setInteger(\"linkDim\", mirror.linkDim);"));
        assertTrue(source.contains("worldIn.isSideSolid(pos.offset(side.getOpposite()), side, false)"));
        assertTrue(source.contains("this.dropBlockAsItem(worldIn, pos, state, 0);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
