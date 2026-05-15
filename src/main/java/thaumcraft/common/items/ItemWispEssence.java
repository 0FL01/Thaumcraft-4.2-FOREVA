package thaumcraft.common.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;

public class ItemWispEssence extends Item implements IEssentiaContainerItem {

    private static final Aspect[] DISPLAY_ASPECTS = Aspect.aspects.values().toArray(new Aspect[0]);

    public ItemWispEssence() {
        this.setMaxStackSize(64);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (Aspect aspect : Aspect.aspects.values()) {
                if (aspect == null) continue;
                ItemStack stack = new ItemStack(this, 1, 0);
                setAspects(stack, new AspectList().add(aspect, 2));
                items.add(stack);
            }
        }
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        if (!itemstack.hasTagCompound()) return null;
        AspectList aspects = new AspectList();
        aspects.readFromNBT(itemstack.getTagCompound());
        return aspects.size() > 0 ? aspects : null;
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (aspects == null || aspects.size() == 0) return;
        if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new NBTTagCompound());
        }
        aspects.writeToNBT(itemstack.getTagCompound());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        AspectList aspects = getAspects(stack);
        if (aspects != null && aspects.size() > 0) {
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (aspect == null) continue;
                if (hasDiscoveredAspect(aspect)) {
                    tooltip.add(aspect.getName() + " x" + aspects.getAmount(aspect));
                } else {
                    tooltip.add(new TextComponentTranslation("tc.aspect.unknown").getFormattedText());
                }
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public int getColorFromItemStack(ItemStack stack) {
        AspectList aspects = getAspects(stack);
        if (aspects != null && aspects.size() > 0 && aspects.getAspects()[0] != null) {
            return aspects.getAspects()[0].getColor();
        }
        if (DISPLAY_ASPECTS.length == 0) return 0xFFFFFF;
        int idx = (int) (System.currentTimeMillis() / 500L % DISPLAY_ASPECTS.length);
        return DISPLAY_ASPECTS[idx].getColor();
    }

    @SideOnly(Side.CLIENT)
    private boolean hasDiscoveredAspect(Aspect aspect) {
        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        return knowledge != null && knowledge.hasDiscoveredAspect(aspect);
    }
}
