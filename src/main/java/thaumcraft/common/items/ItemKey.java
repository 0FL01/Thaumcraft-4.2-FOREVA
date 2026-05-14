package thaumcraft.common.items;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockWoodenDevice;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileOwned;

public class ItemKey extends Item {

    public static final int META_IRON = 0;
    public static final int META_GOLD = 1;

    public ItemKey() {
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
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, META_IRON));
            items.add(new ItemStack(this, 1, META_GOLD));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != ConfigBlocks.blockWoodenDevice) return EnumActionResult.PASS;
        int meta = state.getValue(BlockWoodenDevice.TYPE);
        if (meta != 2 && meta != 3) return EnumActionResult.PASS;
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileOwned)) return EnumActionResult.PASS;
        TileOwned owned = (TileOwned) tile;
        if (owned.accessList == null) owned.accessList = new java.util.ArrayList<>();
        String playerName = player.getName();
        String location = pos.getX() + "," + pos.getY() + "," + pos.getZ();
        byte type = 1;

        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("location")) {
            if (world.isRemote) return EnumActionResult.SUCCESS;
            if (playerName.equals(owned.owner) || (stack.getItemDamage() == META_IRON && owned.accessList.contains(META_GOLD + playerName))) {
                ItemStack linked = new ItemStack(this, 1, stack.getItemDamage());
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("location", location);
                tag.setByte("type", type);
                linked.setTagCompound(tag);
                if (!player.inventory.addItemStackToInventory(linked)) {
                    player.dropItem(linked, false);
                }
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                sendKeyMessage(player, "tc.key2");
                world.playSound(null, pos, TCSounds.KEY, SoundCategory.PLAYERS, 1.0F, 0.9F);
                player.swingArm(hand);
            }
            return EnumActionResult.SUCCESS;
        }

        NBTTagCompound tag = stack.getTagCompound();
        boolean sameLocation = location.equals(tag.getString("location")) && tag.getByte("type") == type;
        String exactAccess = stack.getItemDamage() + playerName;
        String goldAccess = META_GOLD + playerName;
        if (sameLocation && !playerName.equals(owned.owner) && !owned.accessList.contains(exactAccess) && !owned.accessList.contains(goldAccess)) {
            if (world.isRemote) return EnumActionResult.SUCCESS;
            owned.accessList.add(exactAccess);
            tile.markDirty();
            world.notifyBlockUpdate(pos, state, state, 3);
            sendKeyMessage(player, "tc.key5", stack.getItemDamage() == META_IRON ? "" : "tc.key6");
            world.playSound(null, pos, TCSounds.KEY, SoundCategory.PLAYERS, 1.0F, 1.1F);
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            player.swingArm(hand);
        } else if (!world.isRemote) {
            sendKeyMessage(player, sameLocation ? "tc.key8" : "tc.key7");
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("location")) {
            String location = stack.getTagCompound().getString("location");
            try {
                String[] parts = location.split(",");
                location = "x " + parts[0] + ", z " + parts[2] + ", y " + parts[1];
            } catch (Exception ignored) {}
            tooltip.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + new TextComponentTranslation("tc.key9").getFormattedText());
            tooltip.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + new TextComponentTranslation(stack.getTagCompound().getByte("type") == 0 ? "tc.key10" : "tc.key11").getFormattedText());
            tooltip.add(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + location);
        }
    }

    private static void sendKeyMessage(EntityPlayer player, String key) {
        player.sendStatusMessage(keyComponent(key), false);
    }

    private static void sendKeyMessage(EntityPlayer player, String key, String suffixKey) {
        if (suffixKey == null || suffixKey.isEmpty()) {
            sendKeyMessage(player, key);
        } else {
            player.sendStatusMessage(keyComponent(key).appendSibling(keyComponent(suffixKey)), false);
        }
    }

    private static TextComponentTranslation keyComponent(String key) {
        TextComponentTranslation component = new TextComponentTranslation(key);
        component.getStyle().setColor(TextFormatting.DARK_PURPLE).setItalic(true);
        return component;
    }
}
