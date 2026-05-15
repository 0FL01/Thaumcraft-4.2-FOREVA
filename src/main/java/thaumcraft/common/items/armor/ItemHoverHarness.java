package thaumcraft.common.items.armor;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;

public class ItemHoverHarness extends ItemArmor implements IRepairable, IRunicArmor, IVisDiscountGear {

    public ItemHoverHarness(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setMaxDamage(400);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == Items.IRON_INGOT || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) {
            Hover.handleHoverArmor(player, stack);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            player.openGui(Thaumcraft.instance, CommonProxy.GUI_HOVER_HARNESS, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return aspect == Aspect.AIR ? 5 : 2;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addJarInformation(stack, tooltip);
        String visDiscount = new TextComponentTranslation("tc.visdiscount").getFormattedText();
        tooltip.add(TextFormatting.DARK_PURPLE + visDiscount + ": " + getVisDiscount(stack, null, null) + "%");
        tooltip.add(TextFormatting.DARK_PURPLE + visDiscount + " (Aer): " + getVisDiscount(stack, null, Aspect.AIR) + "%");
    }

    @SideOnly(Side.CLIENT)
    private void addJarInformation(ItemStack stack, List<String> tooltip) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("jar")) return;
        try {
            ItemStack jar = new ItemStack(stack.getTagCompound().getCompoundTag("jar"));
            if (jar.isEmpty() || !(jar.getItem() instanceof BlockJarItem)) return;
            AspectList aspects = ((BlockJarItem) jar.getItem()).getAspects(jar);
            if (aspects == null || aspects.size() <= 0) return;
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (aspect == null) continue;
                if (hasDiscoveredAspect(aspect)) {
                    tooltip.add(aspect.getName() + " x " + aspects.getAmount(aspect));
                } else {
                    tooltip.add(new TextComponentTranslation("tc.aspect.unknown").getFormattedText());
                }
            }
        } catch (RuntimeException ignored) {}
    }

    @SideOnly(Side.CLIENT)
    private boolean hasDiscoveredAspect(Aspect aspect) {
        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        return knowledge != null && knowledge.hasDiscoveredAspect(aspect);
    }
}
