package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXBeamPulseGolemBoss extends PacketBase {
    private int source;
    private int target;

    public PacketFXBeamPulseGolemBoss() {}

    public PacketFXBeamPulseGolemBoss(int source, int target) {
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
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            Entity sourceEntity = world.getEntityByID(this.source);
            Entity targetEntity = world.getEntityByID(this.target);
            if (!(sourceEntity instanceof EntityLivingBase) || targetEntity == null) return;
            Thaumcraft.proxy.beamPulseGolemBossFX(world, (EntityLivingBase) sourceEntity, targetEntity);
        });
        return null;
    }
}
