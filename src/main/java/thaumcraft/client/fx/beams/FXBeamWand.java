package thaumcraft.client.fx.beams;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBeamWand extends FXBeam {
    private final EntityPlayer player;
    public int impact;

    public FXBeamWand(World world, EntityPlayer player, double tx, double ty, double tz,
                      float red, float green, float blue, int age, boolean flicker, int density) {
        super(world,
                player.posX,
                player.posY + player.getEyeHeight(),
                player.posZ,
                tx, ty, tz,
                red, green, blue,
                age, flicker, density);
        this.player = player;
        this.impact = 0;
    }

    public void updateBeam(double tx, double ty, double tz) {
        if (this.player != null) {
            super.updateBeam(
                    this.player.posX,
                    this.player.posY + this.player.getEyeHeight(),
                    this.player.posZ,
                    tx, ty, tz);
        } else {
            super.updateBeam(this.posX, this.posY, this.posZ, tx, ty, tz);
        }
    }
}
