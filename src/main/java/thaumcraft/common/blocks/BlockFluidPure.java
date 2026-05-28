package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

public class BlockFluidPure extends BlockFluidClassic {

    /** Non-null zero-size AABB.  Block.NULL_AABB is null in 1.12.2; callers like
     *  WalkNodeProcessor.getSafePoint do not null-check, so we must never return null. */
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public static final int SOURCE_LEVEL = 0;

    public BlockFluidPure() {
        super(ConfigBlocks.FLUIDPURE, Material.WATER);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    /** Override required: BlockFluidBase.getBoundingBox returns null (Block.NULL_AABB),
     *  which causes NPE in vanilla WalkNodeProcessor.getSafePoint. */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ZERO_AABB;
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote
                || !(entity instanceof EntityPlayer)
                || !this.isSourceBlock((IBlockAccess) world, pos)
                || Config.potionWarpWard == null) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        if (player.isPotionActive(Config.potionWarpWard)) {
            return;
        }

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        int warp = knowledge == null ? 0 : knowledge.getWarpPerm();
        int divisor = 1;
        if (warp > 0) {
            divisor = (int) Math.sqrt(warp);
            if (divisor < 1) divisor = 1;
        }
        player.addPotionEffect(new PotionEffect(Config.potionWarpWard, Math.min(32000, 200000 / divisor), 0, true, true));
        world.setBlockToAir(pos);
    }
}
