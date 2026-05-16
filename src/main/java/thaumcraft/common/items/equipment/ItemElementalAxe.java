package thaumcraft.common.items.equipment;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.IRepairable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBubble;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;

public class ItemElementalAxe extends ItemAxe implements IRepairable {

    public static List<List<?>> oreDictLogs = new ArrayList<>();

    public ItemElementalAxe(ToolMaterial material) {
        super(material, 8.0f, -3.0f);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("axe");
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
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(net.minecraft.world.World world, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        List<EntityItem> nearbyItems = EntityUtils.getEntitiesInRange(
                player.world, player.posX, player.posY, player.posZ, player, EntityItem.class, 10.0D
        );
        if (nearbyItems == null || nearbyItems.isEmpty()) {
            return;
        }

        for (Entity itemEntity : nearbyItems) {
            if (!(itemEntity instanceof EntityItem) || itemEntity.isDead) {
                continue;
            }
            if (itemEntity instanceof EntityFollowingItem) {
                continue;
            }

            double dx = itemEntity.posX - player.posX;
            double dy = itemEntity.posY - player.posY + (player.height / 2.0F);
            double dz = itemEntity.posZ - player.posZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance < 1.0E-6D) {
                continue;
            }

            double pull = 0.3D;
            itemEntity.motionX -= (dx / distance) * pull;
            itemEntity.motionY -= (dy / distance) * pull;
            itemEntity.motionZ -= (dz / distance) * pull;
            itemEntity.motionX = clamp(itemEntity.motionX, -0.35D, 0.35D);
            itemEntity.motionY = clamp(itemEntity.motionY, -0.35D, 0.35D);
            itemEntity.motionZ = clamp(itemEntity.motionZ, -0.35D, 0.35D);

            float px = (float) itemEntity.posX + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.125F;
            float py = (float) itemEntity.posY + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.125F;
            float pz = (float) itemEntity.posZ + (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.125F;
            thaumcraft.common.Thaumcraft.proxy.crucibleBubble(player.world, px, py, pz, 0.33F, 0.33F, 1.0F);
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        net.minecraft.world.World world = player.world;
        if (!player.isSneaking() && Utils.isWoodLog(world, pos)) {
            if (!world.isRemote) {
                BlockUtils.breakFurthestBlock(world, pos, player);
                PacketHandler.INSTANCE.sendToAllAround(
                        new PacketFXBlockBubble(pos.getX(), pos.getY(), pos.getZ(),
                                new Color(0.33F, 0.33F, 1.0F).getRGB()),
                        new NetworkRegistry.TargetPoint(
                                world.provider.getDimension(),
                                pos.getX(), pos.getY(), pos.getZ(),
                                32.0D
                        )
                );
                world.playSound(null, pos, TCSounds.BUBBLE, SoundCategory.PLAYERS, 0.15F, 1.0F);
            }
            stack.damageItem(1, player);
            return true;
        }
        return super.onBlockStartBreak(stack, pos, player);
    }
}
