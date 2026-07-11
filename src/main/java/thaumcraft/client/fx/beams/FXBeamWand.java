package thaumcraft.client.fx.beams;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBeamWand extends FXBeam {
    private final EntityPlayer player;
    private final double sourceYOffset;
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
        this.sourceYOffset = (Minecraft.getMinecraft().player != null && player.getEntityId() != Minecraft.getMinecraft().player.getEntityId())
                ? player.height / 2.0F + 0.25D
                : 0.0D;
        this.impact = 0;
    }

    public void updateBeam(double tx, double ty, double tz) {
        if (this.player != null) {
            Vec3d src = sourcePos(this.player, 1.0F, this.sourceYOffset);
            super.updateBeam(
                    src.x,
                    src.y,
                    src.z,
                    tx, ty, tz);
        } else {
            super.updateBeam(this.posX, this.posY, this.posZ, tx, ty, tz);
        }
    }

    @Override
    public void onUpdate() {
        if (this.player == null || !this.player.isEntityAlive()) {
            this.setExpired();
            return;
        }
        Vec3d src = sourcePos(this.player, 1.0F, this.sourceYOffset);
        this.setPosition(src.x, src.y, src.z);
        if (this.impact > 0) {
            --this.impact;
        }
        super.onUpdate();
    }

    @Override
    protected void renderImpact(float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.impact <= 0) {
            return;
        }
        super.renderImpact(partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    private static Vec3d sourcePos(EntityPlayer player, float partialTicks, double yOffset) {
        Vec3d look = player.getLook(partialTicks);
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
        double yawRad = yaw / 180.0F * Math.PI;
        double ix = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double iy = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double iz = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
        double sx = ix - MathHelper.cos((float) yawRad) * 0.066D + look.x * 0.3D;
        double sy = iy + player.getEyeHeight() + yOffset - 0.06D + look.y * 0.3D;
        double sz = iz - MathHelper.sin((float) yawRad) * 0.04D + look.z * 0.3D;
        return new Vec3d(sx, sy, sz);
    }
}
