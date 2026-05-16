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

public class PacketFXWispZap extends PacketBase {
    private int source;
    private int target;

    public PacketFXWispZap() {}

    public PacketFXWispZap(int source, int target) {
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
            Entity targetEntity = Minecraft.getMinecraft().world.getEntityByID(this.target);
            if (sourceEntity == null || targetEntity == null) return;
            Thaumcraft.proxy.bolt(
                    Minecraft.getMinecraft().world,
                    sourceEntity.posX,
                    sourceEntity.getEntityBoundingBox().minY + sourceEntity.height * 0.5,
                    sourceEntity.posZ,
                    targetEntity.posX,
                    targetEntity.getEntityBoundingBox().minY + targetEntity.height * 0.5,
                    targetEntity.posZ,
                    0x99CCFF,
                    4);
        });
        return null;
    }
}
