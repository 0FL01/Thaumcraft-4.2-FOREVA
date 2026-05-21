package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemSanityChecker extends Item {

    public ItemSanityChecker() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            int perm = knowledge == null ? 0 : knowledge.getWarpPerm();
            int sticky = knowledge == null ? 0 : knowledge.getWarpSticky();
            int temp = knowledge == null ? 0 : knowledge.getWarpTemp();
            int total = perm + sticky + temp;
            player.sendStatusMessage(new TextComponentTranslation("tc.sanity", total), true);
            player.sendStatusMessage(new TextComponentTranslation("tc.sanity.detail", perm, sticky, temp), true);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
