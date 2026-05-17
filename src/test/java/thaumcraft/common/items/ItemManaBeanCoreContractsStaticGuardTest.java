package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemManaBeanCoreContractsStaticGuardTest {

    @Test
    public void manaBeanKeepsReferenceUseAndAspectContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemManaBean.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemManaBean must keep essentia aspect container contracts",
                source.contains("implements IEssentiaContainerItem")
                        && source.contains("setAspects(stack, new AspectList().add(getRandomDisplayAspect(), 1));")
                        && source.contains("aspects.readFromNBT(itemstack.getTagCompound())"));
        assertTrue("ItemManaBean must keep reference-shaped use-result semantics",
                source.contains("facing != EnumFacing.DOWN")
                        && source.contains("return EnumActionResult.PASS;")
                        && source.contains("if (!world.isAirBlock(place))")
                        && source.contains("return EnumActionResult.SUCCESS;")
                        && source.contains("world.setBlockState(place, ConfigBlocks.blockManaPod.getDefaultState(), 3);"));
        assertTrue("ItemManaBean must keep magical-biome and support-block gates",
                source.contains("BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.MAGICAL)")
                        && source.contains("Blocks.LOG")
                        && source.contains("Blocks.LOG2")
                        && source.contains("ConfigBlocks.blockMagicalLog"));
        assertTrue("ItemManaBean tooltip/localization contracts must remain available",
                source.contains("new TextComponentTranslation(\"tc.aspect.unknown\")")
                        && lang.contains("item.thaumcraft.mana_bean.name=Mana Bean")
                        && lang.contains("tc.aspect.unknown=Unknown Aspect"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
