package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.bolt.FXLightningBolt;
import thaumcraft.client.fx.other.FXShieldRunes;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXShield extends PacketBase {
    private int source;
    private int target;

    public PacketFXShield() {}

    public PacketFXShield(int source, int target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.source);
        buf.writeInt(this.target);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.source = buf.readInt();
        this.target = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world == null) return;
            Entity sourceEntity = mc.world.getEntityByID(this.source);
            if (sourceEntity == null) return;

            double sx = sourceEntity.posX;
            double sy = sourceEntity.getEntityBoundingBox().minY + sourceEntity.height * 0.5;
            double sz = sourceEntity.posZ;
            spawnRunes(mc, sourceEntity, sourceEntity.rotationYaw, sourceEntity.rotationPitch);
            thaumcraft.common.Thaumcraft.proxy.burst(mc.world, sx, sy, sz, 1.0f);

            if (this.target >= 0) {
                Entity targetEntity = mc.world.getEntityByID(this.target);
                if (targetEntity != null) {
                    double tx = targetEntity.posX;
                    double ty = targetEntity.getEntityBoundingBox().minY + targetEntity.height * 0.5;
                    double tz = targetEntity.posZ;
                    float[] orientation = facingAngles(sx, sy, sz, tx, ty, tz);
                    spawnRunes(mc, sourceEntity, orientation[0], orientation[1]);
                    ParticleEngine.addEffect(mc.world,
                            new FXLightningBolt(mc.world, sx, sy, sz, tx, ty, tz, 0.4F, 0.8F, 1.0F, 4, 7));
                }
                return;
            }

            if (this.target == -1) {
                spawnRunes(mc, sourceEntity, 0.0F, 90.0F);
                spawnRunes(mc, sourceEntity, 0.0F, 270.0F);
                ParticleEngine.addEffect(mc.world,
                        new FXLightningBolt(mc.world, sx, sy, sz, sx, sy + 1.0D, sz, 0.4F, 0.8F, 1.0F, 3, 6));
                ParticleEngine.addEffect(mc.world,
                        new FXLightningBolt(mc.world, sx, sy, sz, sx, sy - 1.0D, sz, 0.4F, 0.8F, 1.0F, 3, 6));
            } else if (this.target == -2) {
                spawnRunes(mc, sourceEntity, 0.0F, 270.0F);
                ParticleEngine.addEffect(mc.world,
                        new FXLightningBolt(mc.world, sx, sy, sz, sx, sy - 1.0D, sz, 0.4F, 0.8F, 1.0F, 3, 6));
            } else if (this.target == -3) {
                spawnRunes(mc, sourceEntity, 0.0F, 90.0F);
                ParticleEngine.addEffect(mc.world,
                        new FXLightningBolt(mc.world, sx, sy, sz, sx, sy + 1.0D, sz, 0.4F, 0.8F, 1.0F, 3, 6));
            }
        });
        return null;
    }

    private static void spawnRunes(Minecraft mc, Entity source, float yaw, float pitch) {
        thaumcraft.common.Thaumcraft.proxy.shieldRunesFX(mc.world, source, 12, yaw, pitch);
        ParticleEngine.addEffect(mc.world, new FXShieldRunes(mc.world, source, 8, yaw, pitch));
    }

    private static float[] facingAngles(double sx, double sy, double sz, double tx, double ty, double tz) {
        double dx = sx - tx;
        double dy = sy - ty;
        double dz = sz - tz;
        double flat = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(dy, flat) * 180.0D / Math.PI));
        return new float[]{yaw, pitch};
    }
}
