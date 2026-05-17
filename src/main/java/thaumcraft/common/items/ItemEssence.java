package thaumcraft.common.items;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileJarFillable;

public class ItemEssence extends Item implements IEssentiaContainerItem {

    private static final int PHIAL_AMOUNT = 8;

    public ItemEssence() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
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
            for (Aspect aspect : Aspect.aspects.values()) {
                if (aspect == null) continue;
                ItemStack stack = new ItemStack(this, 1, 1);
                setAspects(stack, new AspectList().add(aspect, PHIAL_AMOUNT));
                items.add(stack);
            }
        }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Block block = world.getBlockState(pos).getBlock();
        int meta = block.getMetaFromState(world.getBlockState(pos));
        TileEntity tile = world.getTileEntity(pos);

        if (stack.getItemDamage() == 0) {
            if (block == ConfigBlocks.blockMetalDevice && meta == 1 && tile instanceof TileAlembic) {
                return fillPhialFromContainer(stack, player, world, pos, hand, (TileAlembic) tile);
            }
            if (block == ConfigBlocks.blockJar && (meta == 0 || meta == 3) && tile instanceof TileJarFillable) {
                return fillPhialFromContainer(stack, player, world, pos, hand, (TileJarFillable) tile);
            }
        }

        AspectList aspects = getAspects(stack);
        if (stack.getItemDamage() != 0 && aspects != null && aspects.size() == 1
                && block == ConfigBlocks.blockJar && (meta == 0 || meta == 3) && tile instanceof TileJarFillable) {
            return emptyPhialIntoJar(stack, player, world, pos, hand, (TileJarFillable) tile, aspects.getAspects()[0]);
        }

        return EnumActionResult.PASS;
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
                    tooltip.add(aspect.getName() + " x " + aspects.getAmount(aspect));
                } else {
                    tooltip.add(new TextComponentTranslation("tc.aspect.unknown").getFormattedText());
                }
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public int getColorFromItemStack(ItemStack stack, int tintIndex) {
        if (stack.getItemDamage() == 0 || tintIndex == 0) return 0xFFFFFF;
        AspectList aspects = getAspects(stack);
        return aspects != null && aspects.size() > 0 && aspects.getAspects()[0] != null
                ? aspects.getAspects()[0].getColor()
                : 0xFFFFFF;
    }

    private EnumActionResult fillPhialFromContainer(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                                    EnumHand hand, IAspectContainer container) {
        AspectList contents = container.getAspects();
        if (contents == null || contents.size() == 0) return EnumActionResult.PASS;
        Aspect aspect = contents.getAspects()[0];
        if (aspect == null || contents.getAmount(aspect) < PHIAL_AMOUNT) return EnumActionResult.PASS;

        if (world.isRemote) {
            player.swingArm(hand);
            return EnumActionResult.PASS;
        }

        ItemStack phial = new ItemStack(this, 1, 1);
        setAspects(phial, new AspectList().add(aspect, PHIAL_AMOUNT));
        if (container.takeFromContainer(aspect, PHIAL_AMOUNT)) {
            stack.shrink(1);
            addOrDrop(player, world, pos, phial);
            playFillSound(world, pos);
            player.openContainer.detectAndSendChanges();
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private EnumActionResult emptyPhialIntoJar(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                               EnumHand hand, TileJarFillable jar, Aspect aspect) {
        if (aspect == null || jar.amount > jar.maxAmount - PHIAL_AMOUNT || !jar.doesContainerAccept(aspect)) {
            return EnumActionResult.PASS;
        }

        if (world.isRemote) {
            player.swingArm(hand);
            return EnumActionResult.PASS;
        }

        if (jar.addToContainer(aspect, PHIAL_AMOUNT) == 0) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            jar.markDirty();
            stack.shrink(1);
            addOrDrop(player, world, pos, new ItemStack(this, 1, 0));
            playFillSound(world, pos);
            player.openContainer.detectAndSendChanges();
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private void addOrDrop(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack)) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
        }
    }

    private void playFillSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_SWIM, SoundCategory.PLAYERS, 0.25F, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    private boolean hasDiscoveredAspect(Aspect aspect) {
        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        return knowledge != null && knowledge.hasDiscoveredAspect(aspect);
    }
}
