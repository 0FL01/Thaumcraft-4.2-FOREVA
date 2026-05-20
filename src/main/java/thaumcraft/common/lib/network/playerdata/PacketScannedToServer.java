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
import thaumcraft.common.items.relics.ItemThaumometer;

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
            dispatch(player, this.playerid, this.dim, this.type, this.id, this.md, this.entityid, this.phenomena, this.prefix);
        });
        return null;
    }

    static boolean dispatch(EntityPlayer player,
                            int playerid,
                            int dim,
                            byte type,
                            int id,
                            int md,
                            int entityid,
                            String phenomena,
                            String prefix) {
        if (player == null || player.getEntityId() != playerid) return false;
        if (player.world.provider.getDimension() != dim) return false;
        String normalizedPrefix = normalizePrefix(prefix);
        ScanResult result = findAuthoritativeMatchingScan(player, type, id, md, entityid, phenomena, normalizedPrefix);
        if (result == null || !ScanManager.completeScan(player, result, normalizedPrefix)) {
            return false;
        }
        if (player instanceof EntityPlayerMP) {
            syncKnowledge((EntityPlayerMP) player);
        }
        return true;
    }

    static ScanResult findAuthoritativeMatchingScan(EntityPlayer player,
                                                    byte type,
                                                    int id,
                                                    int md,
                                                    int entityid,
                                                    String phenomena,
                                                    String prefix) {
        if (player == null || !"@".equals(normalizePrefix(prefix))) {
            return null;
        }
        ScanResult mainHand = getHeldThaumometerScan(player, player.getHeldItemMainhand(), type, id, md, entityid, phenomena);
        if (mainHand != null) {
            return mainHand;
        }
        return getHeldThaumometerScan(player, player.getHeldItemOffhand(), type, id, md, entityid, phenomena);
    }

    private static ScanResult getHeldThaumometerScan(EntityPlayer player,
                                                     ItemStack held,
                                                     byte type,
                                                     int id,
                                                     int md,
                                                     int entityid,
                                                     String phenomena) {
        if (player == null || held == null || held.isEmpty() || !(held.getItem() instanceof ItemThaumometer)) {
            return null;
        }
        ItemThaumometer thaumometer = (ItemThaumometer) held.getItem();
        ScanResult authoritative = thaumometer.findScanTarget(held, player.world, player);
        return matchesPayload(authoritative, type, id, md, entityid, phenomena) ? authoritative : null;
    }

    static boolean matchesPayload(ScanResult authoritative,
                                  byte type,
                                  int id,
                                  int md,
                                  int entityid,
                                  String phenomena) {
        if (authoritative == null || authoritative.type != type) {
            return false;
        }
        if (type == 1) {
            Item item = Item.getItemById(id);
            return item != null && authoritative.id == id && authoritative.meta == md;
        }
        if (type == 2) {
            Entity entity = authoritative.entity;
            return entity != null && entity.getEntityId() == entityid;
        }
        return type == 3 && authoritative.phenomena != null && authoritative.phenomena.equals(phenomena);
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

    private static String normalizePrefix(String prefix) {
        return (prefix == null || prefix.isEmpty()) ? "@" : prefix;
    }
}
