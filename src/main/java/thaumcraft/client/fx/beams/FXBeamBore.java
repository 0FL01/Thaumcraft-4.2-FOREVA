package thaumcraft.client.fx.beams;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBeamBore extends FXBeam {
    public int impact;

    public FXBeamBore(World world,
                      double px, double py, double pz,
                      double tx, double ty, double tz,
                      float red, float green, float blue,
                      int age, boolean flicker, int density) {
        super(world, px, py, pz, tx, ty, tz, red, green, blue, age, flicker, density);
        this.impact = 0;
    }

    public void updateBeam(double tx, double ty, double tz) {
        super.updateBeam(this.posX, this.posY, this.posZ, tx, ty, tz);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.impact > 0) {
            --this.impact;
        }
    }

    @Override
    protected void renderImpact(float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.impact <= 0) {
            return;
        }
        super.renderImpact(partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
}
