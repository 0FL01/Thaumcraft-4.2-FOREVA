package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.TCSounds;

public class TileCentrifuge extends TileThaumcraft implements ITickable, IAspectContainer, IEssentiaTransport {
    public Aspect aspectOut = null;
    public Aspect aspectIn = null;
    public EnumFacing facing = EnumFacing.NORTH;
    public float rotation = 0.0F;

    private int count = 0;
    private int process = 0;
    private float rotationSpeed = 0.0F;

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.aspectIn = nbt.hasKey("aspectIn") ? Aspect.getAspect(nbt.getString("aspectIn")) : null;
        this.aspectOut = nbt.hasKey("aspectOut") ? Aspect.getAspect(nbt.getString("aspectOut")) : null;
        this.facing = EnumFacing.byIndex(nbt.getInteger("facing"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        if (this.aspectIn != null) nbt.setString("aspectIn", this.aspectIn.getTag());
        if (this.aspectOut != null) nbt.setString("aspectOut", this.aspectOut.getTag());
        nbt.setInteger("facing", this.facing.getIndex());
    }

    @Override
    public void update() {
        if (this.world == null) return;

        if (!this.world.isRemote) {
            if (!this.gettingPower()) {
                if (this.aspectOut == null && this.aspectIn == null && ++this.count % 5 == 0) {
                    this.drawEssentia();
                }
                if (this.process > 0) {
                    --this.process;
                }
                if (this.aspectOut == null && this.aspectIn != null && this.process == 0) {
                    this.processEssentia();
                }
            }
        } else {
            if (this.aspectIn != null && !this.gettingPower() && this.rotationSpeed < 20.0F) {
                this.rotationSpeed += 2.0F;
            }
            if ((this.aspectIn == null || this.gettingPower()) && this.rotationSpeed > 0.0F) {
                this.rotationSpeed -= 0.5F;
            }
            int previous = (int) this.rotation;
            this.rotation += this.rotationSpeed;
            if (this.rotation % 180.0F <= 20.0F && previous % 180 >= 160 && this.rotationSpeed > 0.0F) {
                this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                        TCSounds.PUMP, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }

    void processEssentia() {
        if (this.aspectIn == null || this.aspectIn.isPrimal() || this.aspectIn.getComponents() == null
                || this.aspectIn.getComponents().length < 2) {
            this.aspectIn = null;
            this.markDirtyAndSync();
            return;
        }

        Aspect[] components = this.aspectIn.getComponents();
        this.aspectOut = components[this.world.rand.nextInt(2)];
        this.aspectIn = null;
        this.markDirtyAndSync();
    }

    void drawEssentia() {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), EnumFacing.DOWN);
        if (!(te instanceof IEssentiaTransport)) return;

        IEssentiaTransport transport = (IEssentiaTransport) te;
        if (!transport.canOutputTo(EnumFacing.UP)) return;

        Aspect aspect = null;
        if (transport.getEssentiaAmount(EnumFacing.UP) > 0
                && transport.getSuctionAmount(EnumFacing.UP) < this.getSuctionAmount(EnumFacing.DOWN)
                && this.getSuctionAmount(EnumFacing.DOWN) >= transport.getMinimumSuction()) {
            aspect = transport.getEssentiaType(EnumFacing.UP);
        }

        if (aspect != null && !aspect.isPrimal()
                && transport.getSuctionAmount(EnumFacing.UP) < this.getSuctionAmount(EnumFacing.DOWN)
                && transport.takeEssentia(aspect, 1, EnumFacing.UP) == 1) {
            this.aspectIn = aspect;
            this.process = 39;
            this.markDirtyAndSync();
        }
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.aspectOut != null) list.add(this.aspectOut, 1);
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (tag != null && amount > 0 && this.aspectOut == null) {
            this.aspectOut = tag;
            this.markDirtyAndSync();
            return amount - 1;
        }
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (amount != 1) return false;
        if (this.aspectOut != null && this.aspectOut == tag) {
            this.aspectOut = null;
            this.markDirtyAndSync();
            return true;
        }
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        return aspects != null && aspects.size() == 1
                && this.takeFromContainer(aspects.getAspects()[0], aspects.getAmount(aspects.getAspects()[0]));
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return amount <= 1 && tag == this.aspectOut;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        return aspects != null && aspects.size() == 1
                && this.doesContainerContainAmount(aspects.getAspects()[0], aspects.getAmount(aspects.getAspects()[0]));
    }

    @Override
    public int containerContains(Aspect tag) {
        return tag == this.aspectOut ? 1 : 0;
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag != null && this.aspectOut == null;
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        return face == EnumFacing.UP || face == EnumFacing.DOWN;
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        return face == EnumFacing.DOWN;
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        return face == EnumFacing.UP;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        return face == EnumFacing.DOWN ? (this.gettingPower() ? 0 : (this.aspectIn == null ? 128 : 64)) : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        return this.aspectOut;
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        return this.aspectOut != null ? 1 : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canOutputTo(face) && this.takeFromContainer(aspect, amount) ? amount : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (this.canInputFrom(face) && this.aspectIn == null && aspect != null && !aspect.isPrimal()) {
            this.aspectIn = aspect;
            this.process = 39;
            this.markDirtyAndSync();
            return 1;
        }
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    public boolean gettingPower() {
        return this.world != null && this.world.isBlockPowered(this.pos);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1));
    }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
