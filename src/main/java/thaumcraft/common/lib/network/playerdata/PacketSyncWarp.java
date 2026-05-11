package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
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
}
