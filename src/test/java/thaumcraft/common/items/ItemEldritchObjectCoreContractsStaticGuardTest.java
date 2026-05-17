package thaumcraft.common.items;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemEldritchObjectCoreContractsStaticGuardTest {

    @Test
    public void eldritchObjectKeepsReferenceSubtypeAndNodeMutationContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/ItemEldritchObject.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemEldritchObject must keep metadata-based translation keys and 5 subtype exposure",
                source.contains("private static final int META_COUNT = 5;")
                        && source.contains("return super.getTranslationKey() + \".\" + stack.getItemDamage();")
                        && source.contains("for (int i = 0; i < META_COUNT; i++)"));
        assertTrue("ItemEldritchObject must keep crimson-rites research unlock contract",
                source.contains("stack.getItemDamage() == META_CRIMSON_RITES")
                        && source.contains("ResearchManager.addResearch(player, \"CRIMSON\");")
                        && source.contains("TCSounds.LEARN"));
        assertTrue("ItemEldritchObject must keep primordial-pearl node mutation/explosion baseline",
                source.contains("stack.getItemDamage() == META_ELDRITCH_OBJECT_3")
                        && source.contains("node.setNodeVisBase(aspect, (short) base);")
                        && source.contains("node.addToContainer(aspect, 1);")
                        && source.contains("world.createExplosion(null"));
        assertTrue("ItemEldritchObject must keep obelisk placer baseline on upward use",
                source.contains("side == EnumFacing.UP && stack.getItemDamage() == META_OB_PLACER")
                        && source.contains("world.setBlockState(pos.up(1), ConfigBlocks.blockEldritch")
                        && source.contains("world.setBlockState(pos.up(7), ConfigBlocks.blockEldritch"));
        assertTrue("Eldritch object subtype localization keys must remain available",
                lang.contains("item.thaumcraft.eldritch_object.0.name=Eldritch Eye")
                        && lang.contains("item.thaumcraft.eldritch_object.1.name=Crimson Rites")
                        && lang.contains("item.thaumcraft.eldritch_object.2.name=Runed Tablet")
                        && lang.contains("item.thaumcraft.eldritch_object.3.name=Primordial Pearl")
                        && lang.contains("item.thaumcraft.eldritch_object.4.name=Eldritch Obelisk Placer"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
