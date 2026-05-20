package thaumcraft.common.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.crafting.InfusionRunicAugmentRecipe;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.utils.InventoryUtils;

public class TileInfusionMatrix extends TileThaumcraft implements ITickable, IWandable, IAspectContainer {
    private final ArrayList<BlockPos> pedestals = new ArrayList<BlockPos>();

    public boolean active = false;
    public boolean crafting = false;
    public boolean checkSurroundings = true;
    public int symmetry = 0;
    public int instability = 0;
    public int count = 0;
    public int craftCount = 0;
    public float startUp = 0.0F;

    private AspectList recipeEssentia = new AspectList();
    private ArrayList<ItemStack> recipeIngredients = new ArrayList<ItemStack>();
    private Object recipeOutput = null;
    private String recipePlayer = null;
    private String recipeOutputLabel = null;
    private ItemStack recipeInput = ItemStack.EMPTY;
    private int recipeInstability = 0;
    private int recipeXP = 0;
    private int recipeType = 0;
    private int countDelay = 10;
    private int itemCount = 0;
    public final HashMap<String, SourceFX> sourceFX = new HashMap<String, SourceFX>();

    @Override
    public void update() {
        if (this.world == null) return;
        ++this.count;

        if (this.checkSurroundings) {
            this.checkSurroundings = false;
            this.getSurroundings();
        }

        if (this.world.isRemote) {
            this.doEffects();
            return;
        }

        if (!this.world.isRemote) {
            if (this.count % (this.crafting ? 20 : 100) == 0 && !this.validLocation()) {
                this.active = false;
                this.crafting = false;
                this.resetCraftingState();
                this.markDirtyAndSync();
                return;
            }
            if (this.active && this.crafting && this.count % this.countDelay == 0) {
                this.craftCycle();
                this.markDirty();
            }
        }
    }

    public boolean validLocation() {
        if (this.world == null) return false;
        TileEntity center = this.world.getTileEntity(this.pos.down(2));
        if (!(center instanceof TilePedestal)) return false;
        if (!(this.world.getTileEntity(this.pos.add(1, -2, 1)) instanceof TileInfusionPillar)) return false;
        if (!(this.world.getTileEntity(this.pos.add(1, -2, -1)) instanceof TileInfusionPillar)) return false;
        if (!(this.world.getTileEntity(this.pos.add(-1, -2, -1)) instanceof TileInfusionPillar)) return false;
        return this.world.getTileEntity(this.pos.add(-1, -2, 1)) instanceof TileInfusionPillar;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.active = nbt.getBoolean("active");
        this.crafting = nbt.getBoolean("crafting");
        this.instability = nbt.getShort("instability");
        this.recipeEssentia.readFromNBT(nbt);
        this.recipeIngredients = new ArrayList<ItemStack>();
        NBTTagList ingredients = nbt.getTagList("recipein", 10);
        for (int i = 0; i < ingredients.tagCount(); ++i) {
            ItemStack stack = new ItemStack(ingredients.getCompoundTagAt(i));
            if (!stack.isEmpty()) this.recipeIngredients.add(stack);
        }
        this.recipeOutput = null;
        this.recipeOutputLabel = null;
        String rotype = nbt.getString("rotype");
        if ("@".equals(rotype) && nbt.hasKey("recipeout", 10)) {
            this.recipeOutput = new ItemStack(nbt.getCompoundTag("recipeout"));
        } else if ("#".equals(rotype) && nbt.hasKey("recipeench", 8)) {
            this.recipeOutput = Enchantment.REGISTRY.getObject(new ResourceLocation(nbt.getString("recipeench")));
        } else if (!rotype.isEmpty() && nbt.hasKey("recipeout")) {
            this.recipeOutputLabel = rotype;
            this.recipeOutput = nbt.getTag("recipeout").copy();
        }
        this.recipeInput = nbt.hasKey("recipeinput", 10) ? new ItemStack(nbt.getCompoundTag("recipeinput")) : ItemStack.EMPTY;
        this.recipeInstability = nbt.getInteger("recipeinst");
        this.recipeType = nbt.getInteger("recipetype");
        this.recipeXP = nbt.getInteger("recipexp");
        this.recipePlayer = nbt.getString("recipeplayer");
        if (this.recipePlayer != null && this.recipePlayer.isEmpty()) this.recipePlayer = null;
        this.countDelay = nbt.hasKey("countdelay") ? Math.max(1, nbt.getInteger("countdelay")) : 10;
        this.itemCount = nbt.getInteger("itemcount");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setBoolean("active", this.active);
        nbt.setBoolean("crafting", this.crafting);
        nbt.setShort("instability", (short) this.instability);
        this.recipeEssentia.writeToNBT(nbt);
        if (this.recipeIngredients != null && !this.recipeIngredients.isEmpty()) {
            NBTTagList ingredients = new NBTTagList();
            for (ItemStack stack : this.recipeIngredients) {
                if (stack == null || stack.isEmpty()) continue;
                NBTTagCompound item = new NBTTagCompound();
                stack.writeToNBT(item);
                ingredients.appendTag(item);
            }
            nbt.setTag("recipein", ingredients);
        }
        if (this.recipeOutput instanceof ItemStack) {
            nbt.setString("rotype", "@");
            nbt.setTag("recipeout", ((ItemStack) this.recipeOutput).writeToNBT(new NBTTagCompound()));
        } else if (this.recipeOutput instanceof NBTBase) {
            nbt.setString("rotype", this.recipeOutputLabel == null ? "" : this.recipeOutputLabel);
            nbt.setTag("recipeout", ((NBTBase) this.recipeOutput).copy());
        } else if (this.recipeOutput instanceof Enchantment) {
            ResourceLocation key = Enchantment.REGISTRY.getNameForObject((Enchantment) this.recipeOutput);
            if (key != null) {
                nbt.setString("rotype", "#");
                nbt.setString("recipeench", key.toString());
            }
        }
        if (this.recipeInput != null && !this.recipeInput.isEmpty()) {
            nbt.setTag("recipeinput", this.recipeInput.writeToNBT(new NBTTagCompound()));
        }
        nbt.setInteger("recipeinst", this.recipeInstability);
        nbt.setInteger("recipetype", this.recipeType);
        nbt.setInteger("recipexp", this.recipeXP);
        nbt.setString("recipeplayer", this.recipePlayer == null ? "" : this.recipePlayer);
        nbt.setInteger("countdelay", this.countDelay);
        nbt.setInteger("itemcount", this.itemCount);
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        if (world.isRemote) return 0;
        if (this.active && !this.crafting) {
            this.craftingStart(player);
            return 0;
        }
        if (!this.active && this.validLocation()) {
            this.active = true;
            this.startUp = 1.0F;
            this.markDirtyAndSync();
            return 0;
        }
        return -1;
    }

    public void craftingStart(EntityPlayer player) {
        if (!this.validLocation()) {
            this.active = false;
            this.resetCraftingState();
            this.markDirtyAndSync();
            return;
        }

        this.getSurroundings();
        TilePedestal center = this.getCenterPedestal();
        if (center == null || center.getStackInSlot(0).isEmpty()) return;

        this.recipeInput = this.copySingle(center.getStackInSlot(0));
        ArrayList<ItemStack> components = this.getPedestalComponents();
        if (components.isEmpty()) return;

        InfusionRecipe recipe = ThaumcraftCraftingManager.findMatchingInfusionRecipe(components, this.recipeInput, player);
        if (recipe != null) {
            this.recipeType = 0;
            this.recipeIngredients = new ArrayList<ItemStack>();
            ItemStack[] recipeComponents = recipe instanceof InfusionRunicAugmentRecipe
                    ? ((InfusionRunicAugmentRecipe) recipe).getComponents(this.recipeInput)
                    : recipe.getComponents();
            for (ItemStack ingredient : recipeComponents) {
                if (ingredient != null && !ingredient.isEmpty()) this.recipeIngredients.add(this.copySingle(ingredient));
            }
            this.setRecipeOutput(recipe.getRecipeOutput(this.recipeInput));
            this.recipeInstability = recipe.getInstability(this.recipeInput);
            AspectList aspects = recipe.getAspects(this.recipeInput);
            this.recipeEssentia = aspects == null ? new AspectList() : aspects.copy();
            this.recipePlayer = player.getName();
            this.instability = this.symmetry + this.recipeInstability;
            this.recipeXP = 0;
            this.countDelay = 10;
            this.itemCount = 0;
            this.crafting = true;
            this.world.playSound(null, this.pos, TCSounds.CRAFTSTART, SoundCategory.BLOCKS, 0.5F, 1.0F);
            this.markDirtyAndSync();
            return;
        }

        InfusionEnchantmentRecipe enchantmentRecipe = ThaumcraftCraftingManager.findMatchingInfusionEnchantmentRecipe(components, this.recipeInput, player);
        if (enchantmentRecipe != null) {
            this.recipeType = 1;
            this.recipeIngredients = new ArrayList<ItemStack>();
            for (ItemStack ingredient : enchantmentRecipe.components) {
                if (ingredient != null && !ingredient.isEmpty()) this.recipeIngredients.add(this.copySingle(ingredient));
            }
            this.recipeOutput = enchantmentRecipe.getEnchantment();
            this.recipeOutputLabel = null;
            this.recipeInstability = enchantmentRecipe.calcInstability(this.recipeInput);
            AspectList essentia = enchantmentRecipe.aspects == null ? new AspectList() : enchantmentRecipe.aspects.copy();
            float essentiaMod = enchantmentRecipe.getEssentiaMod(this.recipeInput);
            for (Aspect aspect : essentia.getAspects()) {
                if (aspect != null) essentia.add(aspect, (int) ((float) essentia.getAmount(aspect) * essentiaMod));
            }
            this.recipeEssentia = essentia;
            this.recipeXP = enchantmentRecipe.calcXP(this.recipeInput);
            this.recipePlayer = player.getName();
            this.instability = this.symmetry + this.recipeInstability;
            this.countDelay = 10;
            this.itemCount = 0;
            this.crafting = true;
            this.world.playSound(null, this.pos, TCSounds.CRAFTSTART, SoundCategory.BLOCKS, 0.5F, 1.0F);
            this.markDirtyAndSync();
        }
    }

    public void craftCycle() {
        boolean valid = this.isCentralInputValid();
        if (!valid || this.instability > 0 && this.world.rand.nextInt(500) <= this.instability) {
            this.runInstabilityEvent();
            if (valid) return;
        }

        if (!valid) {
            this.failCrafting();
            return;
        }

        if (this.recipeType == 1 && this.recipeXP > 0) {
            int xpDrainState = this.drainRecipeXP();
            if (xpDrainState > 0) return;
            if (xpDrainState < 0) this.addMissingIngredientInstability(3);
            return;
        }
        if (this.recipeType == 1 && this.recipeXP == 0) this.countDelay = 10;

        if (this.recipeEssentia.visSize() > 0) {
            for (Aspect aspect : this.recipeEssentia.getAspects()) {
                if (aspect == null || this.recipeEssentia.getAmount(aspect) <= 0) continue;
                if (EssentiaHandler.drainEssentia(this, aspect, null, 12)) {
                    this.recipeEssentia.reduce(aspect, 1);
                    this.markDirtyAndSync();
                    return;
                }
                this.increaseInstabilityRandom(Math.max(1, 100 - this.recipeInstability * 3));
                this.markDirtyAndSync();
            }
            this.checkSurroundings = true;
            return;
        }

        if (this.recipeIngredients != null && !this.recipeIngredients.isEmpty()) {
            for (int i = 0; i < this.recipeIngredients.size(); ++i) {
                ItemStack ingredient = this.recipeIngredients.get(i);
                BlockPos pedestalPos = this.findPedestalWith(ingredient);
                if (pedestalPos != null) {
                    if (this.itemCount == 0) {
                        this.itemCount = 5;
                        PacketHandler.INSTANCE.sendToAllAround(
                                new PacketFXInfusionSource(
                                        this.pos.getX(),
                                        this.pos.getY(),
                                        this.pos.getZ(),
                                        (byte) (this.pos.getX() - pedestalPos.getX()),
                                        (byte) (this.pos.getY() - pedestalPos.getY()),
                                        (byte) (this.pos.getZ() - pedestalPos.getZ()),
                                        0),
                                new NetworkRegistry.TargetPoint(
                                        this.world.provider.getDimension(),
                                        this.pos.getX(),
                                        this.pos.getY(),
                                        this.pos.getZ(),
                                        32.0));
                        this.markDirtyAndSync();
                    } else if (--this.itemCount <= 0) {
                        this.consumePedestalIngredient(pedestalPos);
                        this.recipeIngredients.remove(i);
                        this.itemCount = 0;
                        this.markDirtyAndSync();
                    }
                    return;
                }
                this.addMissingIngredientInstability(1 + i);
            }
            return;
        }

        this.instability = 0;
        this.crafting = false;
        this.craftingFinish(this.recipeOutput, this.recipeOutputLabel);
        this.recipeOutput = null;
        this.recipeOutputLabel = null;
        this.recipeInput = ItemStack.EMPTY;
        this.recipeInstability = 0;
        this.recipeXP = 0;
        this.recipeType = 0;
        this.recipePlayer = null;
        this.itemCount = 0;
        this.countDelay = 10;
        this.markDirtyAndSync();
    }

    public void craftingFinish(Object output, String label) {
        TilePedestal center = this.getCenterPedestal();
        if (center == null) return;

        if (output instanceof ItemStack) {
            center.setInventorySlotContentsFromInfusion(0, ((ItemStack) output).copy());
        } else if (output instanceof NBTBase) {
            ItemStack stack = center.getStackInSlot(0);
            if (!stack.isEmpty() && label != null && !label.isEmpty()) {
                stack.setTagInfo(label, ((NBTBase) output).copy());
                center.setInventorySlotContentsFromInfusion(0, stack);
            }
        } else if (output instanceof Enchantment) {
            ItemStack stack = center.getStackInSlot(0);
            if (!stack.isEmpty()) {
                Enchantment enchantment = (Enchantment) output;
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                int level = EnchantmentHelper.getEnchantmentLevel(enchantment, stack) + 1;
                enchantments.put(enchantment, level);
                EnchantmentHelper.setEnchantments(enchantments, stack);
                center.setInventorySlotContentsFromInfusion(0, stack);
            }
        }

        EntityPlayer player = this.recipePlayer == null ? null : this.world.getPlayerEntityByName(this.recipePlayer);
        if (player != null) {
            ItemStack crafted = center.getStackInSlot(0);
            FMLCommonHandler.instance().firePlayerCraftingEvent(player, crafted, new InventoryFake(this.recipeIngredients));
        }
        this.recipeEssentia = new AspectList();
        this.recipeIngredients.clear();
        this.world.addBlockEvent(this.pos.down(2), this.world.getBlockState(this.pos.down(2)).getBlock(), 12, 0);
        this.markDirtyAndSync();
    }

    private void setRecipeOutput(Object output) {
        this.recipeOutput = null;
        this.recipeOutputLabel = null;
        if (output instanceof Object[]) {
            Object[] out = (Object[]) output;
            if (out.length >= 2 && out[0] instanceof String && out[1] instanceof NBTBase) {
                this.recipeOutputLabel = (String) out[0];
                this.recipeOutput = ((NBTBase) out[1]).copy();
            }
        } else if (output instanceof ItemStack) {
            this.recipeOutput = ((ItemStack) output).copy();
        } else if (output instanceof NBTBase) {
            this.recipeOutput = ((NBTBase) output).copy();
        }
    }

    private TilePedestal getCenterPedestal() {
        TileEntity tile = this.world == null ? null : this.world.getTileEntity(this.pos.down(2));
        return tile instanceof TilePedestal ? (TilePedestal) tile : null;
    }

    private ArrayList<ItemStack> getPedestalComponents() {
        ArrayList<ItemStack> components = new ArrayList<ItemStack>();
        for (BlockPos pedestalPos : this.pedestals) {
            TileEntity tile = this.world.getTileEntity(pedestalPos);
            if (!(tile instanceof TilePedestal)) continue;
            ItemStack stack = ((TilePedestal) tile).getStackInSlot(0);
            if (!stack.isEmpty()) components.add(this.copySingle(stack));
        }
        return components;
    }

    private ItemStack copySingle(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }

    private boolean isCentralInputValid() {
        TilePedestal center = this.getCenterPedestal();
        if (center == null || this.recipeInput == null || this.recipeInput.isEmpty()) return false;
        ItemStack current = center.getStackInSlot(0);
        if (current.isEmpty()) return false;
        ItemStack compare = current.copy();
        if (this.recipeInput.getMetadata() == Short.MAX_VALUE) compare.setItemDamage(Short.MAX_VALUE);
        return InventoryUtils.areItemStacksEqualForCrafting(compare, this.recipeInput, true, true, false);
    }

    private int drainRecipeXP() {
        List<EntityPlayer> targets = this.world.getEntitiesWithinAABB(EntityPlayer.class, this.getEffectBounds(10.0D));
        boolean hadTargets = !targets.isEmpty();
        for (EntityPlayer target : targets) {
            if (target.experienceLevel <= 0) continue;
            target.addExperienceLevel(-1);
            --this.recipeXP;
            target.attackEntityFrom(DamageSource.MAGIC, this.world.rand.nextInt(2));
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXInfusionSource(
                            this.pos.getX(),
                            this.pos.getY(),
                            this.pos.getZ(),
                            (byte) 0,
                            (byte) 0,
                            (byte) 0,
                            target.getEntityId()),
                    new NetworkRegistry.TargetPoint(
                            this.world.provider.getDimension(),
                            this.pos.getX(),
                            this.pos.getY(),
                            this.pos.getZ(),
                            32.0));
            this.world.playSound(null, target.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.PLAYERS, 1.0F, 2.0F + this.world.rand.nextFloat() * 0.4F);
            this.countDelay = 20;
            this.markDirtyAndSync();
            return 1;
        }
        return hadTargets ? -1 : 0;
    }

    private BlockPos findPedestalWith(ItemStack ingredient) {
        if (ingredient == null || ingredient.isEmpty()) return null;
        for (BlockPos pedestalPos : this.pedestals) {
            TileEntity tile = this.world.getTileEntity(pedestalPos);
            if (!(tile instanceof TilePedestal)) continue;
            ItemStack stack = ((TilePedestal) tile).getStackInSlot(0);
            if (!stack.isEmpty() && InfusionRecipe.areItemStacksEqual(stack, ingredient, true)) return pedestalPos;
        }
        return null;
    }

    private void consumePedestalIngredient(BlockPos pedestalPos) {
        TileEntity tile = this.world.getTileEntity(pedestalPos);
        if (!(tile instanceof TilePedestal)) return;
        TilePedestal pedestal = (TilePedestal) tile;
        ItemStack stack = pedestal.getStackInSlot(0);
        if (stack.isEmpty()) return;
        ItemStack container = stack.getItem().getContainerItem(stack);
        pedestal.setInventorySlotContents(0, container == null ? ItemStack.EMPTY : container.copy());
        this.world.addBlockEvent(pedestalPos, this.world.getBlockState(pedestalPos).getBlock(), 11, 0);
    }

    private void addMissingIngredientInstability(int aspectChanceBound) {
        Aspect[] aspects = this.recipeEssentia.getAspects();
        if (aspects == null || aspects.length == 0 || this.world.rand.nextInt(Math.max(1, aspectChanceBound)) != 0) return;
        Aspect aspect = aspects[this.world.rand.nextInt(aspects.length)];
        if (aspect != null) this.recipeEssentia.add(aspect, 1);
        this.increaseInstabilityRandom(Math.max(1, 50 - this.recipeInstability * 2));
        this.markDirtyAndSync();
    }

    private void increaseInstabilityRandom(int bound) {
        if (this.world.rand.nextInt(Math.max(1, bound)) == 0) {
            ++this.instability;
            if (this.instability > 25) this.instability = 25;
        }
    }

    private void runInstabilityEvent() {
        switch (this.world.rand.nextInt(21)) {
            case 0:
            case 2:
            case 10:
            case 13:
                this.inEvEjectItem(0);
                break;
            case 6:
            case 17:
                this.inEvEjectItem(1);
                break;
            case 1:
            case 11:
                this.inEvEjectItem(2);
                break;
            case 3:
            case 8:
            case 14:
                this.inEvZap(false);
                break;
            case 5:
            case 16:
                this.inEvHarm(false);
                break;
            case 12:
                this.inEvZap(true);
                break;
            case 19:
                this.inEvEjectItem(3);
                break;
            case 7:
                this.inEvEjectItem(4);
                break;
            case 4:
            case 15:
                this.inEvEjectItem(5);
                break;
            case 18:
                this.inEvHarm(true);
                break;
            case 9:
                this.world.createExplosion(null, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D,
                        1.5F + this.world.rand.nextFloat(), false);
                break;
            case 20:
                this.inEvWarp();
                break;
            default:
                break;
        }
    }

    private void inEvZap(boolean all) {
        List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEffectBounds(10.0D));
        for (EntityLivingBase target : targets) {
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXBlockZap(
                            this.pos.getX() + 0.5F,
                            this.pos.getY() + 0.5F,
                            this.pos.getZ() + 0.5F,
                            (float) target.posX,
                            (float) target.posY + target.height / 2.0F,
                            (float) target.posZ),
                    new NetworkRegistry.TargetPoint(
                            this.world.provider.getDimension(),
                            this.pos.getX(),
                            this.pos.getY(),
                            this.pos.getZ(),
                            32.0));
            target.attackEntityFrom(DamageSource.MAGIC, 4.0F + this.world.rand.nextInt(4));
            if (!all) break;
        }
    }

    private void inEvHarm(boolean all) {
        List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEffectBounds(10.0D));
        for (EntityLivingBase target : targets) {
            if (this.world.rand.nextBoolean() && Config.potionFluxTaint != null) {
                target.addPotionEffect(new PotionEffect(Config.potionFluxTaint, 120, 0, false, true));
            } else if (Config.potionVisExhaust != null) {
                target.addPotionEffect(new PotionEffect(Config.potionVisExhaust, 2400, 0, false, true));
            }
            if (!all) break;
        }
    }

    private void inEvWarp() {
        List<EntityPlayer> targets = this.world.getEntitiesWithinAABB(EntityPlayer.class, this.getEffectBounds(10.0D));
        if (targets.isEmpty()) return;
        EntityPlayer target = targets.get(this.world.rand.nextInt(targets.size()));
        if (this.world.rand.nextFloat() < 0.25F) {
            Thaumcraft.addStickyWarpToPlayer(target, 1);
        } else {
            Thaumcraft.addWarpToPlayer(target, 1 + this.world.rand.nextInt(5), true);
        }
    }

    private void inEvEjectItem(int type) {
        for (int tries = 0; tries < 50 && !this.pedestals.isEmpty(); ++tries) {
            BlockPos pedestalPos = this.pedestals.get(this.world.rand.nextInt(this.pedestals.size()));
            TileEntity tile = this.world.getTileEntity(pedestalPos);
            if (!(tile instanceof TilePedestal) || ((TilePedestal) tile).getStackInSlot(0).isEmpty()) continue;

            if (type < 3 || type == 5) {
                InventoryUtils.dropItems(this.world, pedestalPos.getX(), pedestalPos.getY(), pedestalPos.getZ());
            } else {
                ((TilePedestal) tile).setInventorySlotContents(0, ItemStack.EMPTY);
            }

            BlockPos above = pedestalPos.up();
            if ((type == 1 || type == 3) && ConfigBlocks.blockFluxGoo != null) {
                this.world.setBlockState(above, ConfigBlocks.blockFluxGoo.getStateFromMeta(7), 3);
                this.world.playSound(null, pedestalPos, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.BLOCKS, 0.3F, 1.0F);
            } else if ((type == 2 || type == 4) && ConfigBlocks.blockFluxGas != null) {
                this.world.setBlockState(above, ConfigBlocks.blockFluxGas.getStateFromMeta(7), 3);
                this.world.playSound(null, pedestalPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.3F, 1.0F);
            } else if (type == 5) {
                this.world.createExplosion(null, pedestalPos.getX() + 0.5D, pedestalPos.getY() + 0.5D, pedestalPos.getZ() + 0.5D, 1.0F, false);
            }

            this.world.addBlockEvent(pedestalPos, this.world.getBlockState(pedestalPos).getBlock(), 11, 0);
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXBlockZap(
                            this.pos.getX() + 0.5F,
                            this.pos.getY() + 0.5F,
                            this.pos.getZ() + 0.5F,
                            pedestalPos.getX() + 0.5F,
                            pedestalPos.getY() + 1.5F,
                            pedestalPos.getZ() + 0.5F),
                    new NetworkRegistry.TargetPoint(
                            this.world.provider.getDimension(),
                            this.pos.getX(),
                            this.pos.getY(),
                            this.pos.getZ(),
                            32.0));
            return;
        }
    }

    private AxisAlignedBB getEffectBounds(double range) {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(range, range, range);
    }

    private void failCrafting() {
        this.instability = 0;
        this.crafting = false;
        this.resetCraftingState();
        this.world.playSound(null, this.pos, TCSounds.CRAFTFAIL, SoundCategory.BLOCKS, 1.0F, 0.6F);
        this.markDirtyAndSync();
    }

    private void resetCraftingState() {
        this.recipeEssentia = new AspectList();
        this.recipeIngredients.clear();
        this.recipeOutput = null;
        this.recipeOutputLabel = null;
        this.recipeInput = ItemStack.EMPTY;
        this.recipeInstability = 0;
        this.recipeXP = 0;
        this.recipeType = 0;
        this.recipePlayer = null;
        this.itemCount = 0;
        this.countDelay = 10;
    }

    private void getSurroundings() {
        ArrayList<BlockPos> stabilizers = new ArrayList<BlockPos>();
        this.pedestals.clear();

        for (int xx = -12; xx <= 12; ++xx) {
            for (int zz = -12; zz <= 12; ++zz) {
                boolean foundPedestalInColumn = false;
                for (int yy = -5; yy <= 10; ++yy) {
                    if (xx == 0 && zz == 0) continue;
                    BlockPos scan = this.pos.add(xx, -yy, zz);
                    if (!this.world.isBlockLoaded(scan)) continue;
                    TileEntity tile = this.world.getTileEntity(scan);
                    if (!foundPedestalInColumn && yy > 0 && Math.abs(xx) <= 8 && Math.abs(zz) <= 8 && tile instanceof TilePedestal) {
                        this.pedestals.add(scan.toImmutable());
                        foundPedestalInColumn = true;
                    }
                    if (this.isStabilizer(scan)) stabilizers.add(scan.toImmutable());
                }
            }
        }

        this.symmetry = 0;
        for (BlockPos pedestalPos : this.pedestals) {
            boolean hasItem = false;
            TileEntity tile = this.world.getTileEntity(pedestalPos);
            if (tile instanceof TilePedestal) {
                this.symmetry += 2;
                hasItem = !((TilePedestal) tile).getStackInSlot(0).isEmpty();
                if (hasItem) ++this.symmetry;
            }
            BlockPos mirror = this.pos.add(this.pos.getX() - pedestalPos.getX(), pedestalPos.getY() - this.pos.getY(), this.pos.getZ() - pedestalPos.getZ());
            TileEntity mirrorTile = this.world.getTileEntity(mirror);
            if (mirrorTile instanceof TilePedestal) {
                this.symmetry -= 2;
                if (hasItem && !((TilePedestal) mirrorTile).getStackInSlot(0).isEmpty()) --this.symmetry;
            }
        }

        float stabilizerSymmetry = 0.0F;
        for (BlockPos stabilizer : stabilizers) {
            if (this.isStabilizer(stabilizer)) stabilizerSymmetry += 0.1F;
            BlockPos mirror = this.pos.add(this.pos.getX() - stabilizer.getX(), stabilizer.getY() - this.pos.getY(), this.pos.getZ() - stabilizer.getZ());
            if (this.isStabilizer(mirror)) stabilizerSymmetry -= 0.2F;
        }
        this.symmetry = (int) ((float) this.symmetry + stabilizerSymmetry);
    }

    private boolean isStabilizer(BlockPos pos) {
        if (this.world == null || !this.world.isBlockLoaded(pos)) return false;
        Block block = this.world.getBlockState(pos).getBlock();
        return block == Blocks.SKULL
                || block instanceof IInfusionStabiliser
                && ((IInfusionStabiliser) block).canStabaliseInfusion(this.world, pos.getX(), pos.getY(), pos.getZ());
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

    private void doEffects() {
        if (this.crafting) {
            if (this.craftCount == 0) {
                this.world.playSound(
                        this.pos.getX(),
                        this.pos.getY(),
                        this.pos.getZ(),
                        TCSounds.INFUSERSTART,
                        SoundCategory.BLOCKS,
                        0.5F,
                        1.0F,
                        false);
            } else if (this.craftCount % 65 == 0) {
                this.world.playSound(
                        this.pos.getX(),
                        this.pos.getY(),
                        this.pos.getZ(),
                        TCSounds.INFUSER,
                        SoundCategory.BLOCKS,
                        0.5F,
                        1.0F,
                        false);
            }
            ++this.craftCount;
        } else if (this.craftCount > 0) {
            this.craftCount -= 2;
            if (this.craftCount < 0) this.craftCount = 0;
            if (this.craftCount > 50) this.craftCount = 50;
        }

        if (this.active && this.startUp < 1.0F) {
            this.startUp += Math.max(this.startUp / 10.0F, 0.001F);
            if (this.startUp > 0.999F) this.startUp = 1.0F;
        }
        if (!this.active && this.startUp > 0.0F) {
            this.startUp -= this.startUp / 10.0F;
            if (this.startUp < 0.001F) this.startUp = 0.0F;
        }

        for (String fxKey : new ArrayList<String>(this.sourceFX.keySet())) {
            SourceFX fx = this.sourceFX.get(fxKey);
            if (fx == null || fx.ticks <= 0) {
                this.sourceFX.remove(fxKey);
                continue;
            }

            if (this.pos.equals(fx.loc)) {
                Entity target = this.world.getEntityByID(fx.color);
                if (target != null) {
                    for (int i = 0; i < Thaumcraft.proxy.particleCount(2); ++i) {
                        Thaumcraft.proxy.drawInfusionParticles4(
                                this.world,
                                target.posX + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * target.width,
                                target.getEntityBoundingBox().minY + this.world.rand.nextFloat() * target.height,
                                target.posZ + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * target.width,
                                this.pos.getX(),
                                this.pos.getY(),
                                this.pos.getZ());
                    }
                }
            } else {
                TileEntity tile = this.world.getTileEntity(fx.loc);
                if (tile instanceof TilePedestal) {
                    ItemStack stack = ((TilePedestal) tile).getStackInSlot(0);
                    if (!stack.isEmpty()) {
                        if (this.world.rand.nextInt(3) == 0) {
                            Thaumcraft.proxy.drawInfusionParticles3(
                                    this.world,
                                    fx.loc.getX() + this.world.rand.nextFloat(),
                                    fx.loc.getY() + this.world.rand.nextFloat() + 1.0D,
                                    fx.loc.getZ() + this.world.rand.nextFloat(),
                                    this.pos.getX(),
                                    this.pos.getY(),
                                    this.pos.getZ());
                        } else {
                            Item item = stack.getItem();
                            int meta = stack.getItemDamage();
                            if (meta == 0 && item instanceof ItemBlock) {
                                Block block = Block.getBlockFromItem(item);
                                IBlockState state = block.getStateFromMeta(meta);
                                for (int i = 0; i < Thaumcraft.proxy.particleCount(2); ++i) {
                                    Thaumcraft.proxy.drawInfusionParticles2(
                                            this.world,
                                            fx.loc.getX() + this.world.rand.nextFloat(),
                                            fx.loc.getY() + this.world.rand.nextFloat() + 1.0D,
                                            fx.loc.getZ() + this.world.rand.nextFloat(),
                                            this.pos.getX(),
                                            this.pos.getY(),
                                            this.pos.getZ(),
                                            state);
                                }
                            } else {
                                for (int i = 0; i < Thaumcraft.proxy.particleCount(2); ++i) {
                                    Thaumcraft.proxy.drawInfusionParticles1(
                                            this.world,
                                            fx.loc.getX() + 0.4D + this.world.rand.nextFloat() * 0.2D,
                                            fx.loc.getY() + 1.23D + this.world.rand.nextFloat() * 0.2D,
                                            fx.loc.getZ() + 0.4D + this.world.rand.nextFloat() * 0.2D,
                                            this.pos.getX(),
                                            this.pos.getY(),
                                            this.pos.getZ(),
                                            item,
                                            meta);
                                }
                            }
                        }
                    }
                } else {
                    fx.ticks = 0;
                }
            }

            --fx.ticks;
            this.sourceFX.put(fxKey, fx);
        }

        if (this.crafting && this.instability > 0 && this.world.rand.nextInt(200) <= this.instability) {
            Thaumcraft.proxy.nodeBolt(
                    this.world,
                    this.pos.getX() + 0.5F,
                    this.pos.getY() + 0.5F,
                    this.pos.getZ() + 0.5F,
                    this.pos.getX() + 0.5F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 2.0F,
                    this.pos.getY() + 0.5F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 2.0F,
                    this.pos.getZ() + 0.5F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 2.0F);
        }
    }

    @Override
    public AspectList getAspects() {
        return this.recipeEssentia;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.recipeEssentia = aspects == null ? new AspectList() : aspects.copy();
        this.markDirtyAndSync();
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0) return amount;
        this.recipeEssentia.add(tag, amount);
        this.markDirtyAndSync();
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (tag == null || amount <= 0 || this.recipeEssentia.getAmount(tag) < amount) return false;
        this.recipeEssentia.remove(tag, amount);
        this.markDirtyAndSync();
        return true;
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        if (aspects == null || !this.doesContainerContain(aspects)) return false;
        for (Aspect aspect : aspects.getAspects()) {
            this.recipeEssentia.remove(aspect, aspects.getAmount(aspect));
        }
        this.markDirtyAndSync();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return tag != null && this.recipeEssentia.getAmount(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        if (aspects == null) return false;
        for (Aspect aspect : aspects.getAspects()) {
            if (this.recipeEssentia.getAmount(aspect) < aspects.getAmount(aspect)) return false;
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return tag == null ? this.recipeEssentia.visSize() : this.recipeEssentia.getAmount(tag);
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag != null;
    }

    public static class SourceFX {
        public final BlockPos loc;
        public int ticks;
        public final int color;
        public int entity;

        public SourceFX(BlockPos loc, int ticks, int color) {
            this.loc = loc;
            this.ticks = ticks;
            this.color = color;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, -1, -1), this.pos.add(2, 2, 2));
    }

    private void markDirtyAndSync() {
        this.markDirty();
        if (this.world != null && !this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }
}
