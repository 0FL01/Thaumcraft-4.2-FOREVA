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
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            Entity sourceEntity = world.getEntityByID(this.source);
            if (sourceEntity == null) return;
            Thaumcraft.proxy.sonicFX(world, sourceEntity, 10);
        });
        return null;
    }
}
