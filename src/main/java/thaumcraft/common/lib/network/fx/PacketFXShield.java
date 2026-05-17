package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
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

            float pitch = 0.0f;
            float yaw = 0.0f;

            if (this.target >= 0) {
                Entity targetEntity = mc.world.getEntityByID(this.target);
                if (targetEntity != null) {
                    double d0 = sourceEntity.posX - targetEntity.posX;
                    double d1 = (sourceEntity.getEntityBoundingBox().minY + sourceEntity.getEntityBoundingBox().maxY) * 0.5D
                            - (targetEntity.getEntityBoundingBox().minY + targetEntity.getEntityBoundingBox().maxY) * 0.5D;
                    double d2 = sourceEntity.posZ - targetEntity.posZ;
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    yaw = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
                    pitch = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
                } else {
                    pitch = 90.0f;
                    yaw = 0.0f;
                }
                ParticleEngine.addEffect(mc.world, new FXShieldRunes(mc.world, sourceEntity, 8, yaw, pitch));
                return;
            }

            if (this.target == -1) {
                ParticleEngine.addEffect(mc.world, new FXShieldRunes(mc.world, sourceEntity, 8, 0.0f, 90.0f));
                ParticleEngine.addEffect(mc.world, new FXShieldRunes(mc.world, sourceEntity, 8, 0.0f, 270.0f));
            } else if (this.target == -2) {
                ParticleEngine.addEffect(mc.world, new FXShieldRunes(mc.world, sourceEntity, 8, 0.0f, 270.0f));
            } else if (this.target == -3) {
                ParticleEngine.addEffect(mc.world, new FXShieldRunes(mc.world, sourceEntity, 8, 0.0f, 90.0f));
            }
        });
        return null;
    }
}
