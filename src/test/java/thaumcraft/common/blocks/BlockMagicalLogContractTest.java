package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockMagicalLogContractTest {

    @Test
    public void magicalLogUsesAxisVariantsAndSilverwoodMetaContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockMagicalLog.java");
        String blockstate = readFile("src/main/resources/assets/thaumcraft/blockstates/blockmagicallog.json");

        assertTrue("BlockMagicalLog creative inventory should expose greatwood and silverwood metas 0/1",
                source.contains("new ItemStack(this, 1, 0)")
                        && source.contains("new ItemStack(this, 1, 1)"));
        assertTrue("BlockMagicalLog knot and legacy alias should drop silverwood meta 1",
                source.contains("if (type == 2 || type == 3) return 1;"));
        assertTrue("BlockMagicalLog should light only TC4 metadata types 2 and 3",
                source.contains("return (type & 2) == 2 ? 7"));
        assertTrue("Silverwood knots should release the TC4 node essence count when harvested",
                source.contains("tile instanceof INode")
                        && source.contains("BlockAiry.getNodeEssenceDropCount")
                        && source.contains("new AspectList().add(aspect, 2)"));
        assertTrue("Block magical log blockstate must use 1.12 axis property names and silverwood type 1 routing",
                blockstate.contains("\"axis=z,type=1\"")
                        && blockstate.contains("\"thaumcraft:blockmagicallog_silverwood\"")
                        && !blockstate.contains("log_axis="));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
