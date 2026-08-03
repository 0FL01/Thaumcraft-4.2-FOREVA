package thaumcraft.common.integration.waila;

import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.entities.GolemIds;
import thaumcraft.api.entities.IGolemInfo;
import thaumcraft.common.Thaumcraft;

@WailaPlugin
public final class GolemWailaPlugin implements IWailaPlugin, IWailaEntityProvider {
    public GolemWailaPlugin() {
    }

    @Override
    public void register(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(this, IGolemInfo.class);
        Thaumcraft.log.info("Thaumcraft Waila golem integration registered");
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currentTip,
                                     IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (entity instanceof IGolemInfo) {
            GolemWailaTooltip.append(currentTip, (IGolemInfo) entity);
        }
        return currentTip;
    }
}

final class GolemWailaTooltip {
    private GolemWailaTooltip() {
    }

    static void append(List<String> tooltip, IGolemInfo golem) {
        int type = golem.getGolemTypeId();
        if (type >= GolemIds.TYPE_STRAW && type <= GolemIds.TYPE_THAUMIUM) {
            String material = I18n.translateToLocal("item.ItemGolemPlacer." + type + ".name");
            if (golem.isAdvancedGolem()) {
                material += " [" + I18n.translateToLocal("tc.adv") + "]";
            }
            tooltip.add(material);
        }

        int core = golem.getCore();
        if (core == GolemIds.CORE_NONE) {
            tooltip.add(I18n.translateToLocal("item.ItemGolemCore.name") + ": "
                    + I18n.translateToLocal("tc.golem.core.none"));
        } else if (core >= GolemIds.CORE_FILL && core <= GolemIds.CORE_FISHING) {
            tooltip.add(I18n.translateToLocal("item.ItemGolemCore.name") + ": "
                    + I18n.translateToLocal("item.ItemGolemCore." + core + ".name"));
        }

        StringBuilder upgrades = new StringBuilder();
        for (int upgrade = GolemIds.UPGRADE_AIR; upgrade <= GolemIds.UPGRADE_ENTROPY; upgrade++) {
            int count = golem.getUpgradeAmount(upgrade);
            if (count <= 0) continue;
            if (upgrades.length() > 0) upgrades.append(", ");
            upgrades.append(I18n.translateToLocal("item.ItemGolemUpgrade." + upgrade + ".name"));
            if (count > 1) upgrades.append(" \u00d7").append(count);
        }
        if (upgrades.length() > 0) tooltip.add(upgrades.toString());
    }
}
