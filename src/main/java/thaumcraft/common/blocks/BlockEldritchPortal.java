package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.dim.TeleporterThaumcraft;
import thaumcraft.common.tiles.TileEldritchPortal;

public class BlockEldritchPortal extends Block {

    public BlockEldritchPortal() {
        super(Material.PORTAL);
        this.setHardness(0.0f);
        this.setLightLevel(1.0f);
        this.setDefaultState(this.blockState.getBaseState());
        this.setTickRandomly(false);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEldritchPortal();
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && entity.getRidingEntity() == null && !entity.isBeingRidden()) {
            // Teleport players to Outer Lands dimension
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                // Check cooldown to prevent rapid teleport loops
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEldritchPortal) {
                    TileEldritchPortal portal = (TileEldritchPortal) te;
                    long now = world.getTotalWorldTime();
                    if (now - portal.lastTeleport < 40) return; // 2 second cooldown
                    portal.lastTeleport = now;
                }

                MinecraftServer server = player.getServer();
                if (server != null) {
                    int targetDim = Config.dimensionOuterId; // Eldritch dimension ID from config

                    if (player.dimension != targetDim) {
                        // Clamp position to valid area in target dimension
                        TeleporterThaumcraft teleporter = new TeleporterThaumcraft(server.getWorld(targetDim));
                        player.changeDimension(targetDim, teleporter);
                    } else {
                        // Already in dimension - teleport to overworld
                        player.changeDimension(0, new TeleporterThaumcraft(server.getWorld(0)));
                    }
                }
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        // Portal breaks if center altar (blockEldritch meta 3) is missing below
        if (world.getBlockState(pos.down()).getBlock() != ConfigBlocks.blockEldritch) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }
}
