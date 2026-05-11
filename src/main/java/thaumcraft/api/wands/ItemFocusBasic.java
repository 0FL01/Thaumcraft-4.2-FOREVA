package thaumcraft.api.wands;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;

public class ItemFocusBasic
extends Item {
    public TextureAtlasSprite icon;

    public ItemFocusBasic() {
        this.maxStackSize = 1;
        this.canRepair = false;
        this.setMaxDamage(0);
    }

    @SideOnly(value=Side.CLIENT)
    public TextureAtlasSprite getSpriteNumber() {
        return this.icon;
    }

    public boolean isRepairable() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        AspectList al = this.getVisCost(stack);
        if (al != null && al.size() > 0) {
            list.add(I18n.translateToLocal((String)(this.isVisCostPerTick(stack) ? "item.Focus.cost2" : "item.Focus.cost1")));
            for (Aspect aspect : al.getAspectsSorted()) {
                DecimalFormat myFormatter = new DecimalFormat("#####.##");
                String amount = myFormatter.format((float)al.getAmount(aspect) / 100.0f);
                list.add(" \u00a7" + aspect.getChatcolor() + aspect.getName() + "\u00a7r x " + amount);
            }
        }
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        this.addFocusInformation(stack, mc.player, list, flagIn.isAdvanced());
    }

    public void addFocusInformation(ItemStack focusstack, EntityPlayer player, List<String> list, boolean advanced) {
        LinkedHashMap<Short, Integer> map = new LinkedHashMap<Short, Integer>();
        for (short id : this.getAppliedUpgrades(focusstack)) {
            if (id < 0) continue;
            int amt = 1;
            if (map.containsKey(id)) {
                amt = (Integer)map.get(id) + 1;
            }
            map.put(id, amt);
        }
        for (Short id : map.keySet()) {
            list.add(TextFormatting.DARK_PURPLE + FocusUpgradeType.types[id].getLocalizedName() + ((Integer)map.get(id) > 1 ? " " + I18n.translateToLocal((String)("enchantment.level." + map.get(id))) : ""));
        }
    }

    public boolean isVisCostPerTick(ItemStack focusstack) {
        return false;
    }

    public EnumRarity getRarity(ItemStack focusstack) {
        return EnumRarity.RARE;
    }

    public int getFocusColor(ItemStack focusstack) {
        return 0;
    }

    public TextureAtlasSprite getOrnament(ItemStack focusstack) {
        return null;
    }

    public TextureAtlasSprite getFocusDepthLayerIcon(ItemStack focusstack) {
        return null;
    }

    public WandFocusAnimation getAnimation(ItemStack focusstack) {
        return WandFocusAnimation.WAVE;
    }

    public String getSortingHelper(ItemStack focusstack) {
        String out = "";
        for (short id : this.getAppliedUpgrades(focusstack)) {
            out = out + id;
        }
        return out;
    }

    public AspectList getVisCost(ItemStack focusstack) {
        return null;
    }

    public int getActivationCooldown(ItemStack focusstack) {
        return 0;
    }

    public int getMaxAreaSize(ItemStack focusstack) {
        return 1;
    }

    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusstack, int rank) {
        return null;
    }

    public short[] getAppliedUpgrades(ItemStack focusstack) {
        short[] l = new short[]{-1, -1, -1, -1, -1};
        NBTTagList nbttaglist = this.getFocusUpgradeTagList(focusstack);
        if (nbttaglist == null) {
            return l;
        }
        for (int j = 0; j < nbttaglist.tagCount() && j < 5; ++j) {
            l[j] = nbttaglist.getCompoundTagAt(j).getShort("id");
        }
        return l;
    }

    public boolean applyUpgrade(ItemStack focusstack, FocusUpgradeType type, int rank) {
        short[] upgrades = this.getAppliedUpgrades(focusstack);
        if (upgrades[rank - 1] != -1 || rank < 1 || rank > 5) {
            return false;
        }
        upgrades[rank - 1] = type.id;
        this.setFocusUpgradeTagList(focusstack, upgrades);
        return true;
    }

    public boolean canApplyUpgrade(ItemStack focusstack, EntityPlayer player, FocusUpgradeType type, int rank) {
        return true;
    }

    public boolean isUpgradedWith(ItemStack focusstack, FocusUpgradeType focusUpgradetype) {
        return this.getUpgradeLevel(focusstack, focusUpgradetype) > 0;
    }

    public int getUpgradeLevel(ItemStack focusstack, FocusUpgradeType focusUpgradetype) {
        short[] list = this.getAppliedUpgrades(focusstack);
        int level = 0;
        for (short id : list) {
            if (id != focusUpgradetype.id) continue;
            ++level;
        }
        return level;
    }

    public ItemStack onFocusRightClick(ItemStack wandstack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        return null;
    }

    public void onUsingFocusTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    public void onPlayerStoppedUsingFocus(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    public boolean onFocusBlockStartBreak(ItemStack wandstack, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    private NBTTagList getFocusUpgradeTagList(ItemStack focusstack) {
        return focusstack.getTagCompound() == null ? null : focusstack.getTagCompound().getTagList("upgrade", 10);
    }

    private void setFocusUpgradeTagList(ItemStack focusstack, short[] upgrades) {
        if (!focusstack.hasTagCompound()) {
            focusstack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbttagcompound = focusstack.getTagCompound();
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("upgrade", (NBTBase)tlist);
        for (short id : upgrades) {
            NBTTagCompound f = new NBTTagCompound();
            f.setShort("id", id);
            tlist.appendTag((NBTBase)f);
        }
    }

    public boolean acceptsEnchant(int id) {
        return true;
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("ench")) {
            stack.getTagCompound().removeTag("ench");
        }
        super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
    }

    public static enum WandFocusAnimation {
        WAVE,
        CHARGE;

    }
}
