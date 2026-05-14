package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

public class BlockFluidDeath extends BlockFluidFinite {

    public static final int FULL_LEVEL = 3;

    public BlockFluidDeath() {
        super(ConfigBlocks.FLUIDDEATH, Material.WATER);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setQuantaPerBlock(4);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            int level = state.getValue(BlockFluidBase.LEVEL);
            entity.attackEntityFrom(DamageSourceThaumcraft.dissolve, (float) level + 1.0F);
        }
    }
}
