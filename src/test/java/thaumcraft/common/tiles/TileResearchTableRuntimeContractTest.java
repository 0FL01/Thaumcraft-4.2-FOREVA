package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TileResearchTableRuntimeContractTest {

    @Test
    public void tileResearchTableKeepsReferenceRuntimeGuardsAndBonusFeedback() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileResearchTable.java");

        assertTrue("TileResearchTable should keep the reference tile-identity and player-distance usability guard",
                source.contains("return this.world.getTileEntity(this.pos) == this")
                        && source.contains("player.getDistanceSq(")
                        && source.contains("<= 64.0D;"));

        assertTrue("TileResearchTable bonus aspect persistence should stay one-bit per aspect like the original note table save path",
                source.contains("private void grantBonusAspect(Aspect aspect)")
                        && source.contains("if (aspect == null || this.bonusAspects.getAmount(aspect) > 0)")
                        && source.contains("this.bonusAspects.merge(aspect, 1);")
                        && !source.contains("tag.setInteger(\"amount\", amount);"));

        assertTrue("TileResearchTable should restore orb feedback on free placement and aspect refund branches for RESEARCHER1/2",
                source.contains("SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP")
                        && source.contains("boolean refundSkip = researcher2 && this.world.rand.nextFloat() < 0.1F;")
                        && source.contains("((researcher1 && chance < 0.25F) || (researcher2 && chance < 0.5F))"));

        assertTrue("TileResearchTable bonus recalc should match reference earth/water environment cues",
                source.contains("mat == Material.GROUND || block == ConfigBlocks.blockCustomOre && md == 4")
                        && source.contains("} else if (mat == Material.WATER) {")
                        && source.contains("this.world.rand.nextInt(15) == 0")
                        && source.contains("} else if (block == ConfigBlocks.blockCustomOre && md == 3) {")
                        && source.contains("this.world.rand.nextInt(20) == 0"));
    }

    @Test
    public void duplicateResearchNotesStayStackedInTheTable() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileResearchTable.java");

        assertTrue(source.contains("data.copies++;"));
        assertTrue(source.contains("ResearchManager.updateData(notesStack, data);"));
        assertTrue(source.contains("notesStack.grow(1);"));
        assertTrue(source.contains("setInventorySlotContents(1, notesStack);"));
        assertFalse(source.contains("player.inventory.addItemStackToInventory(duplicate)"));
        assertFalse(source.contains("player.dropItem(duplicate, false)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
