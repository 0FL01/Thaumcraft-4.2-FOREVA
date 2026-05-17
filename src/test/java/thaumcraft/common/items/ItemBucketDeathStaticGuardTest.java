package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemBucketDeathStaticGuardTest {

    @Test
    public void bucketDeathKeepsFluidPlacementAndSurvivalBucketReturnContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemBucketDeath.java");

        assertTrue("Bucket of Death must place full-level death fluid from ConfigBlocks.blockFluidDeath",
                source.contains("ConfigBlocks.blockFluidDeath.getDefaultState()")
                        && source.contains(".withProperty(BlockFluidBase.LEVEL, BlockFluidDeath.FULL_LEVEL)")
                        && source.contains("world.setBlockState(pos, death, 3);"));
        assertTrue("Bucket of Death must keep creative bypass and survival empty-bucket return",
                source.contains("if (player.capabilities.isCreativeMode)")
                        && source.contains("new ItemStack(Items.BUCKET)"));
        assertTrue("Bucket of Death no-op branches must return PASS rather than hard FAIL",
                source.contains("return new ActionResult<>(EnumActionResult.PASS, stack);")
                        && !source.contains("new ActionResult<>(EnumActionResult.FAIL, stack)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
