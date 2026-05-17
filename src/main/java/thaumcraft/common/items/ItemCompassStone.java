package thaumcraft.common.items;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemCompassStone extends Item {

    public static final HashMap<WorldCoordinates, Long> sinisterNodes = new HashMap<>();

    public ItemCompassStone() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
        this.addPropertyOverride(new ResourceLocation("thaumcraft", "active"),
                (stack, world, entity) -> entity != null && isSinisterVisible(entity.world, entity) ? 1.0F : 0.0F);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote && entity != null) {
            isSinisterVisible(world, entity);
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    private static boolean isSinisterVisible(World world, Entity entity) {
        if (world == null || entity == null) return false;
        boolean active = false;
        long cutoff = System.currentTimeMillis() - 10000L;
        int dim = world.provider.getDimension();
        Iterator<Map.Entry<WorldCoordinates, Long>> it = sinisterNodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<WorldCoordinates, Long> entry = it.next();
            WorldCoordinates coordinates = entry.getKey();
            if (entry.getValue() < cutoff) {
                it.remove();
                continue;
            }
            if (coordinates.dim == dim && isVisibleTo(0.66F, entity,
                    (double) coordinates.x + 0.5D,
                    (double) coordinates.y + 0.5D,
                    (double) coordinates.z + 0.5D,
                    256.0F)) {
                active = true;
            }
        }
        return active;
    }

    private static boolean isVisibleTo(float fov, Entity entity, double x, double y, double z, float range) {
        if (!(entity instanceof EntityLivingBase)) return false;
        Vec3d eyes = new Vec3d(entity.posX, entity.getEntityBoundingBox().minY + entity.getEyeHeight(), entity.posZ);
        Vec3d target = new Vec3d(x, y, z);
        Vec3d toTarget = target.subtract(eyes);
        double distance = Math.sqrt(toTarget.x * toTarget.x + toTarget.y * toTarget.y + toTarget.z * toTarget.z);
        if (distance <= 0.0D || distance > (double) range) return false;
        Vec3d look = entity.getLook(1.0F).normalize();
        return toTarget.normalize().dotProduct(look) > Math.cos((double) fov / 2.0D);
    }
}
