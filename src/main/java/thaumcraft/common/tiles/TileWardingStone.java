package thaumcraft.common.tiles;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;

public class TileWardingStone extends TileEntity implements ITickable {
    private int count = 0;

    public boolean gettingPower() {
        return this.world != null && this.world.isBlockPowered(this.pos);
    }

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) return;

        if (this.count == 0) {
            this.count = this.world.rand.nextInt(100);
        }

        if (this.count % 5 == 0 && !this.gettingPower()) {
            List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    new AxisAlignedBB(this.pos, this.pos.add(1, 3, 1)).grow(0.1D, 0.1D, 0.1D));
            if (!targets.isEmpty()) {
                for (EntityLivingBase entity : targets) {
                    if (entity.onGround || entity instanceof EntityPlayer) continue;
                    entity.addVelocity(
                            -MathHelper.sin((entity.rotationYaw + 180.0F) * (float) Math.PI / 180.0F) * 0.2F,
                            -0.1D,
                            MathHelper.cos((entity.rotationYaw + 180.0F) * (float) Math.PI / 180.0F) * 0.2F);
                }
            }
        }

        if (++this.count % 100 == 0) {
            if ((this.world.getBlockState(this.pos.up()).getBlock() != ConfigBlocks.blockAiry
                    || this.world.getBlockState(this.pos.up()).getValue(BlockAiry.TYPE) != 3)
                    && this.world.getBlockState(this.pos.up()).getBlock().isReplaceable(this.world, this.pos.up())) {
                this.world.setBlockState(this.pos.up(), ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 4), 3);
            }
            if ((this.world.getBlockState(this.pos.up(2)).getBlock() != ConfigBlocks.blockAiry
                    || this.world.getBlockState(this.pos.up(2)).getValue(BlockAiry.TYPE) != 3)
                    && this.world.getBlockState(this.pos.up(2)).getBlock().isReplaceable(this.world, this.pos.up(2))) {
                this.world.setBlockState(this.pos.up(2), ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 4), 3);
            }
        }
    }
}
