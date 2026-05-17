package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketBase;

public class PacketSyncAspects extends PacketBase {
    private AspectList aspects;

    public PacketSyncAspects() {}

    public PacketSyncAspects(AspectList aspects) {
        this.aspects = aspects == null ? new AspectList() : aspects.copy();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        aspects = new AspectList();
        for (int i = 0; i < count; i++) {
            String tag = ByteBufUtils.readUTF8String(buf);
            int amount = buf.readInt();
            Aspect aspect = Aspect.getAspect(tag);
            if (aspect != null) {
                aspects.add(aspect, amount);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (aspects == null || aspects.size() <= 0) {
            buf.writeInt(0);
            return;
        }
        Aspect[] aspectArray = aspects.getAspects();
        int count = 0;
        for (Aspect aspect : aspectArray) {
            if (aspect != null) {
                count++;
            }
        }
        buf.writeInt(count);
        for (Aspect aspect : aspectArray) {
            if (aspect == null) {
                continue;
            }
            ByteBufUtils.writeUTF8String(buf, aspect.getTag());
            buf.writeInt(aspects.getAmount(aspect));
        }
    }

    public AspectList getAspects() {
        return aspects == null ? new AspectList() : aspects.copy();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            if (player != null && aspects != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.setAspectsDiscovered(aspects);
                }
            }
        });
        return null;
    }
}
