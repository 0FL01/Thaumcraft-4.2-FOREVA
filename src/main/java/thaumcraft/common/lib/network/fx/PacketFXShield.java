package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
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
            if (Minecraft.getMinecraft().world == null) return;
            Entity sourceEntity = Minecraft.getMinecraft().world.getEntityByID(this.source);
            if (sourceEntity == null) return;

            double sx = sourceEntity.posX;
            double sy = sourceEntity.getEntityBoundingBox().minY + sourceEntity.height * 0.5;
            double sz = sourceEntity.posZ;
            Thaumcraft.proxy.shieldRunesFX(
                    Minecraft.getMinecraft().world,
                    sourceEntity,
                    12,
                    sourceEntity.rotationYaw,
                    sourceEntity.rotationPitch);
            Thaumcraft.proxy.burst(Minecraft.getMinecraft().world, sx, sy, sz, 1.0f);

            if (this.target >= 0) {
                Entity targetEntity = Minecraft.getMinecraft().world.getEntityByID(this.target);
                if (targetEntity != null) {
                    double tx = targetEntity.posX;
                    double ty = targetEntity.getEntityBoundingBox().minY + targetEntity.height * 0.5;
                    double tz = targetEntity.posZ;
                    Thaumcraft.proxy.bolt(Minecraft.getMinecraft().world, sx, sy, sz, tx, ty, tz, 0x66CCFF, 4);
                }
                return;
            }

            if (this.target == -1) {
                Thaumcraft.proxy.bolt(Minecraft.getMinecraft().world, sx, sy, sz, sx, sy + 1.0, sz, 0x66CCFF, 3);
                Thaumcraft.proxy.bolt(Minecraft.getMinecraft().world, sx, sy, sz, sx, sy - 1.0, sz, 0x66CCFF, 3);
            } else if (this.target == -2) {
                Thaumcraft.proxy.bolt(Minecraft.getMinecraft().world, sx, sy, sz, sx, sy - 1.0, sz, 0x66CCFF, 3);
            } else if (this.target == -3) {
                Thaumcraft.proxy.bolt(Minecraft.getMinecraft().world, sx, sy, sz, sx, sy + 1.0, sz, 0x66CCFF, 3);
            }
        });
        return null;
    }
}
