package thaumcraft.common.blocks;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class BlockFluxGoo extends BlockFluidFinite {

    /** Non-null zero-size AABB — replaces NULL_AABB which is null in 1.12.2. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    private static final int THAUMIC_SLIME_GROWTH_INTERVAL_TICKS = 20;

    public BlockFluxGoo() {
        super(ConfigBlocks.FLUXGOO, Config.fluxGoomaterial);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setHardness(0.0F);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(MathHelper.clamp(meta, 0, this.getMaxRenderHeightMeta()));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return MathHelper.clamp(state.getValue(BlockFluidBase.LEVEL), 0, this.getMaxRenderHeightMeta());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        int meta = this.getMetaFromState(state);
        if (entity instanceof EntityThaumicSlime) {
            EntityThaumicSlime slime = (EntityThaumicSlime) entity;
            if (!world.isRemote
                    && slime.getSlimeSize() < meta
                    && slime.ticksExisted % THAUMIC_SLIME_GROWTH_INTERVAL_TICKS == 0
                    && world.rand.nextBoolean()) {
                slime.setSlimeSize(slime.getSlimeSize() + 1);
                if (meta > 1) {
                    world.setBlockState(pos, this.getStateFromMeta(meta - 1), 3);
                } else {
                    world.setBlockToAir(pos);
                }
            }
            return;
        }

        float quanta = this.getQuantaPercentage(world, pos);
        entity.motionX *= 1.0F - quanta;
        entity.motionZ *= 1.0F - quanta;

        if (!world.isRemote && entity instanceof EntityLivingBase && Config.potionVisExhaust != null) {
            PotionEffect pe = new PotionEffect(Config.potionVisExhaust, 600, meta / 3, true, true);
            pe.getCurativeItems().clear();
            ((EntityLivingBase) entity).addPotionEffect(pe);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);
        if (world.isRemote || world.getBlockState(pos).getBlock() != this) {
            return;
        }

        int meta = this.getMetaFromState(world.getBlockState(pos));
        BlockPos above = pos.up();
        if (meta >= 2 && meta < 6 && world.isAirBlock(above) && rand.nextInt(25) == 0) {
            world.setBlockToAir(pos);
            this.spawnThaumicSlime(world, pos, 1);
        } else if (meta >= 6 && world.isAirBlock(above)) {
            if (rand.nextInt(25) == 0) {
                world.setBlockToAir(pos);
                this.spawnThaumicSlime(world, pos, 2);
            } else if (Config.taintFromFlux && rand.nextInt(50) == 0) {
                Utils.setBiomeAt(world, pos.getX(), pos.getZ(), ThaumcraftWorldGenerator.biomeTaint);
                world.setBlockState(pos, ConfigBlocks.blockTaintFibres.getStateFromMeta(0), 3);
                world.addBlockEvent(pos, ConfigBlocks.blockTaintFibres, 1, 0);
            }
        } else if (rand.nextInt(30) == 0) {
            if (meta == 0) {
                world.setBlockToAir(pos);
            } else {
                world.setBlockState(pos, this.getStateFromMeta(meta - 1), 3);
                if (rand.nextBoolean() && world.isAirBlock(above)) {
                    world.setBlockState(above, ConfigBlocks.blockFluxGas.getStateFromMeta(0), 3);
                }
            }
        }
    }

    private void spawnThaumicSlime(World world, BlockPos pos, int size) {
        EntityThaumicSlime slime = new EntityThaumicSlime(world);
        slime.setLocationAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        slime.setSlimeSize(size);
        world.spawnEntity(slime);
        world.playSound(null, slime.posX, slime.posY, slime.posZ, TCSounds.GORE, SoundCategory.HOSTILE, 1.0F, 1.0F);
    }

    @Override
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
        return originalColor;
    }

    public int getQuanta() {
        return this.quantaPerBlock;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
        return this.getMetaFromState(world.getBlockState(pos)) < 2;
    }
}
