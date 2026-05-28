package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

public class BlockFluidDeath extends BlockFluidFinite {

    /** Non-null zero-size AABB.  Block.NULL_AABB is null in 1.12.2; callers like
     *  WalkNodeProcessor.getSafePoint do not null-check, so we must never return null. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public static final int FULL_LEVEL = 3;

    public BlockFluidDeath() {
        super(ConfigBlocks.FLUIDDEATH, Material.WATER);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setQuantaPerBlock(4);
    }

    /** Override required: BlockFluidBase.getBoundingBox returns null (Block.NULL_AABB),
     *  which causes NPE in vanilla WalkNodeProcessor.getSafePoint. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            int level = state.getValue(BlockFluidBase.LEVEL);
            entity.attackEntityFrom(DamageSourceThaumcraft.dissolve, (float) level + 1.0F);
        }
    }
}
