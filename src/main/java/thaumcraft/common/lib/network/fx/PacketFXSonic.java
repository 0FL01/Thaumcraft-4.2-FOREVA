package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.other.FXSonic;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXSonic extends PacketBase {
    private int source;

    public PacketFXSonic() {}

    public PacketFXSonic(int source) {
        this.source = source;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.source);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.source = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.world == null) return;
            Entity sourceEntity = mc.world.getEntityByID(this.source);
            if (sourceEntity == null) return;
            ParticleEngine.addEffect(mc.world, new FXSonic(mc.world, sourceEntity, 10));
        });
        return null;
    }
}
