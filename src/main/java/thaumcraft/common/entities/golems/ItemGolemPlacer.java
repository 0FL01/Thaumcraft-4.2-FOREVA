package thaumcraft.common.entities.golems;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemGolemPlacer extends Item {
    public ItemGolemPlacer() {
        this.setMaxStackSize(1);
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
            for (int meta = 0; meta <= 7; meta++) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey("core")) {
            tooltip.add(I18n.translateToLocal("item.ItemGolemCore.name") + ": \u00a76"
                    + I18n.translateToLocal("item.ItemGolemCore." + tag.getByte("core") + ".name"));
        }
        if (tag.hasKey("advanced")) {
            tooltip.add(I18n.translateToLocal("tc.adv"));
        }
        if (tag.hasKey("upgrades")) {
            String text = "\u00a79";
            for (byte b : tag.getByteArray("upgrades")) {
                if (b <= -1) continue;
                text = text + I18n.translateToLocal("item.ItemGolemUpgrade." + b + ".name") + " ";
            }
            tooltip.add(text);
        }
        if (tag.hasKey("markers")) {
            NBTTagList markers = tag.getTagList("markers", 10);
            tooltip.add("\u00a75" + markers.tagCount() + " " + I18n.translateToLocal("tc.markedloc"));
        }
        if (tag.hasKey("deco")) {
            String decoDesc = "\u00a72";
            String deco = tag.getString("deco");
            if (deco.contains("H")) decoDesc = decoDesc + I18n.translateToLocal("item.ItemGolemDecoration.0.name") + " ";
            if (deco.contains("G")) decoDesc = decoDesc + I18n.translateToLocal("item.ItemGolemDecoration.1.name") + " ";
            if (deco.contains("B")) decoDesc = decoDesc + I18n.translateToLocal("item.ItemGolemDecoration.2.name") + " ";
            if (deco.contains("F")) decoDesc = decoDesc + I18n.translateToLocal("item.ItemGolemDecoration.3.name") + " ";
            if (deco.contains("R")) decoDesc = decoDesc + I18n.translateToLocal("item.ItemGolemDecoration.4.name") + " ";
            if (deco.contains("V")) decoDesc = decoDesc + I18n.translateToLocal("item.ItemGolemDecoration.5.name") + " ";
            if (deco.contains("P")) decoDesc = decoDesc + I18n.translateToLocal("item.ItemGolemDecoration.6.name") + " ";
            tooltip.add(decoDesc);
        }
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote || player.isSneaking()) {
            return EnumActionResult.PASS;
        }
        ItemStack stack = player.getHeldItem(hand);
        BlockPos spawnPos = pos.offset(side);
        double yOffset = getPlacementYOffset(world.getBlockState(pos), side);
        boolean spawned = this.spawnCreature(world, spawnPos.getX() + 0.5D, spawnPos.getY() + yOffset,
                spawnPos.getZ() + 0.5D, side.getIndex(), stack, player);
        if (spawned && !player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    public boolean spawnCreature(World world, double x, double y, double z, int side, ItemStack stack, EntityPlayer player) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : null;
        boolean advanced = tag != null && tag.hasKey("advanced") && tag.getBoolean("advanced");
        EntityGolemBase golem = new EntityGolemBase(world, EnumGolemType.getType(stack.getItemDamage()), advanced);
        golem.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
        golem.setHomePosAndDistance(new BlockPos(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), 32);

        if (tag != null && tag.hasKey("core")) {
            golem.setCore(tag.getByte("core"));
        }
        if (tag != null && tag.hasKey("upgrades")) {
            byte[] restored = tag.getByteArray("upgrades");
            byte[] normalized = new byte[golem.upgrades.length];
            Arrays.fill(normalized, (byte) -1);
            System.arraycopy(restored, 0, normalized, 0, Math.min(restored.length, normalized.length));
            golem.upgrades = normalized;
        }
        String decoration = "";
        if (tag != null && tag.hasKey("deco")) {
            decoration = tag.getString("deco");
            golem.decoration = decoration;
        }

        golem.setup(side);
        golem.setGolemDecoration(decoration);
        golem.setOwner(player.getName());
        if (tag != null && tag.hasKey("markers")) {
            golem.setMarkers(ItemGolemBell.getMarkers(stack));
        }
        for (int slot = 0; slot < golem.upgrades.length; slot++) {
            golem.setUpgrade(slot, golem.upgrades[slot]);
        }
        if (stack.hasDisplayName()) {
            golem.setCustomNameTag(stack.getDisplayName());
            golem.enablePersistence();
        }
        if (tag != null && tag.hasKey("Inventory") && golem.inventory != null) {
            NBTTagList inventoryTag = tag.getTagList("Inventory", 10);
            golem.inventory.readFromNBT(inventoryTag);
        }

        boolean spawned = world.spawnEntity(golem);
        if (spawned) {
            golem.playLivingSound();
        }
        return spawned;
    }

    private static double getPlacementYOffset(IBlockState state, EnumFacing side) {
        if (side != EnumFacing.UP) {
            return 0.0D;
        }
        Block block = state.getBlock();
        return block instanceof BlockFence || block instanceof BlockWall ? 0.5D : 0.0D;
    }
}
