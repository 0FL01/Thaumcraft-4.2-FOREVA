package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketBase;

public class PacketSyncWarp extends PacketBase {
    private int warpPerm;
    private int warpSticky;
    private int warpTemp;

    public PacketSyncWarp() {}

    public PacketSyncWarp(int warpPerm, int warpSticky, int warpTemp) {
        this.warpPerm = warpPerm;
        this.warpSticky = warpSticky;
        this.warpTemp = warpTemp;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        warpPerm = buf.readInt();
        warpSticky = buf.readInt();
        warpTemp = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(warpPerm);
        buf.writeInt(warpSticky);
        buf.writeInt(warpTemp);
    }

    public int getWarpPerm() { return warpPerm; }
    public int getWarpSticky() { return warpSticky; }
    public int getWarpTemp() { return warpTemp; }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player != null) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    knowledge.setWarpPerm(warpPerm);
                    knowledge.setWarpSticky(warpSticky);
                    knowledge.setWarpTemp(warpTemp);
                }
            }
        });
        return null;
    }
}
