package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EldritchCrustBakedModelStaticGuardTest {

    @Test
    public void crustModelShouldExpandOnlyTowardSolidNeighbors() throws IOException {
        String block = read("src/main/java/thaumcraft/common/blocks/BlockEldritch.java");
        String model = read("src/main/java/thaumcraft/client/renderers/block/EldritchCrustBakedModel.java");
        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");

        assertTrue(block.contains("new IntUnlistedProperty(\"crust_neighbor_mask\", 0, 63)")
                && block.contains("new ExtendedBlockState(this,")
                && block.contains("if (meta < 4 || meta > 6)")
                && block.contains("neighbor.isSideSolid(worldIn, neighborPos, facing.getOpposite())")
                && block.contains("mask |= 1 << facing.getIndex();")
                && block.contains("withProperty(CRUST_NEIGHBOR_MASK, mask)"));

        assertTrue(model.contains("for (int mask = 0; mask < 64; mask++)")
                && model.contains("if (side == null)")
                && model.contains("this.getNeighborMask(state) * EnumFacing.values().length + side.getIndex()")
                && model.contains("return this.faceQuads.get(index);")
                && model.contains("EnumFacing.WEST) ? 0.0F : 2.0F")
                && model.contains("EnumFacing.DOWN) ? 0.0F : 2.0F")
                && model.contains("EnumFacing.NORTH) ? 0.0F : 2.0F")
                && model.contains("EnumFacing.EAST) ? 16.0F : 14.0F")
                && model.contains("EnumFacing.UP) ? 16.0F : 14.0F")
                && model.contains("EnumFacing.SOUTH) ? 16.0F : 14.0F"));

        assertTrue("The item mask must remain centered and BlockPart must provide vanilla cropped default UVs",
                model.contains("return 0;")
                        && model.contains("new BlockFaceUV(null, 0)")
                        && model.contains("new BlockPart(from, to, faces, null, true)"));

        assertTrue(registry.contains("replaceEldritchCrustModels(event);")
                && registry.contains("for (int meta = 4; meta <= 6; meta++)")
                && registry.contains("new ModelResourceLocation(\"thaumcraft:blockeldritch\", \"type=\" + meta)")
                && registry.contains("new EldritchCrustBakedModel(delegate)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
