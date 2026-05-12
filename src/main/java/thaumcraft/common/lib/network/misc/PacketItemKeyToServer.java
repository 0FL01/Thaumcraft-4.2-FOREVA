package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.entities.golems.ItemGolemBell;
import thaumcraft.common.items.equipment.ItemElementalShovel;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.network.PacketBase;

public class PacketItemKeyToServer extends PacketBase {
    private int dim;
    private int playerid;
    private byte key;

    public PacketItemKeyToServer() {}

    public PacketItemKeyToServer(EntityPlayer player, int key) {
        this.dim = player.world.provider.getDimension();
        this.playerid = player.getEntityId();
        this.key = (byte) key;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dim = buf.readInt();
        this.playerid = buf.readInt();
        this.key = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dim);
        buf.writeInt(this.playerid);
        buf.writeByte(this.key);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (player.getEntityId() != this.playerid) return;
            if (player.world.provider.getDimension() != this.dim) return;
            ItemStack held = player.getHeldItemMainhand();
            if (held.isEmpty()) return;
            if (this.key == 0 && held.getItem() instanceof ItemGolemBell) {
                ItemGolemBell.resetMarkers(held, player.world, player);
            } else if (this.key == 1 && held.getItem() instanceof ItemWandCasting) {
                WandManager.toggleMisc(held, player.world, player);
            } else if (this.key == 1 && held.getItem() instanceof ItemElementalShovel) {
                ItemElementalShovel.setOrientation(held, (byte) (ItemElementalShovel.getOrientation(held) + 1));
            }
        });
        return null;
    }
}
