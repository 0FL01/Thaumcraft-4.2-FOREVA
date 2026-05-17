package thaumcraft.common.items;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.EntityAspectOrb;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;

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
                if (i == META_BRAIN) {
                    continue;
                }
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!world.isRemote && (stack.getItemDamage() == META_TAINT_SLIME || stack.getItemDamage() == META_TAINT_TENDRIL)
                && entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            if (!living.isEntityUndead()
                    && Config.potionFluxTaint != null
                    && !living.isPotionActive(Config.potionFluxTaint)
                    && world.rand.nextInt(4321) <= stack.getCount()) {
                living.addPotionEffect(new PotionEffect(Config.potionFluxTaint, 120, 0, false, true));
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    String text = I18n.translateToLocal("tc.taint_item_poison")
                            .replace("%s", TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + stack.getDisplayName() + TextFormatting.RESET);
                    player.sendMessage(new TextComponentString(text));
                    InventoryUtils.consumeInventoryItem(player, stack.getItem(), stack.getItemDamage());
                }
            }
        } else if (!world.isRemote && stack.getItemDamage() == META_CHARM) {
            int r = world.rand.nextInt(20000);
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("blurb")) {
                stack.getTagCompound().removeTag("blurb");
            }
            if (r < 20) {
                Aspect aspect = null;
                switch (world.rand.nextInt(6)) {
                    case 0:
                        aspect = Aspect.AIR;
                        break;
                    case 1:
                        aspect = Aspect.EARTH;
                        break;
                    case 2:
                        aspect = Aspect.FIRE;
                        break;
                    case 3:
                        aspect = Aspect.WATER;
                        break;
                    case 4:
                        aspect = Aspect.ORDER;
                        break;
                    case 5:
                        aspect = Aspect.ENTROPY;
                        break;
                    default:
                        break;
                }
                if (aspect != null) {
                    EntityAspectOrb orb = new EntityAspectOrb(world, entity.posX, entity.posY, entity.posZ, aspect, 1);
                    world.spawnEntity(orb);
                }
            } else if (r == 42 && entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (!ResearchManager.isResearchComplete(player, "FOCUSPRIMAL")
                        && !ResearchManager.isResearchComplete(player, "@FOCUSPRIMAL")) {
                    player.sendMessage(new TextComponentString(
                            TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + I18n.translateToLocal("tc.primalcharm.trigger")));
                    ResearchManager.addResearch(player, "@FOCUSPRIMAL");
                }
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
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() != META_NITOR) {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.isReplaceable(world, pos)
                && block != Blocks.VINE
                && block != Blocks.TALLGRASS
                && block != Blocks.DEADBUSH) {
            pos = pos.offset(facing);
        }

        if (stack.isEmpty()) {
            return EnumActionResult.PASS;
        }
        if (!player.canPlayerEdit(pos, facing, stack)) {
            return EnumActionResult.PASS;
        }
        if (!world.mayPlace(ConfigBlocks.blockAiry, pos, false, facing, player)) {
            return EnumActionResult.PASS;
        }
        if (!placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, ConfigBlocks.blockAiry, META_NITOR)) {
            return EnumActionResult.PASS;
        }

        SoundType sound = ConfigBlocks.blockAiry.getSoundType(world.getBlockState(pos), world, pos, player);
        world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
                (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    private boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                 float hitX, float hitY, float hitZ, Block block, int meta) {
        if (!world.setBlockState(pos, block.getStateFromMeta(meta), 3)) {
            return false;
        }
        if (world.getBlockState(pos).getBlock() == block) {
            block.onBlockPlacedBy(world, pos, world.getBlockState(pos), player, stack);
            block.onBlockAdded(world, pos, world.getBlockState(pos));
        }
        return true;
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTagCompound());
            return aspects.size() > 0 ? aspects : null;
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

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return stack.getItemDamage() == META_CHARM ? 1 : super.getItemStackLimit(stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        AspectList aspects = this.getAspects(stack);
        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        if (aspects != null && aspects.size() > 0) {
            for (Aspect aspect : aspects.getAspectsSorted()) {
                if (aspect == null) continue;
                if (knowledge != null && knowledge.hasDiscoveredAspect(aspect)) {
                    tooltip.add(aspect.getName());
                } else {
                    tooltip.add(I18n.translateToLocal("tc.aspect.unknown"));
                }
            }
        }

        if (stack.getItemDamage() == META_CHARM && player != null) {
            Random rand = new Random(stack.hashCode() + player.ticksExisted / 120);
            int r = rand.nextInt(200);
            if (r < 25) {
                tooltip.add(TextFormatting.GOLD + I18n.translateToLocal("tc.primalcharm." + rand.nextInt(5)));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
