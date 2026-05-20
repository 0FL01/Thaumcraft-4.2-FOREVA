package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockStoneDeviceContractTest {

    @Test
    public void blockStoneDeviceShouldKeepMetaTileDropAndSupportContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockstonedevice.json");

        assertTrue("creative set should expose wand pedestal focus meta 8 and not regress to infusion pillar meta 3 item exposure",
                source.contains("list.add(new ItemStack(this, 1, 8)); // wand pedestal focus")
                        && !source.contains("list.add(new ItemStack(this, 1, 3)); // infusion pillar"));

        assertTrue("tile routing should keep real tile metas and return null for support-only metas",
                source.contains("return meta == 0 || meta == 1 || meta == 2 || meta == 3 || meta == 5")
                        && source.contains("|| meta == 9 || meta == 10 || meta == 11 || meta == 12 || meta == 13 || meta == 14;")
                        && source.contains("if (meta == 3) return new TileInfusionPillar();")
                        && source.contains("if (meta == 12) return new TileSpa();")
                        && source.contains("return null;"));

        assertTrue("damage drops should keep the original infusion-pillar support remap",
                source.contains("if (meta == 3) return 7;")
                        && source.contains("if (meta == 4) return 6;"));

        assertTrue("neighbor support checks should keep pedestal, wand pedestal, and infusion-pillar structural contracts",
                source.contains("if (type == 1) {")
                        && source.contains("InventoryUtils.dropItems(worldIn, pos.getX(), pos.getY(), pos.getZ());")
                        && source.contains("} else if (type == 5) {")
                        && source.contains("above.getBlock() != this || above.getValue(TYPE) != 8")
                        && source.contains("} else if (type == 3) {")
                        && source.contains("above.getBlock() != this || above.getValue(TYPE) != 4")
                        && source.contains("} else if (type == 4) {")
                        && source.contains("below.getBlock() != this || below.getValue(TYPE) != 3"));

        assertTrue("break and comparator contracts should keep infusion-matrix blast, inventory comparators, and wand fill output",
                source.contains("if (te instanceof TileInfusionMatrix && ((TileInfusionMatrix) te).crafting) {")
                        && source.contains("worldIn.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2.0F, true);")
                        && source.contains("return Container.calcRedstoneFromInventory((IInventory) te);")
                        && source.contains("wand.getAllVis(stack).visSize()")
                        && source.contains("return MathHelper.floor(fill * 14.0F) + 1;"));

        assertTrue("light and blockstate contracts should keep infusion-matrix glow and wand-focus shell routing",
                source.contains("} else if (meta == 2) {")
                        && source.contains("return 10;")
                        && blockstate.contains("\"type=8\": { \"model\": \"thaumcraft:blockstonedevice_8\" }"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
