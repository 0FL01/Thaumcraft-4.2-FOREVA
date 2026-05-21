package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketBase;

public class PacketAspectPool extends PacketBase {
    private String aspectTag;
    private short amount;
    private int total;

    public PacketAspectPool() {}

    public PacketAspectPool(String aspectTag, short amount, int total) {
        this.aspectTag = aspectTag;
        this.amount = amount;
        this.total = total;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.aspectTag = ByteBufUtils.readUTF8String(buf);
        this.amount = buf.readShort();
        this.total = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.aspectTag);
        buf.writeShort(this.amount);
        buf.writeInt(this.total);
    }

    public String getAspectTag() { return aspectTag; }
    public short getAmount() { return amount; }
    public int getTotal() { return total; }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (player != null && aspect != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.setAspectPool(aspect, total);
                }
                if (amount > 0) {
                    Thaumcraft.proxy.notifyThaumometerAspectPool(aspect, amount);
                    if (player.world != null) {
                        player.world.playSound(
                                player,
                                player.posX, player.posY, player.posZ,
                                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                                SoundCategory.PLAYERS,
                                0.1F,
                                0.9F + player.world.rand.nextFloat() * 0.2F);
                    }
                }
            }
        });
        return null;
    }
}
