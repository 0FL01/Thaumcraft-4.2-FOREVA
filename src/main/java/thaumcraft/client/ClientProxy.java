package thaumcraft.client;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.CommonProxy;

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
        // Phase 8: register client event handlers
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
