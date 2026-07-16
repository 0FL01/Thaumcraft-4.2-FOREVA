package thaumcraft.common.tiles;

import java.awt.Color;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class TileCrucible extends TileThaumcraft implements ITickable, IWandable, IAspectContainer {

    public short heat = 0;
    public AspectList aspects = new AspectList();
    public final int maxTags = 100;

    private static final int FLUID_CAPACITY = 1000;

    int bellows = -1;
    private int delay = 0;
    private final FluidTank tank;
    private long counter = -100L;
    int prevcolor = 0;
    int prevx = 0;
    int prevy = 0;

    public TileCrucible() {
        this.tank = new FluidTank(FLUID_CAPACITY) {
            @Override
            public boolean canFillFluidType(FluidStack stack) {
                return stack != null && stack.getFluid() == FluidRegistry.WATER;
            }

            @Override
            protected void onContentsChanged() {
                markDirty();
                if (world != null && !world.isRemote) {
                    IBlockState state = world.getBlockState(pos);
                    world.notifyBlockUpdate(pos, state, state, 3);
                }
            }
        };
    }

    // ========== NBT ==========

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.heat = nbt.getShort("Heat");
        this.tank.readFromNBT(nbt);
        if (nbt.hasKey("Empty")) {
            this.tank.setFluid(null);
        }
        this.aspects.readFromNBT(nbt);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setShort("Heat", this.heat);
        this.tank.writeToNBT(nbt);
        this.aspects.writeToNBT(nbt);
    }

    // ========== Chunk sync (initial load) ==========

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        writeCustomNBT(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readCustomNBT(tag);
    }

    // ========== Capability: Fluid Handler ==========

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        }
        return super.getCapability(capability, facing);
    }

    // ========== Update tick ==========

    @Override
    public void update() {
        ++this.counter;
        short prevheat = this.heat;

        if (!this.world.isRemote) {
            // Server-side logic
            if (this.bellows < 0) {
                this.getBellows();
            }

            if (this.tank.getFluidAmount() > 0) {
                BlockPos below = pos.down();
                IBlockState belowState = world.getBlockState(below);
                Material mat = belowState.getMaterial();
                Block bi = belowState.getBlock();
                int md = bi.getMetaFromState(belowState);

                boolean isHeated = mat == Material.FIRE
                        || mat == Material.LAVA
                        || (bi == ConfigBlocks.blockAiry && belowState.getValue(BlockAiry.TYPE) == 1);

                if (isHeated) {
                    if (this.heat < 200) {
                        this.heat = (short) (this.heat + (1 + this.bellows * 2));
                        if (prevheat < 151 && this.heat >= 151) {
                            this.markDirty();
                            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                        }
                    }
                } else if (this.heat > 0) {
                    this.heat = (short) (this.heat - 1);
                    if (this.heat == 149) {
                        this.markDirty();
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    }
                }
            } else if (this.heat > 0) {
                this.heat = (short) (this.heat - 1);
            }

            // Overflow protection: if aspect count exceeds maxTags, spill
            if (this.tagAmount() > this.maxTags && this.counter % 5L == 0L) {
                this.takeRandomFromSource();
                this.spill();
            }

            // Aspect decomposition: non-primal -> primal components
            if (this.counter > 100L && this.heat > 150) {
                this.counter = 0L;
                if (this.tagAmount() > 0) {
                    Aspect[] aspectArray = this.aspects.getAspects();
                    int s = aspectArray.length;
                    if (s > 0) {
                        Aspect a = aspectArray[this.world.rand.nextInt(s)];
                        if (a.isPrimal()) {
                            a = aspectArray[this.world.rand.nextInt(s)];
                        }
                        this.tank.drain(2, true);
                        this.aspects.remove(a, 1);
                        if (!a.isPrimal() && a.getComponents() != null && a.getComponents().length == 2) {
                            if (this.world.rand.nextBoolean()) {
                                this.aspects.add(a.getComponents()[0], 1);
                            } else {
                                this.aspects.add(a.getComponents()[1], 1);
                            }
                        } else {
                            this.spill();
                        }
                    }
                }
                this.markDirty();
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
        } else {
            // Client-side effects
            if (this.tank.getFluidAmount() > 0) {
                this.drawEffects();
            }
        }

        // Client heat smoothing
        if (this.world.isRemote && prevheat < 151 && this.heat >= 151) {
            this.heat = (short) (this.heat + 1);
        }
    }

    private void drawEffects() {
        if (this.heat > 150) {
            Thaumcraft.proxy.crucibleFroth(this.world,
                    this.pos.getX() + 0.2f + this.world.rand.nextFloat() * 0.6f,
                    this.pos.getY() + this.getFluidHeight(),
                    this.pos.getZ() + 0.2f + this.world.rand.nextFloat() * 0.6f);
            if (this.tagAmount() > this.maxTags) {
                for (int a = 0; a < 2; ++a) {
                    Thaumcraft.proxy.crucibleFrothDown(this.world,
                            this.pos.getX(),
                            this.pos.getY() + 1.0f,
                            this.pos.getZ() + this.world.rand.nextFloat());
                    Thaumcraft.proxy.crucibleFrothDown(this.world,
                            this.pos.getX() + 1.0f,
                            this.pos.getY() + 1.0f,
                            this.pos.getZ() + this.world.rand.nextFloat());
                    Thaumcraft.proxy.crucibleFrothDown(this.world,
                            this.pos.getX() + this.world.rand.nextFloat(),
                            this.pos.getY() + 1.0f,
                            this.pos.getZ());
                    Thaumcraft.proxy.crucibleFrothDown(this.world,
                            this.pos.getX() + this.world.rand.nextFloat(),
                            this.pos.getY() + 1.0f,
                            this.pos.getZ() + 1.0f);
                }
            }
        }
        if (this.world.rand.nextInt(6) == 0 && this.aspects.size() > 0) {
            int color = this.aspects.getAspects()[this.world.rand.nextInt(this.aspects.size())].getColor() - 16777216;
            int x = 5 + this.world.rand.nextInt(22);
            int y = 5 + this.world.rand.nextInt(22);
            this.delay = this.world.rand.nextInt(10);
            this.prevcolor = color;
            this.prevx = x;
            this.prevy = y;
            Color c = new Color(color);
            float red = c.getRed() / 255.0f;
            float green = c.getGreen() / 255.0f;
            float blue = c.getBlue() / 255.0f;
            Thaumcraft.proxy.crucibleBubble(this.world,
                    this.pos.getX() + x / 32.0f + 0.015625f,
                    this.pos.getY() + 0.05f + this.getFluidHeight(),
                    this.pos.getZ() + y / 32.0f + 0.015625f,
                    red, green, blue);
        }
    }

    // ========== Aspect decomposition helpers ==========

    public int tagAmount() {
        if (this.aspects.size() > 0) {
            int tt = 0;
            for (Aspect tag : this.aspects.getAspects()) {
                tt += this.aspects.getAmount(tag);
            }
            return tt;
        }
        return 0;
    }

    public float getFluidHeight() {
        float base = 0.3f + 0.5f * ((float) this.tank.getFluidAmount() / (float) this.tank.getCapacity());
        float out = base + (float) this.tagAmount() / 100.0f * (1.0f - base);
        if (out > 1.0f) out = 1.001f;
        if (out == 1.0f) out = 0.9999f;
        return out;
    }

    public boolean hasWater() {
        return this.tank.getFluidAmount() > 0;
    }

    public AspectList takeRandomFromSource() {
        AspectList output = new AspectList();
        if (this.aspects.size() > 0) {
            Aspect tag = this.aspects.getAspects()[this.world.rand.nextInt(this.aspects.getAspects().length)];
            output.add(tag, 1);
            this.aspects.remove(tag, 1);
        }
        this.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        return output;
    }

    public boolean canProcessItems() {
        return this.heat > 150 && this.tank.getFluidAmount() > 0;
    }

    // ========== Spill / overflow ==========

    public void spill() {
        if (this.world.rand.nextInt(4) == 0) {
            BlockPos above = pos.up();
            if (this.world.isAirBlock(above)) {
                if (this.world.rand.nextBoolean()) {
                    this.world.setBlockState(above, ConfigBlocks.blockFluxGas.getStateFromMeta(0), 3);
                } else {
                    this.world.setBlockState(above, ConfigBlocks.blockFluxGoo.getStateFromMeta(0), 3);
                }
            } else {
                IBlockState aboveState = world.getBlockState(above);
                Block bi = aboveState.getBlock();
                int md = bi.getMetaFromState(aboveState);

                if (bi == ConfigBlocks.blockFluxGoo && md < 7) {
                    this.world.setBlockState(above, ConfigBlocks.blockFluxGoo.getStateFromMeta(md + 1), 3);
                } else if (bi == ConfigBlocks.blockFluxGas && md < 7) {
                    this.world.setBlockState(above, ConfigBlocks.blockFluxGas.getStateFromMeta(md + 1), 3);
                } else {
                    int x = -1 + this.world.rand.nextInt(3);
                    int y = -1 + this.world.rand.nextInt(3);
                    int z = -1 + this.world.rand.nextInt(3);
                    BlockPos offsetPos = pos.add(x, y, z);
                    if (this.world.isAirBlock(offsetPos)) {
                        if (this.world.rand.nextBoolean()) {
                            this.world.setBlockState(offsetPos, ConfigBlocks.blockFluxGas.getStateFromMeta(0), 3);
                        } else {
                            this.world.setBlockState(offsetPos, ConfigBlocks.blockFluxGoo.getStateFromMeta(0), 3);
                        }
                    }
                }
            }
        }
    }

    public void spillRemnants() {
        if (this.tank.getFluidAmount() > 0 || this.aspects.visSize() > 0) {
            this.tank.setFluid(null);
            for (int a = 0; a < this.aspects.visSize() / 2; ++a) {
                this.spill();
            }
            this.aspects = new AspectList();
            this.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.addBlockEvent(pos, ConfigBlocks.blockMetalDevice, 2, 5);
        }
    }

    // ========== Item ejection ==========

    public void ejectItem(ItemStack items) {
        boolean first = true;
        while (items.getCount() > 0) {
            ItemStack spitout = items.copy();
            if (spitout.getCount() > spitout.getMaxStackSize()) {
                spitout.setCount(spitout.getMaxStackSize());
            }
            items.shrink(spitout.getCount());

            EntitySpecialItem entityitem = new EntitySpecialItem(
                    this.world,
                    (float) this.pos.getX() + 0.5f,
                    (float) this.pos.getY() + 0.71f,
                    (float) this.pos.getZ() + 0.5f,
                    spitout);
            entityitem.motionY = 0.1f;
            if (first) {
                entityitem.motionX = 0.0;
                entityitem.motionZ = 0.0;
            } else {
                entityitem.motionX = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.01f;
                entityitem.motionZ = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.01f;
            }
            this.world.spawnEntity(entityitem);
            first = false;
        }
    }

    // ========== Smelting / item processing ==========

    public void attemptSmelt(EntityItem entity) {
        boolean bubble = false;
        boolean event = false;
        ItemStack item = entity.getItem();
        String username = entity.getThrower();
        if (username == null || username.isEmpty()) {
            NBTTagCompound entityData = entity.getEntityData();
            if (entityData.hasKey("thrower")) {
                username = entityData.getString("thrower");
            }
        }

        int stacksize = item.getCount();
        for (int a = 0; a < item.getCount(); ++a) {
            if (stacksize <= 0) break;

            CrucibleRecipe rc = ThaumcraftCraftingManager.findMatchingCrucibleRecipe(username, this.aspects, item);
            if (rc != null && this.tank.getFluidAmount() > 0) {
                // Recipe match: consume aspects + water, output result
                ItemStack out = rc.getRecipeOutput().copy();
                EntityPlayer p = this.world.getPlayerEntityByName(username);
                if (p != null) {
                    FMLCommonHandler.instance().firePlayerCraftingEvent(
                            p, out, new InventoryFake(new ItemStack[]{item}));
                }
                this.aspects = rc.removeMatching(this.aspects);
                this.tank.drain(50, true);
                this.ejectItem(out);
                event = true;
                --stacksize;
                this.counter = -250L;
                continue;
            }

            // No recipe match: decompose item into aspects
            AspectList ot = ThaumcraftCraftingManager.getObjectTags(item);
            if ((ot = ThaumcraftCraftingManager.getBonusTags(item, ot)) == null || ot.size() == 0) {
                // Item has no aspects: reject with pop sound + push away
                entity.motionY = 0.35f;
                entity.motionX = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2f;
                entity.motionZ = (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2f;
                this.world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP,
                        SoundCategory.BLOCKS, 0.2f,
                        (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.7f + 1.0f);
                return;
            }

            // Decompose: add aspects from item
            for (Aspect tag : ot.getAspects()) {
                this.aspects.add(tag, ot.getAmount(tag));
            }
            bubble = true;
            --stacksize;
            this.counter = -150L;
        }

        if (bubble) {
            this.world.playSound(null, pos, TCSounds.BUBBLE, SoundCategory.BLOCKS, 0.2f,
                    1.0f + this.world.rand.nextFloat() * 0.4f);
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.addBlockEvent(pos, ConfigBlocks.blockMetalDevice, 2, 1);
        }
        if (event) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.addBlockEvent(pos, ConfigBlocks.blockMetalDevice, 2, 5);
        }

        if (stacksize <= 0) {
            entity.setDead();
        } else {
            item.setCount(stacksize);
            entity.setItem(item);
        }
        this.markDirty();
    }

    // ========== Bellows ==========

    public void getBellows() {
        // TC4 counts adjacent bellows blocks directly, regardless of facing or redstone power.
        this.bellows = 0;
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            IBlockState state = this.world.getBlockState(this.pos.offset(dir));
            if (state.getBlock() == ConfigBlocks.blockWoodenDevice
                    && ConfigBlocks.blockWoodenDevice.getMetaFromState(state) == 0) {
                ++this.bellows;
            }
        }
    }

    // ========== IWandable ==========

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        if (!world.isRemote && player.isSneaking()) {
            this.spillRemnants();
        }
        return wandstack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    // ========== IAspectContainer ==========

    @Override
    public AspectList getAspects() {
        return this.aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.aspects = aspects == null ? new AspectList() : aspects.copy();
        this.markDirtyAndSync();
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return amount;
        int add = Math.min(amount, Math.max(0, this.maxTags - this.tagAmount()));
        if (add <= 0) return amount;
        this.aspects.add(tag, add);
        this.markDirtyAndSync();
        return amount - add;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0 || this.aspects.getAmount(tag) < amount) return false;
        this.aspects.remove(tag, amount);
        this.markDirtyAndSync();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        if (ot == null || !this.doesContainerContain(ot)) return false;
        for (Aspect tag : ot.getAspects()) {
            if (tag == null) continue;
            this.aspects.remove(tag, ot.getAmount(tag));
        }
        this.markDirtyAndSync();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return tag != null && this.aspects.getAmount(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        if (ot == null) return false;
        for (Aspect tag : ot.getAspects()) {
            if (tag == null) continue;
            if (this.aspects.getAmount(tag) < ot.getAmount(tag)) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return tag == null ? this.tagAmount() : this.aspects.getAmount(tag);
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag != null && this.tagAmount() < this.maxTags;
    }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }

    // ========== Client event ==========

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            if (this.world.isRemote) {
                Thaumcraft.proxy.blockSparkle(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), -9999, 5);
            }
            return true;
        }
        if (id == 2) {
            Thaumcraft.proxy.crucibleBoilSound(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ());
            if (this.world.isRemote) {
                for (int q = 0; q < 10; ++q) {
                    Thaumcraft.proxy.crucibleBoil(this.world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), this, type);
                }
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    // ========== Render bounding box ==========

    @Override
    public net.minecraft.util.math.AxisAlignedBB getRenderBoundingBox() {
        return new net.minecraft.util.math.AxisAlignedBB(pos, pos.add(1, 1, 1));
    }
}
