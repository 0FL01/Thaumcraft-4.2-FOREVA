package thaumcraft.common.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;

import java.util.List;

public class ItemResearchNotes extends Item {

    private static final int META_UNKNOWN_DISCOVERY = 24;
    private static final int META_UNKNOWN_DISCOVERY_ALT = 42;
    private static final int META_DISCOVERY_START = 64;

    public ItemResearchNotes() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getItemDamage() >= META_DISCOVERY_START) {
            return I18n.translateToLocal("item.discovery.name");
        }
        String translated = I18n.translateToLocal("item.researchnotes.name");
        return "item.researchnotes.name".equals(translated) ? super.getItemStackDisplayName(stack) : translated;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() >= META_DISCOVERY_START ? EnumRarity.EPIC : EnumRarity.RARE;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            int color = 0x999999;
            ResearchNoteData data = ResearchManager.getData(stack);
            if (data != null) {
                color = data.color;
            }
            return color;
        }
        return -1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.getItemDamage() == META_UNKNOWN_DISCOVERY || stack.getItemDamage() == META_UNKNOWN_DISCOVERY_ALT) {
            tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("item.researchnotes.unknown.1"));
            tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("item.researchnotes.unknown.2"));
        }
        ResearchNoteData data = ResearchManager.getData(stack);
        if (data != null && data.key != null && ResearchCategories.getResearch(data.key) != null) {
            ResearchItem research = ResearchCategories.getResearch(data.key);
            tooltip.add(TextFormatting.GOLD + research.getName());
            tooltip.add(TextFormatting.ITALIC + research.getText());
            int warp = ThaumcraftApi.getWarp(data.key);
            if (warp > 0) {
                warp = Math.min(warp, 5);
                String ws = I18n.translateToLocal("tc.forbidden");
                String wr = I18n.translateToLocal("tc.forbidden.level." + warp);
                tooltip.add(TextFormatting.DARK_PURPLE + ws.replace("%n", wr));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            ResearchNoteData data = ResearchManager.getData(stack);
            if (data != null && data.isComplete() && data.key != null && !data.key.isEmpty()) {
                return completeResearchNote(world, player, stack, data);
            }
            if (stack.getItemDamage() == META_UNKNOWN_DISCOVERY || stack.getItemDamage() == META_UNKNOWN_DISCOVERY_ALT) {
                return revealDiscovery(world, player, stack);
            }
            player.sendStatusMessage(new TextComponentTranslation("tc.researchnotes.invalid"), true);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private ActionResult<ItemStack> completeResearchNote(World world, EntityPlayer player, ItemStack stack, ResearchNoteData data) {
        if (ResearchManager.isResearchComplete(player, data.key)) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!ResearchManager.doesPlayerHaveRequisites(player, data.key)) {
            player.sendMessage(new TextComponentTranslation("tc.researcherror"));
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        ResearchManager.addResearch(player, data.key);
        ResearchItem research = ResearchCategories.getResearch(data.key);
        if (research != null && research.siblings != null) {
            for (String sibling : research.siblings) {
                if (!ResearchManager.isResearchComplete(player, sibling)
                        && ResearchManager.doesPlayerHaveRequisites(player, sibling)) {
                    ResearchManager.addResearch(player, sibling);
                }
            }
        }
        stack.shrink(1);
        play(world, player, TCSounds.LEARN);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private ActionResult<ItemStack> revealDiscovery(World world, EntityPlayer player, ItemStack stack) {
        String key = ResearchManager.findHiddenResearch(player);
        if ("FAIL".equals(key)) {
            stack.shrink(1);
            ItemStack fragments = new ItemStack(ConfigItems.itemResource, 7 + world.rand.nextInt(3), ItemResource.META_KNOWLEDGE_FRAGMENT);
            EntityItem entity = new EntityItem(world, player.posX, player.posY + player.getEyeHeight() / 2.0D, player.posZ, fragments);
            world.spawnEntity(entity);
            play(world, player, TCSounds.ERASE);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        stack.setItemDamage(0);
        ItemStack note = ResearchManager.createNote(stack, key, world);
        if (!note.isEmpty() && note.hasTagCompound()) {
            stack.setTagCompound(note.getTagCompound());
            play(world, player, TCSounds.WRITE);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        player.sendStatusMessage(new TextComponentTranslation("tc.researchnotes.invalid"), true);
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private static void play(World world, EntityPlayer player, net.minecraft.util.SoundEvent sound) {
        world.playSound(null, player.posX, player.posY, player.posZ, sound, SoundCategory.PLAYERS, 0.75F, 1.0F);
    }
}
