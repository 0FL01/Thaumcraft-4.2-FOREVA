package thaumcraft.common.items.relics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncAspects;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.research.ResearchManager;

import javax.annotation.Nullable;
import java.util.List;

public class ItemThaumonomicon extends Item {

    public ItemThaumonomicon() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
            if (Config.allowCheatSheet) {
                items.add(new ItemStack(this, 1, 42));
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if (stack.getItemDamage() == 42) {
            return EnumRarity.EPIC;
        }
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            world.playSound(player, player.posX, player.posY, player.posZ, TCSounds.PAGE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        } else {
            applyResearchUnlocks(player, stack);
        }
        player.openGui(Thaumcraft.instance, CommonProxy.GUI_THAUMONOMICON, world, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flagIn) {
        if (stack.getItemDamage() == 42) {
            tooltip.add("Cheat Sheet");
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    private static void applyResearchUnlocks(EntityPlayer player, ItemStack stack) {
        if (player == null) {
            return;
        }

        if (Config.allowCheatSheet && stack.getItemDamage() == 42) {
            unlockAllResearch(player);
            unlockAllAspects(player);
        } else {
            unlockCompletedResearchSiblings(player);
        }
        syncResearchAndAspects(player);
    }

    private static void unlockAllResearch(EntityPlayer player) {
        for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {
            for (ResearchItem research : category.research.values()) {
                if (research == null || ResearchManager.isResearchComplete(player, research.key)) {
                    continue;
                }
                ResearchManager.addResearch(player, research.key);
            }
        }
    }

    private static void unlockCompletedResearchSiblings(EntityPlayer player) {
        for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {
            for (ResearchItem research : category.research.values()) {
                if (research == null || !ResearchManager.isResearchComplete(player, research.key) || research.siblings == null) {
                    continue;
                }
                for (String sibling : research.siblings) {
                    if (ResearchCategories.getResearch(sibling) != null && !ResearchManager.isResearchComplete(player, sibling)) {
                        ResearchManager.addResearch(player, sibling);
                    }
                }
            }
        }
    }

    private static void unlockAllAspects(EntityPlayer player) {
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        ResearchManager manager = new ResearchManager();
        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspect == null || (knowledge != null && knowledge.hasDiscoveredAspect(aspect))) {
                continue;
            }
            manager.completeAspect(player, aspect, (short) 50);
        }
    }

    private static void syncResearchAndAspects(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) {
            return;
        }
        PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(knowledge.getResearchComplete()), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(knowledge.getAspectsDiscovered()), (EntityPlayerMP) player);
        ResearchManager.updateCache(player.getName(), knowledge);
    }
}
