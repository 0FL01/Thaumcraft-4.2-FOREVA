package thaumcraft.common.items.equipment;

import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtificeEquipmentParityTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void elementalMaterialUsesTheTc4ApiContractAndHarvestsObsidian() throws Exception {
        Item.ToolMaterial material = ThaumcraftApi.toolMatElemental;
        assertEquals(3, material.getHarvestLevel());
        assertEquals(1500, material.getMaxUses());
        assertEquals(10.0F, material.getEfficiency(), 0.0F);
        assertEquals(3.0F, material.getAttackDamage(), 0.0F);
        assertEquals(18, material.getEnchantability());
        assertTrue(new ItemElementalPickaxe(material).canHarvestBlock(Blocks.OBSIDIAN.getDefaultState()));

        String config = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        assertTrue(config.contains("TOOLMAT_ELEMENTAL = thaumcraft.api.ThaumcraftApi.toolMatElemental;"));
    }

    @Test
    public void boneBowCurveAndOriginalDisplayNamesAreRestored() throws Exception {
        assertEquals(0.0F, ItemBowBone.getBoneArrowVelocity(0), 0.0F);
        assertEquals(5.0F / 12.0F, ItemBowBone.getBoneArrowVelocity(5), 0.0001F);
        assertEquals(1.0F, ItemBowBone.getBoneArrowVelocity(10), 0.0F);
        assertEquals(1.0F, ItemBowBone.getBoneArrowVelocity(19), 0.0F);

        String lang = read("src/main/resources/assets/thaumcraft/lang/en_us.lang");
        assertTrue(lang.contains("item.thaumcraft.sword_elemental.name=Sword of the Zephyr"));
        assertTrue(lang.contains("item.thaumcraft.pick_elemental.name=Pickaxe of the Core"));
        assertTrue(lang.contains("item.thaumcraft.axe_elemental.name=Axe of the Stream"));
        assertTrue(lang.contains("item.thaumcraft.shovel_elemental.name=Shovel of the Earthmover"));
        assertTrue(lang.contains("item.thaumcraft.hoe_elemental.name=Hoe of Growth"));
        assertTrue(lang.contains("item.thaumcraft.bow_bone.name=Bow of Bone"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
