package thaumcraft.common.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ITickable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.lib.TCSounds;

public class TileEssentiaReservoir extends TileThaumcraft implements ITickable, IAspectSource, IEssentiaTransport, IWandable {
    public AspectList essentia = new AspectList();
    public int maxAmount = 256;
    public EnumFacing facing = EnumFacing.DOWN;
    public float colorR = 1.0f;
    public float colorG = 1.0f;
    public float colorB = 1.0f;
    public Aspect displayAspect = null;
    private int count = 0;
    private float targetR = 1.0F;
    private float targetG = 1.0F;
    private float targetB = 1.0F;
    private float stepR;
    private float stepG;
    private float stepB;

    @Override
    public void update() {
        if (this.world == null) {
            return;
        }
        ++this.count;
        if (!this.world.isRemote && this.count % 5 == 0 && this.essentia.visSize() < this.maxAmount) {
            fillReservoir();
        }
        int amount = this.essentia.visSize();
        if (this.world.isRemote && amount > 0) {
            if (this.world.rand.nextInt(500 - amount) == 0) {
                this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                        TCSounds.CREAK, SoundCategory.BLOCKS, 1.0F, 1.4F + this.world.rand.nextFloat() * 0.2F, false);
            }
            if (this.count % 20 == 0 && this.essentia.size() > 0) {
                Aspect[] aspects = this.essentia.getAspects();
                this.displayAspect = aspects[this.count / 20 % aspects.length];
                int color = this.displayAspect.getColor();
                this.targetR = ((color >> 16) & 0xFF) / 255.0F;
                this.targetG = ((color >> 8) & 0xFF) / 255.0F;
                this.targetB = (color & 0xFF) / 255.0F;
                this.stepR = (this.colorR - this.targetR) / 20.0F;
                this.stepG = (this.colorG - this.targetG) / 20.0F;
                this.stepB = (this.colorB - this.targetB) / 20.0F;
            }
            if (this.displayAspect == null) {
                this.targetR = 1.0F;
                this.targetG = 1.0F;
                this.targetB = 1.0F;
                this.stepR = 0.0F;
                this.stepG = 0.0F;
                this.stepB = 0.0F;
            } else {
                this.colorR -= this.stepR;
                this.colorG -= this.stepG;
                this.colorB -= this.stepB;
            }
        }
    }

    private void fillReservoir() {
        TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.facing);
        if (!(tile instanceof IEssentiaTransport)) return;
        IEssentiaTransport transport = (IEssentiaTransport) tile;
        EnumFacing remote = this.facing.getOpposite();
        if (!transport.canOutputTo(remote)) return;
        if (transport.getEssentiaAmount(remote) <= 0) return;
        if (transport.getSuctionAmount(remote) >= this.getSuctionAmount(this.facing)) return;
        if (this.getSuctionAmount(this.facing) < transport.getMinimumSuction()) return;
        Aspect aspect = transport.getEssentiaType(remote);
        if (aspect != null) {
            this.addToContainer(aspect, transport.takeEssentia(aspect, 1, remote));
        }
    }

    // --- NBT ---

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        this.essentia = new AspectList();
        this.essentia.readFromNBT(nbt);
        if (this.essentia.visSize() > this.maxAmount) this.essentia = new AspectList();
        if (nbt.hasKey("maxAmount")) this.maxAmount = nbt.getInteger("maxAmount");
        if (nbt.hasKey("face")) {
            this.facing = EnumFacing.byIndex(nbt.getByte("face"));
        } else if (nbt.hasKey("facing")) {
            this.facing = EnumFacing.byIndex(nbt.getByte("facing"));
        }
        if (this.facing == null) this.facing = EnumFacing.DOWN;
        if (nbt.hasKey("displayAspect")) {
            this.displayAspect = Aspect.getAspect(nbt.getString("displayAspect"));
        } else {
            this.displayAspect = null;
        }
        if (nbt.hasKey("colorR")) this.colorR = nbt.getFloat("colorR");
        if (nbt.hasKey("colorG")) this.colorG = nbt.getFloat("colorG");
        if (nbt.hasKey("colorB")) this.colorB = nbt.getFloat("colorB");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        if (this.essentia != null) {
            this.essentia.writeToNBT(nbt);
        }
        nbt.setByte("face", (byte) this.facing.getIndex());
        if (this.displayAspect != null) {
            nbt.setString("displayAspect", this.displayAspect.getTag());
        }
        nbt.setFloat("colorR", this.colorR);
        nbt.setFloat("colorG", this.colorG);
        nbt.setFloat("colorB", this.colorB);
    }

    // --- IAspectContainer ---

    @Override
    public AspectList getAspects() {
        return this.essentia;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.essentia = aspects != null ? aspects : new AspectList();
        this.markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag != null && this.containerContains(null) < this.maxAmount;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return amount;
        int total = this.containerContains(null);
        int add = Math.min(amount, this.maxAmount - total);
        if (add <= 0) return amount;
        this.essentia.add(tag, add);
        this.markDirty();
        return amount - add;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return false;
        if (this.containerContains(tag) < amount) return false;
        this.essentia.remove(tag, amount);
        this.markDirty();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList list) {
        if (list == null) return false;
        for (Aspect aspect : list.getAspects()) {
            if (this.essentia.getAmount(aspect) < list.getAmount(aspect)) {
                return false;
            }
        }
        for (Aspect aspect : list.getAspects()) {
            this.essentia.remove(aspect, list.getAmount(aspect));
        }
        this.markDirty();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return this.containerContains(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList list) {
        if (list == null) return false;
        for (Aspect aspect : list.getAspects()) {
            if (this.essentia.getAmount(aspect) < list.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        if (tag == null) {
            int total = 0;
            for (Aspect a : this.essentia.getAspects()) {
                if (a != null) total += this.essentia.getAmount(a);
            }
            return total;
        }
        return this.essentia.getAmount(tag);
    }

    // --- IEssentiaTransport ---

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
        return face == this.facing;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        return this.containerContains(null) < this.maxAmount ? 24 : 0;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return this.essentia.visSize() > 0 && loc == null ? this.essentia.getAspects()[0] : null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.containerContains(null);
    }

    @Override
    public int getMinimumSuction() {
        return 24;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!this.canOutputTo(face)) return 0;
        int taken = Math.min(amount, this.containerContains(aspect));
        if (taken <= 0) return 0;
        this.takeFromContainer(aspect, taken);
        return taken;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!this.canInputFrom(face)) return 0;
        int leftover = this.addToContainer(aspect, amount);
        return amount - leftover;
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        EnumFacing clicked = EnumFacing.byIndex(side);
        if (clicked == null) return 0;
        this.facing = player != null && player.isSneaking() ? clicked : clicked.getOpposite();
        if (player != null) player.swingArm(EnumHand.MAIN_HAND);
        this.markDirty();
        if (world != null && !world.isRemote) world.notifyBlockUpdate(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) { return wandstack; }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {}

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {}
}
