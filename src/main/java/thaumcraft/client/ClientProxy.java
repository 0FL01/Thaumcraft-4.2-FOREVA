package thaumcraft.client;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.lib.events.EventHandlerRunic;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerDisplayInformation() {
        // Phase 8: register models, entity renderers, etc.
    }

    @Override
    public void registerKeyBindings() {
        // Phase 8: register key bindings
    }

    @Override
    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    private static final class ClientEventHandler {
        @SubscribeEvent
        public void onItemTooltip(ItemTooltipEvent event) {
            int charge = EventHandlerRunic.getFinalCharge(event.getItemStack());
            int warp = EventHandlerRunic.getFinalWarp(event.getItemStack(), event.getEntityPlayer());

            if (charge > 0) {
                event.getToolTip().add(TextFormatting.GOLD + I18n.translateToLocal("item.runic.charge") + " +" + charge);
            }
            if (warp > 0 && event.getEntityPlayer() != null) {
                event.getToolTip().add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.warping") + " " + warp);
            }
        }
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // Phase 8: return GUI screens
        return null;
    }

    // ---- FX overrides ----

    @Override
    public void blockSparkle(World world, int x, int y, int z, int color, int count) {
        // Phase 8: particle FX
    }

    @Override
    public void beam(World world, double x, double y, double z, double tx, double ty, double tz, int color, boolean flicker, int ticks) {
        // Phase 8: beam FX
    }

    @Override
    public void bolt(World world, double x, double y, double z, double tx, double ty, double tz, int color, int speed) {
        // Phase 8: lightning bolt FX
    }
}
