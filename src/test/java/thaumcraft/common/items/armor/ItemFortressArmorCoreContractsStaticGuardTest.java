package thaumcraft.common.items.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemFortressArmorCoreContractsStaticGuardTest {

    @Test
    public void fortressArmorKeepsReferenceRarityRepairMaskAndGogglesContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/items/armor/ItemFortressArmor.java");
        String lang = readFile("src/main/resources/assets/thaumcraft/lang/en_us.lang");

        assertTrue("ItemFortressArmor must keep fortress interface surface and rare rarity contract",
                source.contains("implements IRepairable, IRunicArmor, ISpecialArmor, IGoggles, IRevealer")
                        && source.contains("return EnumRarity.RARE;"));
        assertTrue("ItemFortressArmor must keep thaumium repair key contract",
                source.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && source.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemFortressArmor must keep set+mask armor-ratio bonus contract",
                source.contains("double set = 0.875;")
                        && source.contains("for (int a = 1; a < 4; a++)")
                        && source.contains("set += 0.125;")
                        && source.contains("piece.getTagCompound().hasKey(\"mask\")")
                        && source.contains("set += 0.05;")
                        && source.contains("ratio *= set;"));
        assertTrue("ItemFortressArmor must keep goggles NBT gates for revealer hooks",
                source.contains("return hasGogglesTag(itemstack);")
                        && source.contains("tag.hasKey(\"goggles\")"));
        assertTrue("ItemFortressArmor must keep goggles/mask tooltip contracts",
                source.contains("public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)")
                        && source.contains("I18n.translateToLocal(\"item.ItemGoggles.name\")")
                        && source.contains("I18n.translateToLocal(\"item.HelmetFortress.mask.\" + tag.getInteger(\"mask\"))"));
        assertTrue("Fortress tooltip localization keys must exist in en_us.lang",
                lang.contains("item.ItemGoggles.name=Goggles of Revealing")
                        && lang.contains("item.HelmetFortress.mask.0=Grinning Devil")
                        && lang.contains("item.HelmetFortress.mask.1=Angry Ghost")
                        && lang.contains("item.HelmetFortress.mask.2=Sipping Fiend"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
