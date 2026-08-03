package thaumcraft.common.integration.waila;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.translation.I18n;
import org.junit.Test;
import thaumcraft.api.entities.GolemIds;
import thaumcraft.api.entities.IGolemInfo;

import static org.junit.Assert.assertEquals;

public class GolemWailaTooltipTest {
    @Test
    public void noCoreAddsMaterialAndLocalizedNoneOnly() {
        List<String> tooltip = new ArrayList<>();
        tooltip.add("existing");

        GolemWailaTooltip.append(tooltip, new StubGolem(
                GolemIds.CORE_NONE, GolemIds.TYPE_WOOD, false));

        assertEquals(3, tooltip.size());
        assertEquals("existing", tooltip.get(0));
        assertEquals(I18n.translateToLocal("item.ItemGolemPlacer.1.name"), tooltip.get(1));
        assertEquals(I18n.translateToLocal("item.ItemGolemCore.name") + ": "
                + I18n.translateToLocal("tc.golem.core.none"), tooltip.get(2));
    }

    @Test
    public void advancedCoreAndDuplicateUpgradesStayWithinThreeAddedLines() {
        StubGolem golem = new StubGolem(GolemIds.CORE_GATHER, GolemIds.TYPE_THAUMIUM, true);
        golem.upgrades[GolemIds.UPGRADE_AIR] = 2;
        golem.upgrades[GolemIds.UPGRADE_ORDER] = 1;
        List<String> tooltip = new ArrayList<>();

        GolemWailaTooltip.append(tooltip, golem);

        assertEquals(3, tooltip.size());
        assertEquals(I18n.translateToLocal("item.ItemGolemPlacer.7.name") + " ["
                + I18n.translateToLocal("tc.adv") + "]", tooltip.get(0));
        assertEquals(I18n.translateToLocal("item.ItemGolemCore.name") + ": "
                + I18n.translateToLocal("item.ItemGolemCore.2.name"), tooltip.get(1));
        assertEquals(I18n.translateToLocal("item.ItemGolemUpgrade.0.name") + " \u00d72, "
                + I18n.translateToLocal("item.ItemGolemUpgrade.4.name"), tooltip.get(2));
    }

    @Test
    public void corruptIdentityIdsAreOmittedWithoutRawTranslationKeys() {
        StubGolem golem = new StubGolem((byte) 100, 99, true);
        golem.upgrades[GolemIds.UPGRADE_FIRE] = 1;
        List<String> tooltip = new ArrayList<>();

        GolemWailaTooltip.append(tooltip, golem);

        assertEquals(1, tooltip.size());
        assertEquals(I18n.translateToLocal("item.ItemGolemUpgrade.2.name"), tooltip.get(0));
    }

    private static final class StubGolem implements IGolemInfo {
        private final byte core;
        private final int type;
        private final boolean advanced;
        private final int[] upgrades = new int[6];

        private StubGolem(byte core, int type, boolean advanced) {
            this.core = core;
            this.type = type;
            this.advanced = advanced;
        }

        @Override public byte getCore() { return this.core; }
        @Override public int getGolemTypeId() { return this.type; }
        @Override public boolean isAdvancedGolem() { return this.advanced; }
        @Override public int getUpgradeAmount(int upgradeId) { return this.upgrades[upgradeId]; }
    }
}
