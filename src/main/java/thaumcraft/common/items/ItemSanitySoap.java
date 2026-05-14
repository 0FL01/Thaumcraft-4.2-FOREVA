package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemSanitySoap extends Item {

    public ItemSanitySoap() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 200;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;
        int used = this.getMaxItemUseDuration(stack) - timeLeft;
        if (used <= 195) return;
        if (!world.isRemote) {
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null) {
                if (knowledge.getWarpSticky() > 0 && world.rand.nextFloat() < 0.33F) {
                    Thaumcraft.addStickyWarpToPlayer(player, -1);
                }
                if (knowledge.getWarpTemp() > 0) {
                    Thaumcraft.addWarpToPlayer(player, -knowledge.getWarpTemp(), true);
                }
            }
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
    }
}
