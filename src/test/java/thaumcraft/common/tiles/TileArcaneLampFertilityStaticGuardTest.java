package thaumcraft.common.tiles;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileArcaneLampFertilityStaticGuardTest {

    @Test
    public void tileArcaneLampFertilityShouldKeepEssentiaBreedingContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneLampFertility.java");

        assertTrue(source.contains("public class TileArcaneLampFertility extends TileThaumcraft implements ITickable, IEssentiaTransport"));
        assertTrue(source.contains("public int charges = 0;"));
        assertTrue(source.contains("if (this.charges < 4 && this.drawEssentia())"));
        assertTrue(source.contains("if (this.charges > 1 && ++this.count % 300 == 0)"));
        assertTrue(source.contains("this.world.getEntitiesWithinAABB(EntityAnimal.class, new AxisAlignedBB(this.pos).grow(7.0D))"));
        assertTrue(source.contains("animal.getGrowingAge() != 0 || animal.isInLove()"));
        assertTrue(source.contains("sameClass.size() > 7"));
        assertTrue(source.contains("this.charges -= 2;"));
        assertTrue(source.contains("other.setInLove(null);"));
        assertTrue(source.contains("mate.setInLove(null);"));
        assertTrue(source.contains("ic.takeEssentia(Aspect.LIFE, 1, this.facing.getOpposite()) == 1"));
        assertTrue(source.contains("return face == this.facing ? 128 - this.charges * 10 : 0;"));
    }

    @Test
    public void blockMetalDeviceShouldKeepFertilityLampRuntimeWiring() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
        String configBlocks = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");
        String blockstate = readFile("src/main/resources/assets/thaumcraft/blockstates/blockmetaldevice.json");
        String model = readFile("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_13.json");

        assertTrue(source.contains("if (meta == 13) return new TileArcaneLampFertility();"));
        assertTrue(source.contains("list.add(new ItemStack(this, 1, 13)); // fertility lamp"));
        assertTrue(source.contains("if (meta == 13)"));
        assertTrue(source.contains("TileArcaneLampFertility lamp = (TileArcaneLampFertility) te;"));
        assertTrue(source.contains("meta == 7 || meta == 8 || meta == 13"));
        assertTrue(source.contains("meta == 1 || meta == 2 || meta == 7 || meta == 8 || meta == 10 || meta == 11 || meta == 13 || meta == 14"));
        assertTrue(source.contains("((TileArcaneLampFertility) te).charges > 0 ? 15 : 8"));
        assertTrue(source.contains("VisNetHandler.isNodeValid(((TileVisRelay) te).getParent()) ? 10 : 2"));
        assertTrue(configBlocks.contains("new TileRegistration(TileArcaneLampFertility.class, \"TileArcaneLampFertility\")"));
        assertTrue(blockstate.contains("\"type=13\": { \"model\": \"thaumcraft:blockmetaldevice_13\" }"));
        assertTrue(model.contains("\"thaumcraft:blocks/lamp_fert_side\""));
        assertTrue(model.contains("\"thaumcraft:blocks/lamp_fert_top\""));
        assertTrue(model.contains("\"from\": [4, 2, 4]"));
        assertTrue(model.contains("\"to\": [12, 14, 12]"));
        assertTrue(model.contains("\"down\": { \"texture\": \"#top\" }"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
