package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;

public class PacketRunicCharge extends PacketBase {
    private int entityId;
    private int charge;
    private int max;

    public PacketRunicCharge() {}

    public PacketRunicCharge(int entityId, int charge, int max) {
        this.entityId = entityId;
        this.charge = charge;
        this.max = max;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        charge = buf.readShort();
        max = buf.readShort();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeShort(charge);
        buf.writeShort(max);
    }

    public int getEntityId() { return entityId; }
    public int getCharge() { return charge; }
    public int getMax() { return max; }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            if (Thaumcraft.instance != null && Thaumcraft.instance.runicEventHandler != null) {
                Thaumcraft.instance.runicEventHandler.runicCharge.put(entityId, charge);
                Thaumcraft.instance.runicEventHandler.runicInfo.put(entityId, new Integer[]{max, 0, 0, 0, 0});
            }
        });
        return null;
    }
}
