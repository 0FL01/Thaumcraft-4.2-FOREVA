package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;

public class PacketFXBlockDig extends PacketBase {
    private int x;
    private int y;
    private int z;
    private int bi;
    private int md;
    private byte dx;
    private byte dy;
    private byte dz;

    public PacketFXBlockDig() {}

    public PacketFXBlockDig(int x, int y, int z, byte dx, byte dy, byte dz, int bi, int md) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.bi = bi;
        this.md = md;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.bi);
        buf.writeInt(this.md);
        buf.writeByte(this.dx);
        buf.writeByte(this.dy);
        buf.writeByte(this.dz);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.bi = buf.readInt();
        this.md = buf.readInt();
        this.dx = buf.readByte();
        this.dy = buf.readByte();
        this.dz = buf.readByte();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            if (world == null) return;
            EntityPlayer player = Thaumcraft.proxy.getClientPlayer();
            Item item = Item.getItemById(this.bi);
            int amount = Thaumcraft.proxy.particleCount(20);
            double tx = this.x + 0.5;
            double ty = this.y + 0.5;
            double tz = this.z + 0.5;

            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();
                IBlockState state;
                try {
                    state = block.getStateFromMeta(this.md);
                } catch (Exception ignored) {
                    state = block.getDefaultState();
                }
                for (int i = 0; i < amount; i++) {
                    double sx = this.dx + world.rand.nextFloat();
                    double sy = this.dy + world.rand.nextFloat();
                    double sz = this.dz + world.rand.nextFloat();
                    Thaumcraft.proxy.boreDigFx(
                            world,
                            sx,
                            sy,
                            sz,
                            tx,
                            ty,
                            tz,
                            state,
                            null,
                            0);
                }
                SoundType sound = state.getBlock().getSoundType(state, world, new BlockPos(this.x, this.y, this.z), null);
                world.playSound(
                        player,
                        this.dx + 0.5,
                        this.dy + 0.5,
                        this.dz + 0.5,
                        sound.getHitSound(),
                        SoundCategory.BLOCKS,
                        (sound.getVolume() + 1.0F) / 2.0F,
                        sound.getPitch() * 0.8F);
                return;
            }

            if (item == null) return;
            for (int i = 0; i < amount; i++) {
                double sx = this.dx + world.rand.nextFloat();
                double sy = this.dy + world.rand.nextFloat();
                double sz = this.dz + world.rand.nextFloat();
                Thaumcraft.proxy.boreDigFx(
                        world,
                        sx,
                        sy,
                        sz,
                        tx,
                        ty,
                        tz,
                        null,
                        item,
                        this.md);
            }
        });
        return null;
    }
}
