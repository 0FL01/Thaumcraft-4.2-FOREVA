package thaumcraft.common.tiles;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.utils.CropUtils;

import java.util.ArrayList;
import java.util.Collections;

public class TileArcaneLampGrowth extends TileThaumcraft implements ITickable, IEssentiaTransport {
    public EnumFacing facing = EnumFacing.byIndex(0);
    private boolean reserve = false;
    public int charges = -1;
    int lx = 0;
    int ly = 0;
    int lz = 0;
    Block lid = Blocks.AIR;
    int lmd = 0;
    final ArrayList<BlockCoordinates> checklist = new ArrayList<>();
    int drawDelay = 0;

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (this.world != null && this.world.isRemote) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
        }
    }

    @Override
    public void update() {
        if (this.world == null) return;
        if (!this.world.isRemote) {
            if (this.charges <= 0) {
                if (this.reserve) {
                    this.charges = 100;
                    this.reserve = false;
                    this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
                } else if (this.drawEssentia()) {
                    this.charges = 100;
                    this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
                }
            }
            if (!this.reserve && this.drawEssentia()) {
                this.reserve = true;
            }
            if (this.charges == 0) {
                this.charges = -1;
                this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            }
            if (this.charges > 0) {
                this.updatePlant();
            }
        }
    }

    boolean isPlant(int x, int y, int z) {
        BlockPos target = new BlockPos(x, y, z);
        boolean flag = this.world.getBlockState(target).getBlock() instanceof IGrowable;
        Material mat = this.world.getBlockState(target).getMaterial();
        return (flag || mat == Material.VINE || mat == Material.PLANTS) && mat != Material.AIR;
    }

    private void updatePlant() {
        BlockPos last = new BlockPos(this.lx, this.ly, this.lz);
        IBlockStateWithMeta current = IBlockStateWithMeta.read(this.world, last);
        if (this.lid != current.block || this.lmd != current.meta) {
            EntityPlayer p = this.world.getClosestPlayer(this.lx, this.ly, this.lz, 32.0, false);
            if (p != null) {
                PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockSparkle(this.lx, this.ly, this.lz, 0x40FF40),
                        new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.lx, this.ly, this.lz, 32.0));
            }
            this.lid = current.block;
            this.lmd = current.meta;
        }
        int distance = 6;
        if (this.checklist.size() == 0) {
            for (int a = -distance; a <= distance; ++a) {
                for (int b = -distance; b <= distance; ++b) {
                    this.checklist.add(new BlockCoordinates(this.pos.getX() + a, this.pos.getY() + distance, this.pos.getZ() + b));
                }
            }
            Collections.shuffle(this.checklist, this.world.rand);
        }
        if (this.checklist.size() == 0) return;
        int x = this.checklist.get(0).x;
        int z = this.checklist.get(0).z;
        int topY = this.checklist.get(0).y;
        this.checklist.remove(0);
        for (int y = topY; y >= this.pos.getY() - distance; --y) {
            BlockPos target = new BlockPos(x, y, z);
            if (this.world.isAirBlock(target) || !this.isPlant(x, y, z) || !(this.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < distance * distance)
                    || CropUtils.isGrownCrop(this.world, target) || !CropUtils.doesLampGrow(this.world, target)) {
                continue;
            }
            --this.charges;
            this.lx = x;
            this.ly = y;
            this.lz = z;
            this.lid = this.world.getBlockState(target).getBlock();
            this.lmd = this.world.getBlockState(target).getBlock().getMetaFromState(this.world.getBlockState(target));
            this.world.scheduleUpdate(target, this.world.getBlockState(target).getBlock(), 1);
            return;
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.facing = EnumFacing.byIndex(nbt.getInteger("orientation"));
        if (this.facing == null) {
            this.facing = EnumFacing.byIndex(0);
        }
        this.reserve = nbt.getBoolean("reserve");
        this.charges = nbt.getInteger("charges");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("orientation", this.facing.getIndex());
        nbt.setBoolean("reserve", this.reserve);
        nbt.setInteger("charges", this.charges);
    }

    boolean drawEssentia() {
        if (++this.drawDelay % 5 != 0) {
            return false;
        }
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.facing);
        if (te instanceof IEssentiaTransport) {
            IEssentiaTransport ic = (IEssentiaTransport) te;
            if (!ic.canOutputTo(this.facing.getOpposite())) {
                return false;
            }
            if (ic.getSuctionAmount(this.facing.getOpposite()) < this.getSuctionAmount(this.facing)
                    && ic.takeEssentia(Aspect.PLANT, 1, this.facing.getOpposite()) == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == this.facing;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face == this.facing;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return Aspect.PLANT;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return face == this.facing && (!this.reserve || this.charges <= 0) ? 128 : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing loc) {
        return 0;
    }

    private static class IBlockStateWithMeta {
        final Block block;
        final int meta;

        private IBlockStateWithMeta(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        static IBlockStateWithMeta read(net.minecraft.world.World world, BlockPos pos) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            return new IBlockStateWithMeta(state.getBlock(), state.getBlock().getMetaFromState(state));
        }
    }
}
