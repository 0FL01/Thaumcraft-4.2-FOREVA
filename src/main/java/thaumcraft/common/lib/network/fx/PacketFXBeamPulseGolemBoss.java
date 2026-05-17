package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.beams.FXBeamGolemBoss;
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
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            Entity sourceEntity = Minecraft.getMinecraft().world.getEntityByID(this.source);
            Entity targetEntity = Minecraft.getMinecraft().world.getEntityByID(this.target);
            if (!(sourceEntity instanceof EntityLivingBase) || targetEntity == null) return;

            FXBeamGolemBoss beamA = new FXBeamGolemBoss(
                    Minecraft.getMinecraft().world,
                    (EntityLivingBase) sourceEntity,
                    targetEntity,
                    0.07F, 0.376F, 0.325F,
                    20);
            beamA.setType(2);
            beamA.setPulse(true);
            ParticleEngine.addEffect(Minecraft.getMinecraft().world, beamA);

            FXBeamGolemBoss beamB = new FXBeamGolemBoss(
                    Minecraft.getMinecraft().world,
                    (EntityLivingBase) sourceEntity,
                    targetEntity,
                    1.0F, 0.5F, 0.5F,
                    20);
            beamB.setType(1);
            beamB.setPulse(true);
            ParticleEngine.addEffect(Minecraft.getMinecraft().world, beamB);
        });
        return null;
    }
}
