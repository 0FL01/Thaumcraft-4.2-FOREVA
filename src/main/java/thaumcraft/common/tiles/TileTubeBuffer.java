package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.lib.TCSounds;

public class TileTubeBuffer extends TileThaumcraft implements ITickable, IAspectContainer, IEssentiaTransport, IWandable {
    public AspectList aspects = new AspectList();
    public boolean[] openSides = new boolean[]{true, true, true, true, true, true};
    public byte[] chokedSides = new byte[]{0, 0, 0, 0, 0, 0};
    private static final int MAX_AMOUNT = 8;
    private int count = 0;
    private int bellows = -1;

    @Override
    public void update() {
        if (this.world == null) return;
        ++this.count;
        if (this.bellows < 0 || this.count % 20 == 0) this.bellows = TileBellows.getBellows(this.world, this.pos, EnumFacing.values());
        if (!this.world.isRemote && this.count % 5 == 0 && this.aspects.visSize() < MAX_AMOUNT) this.fillBuffer();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.aspects = new AspectList();
        this.aspects.readFromNBT(nbt);
        byte[] sides = nbt.getByteArray("open");
        if (sides.length == 6) for (int i = 0; i < 6; ++i) this.openSides[i] = sides[i] == 1;
        byte[] choke = nbt.getByteArray("choke");
        if (choke.length == 6) this.chokedSides = choke;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        this.aspects.writeToNBT(nbt);
        byte[] sides = new byte[6];
        for (int i = 0; i < 6; ++i) sides[i] = (byte) (this.openSides[i] ? 1 : 0);
        nbt.setByteArray("open", sides);
        nbt.setByteArray("choke", this.chokedSides);
    }

    private void fillBuffer() {
        for (EnumFacing dir : EnumFacing.values()) {
            TileEntity tile = ThaumcraftApiHelper.getConnectableTile(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), dir);
            if (!(tile instanceof IEssentiaTransport)) continue;
            IEssentiaTransport transport = (IEssentiaTransport) tile;
            EnumFacing remote = dir.getOpposite();
            if (transport.getEssentiaAmount(remote) <= 0) continue;
            if (transport.getSuctionAmount(remote) >= this.getSuctionAmount(dir)) continue;
            if (this.getSuctionAmount(dir) < transport.getMinimumSuction()) continue;
            Aspect aspect = transport.getEssentiaType(remote);
            if (aspect == null) aspect = transport.getEssentiaType(null);
            if (aspect == null) continue;
            this.addToContainer(aspect, transport.takeEssentia(aspect, 1, remote));
            return;
        }
    }

    @Override
    public AspectList getAspects() { return this.aspects; }

    @Override
    public void setAspects(AspectList aspects) { this.aspects = aspects == null ? new AspectList() : aspects.copy(); }

    @Override
    public boolean doesContainerAccept(Aspect tag) { return tag != null; }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (tag == null || amount != 1 || this.aspects.visSize() >= MAX_AMOUNT) return amount;
        this.aspects.add(tag, 1);
        this.markDirtyAndSync();
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == null || this.aspects.getAmount(tag) < amount) return false;
        this.aspects.remove(tag, amount);
        this.markDirtyAndSync();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) { return false; }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) { return this.aspects.getAmount(tag) >= amount; }

    @Override
    public boolean doesContainerContain(AspectList ot) { return false; }

    @Override
    public int containerContains(Aspect tag) { return this.aspects.getAmount(tag); }

    @Override
    public boolean isConnectable(EnumFacing face) { return face != null && this.openSides[face.getIndex()]; }

    @Override
    public boolean canInputFrom(EnumFacing face) { return this.isConnectable(face); }

    @Override
    public boolean canOutputTo(EnumFacing face) { return this.isConnectable(face); }

    @Override
    public void setSuction(Aspect aspect, int amount) {}

    @Override
    public Aspect getSuctionType(EnumFacing loc) { return null; }

    @Override
    public int getSuctionAmount(EnumFacing loc) {
        int index = loc == null ? 0 : loc.getIndex();
        if (this.chokedSides[index] == 2) return 0;
        return this.bellows <= 0 || this.chokedSides[index] == 1 ? 1 : this.bellows * 32;
    }

    @Override
    public Aspect getEssentiaType(EnumFacing loc) {
        Aspect[] present = this.aspects.getAspects();
        return present.length > 0 ? present[this.world == null ? 0 : this.world.rand.nextInt(present.length)] : null;
    }

    @Override
    public int getEssentiaAmount(EnumFacing loc) { return this.aspects.visSize(); }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        if (!this.canOutputTo(face)) return 0;
        int take = Math.min(amount, this.aspects.getAmount(aspect));
        return take > 0 && this.takeFromContainer(aspect, take) ? take : 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        return this.canInputFrom(face) ? amount - this.addToContainer(aspect, amount) : 0;
    }

    @Override
    public int getMinimumSuction() { return 0; }

    @Override
    public boolean renderExtendedTube() { return false; }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        EnumFacing clicked = EnumFacing.byIndex(side);
        if (clicked == null) return 0;
        if (player != null && player.isSneaking()) {
            int i = clicked.getIndex();
            this.chokedSides[i] = (byte) ((this.chokedSides[i] + 1) % 3);
        } else {
            this.openSides[clicked.getIndex()] = !this.openSides[clicked.getIndex()];
            TileEntity neighbour = world.getTileEntity(this.pos.offset(clicked));
            if (neighbour instanceof TileTubeBuffer) {
                ((TileTubeBuffer) neighbour).openSides[clicked.getOpposite().getIndex()] = this.openSides[clicked.getIndex()];
                neighbour.markDirty();
            } else if (neighbour instanceof TileTube) {
                ((TileTube) neighbour).openSides[clicked.getOpposite().getIndex()] = this.openSides[clicked.getIndex()];
                neighbour.markDirty();
            }
        }
        if (world != null) world.playSound(null, this.pos, TCSounds.TOOL, SoundCategory.BLOCKS, 0.5F, 0.9F + world.rand.nextFloat() * 0.2F);
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
