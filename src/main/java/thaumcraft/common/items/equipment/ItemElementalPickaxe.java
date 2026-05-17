package thaumcraft.common.items.equipment;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;

import java.util.Set;

public class ItemElementalPickaxe extends ItemPickaxe implements IRepairable {

    public ItemElementalPickaxe(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("pickaxe");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack thaumiumIngot = new ItemStack(ConfigItems.itemResource, 1, 2);
        return repair.isItemEqual(thaumiumIngot) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.world.isRemote
                && (!(entity instanceof EntityPlayer)
                || player.getServer() == null
                || player.getServer().isPVPEnabled())) {
            entity.setFire(2);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        stack.damageItem(5, player);
        if (!world.isRemote) {
            world.playSound(null, pos, TCSounds.WANDFAIL, SoundCategory.PLAYERS, 0.2F, 0.2F + world.rand.nextFloat() * 0.2F);
        } else {
            Thaumcraft.proxy.startScan(player, pos, System.currentTimeMillis() + 5000L, 8);
            player.swingArm(hand);
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}
