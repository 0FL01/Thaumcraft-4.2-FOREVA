package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.monster.boss.EntityCultistPortal;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXBlockArc extends PacketBase {

    private int x;
    private int y;
    private int z;
    private int entityId;

    public PacketFXBlockArc() {}

    public PacketFXBlockArc(int x, int y, int z, int entityId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.entityId = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            Entity source = Minecraft.getMinecraft().world.getEntityByID(this.entityId);
            if (source == null) return;
            int color = source instanceof EntityCultistPortal ? 0xCC3300 : 0x6633CC;
            Thaumcraft.proxy.bolt(
                    Minecraft.getMinecraft().world,
                    source.posX,
                    source.getEntityBoundingBox().minY + source.height * 0.5,
                    source.posZ,
                    this.x + 0.5,
                    this.y + 1.0,
                    this.z + 0.5,
                    color,
                    4);
        });
        return null;
    }
}
