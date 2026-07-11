package thaumcraft.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

public class TileWarded extends TileThaumcraft {
    public int owner = 0;
    public Block block = Blocks.AIR;
    public byte blockMd = 0;
    public boolean safeToRemove = false;
    public byte light = 0;

    public IBlockState getStoredState() {
        Block stored = this.block;
        if (stored == null || stored == Blocks.AIR || stored == ConfigBlocks.blockWarded) {
            stored = Blocks.STONE;
        }
        try {
            return stored.getStateFromMeta(this.blockMd & 255);
        } catch (RuntimeException ignored) {
            return stored.getDefaultState();
        }
    }

    public void setStoredBlock(IBlockState state, int light, String ownerName) {
        this.block = state.getBlock();
        this.blockMd = (byte) this.block.getMetaFromState(state);
        this.light = (byte) light;
        this.owner = ownerName != null ? ownerName.hashCode() : 0;
        this.markDirty();
    }

    public void restoreBlock(World world, BlockPos pos) {
        this.safeToRemove = true;
        world.setBlockState(pos, this.getStoredState(), 3);
        world.notifyNeighborsRespectDebug(pos, this.getStoredState().getBlock(), false);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        String name = nbt.getString("blockName");
        if (!name.isEmpty()) {
            this.block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        }
        if (this.block == null || this.block == Blocks.AIR || this.block == ConfigBlocks.blockWarded) {
            this.block = Blocks.STONE;
        }
        this.blockMd = nbt.getByte("md");
        this.light = nbt.getByte("ll");
        this.owner = nbt.getInteger("oi");
        if (this.owner == 0 && nbt.hasKey("owner")) {
            this.owner = nbt.getString("owner").hashCode();
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        if (this.block != null && this.block.getRegistryName() != null) {
            nbt.setString("blockName", this.block.getRegistryName().toString());
        }
        nbt.setByte("md", this.blockMd);
        nbt.setByte("ll", this.light);
        nbt.setInteger("oi", this.owner);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        this.requestRenderUpdate();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.requestRenderUpdate();
    }

    private void requestRenderUpdate() {
        if (this.world != null && this.pos != null) {
            this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
            if (Thaumcraft.proxy != null) {
                Thaumcraft.proxy.refreshWardedBlockRender(this.world, this.pos);
            }
        }
    }

}
