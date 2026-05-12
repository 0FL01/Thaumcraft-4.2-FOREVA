package thaumcraft.common.items.wands;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
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

import java.util.List;

public class ItemWandCasting extends Item {

    public static final String TAG_ROD = "rod";
    public static final String TAG_CAP = "cap";
    public static final String TAG_FOCUS = "focus";
    public static final String TAG_VIS_PREFIX = "vis_";

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
        setVis(stack, aspect, getVis(stack, aspect) + amount);
    }

    public static int getMaxVis(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;
        WandRod rod = getRod(stack);
        return rod != null ? rod.getCapacity() : 100;
    }

    public static float getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        WandCap cap = getCap(stack);
        float discount = cap != null ? cap.getBaseCostModifier() : 1.0f;
        // Check for special aspect discounts
        if (cap != null && cap.getSpecialCostModifierAspects() != null && cap.getSpecialCostModifierAspects().contains(aspect)) {
            discount = Math.min(discount, cap.getSpecialCostModifier());
        }
        return discount;
    }

    /**
     * Checks if the wand can supply the given vis cost. Uses crafting mode for exact matching.
     */
    public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit, boolean crafting) {
        if (cost == null) return true;
        if (player.capabilities.isCreativeMode) return true;

        // Check if we have enough
        for (Aspect aspect : cost.getAspects()) {
            int needed = cost.getAmount(aspect);
            int discount = 0;
            if (!crafting) {
                // Calculate enchantment-based discount (Frugal)
                // Simplified: apply cap discount
                float discountMult = getVisDiscount(stack, player, aspect);
                needed = Math.round(needed * discountMult);
            }
            if (needed > 0 && getVis(stack, aspect) < needed) {
                return false;
            }
        }

        if (doit) {
            for (Aspect aspect : cost.getAspects()) {
                int needed = cost.getAmount(aspect);
                float discountMult = getVisDiscount(stack, player, aspect);
                if (!crafting) {
                    needed = Math.round(needed * discountMult);
                }
                addVis(stack, aspect, -needed);
            }
        }
        return true;
    }

    public boolean consumeAllVisCrafting(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit) {
        return consumeAllVis(stack, player, cost, doit, true);
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

    // ---- Item Overrides ----

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            WandRod rod = getRod(stack);
            if (rod != null && rod.getOnUpdate() != null) {
                rod.getOnUpdate().onUpdate(stack, player);
            }

            // Recharge vis from the environment (simplified)
            if (world.getTotalWorldTime() % 20 == 0) {
                for (Aspect aspect : Aspect.getPrimalAspects()) {
                    int current = getVis(stack, aspect);
                    int max = getMaxVis(stack);
                    if (current < max) {
                        int regen = Math.max(1, max / 100);
                        if (rod != null && rod.getTag().equals("greatwood")) {
                            regen *= 2;
                        }
                        addVis(stack, aspect, regen);
                    }
                }
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
        tooltip.add(TextFormatting.AQUA + "" + TextFormatting.ITALIC + I18n.translateToLocal("item.WandCasting.vis"));
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (aspect == null) continue;
            int vis = getVis(stack, aspect);
            int max = getMaxVis(stack);
            if (vis > 0 || max > 0) {
                tooltip.add(" " + TextFormatting.GRAY + aspect.getName() + ": " +
                        TextFormatting.WHITE + vis + "/" + max);
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
                        setVis(stack, aspect, rod.getCapacity());
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
