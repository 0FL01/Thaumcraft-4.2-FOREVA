package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
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
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            Entity source = world.getEntityByID(this.entityId);
            if (source == null) return;
            float red = 0.3F - world.rand.nextFloat() * 0.1F;
            float green = 0.0F;
            float blue = 0.5F + world.rand.nextFloat() * 0.2F;
            if (source instanceof EntityCultistPortal) {
                red = 0.5F + world.rand.nextFloat() * 0.2F;
                green = 0.0F;
                blue = 0.0F;
            }
            Thaumcraft.proxy.arcLightning(
                    world,
                    source.posX,
                    source.getEntityBoundingBox().minY + source.height * 0.5,
                    source.posZ,
                    this.x + 0.5,
                    this.y + 1.0,
                    this.z + 0.5,
                    red, green, blue, 0.5F);
        });
        return null;
    }
}
