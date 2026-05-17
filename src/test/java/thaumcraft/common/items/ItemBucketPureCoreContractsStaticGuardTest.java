package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemBucketPureCoreContractsStaticGuardTest {

    @Test
    public void bucketPureKeepsReferenceUseAndPlacementContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemBucketPure.java");

        assertTrue("ItemBucketPure must keep right-click raytrace and neutral no-op return contracts",
                source.contains("RayTraceResult mop = this.rayTrace(world, player, true);")
                        && source.contains("if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)")
                        && source.contains("return new ActionResult<>(EnumActionResult.PASS, stack);"));
        assertTrue("ItemBucketPure must keep creative/survival placement result contracts",
                source.contains("if (tryPlaceContainedLiquid(world, target))")
                        && source.contains("if (player.capabilities.isCreativeMode)")
                        && source.contains("new ItemStack(Items.BUCKET)"));
        assertTrue("ItemBucketPure must keep pure-fluid placement state contracts",
                source.contains("ConfigBlocks.blockFluidPure.getDefaultState()")
                        && source.contains("withProperty(BlockFluidBase.LEVEL, BlockFluidPure.SOURCE_LEVEL)")
                        && source.contains("world.setBlockState(pos, pure, 3);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
