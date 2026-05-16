package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;

public class TileEldritchTrap extends TileEntity implements ITickable {
    private int count = 20;

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote || this.count-- > 0) return;

        this.count = 10 + this.world.rand.nextInt(25);
        EntityPlayer player = this.world.getClosestPlayer(
                this.pos.getX() + 0.5D,
                this.pos.getY() + 0.5D,
                this.pos.getZ() + 0.5D,
                3.0D,
                false);
        if (player == null) return;

        player.attackEntityFrom(DamageSource.MAGIC, 2.0F);
        if (this.world.rand.nextBoolean()) {
            Thaumcraft.addWarpToPlayer(player, 1 + this.world.rand.nextInt(2), true);
        }
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockZap(
                        this.pos.getX() + 0.5F,
                        this.pos.getY() + 0.5F,
                        this.pos.getZ() + 0.5F,
                        (float) player.posX,
                        (float) player.getEntityBoundingBox().minY + player.getEyeHeight(),
                        (float) player.posZ),
                new NetworkRegistry.TargetPoint(
                        this.world.provider.getDimension(),
                        this.pos.getX(),
                        this.pos.getY(),
                        this.pos.getZ(),
                        32.0));
    }
}
