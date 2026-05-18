package thaumcraft.common.tiles;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
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
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemCrystalEssence;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.awt.Color;

public class TileEssentiaCrystalizer extends TileThaumcraft implements ITickable, IAspectContainer, IEssentiaTransport {
    public Aspect aspect = null;
    public EnumFacing facing = EnumFacing.DOWN;
    private int count = 0;
    private int progress = 0;
    private static final int PROGRESS_MAX = 200;
    public float spin = 0.0F;
    public float spinInc = 0.0F;
    private float tr = 1.0F;
    private float tg = 1.0F;
    private float tb = 1.0F;
    public float cr = 1.0F;
    public float cg = 1.0F;
    public float cb = 1.0F;
    private int venting = 0;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos, this.pos.add(1, 1, 1));
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.aspect = Aspect.getAspect(nbt.getString("Aspect"));
        this.facing = EnumFacing.byIndex(nbt.getByte("face"));
        if (this.facing == null) {
            this.facing = EnumFacing.DOWN;
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        if (this.aspect != null) {
            nbt.setString("Aspect", this.aspect.getTag());
        }
        nbt.setByte("face", (byte) this.facing.getIndex());
    }

    @Override
    public void update() {
        if (this.world == null) {
            return;
        }

        if (!this.world.isRemote) {
            if (++this.count % 5 == 0 && !gettingPower()) {
                if (this.aspect == null) {
                    fillReservoir();
                    this.progress = 0;
                } else {
                    int vis = VisNetHandler.drainVis(
                            this.world,
                            this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                            Aspect.EARTH,
                            Math.min(20, Math.max(1, (PROGRESS_MAX - this.progress) / 2)));
                    this.progress += 1 + vis * 2;
                }
            }

            if (this.aspect != null && this.progress >= PROGRESS_MAX) {
                eject();
                this.aspect = null;
                this.progress = 0;
                this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
                this.markDirty();
            }
            return;
        }

        if (this.aspect == null) {
            this.tr = 1.0F;
            this.tg = 1.0F;
            this.tb = 1.0F;
        } else {
            Color c = new Color(this.aspect.getColor());
            this.tr = c.getRed() / 220.0F;
            this.tg = c.getGreen() / 220.0F;
            this.tb = c.getBlue() / 220.0F;
        }

        if (this.cr < this.tr) this.cr += 0.05F;
        if (this.cr > this.tr) this.cr -= 0.05F;
        if (this.cg < this.tg) this.cg += 0.05F;
        if (this.cg > this.tg) this.cg -= 0.05F;
        if (this.cb < this.tb) this.cb += 0.05F;
        if (this.cb > this.tb) this.cb -= 0.05F;

        this.spin += this.spinInc;
        if (this.spin > 360.0F) {
            this.spin -= 360.0F;
        }

        if (this.aspect != null && this.spinInc < 20.0F && !gettingPower()) {
            this.spinInc += 0.1F;
            if (this.spinInc > 20.0F) this.spinInc = 20.0F;
        } else if ((this.aspect == null || gettingPower()) && this.spinInc > 0.0F) {
            this.spinInc -= 0.2F;
            if (this.spinInc < 0.0F) this.spinInc = 0.0F;
        }

        if (this.venting <= 0) {
            return;
        }
        this.venting--;

        float fx = 0.1F - this.world.rand.nextFloat() * 0.2F;
        float fy = 0.1F - this.world.rand.nextFloat() * 0.2F;
        float fz = 0.1F - this.world.rand.nextFloat() * 0.2F;
        float fx2 = 0.1F - this.world.rand.nextFloat() * 0.2F;
        float fy2 = 0.1F - this.world.rand.nextFloat() * 0.2F;
        float fz2 = 0.1F - this.world.rand.nextFloat() * 0.2F;
        EnumFacing out = this.facing.getOpposite();
        Thaumcraft.proxy.drawVentParticles(
                this.world,
                this.pos.getX() + 0.5F + fx + out.getXOffset() / 2.1F,
                this.pos.getY() + 0.5F + fy + out.getYOffset() / 2.1F,
                this.pos.getZ() + 0.5F + fz + out.getZOffset() / 2.1F,
                out.getXOffset() / 4.0F + fx2,
                out.getYOffset() / 4.0F + fy2,
                out.getZOffset() / 4.0F + fz2,
                0xFFFFFF);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id >= 0) {
            if (this.world != null && this.world.isRemote) {
                this.venting = 7;
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public AspectList getAspects() {
        AspectList list = new AspectList();
        if (this.aspect != null) {
            list.add(this.aspect, 1);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (amount <= 0) {
            return amount;
        }
        if (this.aspect == null) {
            this.aspect = tag;
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            this.markDirty();
            return amount - 1;
        }
        return amount;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (this.aspect != null && amount == 1 && this.aspect == tag) {
            this.aspect = null;
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            this.markDirty();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return amt == 1 && this.aspect != null && this.aspect == tag;
    }

    @Override
    @Deprecated
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (this.aspect == null || this.aspect != tt || ot.getAmount(tt) != 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return this.aspect != null && tag == this.aspect ? 1 : 0;
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
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
    public Aspect getSuctionType(EnumFacing loc) {
        return null;
    }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        if (gettingPower()) {
            return 0;
        }
        return loc == this.facing && this.aspect == null ? 128 : 64;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        return this.aspect;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) {
        return this.aspect == null ? 0 : 1;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
    }

    private void eject() {
        if (ConfigItems.itemCrystalEssence == null) {
            return;
        }
        ItemStack stack = new ItemStack(ConfigItems.itemCrystalEssence, 1, 0);
        ((ItemCrystalEssence) stack.getItem()).setAspects(stack, new AspectList().add(this.aspect, 1));
        EnumFacing out = this.facing.getOpposite();
        TileEntity inventory = this.world.getTileEntity(this.pos.offset(out));
        if (inventory instanceof IInventory) {
            stack = InventoryUtils.placeItemStackIntoInventory(stack, (IInventory) inventory, this.facing.getIndex(), true);
        }
        if (!stack.isEmpty()) {
            spawnItem(stack);
        }
        this.world.playSound(
                null,
                this.pos,
                net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH,
                SoundCategory.BLOCKS,
                0.25F,
                2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
    }

    private void spawnItem(ItemStack stack) {
        EnumFacing out = this.facing.getOpposite();
        EntityItem entity = new EntityItem(
                this.world,
                this.pos.getX() + 0.5D + out.getXOffset() * 0.65D,
                this.pos.getY() + 0.5D + out.getYOffset() * 0.65D,
                this.pos.getZ() + 0.5D + out.getZOffset() * 0.65D,
                stack);
        entity.motionX = out.getXOffset() * 0.04F;
        entity.motionY = out.getYOffset() * 0.04F;
        entity.motionZ = out.getZOffset() * 0.04F;
        this.world.addBlockEvent(this.pos, this.getBlockType(), 0, 0);
        this.world.spawnEntity(entity);
    }

    private void fillReservoir() {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(
                this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.facing);
        if (!(te instanceof IEssentiaTransport)) {
            return;
        }
        IEssentiaTransport transport = (IEssentiaTransport) te;
        EnumFacing remote = this.facing.getOpposite();
        if (!transport.canOutputTo(remote)) {
            return;
        }

        Aspect pulled = null;
        if (transport.getEssentiaAmount(remote) > 0
                && transport.getSuctionAmount(remote) < this.getSuctionAmount(this.facing)
                && this.getSuctionAmount(this.facing) >= transport.getMinimumSuction()) {
            pulled = transport.getEssentiaType(remote);
        }
        if (pulled != null && transport.getSuctionAmount(remote) < this.getSuctionAmount(this.facing)) {
            this.addToContainer(pulled, transport.takeEssentia(pulled, 1, remote));
        }
    }

    private boolean gettingPower() {
        return this.world.isBlockPowered(this.pos);
    }
}
