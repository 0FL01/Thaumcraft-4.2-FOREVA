package thaumcraft.common.config;

import net.minecraft.init.Bootstrap;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ConfigItemsThaumiumMaterialRuntimeTest {
    @BeforeClass
    public static void bootstrapItems() {
        Bootstrap.register();
        if (ConfigItems.itemSwordThaumium == null) ConfigItems.init();
    }

    @Test
    public void coreThaumiumToolsUseTheTc4ApiMaterial() {
        assertSame(ThaumcraftApi.toolMatThaumium, ConfigItems.TOOLMAT_THAUMIUM);
        assertEquals(3, ConfigItems.TOOLMAT_THAUMIUM.getHarvestLevel());
        assertEquals(400, ConfigItems.TOOLMAT_THAUMIUM.getMaxUses());
        assertEquals(7.0F, ConfigItems.TOOLMAT_THAUMIUM.getEfficiency(), 0.0F);
        assertEquals(2.0F, ConfigItems.TOOLMAT_THAUMIUM.getAttackDamage(), 0.0F);
        assertEquals(22, ConfigItems.TOOLMAT_THAUMIUM.getEnchantability());

        assertEquals(400, ConfigItems.itemSwordThaumium.getMaxDamage());
        assertEquals(400, ConfigItems.itemPickThaumium.getMaxDamage());
        assertEquals(400, ConfigItems.itemAxeThaumium.getMaxDamage());
        assertEquals(400, ConfigItems.itemShovelThaumium.getMaxDamage());
        assertEquals(400, ConfigItems.itemHoeThaumium.getMaxDamage());
        assertEquals(22, ConfigItems.itemSwordThaumium.getItemEnchantability());
        assertEquals(22, ConfigItems.itemPickThaumium.getItemEnchantability());
        assertEquals(5, ConfigItems.itemHoeThaumium.getItemEnchantability());
    }
}
