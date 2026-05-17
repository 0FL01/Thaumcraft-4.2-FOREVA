package thaumcraft.common.items;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileManaPod;

public class ItemManaBean extends ItemFood implements IEssentiaContainerItem {

    private static final Random DISPLAY_RANDOM = new Random();
    private static final Aspect[] DISPLAY_ASPECTS = Aspect.aspects.values().toArray(new Aspect[0]);
    private static final Potion[] REFERENCE_POTION_ROLLS = new Potion[]{
            null,
            MobEffects.SPEED,
            MobEffects.SLOWNESS,
            MobEffects.HASTE,
            MobEffects.MINING_FATIGUE,
            MobEffects.STRENGTH,
            MobEffects.INSTANT_HEALTH,
            MobEffects.INSTANT_DAMAGE,
            MobEffects.JUMP_BOOST,
            MobEffects.NAUSEA,
            MobEffects.REGENERATION,
            MobEffects.RESISTANCE,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY,
            MobEffects.BLINDNESS,
            MobEffects.NIGHT_VISION,
            MobEffects.HUNGER,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION,
            MobEffects.SATURATION,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    };

    public ItemManaBean() {
        super(1, 0.5F, true);
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 10;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            applyRandomPotion(world, player);
            AspectList aspects = getAspects(stack);
            Aspect aspect = aspects != null && aspects.size() > 0 ? aspects.getAspects()[0] : null;
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null && aspect != null && world.rand.nextFloat() < 0.25F && knowledge.addAspectPool(aspect, 1)) {
                ResearchManager.updateCache(player);
                if (player instanceof EntityPlayerMP) {
                    PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), (short) 1, knowledge.getAspectPoolFor(aspect)), (EntityPlayerMP) player);
                }
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        if (!world.isRemote && !stack.hasTagCompound()) {
            setAspects(stack, new AspectList().add(getRandomDisplayAspect(), 1));
        }
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (!stack.hasTagCompound()) {
            setAspects(stack, new AspectList().add(getRandomDisplayAspect(), 1));
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.canPlayerEdit(pos, facing, stack) || facing != EnumFacing.DOWN || ConfigBlocks.blockManaPod == null) {
            return EnumActionResult.PASS;
        }
        if (!BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.MAGICAL) || !isValidManaPodSupport(world, pos)) {
            return EnumActionResult.PASS;
        }

        BlockPos place = pos.down();
        if (!world.isAirBlock(place)) {
            return EnumActionResult.SUCCESS;
        }

        if (!world.isRemote) {
            world.setBlockState(place, ConfigBlocks.blockManaPod.getDefaultState(), 3);
            TileEntity tile = world.getTileEntity(place);
            AspectList aspects = getAspects(stack);
            if (tile instanceof TileManaPod && aspects != null && aspects.size() > 0) {
                ((TileManaPod) tile).aspect = aspects.getAspects()[0];
                tile.markDirty();
            }
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        if (!itemstack.hasTagCompound()) return null;
        AspectList aspects = new AspectList();
        aspects.readFromNBT(itemstack.getTagCompound());
        if (aspects.size() > 0) return aspects;

        Aspect legacyAspect = Aspect.getAspect(itemstack.getTagCompound().getString("aspect"));
        return legacyAspect == null ? null : new AspectList().add(legacyAspect, 1);
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (aspects == null || aspects.size() == 0) return;
        if (!itemstack.hasTagCompound()) itemstack.setTagCompound(new NBTTagCompound());
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

    private void applyRandomPotion(World world, EntityPlayer player) {
        Potion potion = getRandomPotion(world);
        if (potion == null) return;
        if (potion.isInstant()) {
            potion.affectEntity(player, player, (EntityLivingBase) player, 2, 3.0D);
        } else {
            player.addPotionEffect(new PotionEffect(potion, 160 + world.rand.nextInt(80), 0));
        }
    }

    private Potion getRandomPotion(World world) {
        return REFERENCE_POTION_ROLLS[world.rand.nextInt(REFERENCE_POTION_ROLLS.length)];
    }

    private boolean isValidManaPodSupport(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block == Blocks.LOG || block == Blocks.LOG2 || block == ConfigBlocks.blockMagicalLog;
    }

    private Aspect getRandomDisplayAspect() {
        if (DISPLAY_ASPECTS.length == 0) return Aspect.PLANT;
        return DISPLAY_ASPECTS[DISPLAY_RANDOM.nextInt(DISPLAY_ASPECTS.length)];
    }

    @SideOnly(Side.CLIENT)
    private boolean hasDiscoveredAspect(Aspect aspect) {
        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        return knowledge != null && knowledge.hasDiscoveredAspect(aspect);
    }
}
