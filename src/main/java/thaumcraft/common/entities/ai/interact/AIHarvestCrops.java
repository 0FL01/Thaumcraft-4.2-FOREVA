package thaumcraft.common.entities.ai.interact;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.lib.utils.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class AIHarvestCrops extends EntityAIBase {
    private EntityGolemBase theGolem;
    int count = 0;
    private BlockPos targetPos = null;
    private int delay = -1;
    private int maxDelay;
    private int mod = 1;
    private FakePlayer player;

    private static final int[] REPLANT_X = {0, 0, 1, 1, -1, 0, -1, -1, 1};
    private static final int[] REPLANT_Z = {0, 1, 0, 1, 0, -1, -1, 1, -1};

    private static final GameProfile GAME_PROFILE = new GameProfile(
            UUID.nameUUIDFromBytes("thaumcraft:golem_ai".getBytes(StandardCharsets.UTF_8)),
            "[TCGolem]");

    public AIHarvestCrops(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
        if (golem.world instanceof WorldServer) {
            this.player = FakePlayerFactory.get((WorldServer) golem.world, GAME_PROFILE);
        }
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
        if (this.targetPos != null) {
            BlockUtils.destroyBlockPartially(this.theGolem.world, this.theGolem.getEntityId(), this.targetPos, -1);
        }
        this.count = 0;
        this.delay = -1;
        this.targetPos = null;
        this.theGolem.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        --this.count;
        if (this.targetPos != null) {
            theGolem.getLookHelper().setLookPosition(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 30.0F, 30.0F);
            double dist = theGolem.getDistanceSq(targetPos);
            if (dist <= 4.0) {
                this.harvestProgress();
            }
        }
    }

    private void harvestProgress() {
        if (theGolem.world.isRemote || this.player == null) return;
        if (!CropUtils.isGrownCrop(theGolem.world, this.targetPos)) {
            this.resetTask();
            return;
        }

        IBlockState state = theGolem.world.getBlockState(targetPos);
        if (this.delay < 0) {
            float hardness = state.getBlockHardness(theGolem.world, this.targetPos);
            if (hardness < 0.0F) {
                this.resetTask();
                return;
            }
            this.delay = Math.max(10, (int) ((20 - theGolem.getGolemStrength() * 2) * hardness));
            this.maxDelay = this.delay;
            this.mod = Math.max(1, this.delay / Math.max(1, Math.round(this.delay / 6.0F)));
        }
        if (this.delay % this.mod == 0) {
            theGolem.startActionTimer();
            SoundType sound = state.getBlock().getSoundType(state, theGolem.world, this.targetPos, theGolem);
            theGolem.world.playSound(null, this.targetPos, sound.getHitSound(), SoundCategory.BLOCKS,
                    (sound.getVolume() + 1.0F) / 8.0F, sound.getPitch() * 0.5F);
            int progress = (int) (9.0F * (1.0F - (float) this.delay / (float) this.maxDelay));
            BlockUtils.destroyBlockPartially(theGolem.world, theGolem.getEntityId(), this.targetPos, progress);
        }
        if (--this.delay <= 0) {
            this.finishHarvest(state);
        }
    }

    private void finishHarvest(IBlockState state) {
        BlockUtils.destroyBlockPartially(theGolem.world, theGolem.getEntityId(), this.targetPos, -1);
        this.player.setPosition(theGolem.posX, theGolem.posY, theGolem.posZ);
        this.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        Block block = state.getBlock();
        boolean harvested = false;
        try {
            harvested = block.onBlockActivated(theGolem.world, this.targetPos, state, this.player,
                    EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 0.5F, 0.5F);
        } catch (Exception ignored) {}
        if (!harvested) {
            harvested = BlockUtils.harvestBlock(theGolem.world, this.targetPos, this.player);
            if (harvested && theGolem.getUpgradeAmount(4) > 0) {
                this.replant(state);
            }
        }
        if (harvested) theGolem.startActionTimer();
        this.count = 0;
        this.delay = -1;
        this.targetPos = null;
    }

    /** Restore the TC4 order-upgrade behavior by consuming nearby dropped planting items. */
    private void replant(IBlockState harvestedState) {
        List<EntityItem> drops = EntityUtils.getEntitiesInRange(theGolem.world,
                theGolem.posX, theGolem.posY, theGolem.posZ, theGolem, EntityItem.class, 6.0D);
        for (EntityItem drop : drops) {
            if (drop == null || drop.isDead) continue;
            this.pullFreshDrop(drop);

            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) continue;

            boolean special = false;
            if (this.isCocoaDrop(harvestedState, stack)) {
                if (this.tryReplantCocoa(harvestedState, stack)) stack.shrink(1);
                special = true;
            } else if (harvestedState.getBlock() == ConfigBlocks.blockManaPod
                    && ConfigItems.itemManaBean != null && stack.getItem() == ConfigItems.itemManaBean) {
                if (this.tryUsePlantingItem(stack, this.targetPos.up(), EnumFacing.DOWN)) stack.shrink(1);
                special = true;
            } else if (isSeedForHarvestedCrop(theGolem.world, this.targetPos, harvestedState, stack)) {
                this.replantStandardCrop(stack);
            }

            if (stack.isEmpty()) drop.setDead();
            else drop.setItem(stack);
            if (special) break;
        }
    }

    private void replantStandardCrop(ItemStack stack) {
        for (int i = 0; i < REPLANT_X.length && !stack.isEmpty(); i++) {
            BlockPos plantPos = this.targetPos.add(REPLANT_X[i], 0, REPLANT_Z[i]);
            if (!theGolem.world.isAirBlock(plantPos)) continue;
            if (this.tryUsePlantingItem(stack, plantPos.down(), EnumFacing.UP)) stack.shrink(1);
        }
    }

    static boolean isSeedForHarvestedCrop(net.minecraft.world.IBlockAccess world, BlockPos pos,
                                          IBlockState harvestedState, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IPlantable)) return false;
        IBlockState plantedState = ((IPlantable) stack.getItem()).getPlant(world, pos);
        return plantedState != null && plantedState.getBlock() == harvestedState.getBlock();
    }

    private boolean isCocoaDrop(IBlockState harvestedState, ItemStack stack) {
        return harvestedState.getBlock() == Blocks.COCOA
                && stack.getItem() == Items.DYE
                && stack.getMetadata() == EnumDyeColor.BROWN.getDyeDamage();
    }

    private boolean tryReplantCocoa(IBlockState harvestedState, ItemStack stack) {
        EnumFacing supportDirection = harvestedState.getValue(BlockHorizontal.FACING);
        BlockPos supportPos = this.targetPos.offset(supportDirection);
        return theGolem.world.isAirBlock(this.targetPos)
                && this.tryUsePlantingItem(stack, supportPos, supportDirection.getOpposite());
    }

    private boolean tryUsePlantingItem(ItemStack source, BlockPos clickedPos, EnumFacing side) {
        ItemStack held = source.copy();
        held.setCount(1);
        this.player.setHeldItem(EnumHand.MAIN_HAND, held);
        try {
            return held.onItemUse(this.player, theGolem.world, clickedPos, EnumHand.MAIN_HAND, side,
                    0.5F, 0.5F, 0.5F) == EnumActionResult.SUCCESS;
        } finally {
            this.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    private void pullFreshDrop(EntityItem drop) {
        if (drop.getAge() >= 2) return;
        Vec3d delta = drop.getPositionVector().subtract(theGolem.getPositionVector());
        if (delta.lengthSquared() > 1.0E-6D) {
            delta = delta.normalize();
            drop.motionX = -delta.x / 4.0D;
            drop.motionY = 0.075D;
            drop.motionZ = -delta.z / 4.0D;
        }
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        this.delay = -1;
        if (this.targetPos != null) {
            theGolem.getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, theGolem.getAIMoveSpeed());
        }
    }
}
