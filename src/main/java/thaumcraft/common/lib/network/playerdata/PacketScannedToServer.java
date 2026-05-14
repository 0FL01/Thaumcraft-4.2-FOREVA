package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.research.ScanManager;

public class PacketScannedToServer extends PacketBase {
    private int playerid;
    private int dim;
    private byte type;
    private int id;
    private int md;
    private int entityid;
    private String phenomena;
    private String prefix;

    public PacketScannedToServer() {}

    public PacketScannedToServer(ScanResult scan, EntityPlayer player, String prefix) {
        this.playerid = player.getEntityId();
        this.dim = player.world.provider.getDimension();
        this.type = scan.type;
        this.id = scan.id;
        this.md = scan.meta;
        this.entityid = scan.entity == null ? 0 : scan.entity.getEntityId();
        this.phenomena = scan.phenomena;
        this.prefix = prefix;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerid = buf.readInt();
        this.dim = buf.readInt();
        this.type = buf.readByte();
        this.id = buf.readInt();
        this.md = buf.readInt();
        this.entityid = buf.readInt();
        this.phenomena = ByteBufUtils.readUTF8String(buf);
        this.prefix = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.playerid);
        buf.writeInt(this.dim);
        buf.writeByte(this.type);
        buf.writeInt(this.id);
        buf.writeInt(this.md);
        buf.writeInt(this.entityid);
        ByteBufUtils.writeUTF8String(buf, this.phenomena == null ? "" : this.phenomena);
        ByteBufUtils.writeUTF8String(buf, this.prefix == null ? "" : this.prefix);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (player.getEntityId() != this.playerid) return;
            if (player.world.provider.getDimension() != this.dim) return;
            ScanResult result = null;
            if (this.type == 1) {
                Item item = Item.getItemById(this.id);
                if (item != null) {
                    result = new ScanResult((byte)1, this.id, this.md, null, null);
                }
            } else if (this.type == 2) {
                Entity entity = this.entityid == 0 ? null : player.world.getEntityByID(this.entityid);
                result = entity == null ? null : new ScanResult((byte)2, 0, 0, entity, null);
            } else if (this.type == 3) {
                result = new ScanResult((byte)3, 0, 0, null, this.phenomena);
            }
            if (result != null && ScanManager.completeScan(player, result, this.prefix)) {
                syncKnowledge(player);
            }
        });
        return null;
    }

    private static void syncKnowledge(EntityPlayerMP player) {
        IPlayerKnowledge knowledge = thaumcraft.common.CommonProxy.getPlayerKnowledge(player);
        if (knowledge != null) {
            PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(knowledge.getAspectsDiscovered()), player);
            PacketHandler.INSTANCE.sendTo(new PacketSyncScannedItems(knowledge.getScannedItems()), player);
            PacketHandler.INSTANCE.sendTo(new PacketSyncScannedEntities(knowledge.getScannedEntities()), player);
            PacketHandler.INSTANCE.sendTo(new PacketSyncScannedPhenomena(knowledge.getScannedPhenomena()), player);
        }
    }
}
