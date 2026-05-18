package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
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
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            Entity sourceEntity = world.getEntityByID(this.source);
            Entity targetEntity = world.getEntityByID(this.target);
            if (sourceEntity == null || targetEntity == null) return;
            Thaumcraft.proxy.beamPulseFX(world, sourceEntity, targetEntity, this.color);
        });
        return null;
    }
}
