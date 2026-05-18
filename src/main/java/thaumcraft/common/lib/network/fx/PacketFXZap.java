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
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXZap extends PacketBase {
    private int source;
    private int target;

    public PacketFXZap() {}

    public PacketFXZap(int source, int target) {
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
            Entity targetEntity = mc.world.getEntityByID(this.target);
            if (sourceEntity == null || targetEntity == null) return;
            FXLightningBolt bolt = new FXLightningBolt(
                    mc.world,
                    sourceEntity.posX,
                    sourceEntity.getEntityBoundingBox().minY + sourceEntity.height * 0.5D,
                    sourceEntity.posZ,
                    targetEntity.posX,
                    targetEntity.getEntityBoundingBox().minY + targetEntity.height * 0.5D,
                    targetEntity.posZ,
                    mc.world.rand.nextLong(),
                    6,
                    0.5F,
                    8);
            bolt.defaultFractal();
            bolt.setType(2);
            bolt.setWidth(0.125F);
            bolt.finalizeBolt();
            ParticleEngine.addEffect(mc.world, bolt);
        });
        return null;
    }
}
