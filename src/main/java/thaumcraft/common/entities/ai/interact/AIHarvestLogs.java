package thaumcraft.common.entities.ai.interact;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AIHarvestLogs extends EntityAIBase {
    private EntityGolemBase theGolem;
    int count = 0;
    private BlockPos targetPos = null;
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
        if (golem.world instanceof WorldServer) {
            this.player = FakePlayerFactory.get((WorldServer) golem.world, GAME_PROFILE);
        }
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        return this.findLog();
    }

    private boolean findLog() {
        BlockPos home = theGolem.getHomePosition();
        float range = theGolem.getRange();
        int r = (int) range;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -1; dy <= 4; dy++) {
                    BlockPos pos = new BlockPos(home.getX() + dx, home.getY() + dy, home.getZ() + dz);
                    if (theGolem.getDistanceSq(pos) > range * range) continue;
                    if (Utils.isWoodLog(theGolem.world, pos)) {
                        this.targetPos = pos;
                        return true;
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
            this.player.setPosition(theGolem.posX, theGolem.posY, theGolem.posZ);
            this.player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
            BlockUtils.breakFurthestBlock(theGolem.world, this.targetPos, this.player);
            this.count = 0;
            this.delay = -1;
            this.targetPos = null;
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
