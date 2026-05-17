package thaumcraft.common.items.baubles;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileVisRelay;

import java.text.DecimalFormat;
import java.lang.ref.WeakReference;
import java.util.List;

public class ItemAmuletVis extends Item implements IBauble, IEssentiaContainerItem, IRunicArmor {
    private static final DecimalFormat VIS_FORMAT = new DecimalFormat("#######.##");

    public ItemAmuletVis() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == 1 ? EnumRarity.RARE : EnumRarity.UNCOMMON;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
            items.add(new ItemStack(this, 1, 1));
        }
    }

    public int getMaxVis(ItemStack stack) {
        return stack.getItemDamage() == 1 ? 25000 : 2500;
    }

    public int getVis(ItemStack stack, Aspect aspect) {
        if (stack == null || stack.isEmpty() || aspect == null || !stack.hasTagCompound()) return 0;
        return stack.getTagCompound().getInteger(aspect.getTag());
    }

    public void storeVis(ItemStack stack, Aspect aspect, int amount) {
        if (stack == null || stack.isEmpty() || aspect == null) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(aspect.getTag(), Math.max(0, Math.min(amount, this.getMaxVis(stack))));
    }

    public int addVis(ItemStack stack, Aspect aspect, int amount, boolean doit) {
        if (aspect == null || !aspect.isPrimal()) return 0;
        int storeAmount = this.getVis(stack, aspect) + amount * 100;
        int leftover = Math.max(storeAmount - this.getMaxVis(stack), 0);
        if (doit) {
            this.storeVis(stack, aspect, Math.min(storeAmount, this.getMaxVis(stack)));
        }
        return leftover / 100;
    }

    public int addRealVis(ItemStack stack, Aspect aspect, int amount, boolean doit) {
        if (aspect == null || !aspect.isPrimal()) return 0;
        int storeAmount = this.getVis(stack, aspect) + amount;
        int leftover = Math.max(storeAmount - this.getMaxVis(stack), 0);
        if (doit) {
            this.storeVis(stack, aspect, Math.min(storeAmount, this.getMaxVis(stack)));
        }
        return leftover;
    }

    public AspectList getAllVis(ItemStack stack) {
        AspectList out = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            out.merge(aspect, this.getVis(stack, aspect));
        }
        return out;
    }

    public AspectList getAspectsWithRoom(ItemStack stack) {
        AspectList out = new AspectList();
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (this.getVis(stack, aspect) < this.getMaxVis(stack)) {
                out.add(aspect, 1);
            }
        }
        return out;
    }

    public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList aspects, boolean doit, boolean crafting) {
        if (aspects == null || aspects.size() == 0) return false;
        for (Aspect aspect : aspects.getAspects()) {
            if (this.getVis(stack, aspect) < aspects.getAmount(aspect)) {
                return false;
            }
        }
        if (doit) {
            for (Aspect aspect : aspects.getAspects()) {
                this.storeVis(stack, aspect, this.getVis(stack, aspect) - aspects.getAmount(aspect));
            }
        }
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.getItemDamage() == 0) {
            tooltip.add(TextFormatting.AQUA + I18n.translateToLocal("item.ItemAmuletVis.text"));
        }
        tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.capacity.text") + " " + (this.getMaxVis(stack) / 100));
        if (!stack.hasTagCompound()) return;
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (!stack.getTagCompound().hasKey(aspect.getTag())) continue;
            String amount = VIS_FORMAT.format((float) stack.getTagCompound().getInteger(aspect.getTag()) / 100.0F);
            tooltip.add(" \u00a7" + aspect.getChatcolor() + aspect.getName() + "\u00a7r x " + amount);
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player.world.isRemote || player.ticksExisted % 5 != 0) return;
        ItemStack held = player.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                int stored = this.getVis(itemstack, aspect);
                int room = ItemWandCasting.getMaxVis(held) - ItemWandCasting.getVis(held, aspect);
                int amount = Math.min(5, Math.min(stored, room));
                if (amount <= 0) continue;
                this.storeVis(itemstack, aspect, stored - amount);
                ItemWandCasting.addRealVis(held, aspect, amount);
            }
        }
        if (player instanceof EntityPlayer) {
            WeakReference<TileVisRelay> relayRef = TileVisRelay.nearbyPlayers.get(player.getEntityId());
            TileVisRelay relay = relayRef == null ? null : relayRef.get();
            if (relay == null || relay.isInvalid() || relay.getWorld() != player.world || player.getDistanceSq(relay.getPos()) >= 26.0D) {
                TileVisRelay.nearbyPlayers.remove(player.getEntityId());
                return;
            }
            for (Aspect aspect : Aspect.getPrimalAspects()) {
                int room = this.getMaxVis(itemstack) - this.getVis(itemstack, aspect);
                int amount = Math.min(5, room);
                if (amount <= 0) continue;
                int drained = relay.consumeVis(aspect, amount);
                if (drained > 0) {
                    this.addRealVis(itemstack, aspect, drained, true);
                    relay.triggerConsumeEffect(aspect);
                }
            }
        }
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return itemstack.getItemDamage() != 1
                || !(player instanceof EntityPlayer)
                || ResearchManager.isResearchComplete((EntityPlayer) player, "VISAMULET");
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) { return true; }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) { return true; }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        return this.getAllVis(itemstack);
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (aspects == null) return;
        for (Aspect aspect : aspects.getAspects()) {
            this.storeVis(itemstack, aspect, aspects.getAmount(aspect));
        }
    }
}
