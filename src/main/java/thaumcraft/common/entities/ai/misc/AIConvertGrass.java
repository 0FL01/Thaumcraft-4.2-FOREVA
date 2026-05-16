package thaumcraft.common.entities.ai.misc;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class AIConvertGrass extends EntityAIBase {

    private EntityLiving entity;
    private World world;
    private int convertTimer = 0;

    public AIConvertGrass(EntityLiving living) {
        this.entity = living;
        this.world = living.world;
        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.getRNG().nextInt(250) != 0) return false;
        int x = MathHelper.floor(this.entity.posX);
        int y = MathHelper.floor(this.entity.posY);
        int z = MathHelper.floor(this.entity.posZ);
        return (this.world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.TALLGRASS
                && this.world.getBlockState(new BlockPos(x, y, z)).getBlock()
                    .getMetaFromState(this.world.getBlockState(new BlockPos(x, y, z))) == 1)
            || this.world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() == Blocks.DIRT;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.convertTimer > 0;
    }

    @Override
    public void startExecuting() {
        this.convertTimer = 40;
        this.world.setEntityState(this.entity, (byte) 10);
        this.entity.getNavigator().clearPath();
    }

    @Override
    public void resetTask() {
        this.convertTimer = 0;
    }

    @Override
    public void updateTask() {
        this.convertTimer = Math.max(0, this.convertTimer - 1);
        if (this.convertTimer == 4) {
            int x = MathHelper.floor(this.entity.posX);
            int y = MathHelper.floor(this.entity.posY);
            int z = MathHelper.floor(this.entity.posZ);
            BlockPos pos = new BlockPos(x, y, z);
            Block blockStanding = this.world.getBlockState(pos).getBlock();
            if (blockStanding == Blocks.TALLGRASS) {
                // Convert grass block to taint fibres + change biome
                this.world.playEvent(2001, pos, Block.getStateId(this.world.getBlockState(pos)));
                this.world.setBlockToAir(pos);
                this.world.setBlockState(pos, ConfigBlocks.blockTaintFibres.getDefaultState(), 3);
                Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeTaint);
                this.entity.eatGrassBonus();
            } else if (this.world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() == Blocks.DIRT) {
                // Convert dirt below to taint fibres
                this.world.playEvent(2001, new BlockPos(x, y - 1, z),
                    Block.getStateId(this.world.getBlockState(new BlockPos(x, y - 1, z))));
                this.world.setBlockState(new BlockPos(x, y, z), ConfigBlocks.blockTaintFibres.getDefaultState(), 3);
                Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeTaint);
                this.entity.eatGrassBonus();
            }
        }
    }

    public int getConvertTimer() {
        return this.convertTimer;
    }
}
