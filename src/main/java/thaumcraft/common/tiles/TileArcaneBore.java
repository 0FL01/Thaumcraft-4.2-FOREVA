package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRepairableExtended;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.equipment.ItemElementalPickaxe;
import thaumcraft.common.items.wands.foci.FocusExcavation;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketBoreDig;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.Utils;

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
    private int count = 0;
    private long soundDelay = 0L;
    private Object beam1 = null;
    private Object beam2 = null;
    private int beamLength = 0;
    private int lastX = 0;
    private int lastY = 0;
    private int lastZ = 0;
    private float radInc = 0.0F;
    private int paused = 100;
    private int maxPause = 100;
    private long repairCounter = 0L;
    private final AspectList repairCost = new AspectList();
    private final AspectList currentRepairVis = new AspectList();
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
        if (this.world == null) return;
        if (this.world.isRemote) {
            if (this.first) {
                this.setOrientation(this.orientation, true);
                this.first = false;
            }
        } else {
            this.rechargeSpeedyTime();
            this.ensureFakePlayer();
        }
        this.updateOrientationRotation();
        if (this.isPowered() && this.hasFocus && this.hasPickaxe && this.canUsePickaxe()) {
            if (this.world.isRemote) {
                this.updateClientDigging();
            } else {
                this.updateMining();
            }
        } else if (this.world.isRemote) {
            this.relaxAimState();
        }
        if (!this.world.isRemote) {
            this.updatePickaxeLifecycle();
        }
    }

    private void ensureFakePlayer() {
        if (this.fakePlayer == null && this.world instanceof WorldServer) {
            this.fakePlayer = FakePlayerFactory.get((WorldServer) this.world,
                    new GameProfile(UUID.nameUUIDFromBytes("FakeThaumcraftBore".getBytes()), "FakeThaumcraftBore"));
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
        this.lastX = 0;
        this.lastY = 0;
        this.lastZ = 0;
        this.toDig = false;
        this.radInc = 0.0F;
        this.paused = 100;
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
        this.speedyTime = nbt.getShort("SpeedyTime");
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
        nbt.setShort("SpeedyTime", (short) this.speedyTime);
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
        if (this.fakePlayer == null || this.rotX != this.tarX || this.rotZ != this.tarZ) return;
        if (--this.count > 0) return;

        boolean dug = false;
        if (this.toDig) {
            this.toDig = false;
            BlockPos target = new BlockPos(this.digX, this.digY, this.digZ);
            IBlockState state = this.world.getBlockState(target);
            if (!state.getBlock().isAir(state, this.world, target)) {
                dug = this.mineBlock(target, state);
            }
        }
        this.findNextBlockToDig();
        if (dug && this.speedyTime > 0.0F) {
            this.speedyTime -= 1.0F;
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

    private void updateAimTarget(BlockPos target) {
        double xd = (double) this.pos.getX() + 0.5D - ((double) target.getX() + 0.5D);
        double yd = (double) this.pos.getY() + 0.5D - ((double) target.getY() + 0.5D);
        double zd = (double) this.pos.getZ() + 0.5D - ((double) target.getZ() + 0.5D);
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
    }

    private void updateAimEasing() {
        if (this.paused < this.maxPause) {
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
        } else {
            this.vRadX *= 0.9F;
            this.vRadZ *= 0.9F;
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

    private void findNextBlockToDig() {
        if (this.radInc == 0.0F) {
            this.radInc = (float) (this.maxRadius + this.area) / 360.0F;
        }

        BlockPos lane;
        do {
            this.spiral = (this.spiral + 2) % 360;
            this.currentRadius += this.radInc;
            int radius = this.maxRadius + this.area;
            if (this.currentRadius > (float) radius || this.currentRadius < (float) -radius) {
                this.radInc *= -1.0F;
            }

            double angle = (double) this.spiral / 180.0D * Math.PI;
            double ox = (double) this.currentRadius * Math.sin(angle);
            double oy = (double) this.currentRadius * Math.cos(angle);
            double oz = 0.0D;

            double yaw = Math.PI * 0.5D * (double) this.orientation.getXOffset();
            double yawX = ox * Math.cos(yaw) + oz * Math.sin(yaw);
            double yawZ = oz * Math.cos(yaw) - ox * Math.sin(yaw);
            double pitch = Math.PI * 0.5D * (double) this.orientation.getYOffset();
            double pitchY = oy * Math.cos(pitch) + yawZ * Math.sin(pitch);
            double pitchZ = yawZ * Math.cos(pitch) - oy * Math.sin(pitch);

            lane = new BlockPos(
                    (double) this.pos.getX() + 0.5D + (double) this.orientation.getXOffset() + yawX,
                    (double) this.pos.getY() + 0.5D + (double) this.orientation.getYOffset() + pitchY,
                    (double) this.pos.getZ() + 0.5D + (double) this.orientation.getZOffset() + pitchZ);
        } while (lane.getX() == this.lastX && lane.getY() == this.lastY && lane.getZ() == this.lastZ);

        this.lastX = lane.getX();
        this.lastY = lane.getY();
        this.lastZ = lane.getZ();
        BlockPos scan = lane.offset(this.orientation, 2);
        for (int depth = 0; depth < 64; ++depth, scan = scan.offset(this.orientation)) {
            IBlockState state = this.world.getBlockState(scan);
            if (state.getBlockHardness(this.world, scan) < 0.0F) break;
            if (!isDiggable(state, scan)) continue;

            BlockPos target = scan;
            Vec3d start = new Vec3d(
                    (double) this.pos.getX() + 0.5D + (double) this.orientation.getXOffset(),
                    (double) this.pos.getY() + 0.5D + (double) this.orientation.getYOffset(),
                    (double) this.pos.getZ() + 0.5D + (double) this.orientation.getZOffset());
            RayTraceResult hit = this.world.rayTraceBlocks(start,
                    new Vec3d((double) scan.getX() + 0.5D, (double) scan.getY() + 0.5D, (double) scan.getZ() + 0.5D),
                    false, true, false);
            if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState hitState = this.world.getBlockState(hit.getBlockPos());
                if (hitState.getBlockHardness(this.world, hit.getBlockPos()) >= 0.0F
                        && isDiggable(hitState, hit.getBlockPos())) {
                    target = hit.getBlockPos();
                    state = hitState;
                }
            }

            this.digX = target.getX();
            this.digY = target.getY();
            this.digZ = target.getZ();
            this.count = this.getDigDelay(state, target);
            this.toDig = true;
            this.sendDigEvent(target);
            break;
        }
    }

    private boolean isDiggable(IBlockState state, BlockPos target) {
        Block block = state.getBlock();
        return !block.isAir(state, this.world, target)
                && block.canCollideCheck(state, false)
                && block.getCollisionBoundingBox(state, this.world, target) != null;
    }

    private int getDigDelay(IBlockState state, BlockPos target) {
        int delay = Math.max(10 - this.speed,
                (int) (state.getBlockHardness(this.world, target) * 2.0F) - this.speed * 2);
        return this.speedyTime < 1.0F ? delay * 4 : delay;
    }

    private boolean mineBlock(BlockPos target, IBlockState state) {
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        this.fakePlayer.setPosition((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D);
        this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, this.getStackInSlot(1));
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(this.world, target, state, this.fakePlayer);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return false;
        int xp = event.getExpToDrop();

        int dropFortune = this.fortune;
        boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, this.getStackInSlot(1)) > 0;
        ItemStack focus = this.getStackInSlot(0);
        if (!silk && !focus.isEmpty() && focus.getItem() instanceof FocusExcavation) {
            silk = ((FocusExcavation) focus.getItem()).isUpgradedWith(focus, FocusUpgradeType.silktouch);
        }

        NonNullList<ItemStack> drops = NonNullList.create();
        if (silk && block.canSilkHarvest(this.world, target, state, this.fakePlayer)) {
            dropFortune = 0;
            ItemStack stack = BlockUtils.createStackedBlock(block, meta);
            if (!stack.isEmpty()) drops.add(stack);
        } else {
            block.getDrops(drops, (IBlockAccess) this.world, target, state, dropFortune);
            block.dropXpOnBlockBreak(this.world, target, xp);
        }

        this.collectExistingDrops(target, drops);
        this.world.addBlockEvent(this.pos, ConfigBlocks.blockWoodenDevice, 99,
                (Block.getIdFromBlock(block) & 0xFFF) | ((meta & 0xFF) << 12));
        this.world.playEvent(2001, target, Block.getStateId(state));
        this.world.setBlockToAir(target);
        for (ItemStack drop : drops) {
            this.ejectOrStore(this.applySpecialMiningResult(drop, silk, dropFortune));
        }
        this.damagePickaxe();
        this.placeTunnelLight();
        return true;
    }

    private ItemStack applySpecialMiningResult(ItemStack drop, boolean silk, int dropFortune) {
        if (silk || drop == null || drop.isEmpty()) return drop;
        ItemStack focus = this.getStackInSlot(0);
        boolean nativeClusters = this.getStackInSlot(1).getItem() instanceof ItemElementalPickaxe
                || (!focus.isEmpty() && focus.getItem() instanceof FocusExcavation
                && ((FocusExcavation) focus.getItem()).isUpgradedWith(focus, FocusExcavation.dowsing));
        return nativeClusters
                ? Utils.findSpecialMiningResult(drop, 0.2F + (float) dropFortune * 0.075F, this.world.rand)
                : drop;
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

    private void updateClientDigging() {
        ++this.paused;
        if (this.paused < this.maxPause && this.soundDelay < System.currentTimeMillis()) {
            this.soundDelay = System.currentTimeMillis() + 1700L + (long) this.world.rand.nextInt(100);
            this.world.playSound(
                    (double) this.pos.getX() + 0.5D,
                    (double) this.pos.getY() + 0.5D,
                    (double) this.pos.getZ() + 0.5D,
                    TCSounds.RUMBLE,
                    SoundCategory.BLOCKS,
                    0.25F,
                    0.9F + this.world.rand.nextFloat() * 0.2F,
                    false);
        }
        if (this.beamLength > 0 && this.paused > this.maxPause) {
            --this.beamLength;
        }
        if (this.toDig) {
            this.paused = 0;
            this.beamLength = 64;
            BlockPos target = new BlockPos(this.digX, this.digY, this.digZ);
            IBlockState state = this.world.getBlockState(target);
            this.maxPause = 10 + Math.max(10 - this.speed,
                    (int) (state.getBlockHardness(this.world, target) * 2.0F) - this.speed * 2);
            if (this.speedyTime <= 0.0F) {
                this.maxPause *= 4;
            }
            this.toDig = false;
            this.updateAimTarget(target);
            if (this.speedyTime > 0.0F) {
                this.speedyTime -= 1.0F;
            }
        }
        this.updateAimEasing();
        this.updateClientBeam();
    }

    private void updateClientBeam() {
        float vx = (float) (this.rotX + 90) - this.vRadX;
        float vz = (float) (this.rotZ + 90) - this.vRadZ;
        float dx = MathHelper.sin(vx / 180.0F * (float) Math.PI) * MathHelper.cos(vz / 180.0F * (float) Math.PI);
        float dz = MathHelper.cos(vx / 180.0F * (float) Math.PI) * MathHelper.cos(vz / 180.0F * (float) Math.PI);
        float dy = MathHelper.sin(vz / 180.0F * (float) Math.PI);
        Vec3d start = new Vec3d(
                (double) this.pos.getX() + 0.5D + (double) dx,
                (double) this.pos.getY() + 0.5D + (double) dy,
                (double) this.pos.getZ() + 0.5D + (double) dz);
        Vec3d end = new Vec3d(
                (double) this.pos.getX() + 0.5D + (double) (dx * (float) this.beamLength),
                (double) this.pos.getY() + 0.5D + (double) (dy * (float) this.beamLength),
                (double) this.pos.getZ() + 0.5D + (double) (dz * (float) this.beamLength));
        RayTraceResult hit = this.world.rayTraceBlocks(start, end, false, true, false);
        int impact = 0;
        double bx = end.x;
        double by = end.y;
        double bz = end.z;
        if (hit != null && hit.hitVec != null) {
            bx = hit.hitVec.x;
            by = hit.hitVec.y;
            bz = hit.hitVec.z;
            impact = 5;
            if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos hitPos = hit.getBlockPos();
                IBlockState state = this.world.getBlockState(hitPos);
                if (!state.getBlock().isAir(state, this.world, hitPos)) {
                    thaumcraft.common.Thaumcraft.proxy.boreDigFx(
                            this.world,
                            hitPos.getX(),
                            hitPos.getY(),
                            hitPos.getZ(),
                            this.pos.getX() + this.orientation.getXOffset(),
                            this.pos.getY() + this.orientation.getYOffset(),
                            this.pos.getZ() + this.orientation.getZOffset(),
                            state,
                            null,
                            0);
                }
            }
        }
        this.topRotation = (this.topRotation + this.beamLength / 6) % 360;
        this.beam1 = thaumcraft.common.Thaumcraft.proxy.beamBore(
                this.world, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                bx, by, bz, 1, 65382, true, impact > 0 ? 2.0F : 0.0F, this.beam1, impact);
        this.beam2 = thaumcraft.common.Thaumcraft.proxy.beamBore(
                this.world, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                bx, by, bz, 2, 0xFF8855, false, impact > 0 ? 2.0F : 0.0F, this.beam2, impact);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 99) {
            Block block = Block.getBlockById(type & 0xFFF);
            if (block != null && block != Blocks.AIR) {
                this.playClientDigFx(block, type >> 12 & 0xFF);
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    private void playClientDigFx(Block block, int meta) {
        if (this.world == null || !this.world.isRemote || block == null || block == Blocks.AIR) return;
        IBlockState state;
        try {
            state = block.getStateFromMeta(meta);
        } catch (Exception ignored) {
            state = block.getDefaultState();
        }
        int sx = this.pos.getX() + this.orientation.getXOffset();
        int sy = this.pos.getY() + this.orientation.getYOffset();
        int sz = this.pos.getZ() + this.orientation.getZOffset();
        this.world.playSound(
                this.digX + 0.5D,
                this.digY + 0.5D,
                this.digZ + 0.5D,
                block.getSoundType(state, this.world, new BlockPos(this.digX, this.digY, this.digZ), null).getHitSound(),
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

    private void updatePickaxeLifecycle() {
        if (!this.hasPickaxe || this.fakePlayer == null) return;
        ItemStack pickaxe = this.getStackInSlot(1);
        if (pickaxe.isEmpty()) return;
        this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, pickaxe);
        if (this.repairCounter++ % 40L == 0L && pickaxe.isItemDamaged()) {
            this.doRepair(pickaxe);
        }
        if (this.repairCost.size() > 0 && this.repairCounter % 5L == 0L) {
            for (Aspect aspect : this.repairCost.getAspects()) {
                if (aspect == null || this.currentRepairVis.getAmount(aspect) >= this.repairCost.getAmount(aspect)) continue;
                this.currentRepairVis.add(aspect, VisNetHandler.drainVis(
                        this.world,
                        this.pos.getX(),
                        this.pos.getY(),
                        this.pos.getZ(),
                        aspect,
                        this.repairCost.getAmount(aspect)));
            }
        }
        this.fakePlayer.ticksExisted = (int) this.repairCounter;
        try {
            pickaxe.updateAnimation(this.world, this.fakePlayer, 0, true);
        } catch (Exception ignored) {
        }
    }

    private void doRepair(ItemStack pickaxe) {
        int level = Config.enchRepair == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Config.enchRepair, pickaxe);
        if (level <= 0) return;
        level = Math.min(level, 2);
        if (!(pickaxe.getItem() instanceof IRepairable)) {
            this.repairCost.aspects.clear();
            return;
        }

        AspectList cost = ResearchManager.reduceToPrimals(ThaumcraftCraftingManager.getObjectTags(pickaxe));
        if (cost == null || cost.size() == 0) return;
        for (Aspect aspect : cost.getAspects()) {
            if (aspect != null) {
                this.repairCost.merge(aspect, (int) Math.sqrt(cost.getAmount(aspect) * 2) * level);
            }
        }

        boolean repair = true;
        if (pickaxe.getItem() instanceof IRepairableExtended) {
            repair = ((IRepairableExtended) pickaxe.getItem()).doRepair(pickaxe, this.fakePlayer, level);
        }
        if (repair) {
            for (Aspect aspect : this.repairCost.getAspects()) {
                if (aspect != null && this.currentRepairVis.getAmount(aspect) < this.repairCost.getAmount(aspect)) {
                    repair = false;
                    break;
                }
            }
        }
        if (!repair) return;
        for (Aspect aspect : this.repairCost.getAspects()) {
            if (aspect != null) {
                this.currentRepairVis.reduce(aspect, this.repairCost.getAmount(aspect));
            }
        }
        pickaxe.setItemDamage(Math.max(0, pickaxe.getItemDamage() - level));
        this.markDirty();
    }

    private void placeTunnelLight() {
        TileArcaneBoreBase base = this.getBase();
        if (base == null) return;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (!(this.world.getTileEntity(base.getPos().offset(facing)) instanceof TileArcaneLamp)) continue;
            int distance = this.world.rand.nextInt(32) * 2;
            int x = this.pos.getX() + this.orientation.getXOffset() * (distance + 1);
            int y = this.pos.getY() + this.orientation.getYOffset() * (distance + 1);
            int z = this.pos.getZ() + this.orientation.getZOffset() * (distance + 1);
            int pattern = distance / 2 % 4;
            if (this.orientation.getXOffset() != 0) {
                z += pattern == 0 ? 3 : (pattern == 2 ? -3 : 0);
            } else {
                x += pattern == 0 ? 3 : (pattern == 2 ? -3 : 0);
            }
            if (pattern == 3 && this.orientation.getYOffset() == 0) {
                y -= 2;
            }
            BlockPos target = new BlockPos(x, y, z);
            IBlockState state = this.world.getBlockState(target);
            if (!this.world.isAirBlock(target)
                    || state.getBlock() == ConfigBlocks.blockAiry
                    || this.world.getLight(target) >= 15) {
                return;
            }
            this.world.setBlockState(target,
                    ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 3), 3);
            return;
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
