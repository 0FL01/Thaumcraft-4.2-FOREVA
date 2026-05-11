package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.network.PacketBase;

public class PacketSyncAspects extends PacketBase {
    private AspectList aspects;

    public PacketSyncAspects() {}

    public PacketSyncAspects(AspectList aspects) {
        this.aspects = aspects;
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
        if (aspects == null) {
            buf.writeInt(0);
            return;
        }
        Aspect[] aspectArray = aspects.getAspects();
        buf.writeInt(aspectArray.length);
        for (Aspect aspect : aspectArray) {
            ByteBufUtils.writeUTF8String(buf, aspect.getTag());
            buf.writeInt(aspects.getAmount(aspect));
        }
    }
}
