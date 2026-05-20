package thaumcraft.common.tiles;

import java.util.Random;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.TCSounds;

public class TileTube extends TileThaumcraft implements ITickable, IEssentiaTransport, IWandable {
    public EnumFacing facing = EnumFacing.NORTH;
    public boolean[] openSides = new boolean[]{true, true, true, true, true, true};
    protected Aspect essentiaType = null;
    protected int essentiaAmount = 0;
    protected Aspect suctionType = null;
    protected int suction = 0;
    protected int venting = 0;
    protected int count = 0;
    protected int ventColor = 0;

    @Override
    public void update() {
        if (this.world == null) return;
        if (this.venting > 0) {
            --this.venting;
        }
        if (this.count == 0) {
            this.count = this.world.rand.nextInt(10);
        }
        if (!this.world.isRemote) {
            if (this.venting <= 0) {
                if (++this.count % 2 == 0) {
                    this.calculateSuction(null, false, false);
                    this.checkVenting();
                    if (this.essentiaType != null && this.essentiaAmount == 0) {
                        this.essentiaType = null;
                    }
                }
                if (this.count % 5 == 0 && this.suction > 0) {
                    this.equalizeWithNeighbours(false);
                }
            }
        } else if (this.venting > 0) {
            Random random = new Random(this.hashCode() * 4L);
            float rp = random.nextFloat() * 360.0F;
            float ry = random.nextFloat() * 360.0F;
            double fx = -MathHelper.sin(ry / 180.0F * (float) Math.PI) * MathHelper.cos(rp / 180.0F * (float) Math.PI);
            double fz = MathHelper.cos(ry / 180.0F * (float) Math.PI) * MathHelper.cos(rp / 180.0F * (float) Math.PI);
            double fy = -MathHelper.sin(rp / 180.0F * (float) Math.PI);
            Thaumcraft.proxy.drawVentParticles(
                    this.world,
                    this.pos.getX() + 0.5D,
                    this.pos.getY() + 0.5D,
                    this.pos.getZ() + 0.5D,
                    fx / 5.0D,
                    fy / 5.0D,
                    fz / 5.0D,
                    this.ventColor);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.essentiaType = Aspect.getAspect(nbt.getString("type"));
        this.essentiaAmount = nbt.getInteger("amount");
        this.suctionType = Aspect.getAspect(nbt.getString("stype"));
        this.suction = nbt.getInteger("samount");
        this.facing = EnumFacing.byIndex(nbt.getInteger("side"));
        if (this.facing == null) this.facing = EnumFacing.NORTH;
        byte[] sides = nbt.getByteArray("open");
        if (sides.length == 6) {
            for (int i = 0; i < 6; ++i) this.openSides[i] = sides[i] == 1;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        if (this.essentiaType != null) nbt.setString("type", this.essentiaType.getTag());
        if (this.suctionType != null) nbt.setString("stype", this.suctionType.getTag());
        nbt.setInteger("amount", this.essentiaAmount);
        nbt.setInteger("samount", this.suction);
        nbt.setInteger("side", this.facing.getIndex());
        byte[] sides = new byte[6];
        for (int i = 0; i < 6; ++i) sides[i] = (byte) (this.openSides[i] ? 1 : 0);
        nbt.setByteArray("open", sides);
    }

    protected void calculateSuction(Aspect filter, boolean restrict, boolean directional) {
        this.suction = 0;
        this.suctionType = null;
        for (EnumFacing dir : EnumFacing.values()) {
            try {
                if (directional && this.facing != dir.getOpposite()) continue;
                if (!this.isConnectable(dir)) continue;
                TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world,
                        this.pos.getX(), this.pos.getY(), this.pos.getZ(), dir);
                if (!(tile instanceof IEssentiaTransport)) continue;
                IEssentiaTransport transport = (IEssentiaTransport) tile;
                EnumFacing remote = dir.getOpposite();
                Aspect remoteSuction = transport.getSuctionType(remote);
                if (filter != null && remoteSuction != null && remoteSuction != filter) continue;
                if (filter == null && this.getEssentiaAmount(dir) > 0 && remoteSuction != null
                        && this.getEssentiaType(dir) != remoteSuction) continue;
                int amount = transport.getSuctionAmount(remote);
                if (amount <= 0 || amount <= this.suction + 1) continue;
                this.setSuction(remoteSuction == null ? filter : remoteSuction, restrict ? amount / 2 : amount - 1);
            } catch (RuntimeException ignored) {
            }
        }
    }

    protected void checkVenting() {
        for (EnumFacing dir : EnumFacing.values()) {
            try {
                if (!this.isConnectable(dir)) continue;
                TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world,
                        this.pos.getX(), this.pos.getY(), this.pos.getZ(), dir);
                if (!(tile instanceof IEssentiaTransport)) continue;
                IEssentiaTransport transport = (IEssentiaTransport) tile;
                int remoteSuction = transport.getSuctionAmount(dir.getOpposite());
                if (this.suction > 0 && (remoteSuction == this.suction || remoteSuction == this.suction - 1)
                        && this.suctionType != transport.getSuctionType(dir.getOpposite())) {
                    int c = -1;
                    if (this.suctionType != null) {
                        c = Config.aspectOrder.indexOf(this.suctionType);
                    }
                    this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 1, c);
                    this.venting = 40;
                    return;
                }
            } catch (RuntimeException ignored) {
            }
        }
    }

    protected void equalizeWithNeighbours(boolean directional) {
        if (this.essentiaAmount > 0) return;
        for (EnumFacing dir : EnumFacing.values()) {
            try {
                if (directional && this.facing == dir.getOpposite()) continue;
                if (!this.isConnectable(dir)) continue;
                TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world,
                        this.pos.getX(), this.pos.getY(), this.pos.getZ(), dir);
                if (!(tile instanceof IEssentiaTransport)) continue;
                IEssentiaTransport transport = (IEssentiaTransport) tile;
                EnumFacing remote = dir.getOpposite();
                if (!transport.canOutputTo(remote)) continue;
                Aspect aspect = this.getSuctionType(null);
                Aspect remoteEssentia = transport.getEssentiaType(remote);
                if (aspect != null && remoteEssentia != null && aspect != remoteEssentia) continue;
                if (this.getSuctionAmount(null) <= transport.getSuctionAmount(remote)) continue;
                if (this.getSuctionAmount(null) < transport.getMinimumSuction()) continue;
                if (aspect == null) aspect = remoteEssentia != null ? remoteEssentia : transport.getEssentiaType(null);
                if (aspect == null) continue;
                int taken = transport.takeEssentia(aspect, 1, remote);
                if (taken > 0 && this.addEssentia(aspect, taken, dir) > 0) {
                    if (this.world.rand.nextInt(100) == 0) {
                        this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 0, 0);
                    }
                    this.markDirtyAndSync();
                    return;
                }
            } catch (RuntimeException ignored) {
            }
        }
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face != null && this.openSides[face.getIndex()];
    }

    @Override
    public boolean canInputFrom(EnumFacing face) { return this.isConnectable(face); }

    @Override
    public boolean canOutputTo(EnumFacing face) { return this.isConnectable(face); }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        this.suctionType = aspect;
        this.suction = amount;
    }

    @Override
    public Aspect getSuctionType(EnumFacing loc) { return this.suctionType; }

    @Override
    public int getSuctionAmount(EnumFacing loc) { return this.suction; }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) { return this.essentiaType; }

    @Override
    public int getEssentiaAmount(EnumFacing loc) { return this.essentiaAmount; }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (this.canOutputTo(face) && this.essentiaType == aspect && this.essentiaAmount > 0 && amount > 0) {
            --this.essentiaAmount;
            if (this.essentiaAmount <= 0) this.essentiaType = null;
            this.markDirtyAndSync();
            return 1;
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (this.canInputFrom(face) && this.essentiaAmount == 0 && amount > 0) {
            this.essentiaType = aspect;
            this.essentiaAmount = 1;
            this.markDirtyAndSync();
            return 1;
        }
        return 0;
    }

    @Override
    public int getMinimumSuction() { return 0; }

    @Override
    public boolean renderExtendedTube() { return false; }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 0) {
            if (this.world != null && this.world.isRemote) {
                this.world.playSound(
                        this.pos.getX() + 0.5D,
                        this.pos.getY() + 0.5D,
                        this.pos.getZ() + 0.5D,
                        TCSounds.CREAK,
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.3F + this.world.rand.nextFloat() * 0.2F,
                        false);
            }
            return true;
        }
        if (id == 1) {
            if (this.world != null && this.world.isRemote) {
                if (this.venting <= 0) {
                    this.world.playSound(
                            this.pos.getX() + 0.5D,
                            this.pos.getY() + 0.5D,
                            this.pos.getZ() + 0.5D,
                            SoundEvents.BLOCK_FIRE_EXTINGUISH,
                            SoundCategory.BLOCKS,
                            0.1F,
                            1.0F + this.world.rand.nextFloat() * 0.1F,
                            false);
                }
                this.venting = 50;
                this.ventColor = type == -1 || type >= Config.aspectOrder.size() ? 0xAAAAAA : Config.aspectOrder.get(type).getColor();
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        EnumFacing clicked = EnumFacing.byIndex(side);
        if (clicked == null) return 0;
        if (player != null && player.isSneaking()) {
            int next = this.facing.getIndex();
            for (int i = 0; i < 6; ++i) {
                EnumFacing candidate = EnumFacing.byIndex((next + 1 + i) % 6);
                if (candidate != null && this.isConnectable(candidate.getOpposite())) {
                    this.facing = candidate;
                    break;
                }
            }
        } else {
            this.openSides[clicked.getIndex()] = !this.openSides[clicked.getIndex()];
            TileEntity neighbour = world.getTileEntity(this.pos.offset(clicked));
            if (neighbour instanceof TileTube) {
                ((TileTube) neighbour).openSides[clicked.getOpposite().getIndex()] = this.openSides[clicked.getIndex()];
                neighbour.markDirty();
            }
        }
        if (world != null) {
            world.playSound(null, this.pos, TCSounds.TOOL, SoundCategory.BLOCKS, 0.5F, 0.9F + world.rand.nextFloat() * 0.2F);
        }
        if (player != null) player.swingArm(EnumHand.MAIN_HAND);
        this.markDirtyAndSync();
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) { return wandstack; }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {}

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {}

    public void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
