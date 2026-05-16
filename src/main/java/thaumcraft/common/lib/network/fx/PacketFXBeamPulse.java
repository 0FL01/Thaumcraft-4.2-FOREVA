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

public class PacketFXBeamPulse extends PacketBase {
    private int source;
    private int target;
    private int color;

    public PacketFXBeamPulse() {}

    public PacketFXBeamPulse(int source, int target, int color) {
        this.source = source;
        this.target = target;
        this.color = color;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.source);
        buf.writeInt(this.target);
        buf.writeInt(this.color);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.source = buf.readInt();
        this.target = buf.readInt();
        this.color = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            Entity sourceEntity = Minecraft.getMinecraft().world.getEntityByID(this.source);
            Entity targetEntity = Minecraft.getMinecraft().world.getEntityByID(this.target);
            if (sourceEntity == null || targetEntity == null) return;
            Thaumcraft.proxy.beam(
                    Minecraft.getMinecraft().world,
                    sourceEntity.posX,
                    sourceEntity.posY + sourceEntity.getEyeHeight(),
                    sourceEntity.posZ,
                    targetEntity.posX,
                    targetEntity.posY + targetEntity.height * 0.5,
                    targetEntity.posZ,
                    this.color,
                    true,
                    20);
        });
        return null;
    }
}
