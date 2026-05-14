package thaumcraft.common.items.wands;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.api.wands.IWandFocus;
import thaumcraft.api.wands.IWandRodOnUpdate;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.Config;

import java.text.DecimalFormat;
import java.util.List;

public class ItemWandCasting extends Item {

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
        ensureTag(stack).setString(TAG_ROD, rod.getTag());
    }

    public static WandRod getRod(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_ROD)) {
            return WandRod.rods.get(stack.getTagCompound().getString(TAG_ROD));
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

    public static void addVis(ItemStack stack, Aspect aspect, int amount) {
        if (stack == null || stack.isEmpty() || aspect == null || amount == 0) return;
        addRealVis(stack, aspect, amount * 100);
    }

    public static int addVis(ItemStack stack, Aspect aspect, int amount, boolean doit) {
        if (stack == null || stack.isEmpty() || aspect == null || !aspect.isPrimal()) return amount;
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
        if (stack == null || stack.isEmpty() || aspect == null || !aspect.isPrimal()) return amount;
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
            discount = Math.min(discount, cap.getSpecialCostModifier());
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
        if (player != null && player.capabilities.isCreativeMode) return true;

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
        if (!focus.isEmpty()) {
            NBTTagCompound focusTag = new NBTTagCompound();
            focus.writeToNBT(focusTag);
            tag.setTag(TAG_FOCUS, focusTag);
        } else {
            tag.removeTag(TAG_FOCUS);
        }
    }

    public int getFocusTreasure(ItemStack stack) {
        ItemFocusBasic focus = getFocus(stack);
        if (focus != null) {
            // Default treasure level
            return 0;
        }
        return 0;
    }

    public static int getFocusFrugal(ItemStack stack) {
        ItemStack focusStack = getFocusItemStatic(stack);
        int level = 0;
        if (!focusStack.isEmpty() && focusStack.getItem() instanceof ItemFocusBasic) {
            level += ((ItemFocusBasic) focusStack.getItem()).getUpgradeLevel(focusStack, FocusUpgradeType.frugal);
            if (Config.enchFrugal != null) {
                level += EnchantmentHelper.getEnchantmentLevel(Config.enchFrugal, focusStack);
            }
        }
        return level;
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

    // ---- Item Overrides ----

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
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.WandCasting.rod") + " " +
                    TextFormatting.WHITE + I18n.translateToLocal(rod.getTag()));
        }
        if (cap != null) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.WandCasting.cap") + " " +
                    TextFormatting.WHITE + I18n.translateToLocal(cap.getTag()));
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
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemFocusBasic focus = getFocus(stack);

        if (focus != null) {
            if (player.isSneaking()) {
                // Open focal manipulator if sneaking
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }

            RayTraceResult mop = this.rayTrace(world, player, false);
            ItemStack result = focus.onFocusRightClick(stack, world, player, mop);
            if (result != null && result != stack) {
                return new ActionResult<>(EnumActionResult.SUCCESS, result);
            }

            // Start using the focus
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer) {
            ItemFocusBasic focus = getFocus(stack);
            if (focus != null) {
                focus.onUsingFocusTick(stack, (EntityPlayer) player, count);
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
        if (player instanceof EntityPlayer) {
            ItemFocusBasic focus = getFocus(stack);
            if (focus != null) {
                focus.onPlayerStoppedUsingFocus(stack, world, (EntityPlayer) player, this.getMaxItemUseDuration(stack) - timeLeft);
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        ItemFocusBasic focus = getFocus(stack);
        if (focus != null) {
            ItemFocusBasic.WandFocusAnimation anim = focus.getAnimation(stack);
            if (anim == ItemFocusBasic.WandFocusAnimation.CHARGE) {
                return EnumAction.BOW;
            }
        }
        return EnumAction.NONE;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (!player.getEntityWorld().isRemote) {
            ItemFocusBasic focus = getFocus(stack);
            if (focus != null) {
                return focus.onFocusBlockStartBreak(stack, pos.getX(), pos.getY(), pos.getZ(), player);
            }
        }
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return getFocus(stack) != null;
    }
}
