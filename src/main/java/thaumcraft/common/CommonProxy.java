package thaumcraft.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class CommonProxy implements IGuiHandler {

    // ---- IGuiHandler ----

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    // ---- Registration stubs (overridden by ClientProxy) ----

    public void registerDisplayInformation() {
    }

    public void registerKeyBindings() {
    }

    // ---- FX stubs (ClientProxy overrides with actual GL calls) ----

    public void blockSparkle(World world, int x, int y, int z, int color, int count) {
    }

    public void beam(World world, double x, double y, double z, double tx, double ty, double tz, int color, boolean flicker, int ticks) {
    }

    public void bolt(World world, double x, double y, double z, double tx, double ty, double tz, int color, int speed) {
    }
}
