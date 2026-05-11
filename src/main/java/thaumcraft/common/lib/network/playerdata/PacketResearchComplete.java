package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import thaumcraft.common.lib.network.PacketBase;

public class PacketResearchComplete extends PacketBase {
    private String researchKey;

    public PacketResearchComplete() {}

    public PacketResearchComplete(String researchKey) {
        this.researchKey = researchKey;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        researchKey = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, researchKey);
    }

    public String getResearchKey() {
        return researchKey;
    }
}
