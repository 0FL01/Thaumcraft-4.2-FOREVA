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
            if (Minecraft.getMinecraft().world == null) return;
            Entity sourceEntity = Minecraft.getMinecraft().world.getEntityByID(this.source);
            if (sourceEntity == null) return;
            Thaumcraft.proxy.sonicFX(Minecraft.getMinecraft().world, sourceEntity, 14);
            Thaumcraft.proxy.burst(
                    Minecraft.getMinecraft().world,
                    sourceEntity.posX,
                    sourceEntity.posY + sourceEntity.height * 0.5,
                    sourceEntity.posZ,
                    1.4f);
        });
        return null;
    }
}
