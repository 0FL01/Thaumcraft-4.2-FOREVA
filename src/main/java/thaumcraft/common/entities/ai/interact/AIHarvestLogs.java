package thaumcraft.common.entities.ai.interact;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class AIHarvestLogs extends EntityAIBase {
    private EntityGolemBase theGolem;
    int count = 0;
    private final int distance;
    private BlockPos targetPos = null;
    private Block targetBlock;
    private int targetMeta;
    private int delay = -1;
    private int maxDelay;
    private int mod = 1;
    private FakePlayer player;

    private static final GameProfile GAME_PROFILE = new GameProfile(
            UUID.nameUUIDFromBytes("thaumcraft:golem_ai".getBytes(StandardCharsets.UTF_8)),
            "[TCGolem]");

    public AIHarvestLogs(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
        this.distance = MathHelper.floor(golem.getRange() / 3.0F);
        if (golem.world instanceof WorldServer) {
            this.player = FakePlayerFactory.get((WorldServer) golem.world, GAME_PROFILE);
        }
    }

    @Override
    public boolean shouldExecute() {
        if (this.delay >= 0 || theGolem.ticksExisted % Config.golemDelay > 0
                || !theGolem.getNavigator().noPath()) return false;
        return this.findLog();
    }

    private boolean findLog() {
        BlockPos home = theGolem.getHomePosition();
        Random random = theGolem.getRNG();
        for (int attempt = 0; attempt < this.distance * 4; attempt++) {
            BlockPos pos = new BlockPos(
                    home.getX() + random.nextInt(1 + this.distance * 2) - this.distance,
                    (int) (home.getY() + random.nextInt(1 + this.distance) - this.distance / 2.0F),
                    home.getZ() + random.nextInt(1 + this.distance * 2) - this.distance);
            if (!Utils.isWoodLog(theGolem.world, pos)) continue;

            double targetDistance = theGolem.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            BlockPos below = pos.down();
            while (Utils.isWoodLog(theGolem.world, below)
                    && theGolem.getDistanceSq(below.getX() + 0.5D, below.getY() + 0.5D, below.getZ() + 0.5D) < targetDistance) {
                pos = below;
                targetDistance = theGolem.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                below = pos.down();
            }
            this.setTarget(pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.targetPos == null) return false;
        IBlockState state = theGolem.world.getBlockState(this.targetPos);
        return state.getBlock() == this.targetBlock
                && state.getBlock().getMetaFromState(state) == this.targetMeta
                && this.count-- > 0
                && (this.delay > 0 || Utils.isWoodLog(theGolem.world, this.targetPos)
                || !theGolem.getNavigator().noPath());
    }

    @Override
    public void resetTask() {
        if (this.targetPos != null) {
            BlockUtils.destroyBlockPartially(this.theGolem.world, this.theGolem.getEntityId(), this.targetPos, -1);
        }
        this.delay = -1;
    }

    @Override
    public void updateTask() {
        if (this.targetPos != null) {
            theGolem.getLookHelper().setLookPosition(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 30.0F, 30.0F);
            double dist = theGolem.getDistanceSq(targetPos.getX() + 0.5D, targetPos.getY() + 0.5D,
                    targetPos.getZ() + 0.5D);
            if (dist <= 4.0) {
                this.chopProgress();
            }
        }
    }

    private void chopProgress() {
        if (theGolem.world.isRemote || this.player == null) return;
        if (!Utils.isWoodLog(theGolem.world, this.targetPos)) {
            this.resetTask();
            return;
        }

        IBlockState state = theGolem.world.getBlockState(this.targetPos);
        if (this.delay < 0) {
            float hardness = state.getBlockHardness(theGolem.world, this.targetPos);
            if (hardness < 0.0F) {
                this.resetTask();
                return;
            }
            this.delay = Math.max(5, (int) ((20 - theGolem.getGolemStrength() * 3) * hardness));
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
            BlockUtils.destroyBlockPartially(theGolem.world, theGolem.getEntityId(), this.targetPos, -1);
            this.harvest();
            if (Utils.isWoodLog(theGolem.world, this.targetPos)) {
                this.delay = -1;
                this.setTarget(this.targetPos);
                this.startExecuting();
            } else {
                this.checkAdjacent();
            }
        }
    }

    private void harvest() {
        this.count = 200;
        IBlockState state = theGolem.world.getBlockState(this.targetPos);
        theGolem.world.playEvent(2001, this.targetPos, Block.getStateId(state));
        this.player.setPosition(theGolem.posX, theGolem.posY, theGolem.posZ);
        this.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        BlockUtils.breakFurthestBlock(theGolem.world, this.targetPos, this.player);
        theGolem.startActionTimer();
    }

    private void checkAdjacent() {
        BlockPos home = theGolem.getHomePosition();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    BlockPos pos = this.targetPos.add(dx, dy, dz);
                    if (Math.abs(home.getX() - pos.getX()) > this.distance
                            || Math.abs(home.getY() - pos.getY()) > this.distance
                            || Math.abs(home.getZ() - pos.getZ()) > this.distance
                            || !Utils.isWoodLog(theGolem.world, pos)) continue;
                    this.setTarget(pos);
                    this.delay = -1;
                    this.startExecuting();
                    return;
                }
            }
        }
    }

    private void setTarget(BlockPos pos) {
        this.targetPos = pos;
        IBlockState state = theGolem.world.getBlockState(pos);
        this.targetBlock = state.getBlock();
        this.targetMeta = this.targetBlock.getMetaFromState(state);
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        if (this.targetPos != null) {
            theGolem.getNavigator().tryMoveToXYZ(targetPos.getX() + 0.5, targetPos.getY() + 0.5,
                    targetPos.getZ() + 0.5, theGolem.getAIMoveSpeed());
        }
    }
}
