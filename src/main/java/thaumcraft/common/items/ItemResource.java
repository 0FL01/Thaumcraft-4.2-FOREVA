package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;

public class ItemResource extends Item implements IEssentiaContainerItem {

    public static final int META_ALUMENTUM = 0;
    public static final int META_NITOR = 1;
    public static final int META_THAUMIUM_INGOT = 2;
    public static final int META_QUICKSILVER = 3;
    public static final int META_TALLOW = 4;
    public static final int META_BRAIN = 5;
    public static final int META_AMBER = 6;
    public static final int META_CLOTH = 7;
    public static final int META_FILTER = 8;
    public static final int META_KNOWLEDGE_FRAGMENT = 9;
    public static final int META_MIRROR_GLASS = 10;
    public static final int META_TAINT_SLIME = 11;
    public static final int META_TAINT_TENDRIL = 12;
    public static final int META_LABEL = 13;
    public static final int META_DUST = 14;
    public static final int META_CHARM = 15;
    public static final int META_VOID_INGOT = 16;
    public static final int META_VOID_SEED = 17;
    public static final int META_COIN = 18;

    public static final String[] NAMES = {
            "alumentum", "nitor", "thaumiumingot", "quicksilver", "tallow",
            "brain", "amber", "cloth", "filter", "knowledgefragment",
            "mirrorglass", "taint_slime", "taint_tendril", "label",
            "dust", "charm", "voidingot", "voidseed", "coin"
    };

    public ItemResource() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int d = Math.min(stack.getItemDamage(), NAMES.length - 1);
        return super.getTranslationKey() + "." + NAMES[d];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < NAMES.length; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() == META_ALUMENTUM) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                    0.3F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            if (!world.isRemote) {
                EntityAlumentum projectile = new EntityAlumentum(world, player);
                projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.75F, 1.0F);
                world.spawnEntity(projectile);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else if (stack.getItemDamage() == META_KNOWLEDGE_FRAGMENT) {
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
            if (!world.isRemote) {
                IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
                if (knowledge != null) {
                    for (Aspect aspect : Aspect.getPrimalAspects()) {
                        short amount = (short) (world.rand.nextInt(2) + 1);
                        knowledge.addAspectPool(aspect, amount);
                        if (player instanceof EntityPlayerMP) {
                            PacketHandler.INSTANCE.sendTo(
                                    new PacketAspectPool(aspect.getTag(), amount, knowledge.getAspectPoolFor(aspect)),
                                    (EntityPlayerMP) player);
                        }
                    }
                    ResearchManager.updateCache(player);
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTagCompound());
            return aspects;
        }
        return null;
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        aspects.writeToNBT(itemstack.getTagCompound());
    }
}
