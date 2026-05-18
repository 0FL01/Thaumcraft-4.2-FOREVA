package thaumcraft.client.fx.beams;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBeamPower extends FXBeam {

    public FXBeamPower(World world,
                       double px, double py, double pz,
                       double tx, double ty, double tz,
                       float red, float green, float blue,
                       int age, boolean flicker, int density) {
        super(world, px, py, pz, tx, ty, tz, red, green, blue, age, flicker, density);
    }

    public void updateBeam(double px, double py, double pz, double tx, double ty, double tz) {
        super.updateBeam(px, py, pz, tx, ty, tz);
    }

    public void setPulse(boolean pulse, float red, float green, float blue) {
        setPulse(pulse);
        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
    }
}
