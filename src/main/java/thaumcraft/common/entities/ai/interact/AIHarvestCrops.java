package thaumcraft.common.entities.ai.interact;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.CropUtils;

import java.util.UUID;

public class AIHarvestCrops extends EntityAIBase {
    private EntityGolemBase theGolem;
    int count = 0;
    private BlockPos targetPos = null;

    public AIHarvestCrops(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        return this.findCrop();
    }

    private boolean findCrop() {
        BlockPos home = theGolem.getHomePosition();
        float range = theGolem.getRange();
        int r = (int) range;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos pos = new BlockPos(home.getX() + dx, home.getY() + dy, home.getZ() + dz);
                    if (theGolem.getDistanceSq(pos) > range * range) continue;
                    if (CropUtils.isGrownCrop(theGolem.world, pos)) {
                        if (theGolem.world.isAirBlock(pos.up()) || CropUtils.isGrownCrop(theGolem.world, pos.up())) {
                            this.targetPos = pos;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && this.targetPos != null;
    }

    @Override
    public void resetTask() {
        this.count = 0;
        this.targetPos = null;
        this.theGolem.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        --this.count;
        if (this.targetPos != null) {
            theGolem.getLookHelper().setLookPosition(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 30.0F, 30.0F);
            double dist = theGolem.getDistanceSq(targetPos);
            if (dist <= 3.0) {
                this.harvest();
            }
        }
    }

    private void harvest() {
        if (theGolem.world.isRemote) return;
        WorldServer world = (WorldServer) theGolem.world;
        GameProfile gp = new GameProfile(UUID.nameUUIDFromBytes("thaumcraft.golem".getBytes()), "[Thaumcraft Golem]");
        FakePlayer fp = FakePlayerFactory.get(world, gp);
        fp.setPosition(theGolem.posX, theGolem.posY, theGolem.posZ);

        IBlockState state = world.getBlockState(targetPos);
        Block block = state.getBlock();

        if (CropUtils.isGrownCrop(world, targetPos)) {
            boolean harvested = false;
            try {
                harvested = block.onBlockActivated(world, targetPos, state, fp, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 0.5F, 0.5F);
            } catch (Exception ignored) {}
            if (!harvested) {
                block.harvestBlock(world, fp, targetPos, state, world.getTileEntity(targetPos), ItemStack.EMPTY);
                world.setBlockToAir(targetPos);
                harvested = true;
            }
            if (harvested) {
                theGolem.startActionTimer();
                this.count = 0;
            }
        }
        this.targetPos = null;
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        if (this.targetPos != null) {
            theGolem.getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, theGolem.getAIMoveSpeed());
        }
    }
}
