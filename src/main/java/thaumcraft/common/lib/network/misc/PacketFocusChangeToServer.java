package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;

public class PacketFocusChangeToServer extends PacketBase {
    private int dim;
    private int playerid;
    private String focus;

    public PacketFocusChangeToServer() {}

    public PacketFocusChangeToServer(EntityPlayer player, String focus) {
        this.dim = player.world.provider.getDimension();
        this.playerid = player.getEntityId();
        this.focus = focus;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dim = buf.readInt();
        this.playerid = buf.readInt();
        this.focus = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dim);
        buf.writeInt(this.playerid);
        ByteBufUtils.writeUTF8String(buf, this.focus == null ? "" : this.focus);
    }

    @Override
    public IMessage onMessage(MessageContext ctx) {
        this.scheduleServer(ctx, player -> {
            if (player.getEntityId() != this.playerid) return;
            if (player.world.provider.getDimension() != this.dim) return;
            ItemStack held = player.getHeldItemMainhand();
            if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
                WandManager.changeFocus(held, player.world, player, this.focus);
            }
        });
        return null;
    }
}
