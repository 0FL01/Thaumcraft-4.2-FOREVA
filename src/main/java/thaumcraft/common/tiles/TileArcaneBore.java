package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.items.wands.foci.FocusExcavation;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketBoreDig;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.InventoryUtils;

public class TileArcaneBore extends TileThaumcraft implements ITickable, IInventory, IWandable {
    public int spiral = 0;
    public float currentRadius = 0.0F;
    public int maxRadius = 2;
    public float vRadX = 0.0F;
    public float vRadZ = 0.0F;
    public float tRadX = 0.0F;
    public float tRadZ = 0.0F;
    public float mRadX = 0.0F;
    public float mRadZ = 0.0F;
    public int topRotation = 0;
    public ItemStack[] contents = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};
    public int rotX = 0;
    public int rotZ = 0;
    public int tarX = 0;
    public int tarZ = 0;
    public int speedX = 0;
    public int speedZ = 0;
    public EnumFacing orientation = EnumFacing.UP;
    public EnumFacing baseOrientation = EnumFacing.UP;
    public boolean hasFocus = false;
    public boolean hasPickaxe = false;
    public int fortune = 0;
    public int speed = 0;
    public int area = 0;

    private boolean first = true;
    private int digCooldown = 20;
    private int scanIndex = 0;
    private FakePlayer fakePlayer = null;
    private float speedyTime = 0.0F;
    private int digX;
    private int digY;
    private int digZ;
    private boolean toDig = false;
    private Block digBlock = Blocks.AIR;
    private int digMd = 0;

    @Override
    public void update() {
        if (this.world != null && this.world.isRemote) {
            if (this.first) {
                this.setOrientation(this.orientation, true);
                this.first = false;
            }
            this.playClientDigFx();
        }
        this.updateOrientationRotation();
        this.topRotation = (this.topRotation + (this.hasFocus && this.hasPickaxe ? 4 : 1)) % 360;
        if (this.world != null && this.isPowered() && this.hasFocus && this.hasPickaxe && this.canUsePickaxe()) {
            this.updateAimPreview();
        } else if (this.world != null && this.world.isRemote) {
            this.relaxAimState();
        }
        if (this.world != null && !this.world.isRemote) {
            this.rechargeSpeedyTime();
            this.updateMining();
        }
    }

    private void rechargeSpeedyTime() {
        if (this.speedyTime >= 20.0F) return;
        int drained = VisNetHandler.drainVis(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), Aspect.ENTROPY, 100);
        if (drained > 0) {
            this.speedyTime += (float) drained / 5.0F;
        }
        if (this.speedyTime < 20.0F) {
            TileArcaneBoreBase base = this.getBase();
            if (base != null && base.drawEssentia()) {
                this.speedyTime += 20.0F;
            }
        }
        if (this.speedyTime > 20.0F) this.speedyTime = 20.0F;
    }

    public void setOrientation(EnumFacing orientation, boolean initial) {
        this.orientation = orientation == null ? EnumFacing.UP : orientation;
        switch (this.orientation) {
            case DOWN:
                this.tarZ = 180;
                this.tarX = 0;
                break;
            case UP:
                this.tarZ = 0;
                this.tarX = 0;
                break;
            case NORTH:
                this.tarZ = 90;
                this.tarX = 270;
                break;
            case SOUTH:
                this.tarZ = 90;
                this.tarX = 90;
                break;
            case WEST:
                this.tarZ = 90;
                this.tarX = 0;
                break;
            case EAST:
            default:
                this.tarZ = 90;
                this.tarX = 180;
                break;
        }
        if (initial) {
            this.rotX = this.tarX;
            this.rotZ = this.tarZ;
        }
        this.speedX = 0;
        this.speedZ = 0;
        this.toDig = false;
        this.tRadX = 0.0F;
        this.tRadZ = 0.0F;
        this.mRadX = 0.0F;
        this.mRadZ = 0.0F;
        this.digX = 0;
        this.digY = 0;
        this.digZ = 0;
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.hasFocus = !this.getStackInSlot(0).isEmpty()
                && this.getStackInSlot(0).getItem() instanceof FocusExcavation;
        this.hasPickaxe = !this.getStackInSlot(1).isEmpty()
                && this.getStackInSlot(1).getItem() instanceof ItemPickaxe;
        this.fortune = 0;
        this.speed = 0;
        this.area = 0;
        ItemStack focus = this.getStackInSlot(0);
        if (this.hasFocus) {
            FocusExcavation excavation = (FocusExcavation) focus.getItem();
            this.fortune = excavation.getUpgradeLevel(focus, FocusUpgradeType.treasure);
            this.speed += excavation.getUpgradeLevel(focus, FocusUpgradeType.potency);
            this.area = excavation.getUpgradeLevel(focus, FocusUpgradeType.enlarge);
        }
        ItemStack pickaxe = this.getStackInSlot(1);
        if (this.hasPickaxe) {
            this.fortune = Math.max(this.fortune, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, pickaxe));
            this.speed += EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, pickaxe);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = EnumFacing.byIndex(nbt.getInteger("orientation"));
        this.baseOrientation = EnumFacing.byIndex(nbt.getInteger("baseOrientation"));
        if (this.orientation == null) this.orientation = EnumFacing.UP;
        if (this.baseOrientation == null) this.baseOrientation = EnumFacing.UP;
        this.speedyTime = nbt.getFloat("SpeedyTime");
        this.contents = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};
        NBTTagList list = nbt.getTagList("Inventory", 10);
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.contents.length) {
                this.contents[slot] = new ItemStack(itemTag);
            }
        }
        this.markDirty();
        this.setOrientation(this.orientation, true);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("orientation", this.orientation.getIndex());
        nbt.setInteger("baseOrientation", this.baseOrientation.getIndex());
        nbt.setFloat("SpeedyTime", this.speedyTime);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i].isEmpty()) continue;
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            this.contents[i].writeToNBT(itemTag);
            list.appendTag(itemTag);
        }
        nbt.setTag("Inventory", list);
    }

    @Override
    public int getSizeInventory() {
        return this.contents.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.contents.length ? this.contents[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < 0 || index >= this.contents.length || this.contents[index].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack;
        if (this.contents[index].getCount() <= count) {
            stack = this.contents[index];
            this.contents[index] = ItemStack.EMPTY;
        } else {
            stack = this.contents[index].splitStack(count);
            if (this.contents[index].getCount() <= 0) this.contents[index] = ItemStack.EMPTY;
        }
        this.markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index < 0 || index >= this.contents.length || this.contents[index].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.contents[index];
        this.contents[index] = ItemStack.EMPTY;
        this.markDirty();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= this.contents.length) return;
        this.contents[index] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public String getName() {
        return "container.arcanebore";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public net.minecraft.util.text.ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world != null && this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (index == 0) return stack.getItem() instanceof FocusExcavation;
        if (index == 1) return stack.getItem() instanceof ItemPickaxe;
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.contents[0] = ItemStack.EMPTY;
        this.contents[1] = ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return this.contents[0].isEmpty() && this.contents[1].isEmpty();
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        this.setOrientation(EnumFacing.byIndex(side), false);
        if (world != null) {
            world.playSound(null, this.pos, TCSounds.TOOL, SoundCategory.BLOCKS, 0.3F, 1.9F + world.rand.nextFloat() * 0.2F);
        }
        if (player != null) {
            player.swingArm(EnumHand.MAIN_HAND);
        }
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return wandstack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, -1, -1), this.pos.add(2, 2, 2));
    }

    private void updateMining() {
        if (!this.hasFocus || !this.hasPickaxe || !this.isPowered() || !this.canUsePickaxe()) {
            this.digCooldown = Math.max(this.digCooldown, 10);
            return;
        }
        if (this.fakePlayer == null && this.world instanceof WorldServer) {
            this.fakePlayer = FakePlayerFactory.get((WorldServer) this.world, new GameProfile(UUID.nameUUIDFromBytes("FakeThaumcraftBore".getBytes()), "FakeThaumcraftBore"));
        }
        if (this.fakePlayer == null) return;
        if (this.digCooldown-- > 0) return;

        BlockPos target = this.findNextBlockToDig();
        if (target == null) {
            this.digCooldown = 20;
            return;
        }
        this.updateAimRotation(target);

        IBlockState state = this.world.getBlockState(target);
        float hardness = state.getBlockHardness(this.world, target);
        this.digCooldown = Math.max(4, 12 + (int) (Math.max(0.0F, hardness) * 2.0F) - this.speed * 2);
        if (this.speedyTime < 1.0F) {
            this.digCooldown *= 4;
        }
        if (this.mineBlock(target, state)) {
            if (this.speedyTime > 0.0F) this.speedyTime -= 1.0F;
            this.topRotation = (this.topRotation + 30) % 360;
        }
    }

    private boolean canUsePickaxe() {
        ItemStack pickaxe = this.getStackInSlot(1);
        return !pickaxe.isEmpty() && pickaxe.isItemStackDamageable() && pickaxe.getItemDamage() + 1 < pickaxe.getMaxDamage();
    }

    private boolean isPowered() {
        if (this.world.isBlockPowered(this.pos)) return true;
        return this.world.isBlockPowered(this.pos.offset(this.baseOrientation.getOpposite()));
    }

    private void updateOrientationRotation() {
        if (this.rotX < this.tarX) {
            this.rotX += this.speedX;
            this.speedX = this.rotX < this.tarX ? this.speedX + 1 : (int) ((float) this.speedX / 3.0F);
        } else if (this.rotX > this.tarX) {
            this.rotX += this.speedX;
            this.speedX = this.rotX > this.tarX ? this.speedX - 1 : (int) ((float) this.speedX / 3.0F);
        } else {
            this.speedX = 0;
        }
        if (this.rotZ < this.tarZ) {
            this.rotZ += this.speedZ;
            this.speedZ = this.rotZ < this.tarZ ? this.speedZ + 1 : (int) ((float) this.speedZ / 3.0F);
        } else if (this.rotZ > this.tarZ) {
            this.rotZ += this.speedZ;
            this.speedZ = this.rotZ > this.tarZ ? this.speedZ - 1 : (int) ((float) this.speedZ / 3.0F);
        } else {
            this.speedZ = 0;
        }
    }

    private void updateAimPreview() {
        BlockPos target = this.findNextBlockToDig();
        if (target != null) {
            this.updateAimRotation(target);
        } else if (this.world.isRemote) {
            this.relaxAimState();
        }
    }

    private void updateAimRotation(BlockPos target) {
        double xd = (double) target.getX() + 0.5D - ((double) this.pos.getX() + 0.5D);
        double yd = (double) target.getY() + 0.5D - ((double) this.pos.getY() + 0.5D);
        double zd = (double) target.getZ() + 0.5D - ((double) this.pos.getZ() + 0.5D);
        double horizontal = Math.sqrt(xd * xd + zd * zd);
        float rx = (float) (Math.atan2(zd, xd) * 180.0D / Math.PI);
        float rz = (float) (-(Math.atan2(yd, horizontal) * 180.0D / Math.PI)) + 90.0F;
        this.tRadX = MathHelper.wrapDegrees((float) this.rotX) + rx;
        if (this.orientation == EnumFacing.EAST) {
            if (this.tRadX > 180.0F) {
                this.tRadX -= 360.0F;
            }
            if (this.tRadX < -180.0F) {
                this.tRadX += 360.0F;
            }
        }
        this.tRadZ = rz - (float) this.rotZ;
        if (this.orientation.getIndex() <= 1) {
            this.tRadZ += 180.0F;
            if (this.vRadX - this.tRadX >= 180.0F) {
                this.vRadX -= 360.0F;
            }
            if (this.vRadX - this.tRadX <= -180.0F) {
                this.vRadX += 360.0F;
            }
        }
        this.mRadX = Math.abs((this.vRadX - this.tRadX) / 6.0F);
        this.mRadZ = Math.abs((this.vRadZ - this.tRadZ) / 6.0F);
        if (this.vRadX < this.tRadX) {
            this.vRadX += this.mRadX;
        } else if (this.vRadX > this.tRadX) {
            this.vRadX -= this.mRadX;
        }
        if (this.vRadZ < this.tRadZ) {
            this.vRadZ += this.mRadZ;
        } else if (this.vRadZ > this.tRadZ) {
            this.vRadZ -= this.mRadZ;
        }
        this.mRadX *= 0.9F;
        this.mRadZ *= 0.9F;
    }

    private void relaxAimState() {
        if (this.topRotation % 90 != 0) {
            this.topRotation += Math.min(10, 90 - this.topRotation % 90);
        }
        this.vRadX *= 0.9F;
        this.vRadZ *= 0.9F;
    }

    private BlockPos findNextBlockToDig() {
        int radius = this.maxRadius + this.area;
        int diameter = radius * 2 + 1;
        int crossSection = diameter * diameter;
        int maxDepth = 64;
        int total = crossSection * maxDepth;
        for (int attempts = 0; attempts < total; attempts++) {
            int index = this.scanIndex++ % total;
            int depth = index / crossSection + 1;
            int cross = index % crossSection;
            int a = cross / diameter - radius;
            int b = cross % diameter - radius;
            BlockPos target = this.pos.offset(this.orientation, depth);
            if (this.orientation.getAxis() == EnumFacing.Axis.Y) {
                target = target.add(a, 0, b);
            } else if (this.orientation.getAxis() == EnumFacing.Axis.Z) {
                target = target.add(a, b, 0);
            } else {
                target = target.add(0, b, a);
            }
            IBlockState state = this.world.getBlockState(target);
            Block block = state.getBlock();
            if (block.isAir(state, this.world, target)) continue;
            if (state.getBlockHardness(this.world, target) < 0.0F) return null;
            if (!block.canCollideCheck(state, false) || block.getCollisionBoundingBox(state, this.world, target) == null) continue;
            return target;
        }
        return null;
    }

    private boolean mineBlock(BlockPos target, IBlockState state) {
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        this.fakePlayer.setPosition((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D);
        int xp = ForgeHooks.onBlockBreakEvent(this.world, this.fakePlayer.interactionManager.getGameType(), (EntityPlayerMP) this.fakePlayer, target);
        if (xp < 0) return false;

        boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, this.getStackInSlot(1)) > 0;
        ItemStack focus = this.getStackInSlot(0);
        if (!silk && !focus.isEmpty() && focus.getItem() instanceof FocusExcavation) {
            silk = ((FocusExcavation) focus.getItem()).isUpgradedWith(focus, FocusUpgradeType.silktouch);
        }

        NonNullList<ItemStack> drops = NonNullList.create();
        if (silk && block.canSilkHarvest(this.world, target, state, this.fakePlayer)) {
            ItemStack stack = BlockUtils.createStackedBlock(block, meta);
            if (!stack.isEmpty()) drops.add(stack);
        } else {
            block.getDrops(drops, (IBlockAccess) this.world, target, state, this.fortune);
            block.dropXpOnBlockBreak(this.world, target, xp);
        }

        this.collectExistingDrops(target, drops);
        this.sendDigEvent(target);
        this.world.setBlockToAir(target);
        this.world.playEvent(2001, target, Block.getStateId(state));
        for (ItemStack drop : drops) {
            this.ejectOrStore(drop);
        }
        this.damagePickaxe();
        return true;
    }

    public void getDigEvent(int packed) {
        int x = ((packed >> 16) & 0xFF) - 64;
        int y = ((packed >> 8) & 0xFF) - 64;
        int z = (packed & 0xFF) - 64;
        this.digX = this.pos.getX() + x;
        this.digY = this.pos.getY() + y;
        this.digZ = this.pos.getZ() + z;
        this.toDig = true;
        IBlockState state = this.world.getBlockState(new BlockPos(this.digX, this.digY, this.digZ));
        this.digBlock = state.getBlock();
        this.digMd = this.digBlock.getMetaFromState(state);
    }

    private void sendDigEvent(BlockPos target) {
        int x = target.getX() - this.pos.getX() + 64;
        int y = target.getY() - this.pos.getY() + 64;
        int z = target.getZ() - this.pos.getZ() + 64;
        int packed = ((x & 0xFF) << 16) | ((y & 0xFF) << 8) | (z & 0xFF);
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketBoreDig(this.pos.getX(), this.pos.getY(), this.pos.getZ(), packed),
                new NetworkRegistry.TargetPoint(
                        this.world.provider.getDimension(),
                        this.pos.getX(),
                        this.pos.getY(),
                        this.pos.getZ(),
                        64.0));
    }

    private void playClientDigFx() {
        if (!this.toDig || this.world == null || this.digBlock == null || this.digBlock == Blocks.AIR) return;
        this.toDig = false;
        IBlockState state;
        try {
            state = this.digBlock.getStateFromMeta(this.digMd);
        } catch (Exception ignored) {
            state = this.digBlock.getDefaultState();
        }
        int sx = this.pos.getX() + this.orientation.getXOffset();
        int sy = this.pos.getY() + this.orientation.getYOffset();
        int sz = this.pos.getZ() + this.orientation.getZOffset();
        this.world.playSound(
                sx + 0.5,
                sy + 0.5,
                sz + 0.5,
                this.digBlock.getSoundType(state, this.world, new BlockPos(this.digX, this.digY, this.digZ), null).getHitSound(),
                SoundCategory.BLOCKS,
                0.45F,
                0.85F,
                false);
        for (int i = 0; i < thaumcraft.common.Thaumcraft.proxy.particleCount(10); i++) {
            double px = this.digX + this.world.rand.nextFloat();
            double py = this.digY + this.world.rand.nextFloat();
            double pz = this.digZ + this.world.rand.nextFloat();
            thaumcraft.common.Thaumcraft.proxy.boreDigFx(
                    this.world,
                    px,
                    py,
                    pz,
                    sx + 0.5,
                    sy + 0.5,
                    sz + 0.5,
                    state,
                    null,
                    0);
        }
    }

    private void collectExistingDrops(BlockPos target, NonNullList<ItemStack> drops) {
        AxisAlignedBB box = new AxisAlignedBB(target).grow(1.0D);
        for (EntityItem item : this.world.getEntitiesWithinAABB(EntityItem.class, box)) {
            if (!item.getItem().isEmpty()) {
                drops.add(item.getItem().copy());
            }
            item.setDead();
        }
    }

    private void ejectOrStore(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        ItemStack remaining = stack.copy();
        TileArcaneBoreBase base = this.getBase();
        if (base != null) {
            TileEntity tile = this.world.getTileEntity(base.getPos().offset(base.orientation));
            if (tile instanceof IInventory) {
                remaining = InventoryUtils.placeItemStackIntoInventory(remaining, (IInventory) tile, base.orientation.getOpposite().getIndex(), true);
            }
        }
        if (remaining == null || remaining.isEmpty()) return;
        EnumFacing out = base != null ? base.orientation : this.orientation.getOpposite();
        double x = (double) this.pos.getX() + 0.5D + (double) out.getXOffset() * 0.66D;
        double y = (double) this.pos.getY() + 0.4D + (double) this.baseOrientation.getOpposite().getYOffset() * 0.66D;
        double z = (double) this.pos.getZ() + 0.5D + (double) out.getZOffset() * 0.66D;
        EntityItem item = new EntityItem(this.world, x, y, z, remaining.copy());
        item.motionX = 0.075D * (double) out.getXOffset();
        item.motionY = 0.025D;
        item.motionZ = 0.075D * (double) out.getZOffset();
        this.world.spawnEntity(item);
    }

    private TileArcaneBoreBase getBase() {
        TileEntity tile = this.world.getTileEntity(this.pos.offset(this.baseOrientation.getOpposite()));
        return tile instanceof TileArcaneBoreBase ? (TileArcaneBoreBase) tile : null;
    }

    private void damagePickaxe() {
        ItemStack pickaxe = InventoryUtils.damageItem(1, this.getStackInSlot(1), this.world);
        if (pickaxe.getItem() == Items.AIR || pickaxe.getCount() <= 0) {
            this.contents[1] = ItemStack.EMPTY;
        } else {
            this.contents[1] = pickaxe;
        }
        this.markDirty();
    }
}
