package thaumcraft.common.items.wands;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.api.wands.IWandFocus;
import thaumcraft.api.wands.IWandRodOnUpdate;
import thaumcraft.api.wands.IWandable;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileOwned;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

public class ItemWandCasting extends Item implements IArchitect {

    private static final UUID STAFF_ATTACK_UUID = UUID.fromString("1d082610-4093-11e4-916c-0800200c9a66");
    private static final UUID STAFF_SPEED_UUID = UUID.fromString("1d082611-4093-11e4-916c-0800200c9a66");

    public static final String TAG_ROD = "rod";
    public static final String TAG_CAP = "cap";
    public static final String TAG_FOCUS = "focus";
    public static final String TAG_VIS_PREFIX = "";

    public ItemWandCasting() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(false);
    }

    // ---- NBT Helpers ----

    public static NBTTagCompound ensureTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    // ---- Rod / Cap ----

    public static void setRod(ItemStack stack, WandRod rod) {
        NBTTagCompound tag = ensureTag(stack);
        tag.setString(TAG_ROD, rod.getTag());
        // Staff attack/speed attribute modifiers are now provided dynamically via
        // getAttributeModifiers(). Strip any legacy NBT AttributeModifiers so the
        // override is reached instead of the stale NBT path.
        if (tag.hasKey("AttributeModifiers")) {
            tag.removeTag("AttributeModifiers");
        }
    }

    public static WandRod getRod(ItemStack stack) {
        if (stack.hasTagCompound()) {
            // Lazy migration: strip legacy NBT AttributeModifiers so the
            // getAttributeModifiers() override is used instead of stale NBT.
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("AttributeModifiers")) {
                tag.removeTag("AttributeModifiers");
            }
            if (tag.hasKey(TAG_ROD)) {
                return WandRod.rods.get(tag.getString(TAG_ROD));
            }
        }
        return WandRod.rods.get("wood");
    }

    public static void setCap(ItemStack stack, WandCap cap) {
        ensureTag(stack).setString(TAG_CAP, cap.getTag());
    }

    public static WandCap getCap(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_CAP)) {
            return WandCap.caps.get(stack.getTagCompound().getString(TAG_CAP));
        }
        return WandCap.caps.get("iron");
    }

    // ---- Vis Storage ----

    public static int getVis(ItemStack stack, Aspect aspect) {
        if (stack == null || stack.isEmpty() || aspect == null) return 0;
        if (!stack.hasTagCompound()) return 0;
        return stack.getTagCompound().getInteger(TAG_VIS_PREFIX + aspect.getTag());
    }

    public static void setVis(ItemStack stack, Aspect aspect, int amount) {
        if (stack == null || stack.isEmpty() || aspect == null) return;
        ensureTag(stack).setInteger(TAG_VIS_PREFIX + aspect.getTag(), Math.max(0, Math.min(amount, getMaxVis(stack))));
    }

    public AspectList getAllVis(ItemStack stack) {
        AspectList out = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            out.merge(aspect, getVis(stack, aspect));
        }
        return out;
    }

    public AspectList getAspectsWithRoom(ItemStack stack) {
        AspectList out = new AspectList();
        AspectList current = this.getAllVis(stack);
        for (Aspect aspect : current.getAspects()) {
            if (current.getAmount(aspect) < getMaxVis(stack)) {
                out.add(aspect, 1);
            }
        }
        return out;
    }

    public void storeAllVis(ItemStack stack, AspectList in) {
        if (stack == null || stack.isEmpty() || in == null) return;
        for (Aspect aspect : in.getAspects()) {
            this.storeVis(stack, aspect, in.getAmount(aspect));
        }
    }

    public void storeVis(ItemStack stack, Aspect aspect, int amount) {
        if (stack == null || stack.isEmpty() || aspect == null) return;
        ensureTag(stack).setInteger(TAG_VIS_PREFIX + aspect.getTag(), amount);
    }

    public static void addVis(ItemStack stack, Aspect aspect, int amount) {
        if (stack == null || stack.isEmpty() || aspect == null || amount == 0) return;
        addRealVis(stack, aspect, amount * 100);
    }

    public static int addVis(ItemStack stack, Aspect aspect, int amount, boolean doit) {
        if (stack == null || stack.isEmpty() || aspect == null || !aspect.isPrimal()) return 0;
        int storeAmount = getVis(stack, aspect) + amount * 100;
        int leftover = Math.max(storeAmount - getMaxVis(stack), 0);
        if (doit) {
            setVis(stack, aspect, Math.min(storeAmount, getMaxVis(stack)));
        }
        return leftover / 100;
    }

    public static int addRealVis(ItemStack stack, Aspect aspect, int amount) {
        return addRealVis(stack, aspect, amount, true);
    }

    public static int addRealVis(ItemStack stack, Aspect aspect, int amount, boolean doit) {
        if (stack == null || stack.isEmpty() || aspect == null || !aspect.isPrimal()) return 0;
        int storeAmount = getVis(stack, aspect) + amount;
        int leftover = Math.max(storeAmount - getMaxVis(stack), 0);
        if (doit) {
            setVis(stack, aspect, Math.min(storeAmount, getMaxVis(stack)));
        }
        return leftover;
    }

    public static int getMaxVis(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        WandRod rod = getRod(stack);
        return rod != null ? rod.getCapacity() * (isSceptre(stack) ? 150 : 100) : 10000;
    }

    public static float getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return getConsumptionModifier(stack, player, aspect, false);
    }

    public static float getConsumptionModifier(ItemStack stack, EntityPlayer player, Aspect aspect, boolean crafting) {
        WandCap cap = getCap(stack);
        float discount = cap != null ? cap.getBaseCostModifier() : 1.0F;
        if (cap != null && cap.getSpecialCostModifierAspects() != null && cap.getSpecialCostModifierAspects().contains(aspect)) {
            discount = cap.getSpecialCostModifier();
        }
        if (player != null) {
            discount -= WandManager.getTotalVisDiscount(player, aspect);
            if (!crafting) {
                discount -= (float) getFocusFrugal(stack) / 10.0F;
            }
        }
        if (isSceptre(stack)) {
            discount -= 0.1F;
        }
        return Math.max(discount, 0.1F);
    }

    /**
     * Checks if the wand can supply the given vis cost. Uses crafting mode for exact matching.
     */
    public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit, boolean crafting) {
        if (cost == null || cost.size() == 0) return false;

        AspectList realCost = new AspectList();
        for (Aspect aspect : cost.getAspects()) {
            int needed = (int)((float) cost.getAmount(aspect) * getConsumptionModifier(stack, player, aspect, crafting));
            realCost.add(aspect, needed);
        }

        for (Aspect aspect : realCost.getAspects()) {
            int needed = realCost.getAmount(aspect);
            if (needed > 0 && getVis(stack, aspect) < needed) {
                return false;
            }
        }

        if (doit && (player == null || !player.world.isRemote)) {
            for (Aspect aspect : realCost.getAspects()) {
                addRealVis(stack, aspect, -realCost.getAmount(aspect));
            }
        }
        return true;
    }

    public boolean consumeAllVisCrafting(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit) {
        if (cost == null || cost.size() == 0) return false;
        AspectList realCost = new AspectList();
        for (Aspect aspect : cost.getAspects()) {
            realCost.add(aspect, cost.getAmount(aspect) * 100);
        }
        return consumeAllVis(stack, player, realCost, doit, true);
    }

    public boolean consumeVis(ItemStack stack, EntityPlayer player, Aspect aspect, int amount, boolean crafting) {
        if (stack == null || stack.isEmpty() || aspect == null) return false;
        int modifiedAmount = (int)((float) amount * getConsumptionModifier(stack, player, aspect, crafting));
        if (getVis(stack, aspect) >= modifiedAmount) {
            this.storeVis(stack, aspect, getVis(stack, aspect) - modifiedAmount);
            return true;
        }
        return false;
    }

    // ---- Focus ----

    public ItemStack getFocusItem(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_FOCUS)) {
            NBTTagCompound focusTag = stack.getTagCompound().getCompoundTag(TAG_FOCUS);
            return new ItemStack(focusTag);
        }
        return ItemStack.EMPTY;
    }

    public ItemFocusBasic getFocus(ItemStack stack) {
        ItemStack focusStack = getFocusItem(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof ItemFocusBasic) {
            return (ItemFocusBasic) focusStack.getItem();
        }
        return null;
    }

    public void setFocus(ItemStack stack, ItemStack focus) {
        NBTTagCompound tag = ensureTag(stack);
        if (focus != null && !focus.isEmpty()) {
            NBTTagCompound focusTag = new NBTTagCompound();
            focus.writeToNBT(focusTag);
            tag.setTag(TAG_FOCUS, focusTag);
        } else {
            tag.removeTag(TAG_FOCUS);
        }
    }

    public int getFocusTreasure(ItemStack stack) {
        return getFocusUpgradeLevel(stack, FocusUpgradeType.treasure);
    }

    public int getFocusPotency(ItemStack stack) {
        return getFocusUpgradeLevel(stack, FocusUpgradeType.potency) + (hasRunes(stack) ? 1 : 0);
    }

    public int getFocusEnlarge(ItemStack stack) {
        return getFocusUpgradeLevel(stack, FocusUpgradeType.enlarge);
    }

    public int getFocusExtend(ItemStack stack) {
        return getFocusUpgradeLevel(stack, FocusUpgradeType.extend);
    }

    private static int getFocusUpgradeLevel(ItemStack stack, FocusUpgradeType type) {
        ItemStack focusStack = getFocusItemStatic(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof ItemFocusBasic) {
            return ((ItemFocusBasic) focusStack.getItem()).getUpgradeLevel(focusStack, type);
        }
        return 0;
    }

    public static int getFocusFrugal(ItemStack stack) {
        ItemStack focusStack = getFocusItemStatic(stack);
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof ItemFocusBasic) {
            return ((ItemFocusBasic) focusStack.getItem()).getUpgradeLevel(focusStack, FocusUpgradeType.frugal);
        }
        return 0;
    }

    private static ItemStack getFocusItemStatic(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_FOCUS)) {
            return new ItemStack(stack.getTagCompound().getCompoundTag(TAG_FOCUS));
        }
        return ItemStack.EMPTY;
    }

    public static boolean isSceptre(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().getBoolean("sceptre");
    }

    public boolean isStaff(ItemStack stack) {
        return getRod(stack) instanceof StaffRod;
    }

    public boolean hasRunes(ItemStack stack) {
        WandRod rod = getRod(stack);
        return rod instanceof StaffRod && ((StaffRod) rod).hasRunes();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND && isStaff(stack)) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(STAFF_ATTACK_UUID, "Weapon modifier", 6.0D, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(STAFF_SPEED_UUID, "Weapon speed modifier", -3.2D, 0));
        }
        return multimap;
    }

    public void setObjectInUse(ItemStack stack, int x, int y, int z) {
        NBTTagCompound tag = ensureTag(stack);
        tag.setInteger("IIUX", x);
        tag.setInteger("IIUY", y);
        tag.setInteger("IIUZ", z);
    }

    public void clearObjectInUse(ItemStack stack) {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound tag = stack.getTagCompound();
        tag.removeTag("IIUX");
        tag.removeTag("IIUY");
        tag.removeTag("IIUZ");
    }

    public IWandable getObjectInUse(ItemStack stack, World world) {
        if (stack == null || stack.isEmpty() || world == null || !stack.hasTagCompound()) return null;
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("IIUX") || !tag.hasKey("IIUY") || !tag.hasKey("IIUZ")) return null;
        TileEntity tile = world.getTileEntity(new BlockPos(tag.getInteger("IIUX"), tag.getInteger("IIUY"), tag.getInteger("IIUZ")));
        return tile instanceof IWandable ? (IWandable) tile : null;
    }

    // ---- Item Overrides ----

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String name = I18n.translateToLocal("item.Wand.name");
        name = name.replace("%CAP", I18n.translateToLocal("item.Wand." + getCap(stack).getTag() + ".cap"));
        String rodTag = getRod(stack).getTag();
        if (rodTag.contains("_staff")) {
            rodTag = rodTag.substring(0, rodTag.indexOf("_staff"));
        }
        name = name.replace("%ROD", I18n.translateToLocal("item.Wand." + rodTag + ".rod"));
        name = name.replace("%OBJ", isStaff(stack)
                ? I18n.translateToLocal("item.Wand.staff.obj")
                : (isSceptre(stack)
                ? I18n.translateToLocal("item.Wand.sceptre.obj")
                : I18n.translateToLocal("item.Wand.wand.obj")));
        return name;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            WandRod rod = getRod(stack);
            if (rod != null && rod.getOnUpdate() != null) {
                rod.getOnUpdate().onUpdate(stack, player);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        WandRod rod = getRod(stack);
        WandCap cap = getCap(stack);

        if (rod != null) {
            String rodDisplay = rod.getTag();
            if (rodDisplay.contains("_staff")) {
                rodDisplay = rodDisplay.substring(0, rodDisplay.indexOf("_staff"));
            }
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.WandCasting.rod") + " " +
                    TextFormatting.WHITE + I18n.translateToLocal("item.Wand." + rodDisplay + ".rod"));
        }
        if (cap != null) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.WandCasting.cap") + " " +
                    TextFormatting.WHITE + I18n.translateToLocal("item.Wand." + cap.getTag() + ".cap"));
        }

        // Vis display
        DecimalFormat formatter = new DecimalFormat("#####.##");
        tooltip.add(TextFormatting.AQUA + "" + TextFormatting.ITALIC + I18n.translateToLocal("item.WandCasting.vis"));
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (aspect == null) continue;
            int vis = getVis(stack, aspect);
            int max = getMaxVis(stack);
            if (vis > 0 || max > 0) {
                tooltip.add(" " + TextFormatting.GRAY + aspect.getName() + ": " +
                        TextFormatting.WHITE + formatter.format((float) vis / 100.0F) + "/" + formatter.format((float) max / 100.0F));
            }
        }

        // Focus info
        ItemFocusBasic focus = getFocus(stack);
        if (focus != null) {
            ItemStack focusStack = getFocusItem(stack);
            tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.translateToLocal("item.WandCasting.focus") + " " +
                    TextFormatting.WHITE + focusStack.getDisplayName());
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            // Create default presets
            for (WandRod rod : WandRod.rods.values()) {
                for (WandCap cap : WandCap.caps.values()) {
                    ItemStack stack = new ItemStack(this);
                    setRod(stack, rod);
                    setCap(stack, cap);
                    // Fill with max vis for creative tab display
                    for (Aspect aspect : Aspect.getPrimalAspects()) {
                        setVis(stack, aspect, getMaxVis(stack));
                    }
                    items.add(stack);
                }
            }

            ItemStack sceptre = new ItemStack(this);
            setRod(sceptre, WandRod.rods.get("silverwood"));
            setCap(sceptre, WandCap.caps.get("thaumium"));
            sceptre.getTagCompound().setByte("sceptre", (byte) 1);
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                setVis(sceptre, aspect, getMaxVis(sceptre));
            }
            items.add(sceptre);
        }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        int sideIndex = side == null ? 1 : side.getIndex();

        if (block instanceof IWandable) {
            int ret = ((IWandable) block).onWandRightClick(world, stack, player, pos.getX(), pos.getY(), pos.getZ(), sideIndex, meta);
            if (ret >= 0) return ret == 1 ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IWandable) {
            int ret = ((IWandable) tile).onWandRightClick(world, stack, player, pos.getX(), pos.getY(), pos.getZ(), sideIndex, meta);
            if (ret >= 0) return ret == 1 ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
        }

        if (WandTriggerRegistry.hasTrigger(block, meta)
                && WandTriggerRegistry.performTrigger(world, stack, player, pos.getX(), pos.getY(), pos.getZ(), sideIndex, block, meta)) {
            return EnumActionResult.SUCCESS;
        }

        if (((block == ConfigBlocks.blockWoodenDevice && meta == 2)
                || (block == ConfigBlocks.blockCosmeticOpaque && meta == 2))
                && (!Config.wardedStone || (tile instanceof TileOwned && player.getName().equals(((TileOwned) tile).owner)))) {
            if (!world.isRemote) {
                if (tile instanceof TileOwned) {
                    ((TileOwned) tile).safeToRemove = true;
                }
                world.spawnEntity(new EntityItem(world,
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D,
                        new ItemStack(block, 1, meta)));
                world.playEvent(2001, pos, Block.getStateId(state));
                world.setBlockToAir(pos);
            } else {
                player.swingArm(hand);
            }
            return EnumActionResult.SUCCESS;
        }

        if (block == ConfigBlocks.blockArcaneDoor
                && (!Config.wardedStone || (tile instanceof TileOwned && player.getName().equals(((TileOwned) tile).owner)))) {
            if (!world.isRemote) {
                if (tile instanceof TileOwned) {
                    ((TileOwned) tile).safeToRemove = true;
                }
                TileEntity upperLower = (meta & 8) == 0 ? world.getTileEntity(pos.up()) : world.getTileEntity(pos.down());
                if (upperLower instanceof TileOwned) {
                    ((TileOwned) upperLower).safeToRemove = true;
                }
                if (Config.wardedStone || (meta & 8) == 0) {
                    world.spawnEntity(new EntityItem(world,
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D,
                            new ItemStack(ConfigItems.itemArcaneDoor)));
                }
                world.playEvent(2001, pos, Block.getStateId(state));
                world.setBlockToAir(pos);
            } else {
                player.swingArm(hand);
            }
            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        RayTraceResult mop = this.rayTrace(world, player, false);

        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            TileEntity tile = world.getTileEntity(mop.getBlockPos());
            if (tile instanceof IWandable) {
                ItemStack result = ((IWandable) tile).onWandRightClick(world, stack, player);
                if (result != null) return new ActionResult<>(EnumActionResult.SUCCESS, result);
            }
            Block block = world.getBlockState(mop.getBlockPos()).getBlock();
            if (block instanceof IWandable) {
                ItemStack result = ((IWandable) block).onWandRightClick(world, stack, player);
                if (result != null) return new ActionResult<>(EnumActionResult.SUCCESS, result);
            }
        }

        ItemFocusBasic focus = getFocus(stack);
        if (focus != null) {
            if (!WandManager.isOnCooldown(player)) {
                WandManager.setCooldown(player, focus.getActivationCooldown(getFocusItem(stack)));
                ItemStack result = focus.onFocusRightClick(stack, world, player, mop);
                if (result != null) {
                    return new ActionResult<>(EnumActionResult.SUCCESS, result);
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer) {
            IWandable wandable = getObjectInUse(stack, player.world);
            if (wandable != null) {
                wandable.onUsingWandTick(stack, (EntityPlayer) player, count);
                return;
            }
            ItemFocusBasic focus = getFocus(stack);
            if (focus != null && !WandManager.isOnCooldown(player)) {
                WandManager.setCooldown(player, focus.getActivationCooldown(getFocusItem(stack)));
                focus.onUsingFocusTick(stack, (EntityPlayer) player, count);
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
        if (player instanceof EntityPlayer) {
            IWandable wandable = getObjectInUse(stack, world);
            if (wandable != null) {
                wandable.onWandStoppedUsing(stack, world, (EntityPlayer) player, this.getMaxItemUseDuration(stack) - timeLeft);
            } else {
                ItemFocusBasic focus = getFocus(stack);
                if (focus != null) {
                focus.onPlayerStoppedUsingFocus(stack, world, (EntityPlayer) player, this.getMaxItemUseDuration(stack) - timeLeft);
                }
            }
            clearObjectInUse(stack);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        ItemStack focusStack = getFocusItem(stack);
        if (!focusStack.isEmpty()) {
            return focusStack.getItem().getDestroySpeed(focusStack, state);
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (!player.getEntityWorld().isRemote) {
            ItemFocusBasic focus = getFocus(stack);
            if (focus != null && !WandManager.isOnCooldown(player)) {
                WandManager.setCooldown(player, focus.getActivationCooldown(getFocusItem(stack)));
                return focus.onFocusBlockStartBreak(stack, pos.getX(), pos.getY(), pos.getZ(), player);
            }
        }
        return false;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        ItemFocusBasic focus = getFocus(stack);
        if (focus != null && !WandManager.isOnCooldown(entityLiving)) {
            WandManager.setCooldown(entityLiving, focus.getActivationCooldown(getFocusItem(stack)));
            return focus.onEntitySwing(entityLiving, stack);
        }
        return super.onEntitySwing(entityLiving, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack stack, World world, int x, int y, int z, int side, EntityPlayer player) {
        ItemFocusBasic focus = getFocus(stack);
        ItemStack focusStack = getFocusItem(stack);
        if (focus instanceof IArchitect && !focusStack.isEmpty() && focus.isUpgradedWith(focusStack, FocusUpgradeType.architect)) {
            return ((IArchitect) focus).getArchitectBlocks(stack, world, x, y, z, side, player);
        }
        return null;
    }

    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, int side, IArchitect.EnumAxis axis) {
        ItemFocusBasic focus = getFocus(stack);
        ItemStack focusStack = getFocusItem(stack);
        return focus instanceof IArchitect
                && !focusStack.isEmpty()
                && focus.isUpgradedWith(focusStack, FocusUpgradeType.architect)
                && ((IArchitect) focus).showAxis(stack, world, player, side, axis);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return getFocus(stack) != null;
    }
}
