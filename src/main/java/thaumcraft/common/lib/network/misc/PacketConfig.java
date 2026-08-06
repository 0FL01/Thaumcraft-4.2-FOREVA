package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.network.PacketBase;

public class PacketConfig extends PacketBase {
    private boolean allowCheatSheet;
    private boolean wardedStone;
    private boolean allowMirrors;
    private boolean hardNode;
    private boolean wuss;
    private byte researchDifficulty;
    private int aspectTotalCap;

    public PacketConfig() {}

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(Config.allowCheatSheet);
        buf.writeBoolean(Config.wardedStone);
        buf.writeBoolean(Config.allowMirrors);
        buf.writeBoolean(Config.hardNode);
        buf.writeBoolean(Config.wuss);
        buf.writeByte(Config.researchDifficulty);
        buf.writeInt(Config.aspectTotalCap);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.allowCheatSheet = buf.readBoolean();
        this.wardedStone = buf.readBoolean();
        this.allowMirrors = buf.readBoolean();
        this.hardNode = buf.readBoolean();
        this.wuss = buf.readBoolean();
        this.researchDifficulty = buf.readByte();
        this.aspectTotalCap = buf.readInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(this::applyConfig);
        return null;
    }

    void applyConfig() {
        Config.allowCheatSheet = this.allowCheatSheet;
        Config.wardedStone = this.wardedStone;
        Config.allowMirrors = this.allowMirrors;
        Config.hardNode = this.hardNode;
        Config.wuss = this.wuss;
        Config.researchDifficulty = this.researchDifficulty;
        Config.aspectTotalCap = this.aspectTotalCap;
    }
}
