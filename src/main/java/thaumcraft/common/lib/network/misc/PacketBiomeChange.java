package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.utils.Utils;

public class PacketBiomeChange extends PacketBase {
    private int x;
    private int z;
    private short biome;

    public PacketBiomeChange() {}

    public PacketBiomeChange(int x, int z, short biome) {
        this.x = x;
        this.z = z;
        this.biome = biome;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
        buf.writeShort(this.biome);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.z = buf.readInt();
        this.biome = buf.readShort();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageContext ctx) {
        Thaumcraft.proxy.scheduleClientTask(() -> {
            World world = Thaumcraft.proxy.getClientWorld();
            Biome biome = Biome.getBiome(this.biome);
            if (world == null || biome == null) return;
            Utils.setBiomeAt(world, this.x, this.z, biome);
        });
        return null;
    }
}
