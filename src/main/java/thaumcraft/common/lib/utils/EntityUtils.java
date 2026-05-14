package thaumcraft.common.lib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EntityUtils {
    public static EntityItem dropItemStack(World world, BlockPos pos, ItemStack stack) {
        return dropItemStack(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
    }
    
    public static EntityItem dropItemStack(World world, double x, double y, double z, ItemStack stack) {
        return new EntityItem(world, x, y, z, stack);
    }

    public static void setRecentlyHit(Entity entity, int time) {
        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).hurtResistantTime = time;
        }
    }

    /**
     * Find all entities of the given class within range of a position.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<T> getEntitiesInRange(
            World world, double x, double y, double z,
            @Nullable Entity exclude, Class<T> clazz, double range) {
        List<T> result = new ArrayList<>();
        AxisAlignedBB aabb = new AxisAlignedBB(x - range, y - range, z - range,
                                                x + range, y + range, z + range);
        for (Entity e : world.getEntitiesInAABBexcluding(exclude, aabb,
                e2 -> e2 != null && clazz.isAssignableFrom(e2.getClass()))) {
            result.add((T) e);
        }
        return result;
    }

    public static <T extends Entity> List<T> getEntitiesInRange(
            World world, double x, double y, double z,
            @Nullable Entity exclude, Class<T> clazz, double range,
            boolean ignoreY) {
        if (!ignoreY) return getEntitiesInRange(world, x, y, z, exclude, clazz, range);
        List<T> result = new ArrayList<>();
        AxisAlignedBB aabb = new AxisAlignedBB(x - range, 0, z - range,
                                                x + range, 256, z + range);
        for (Entity e : world.getEntitiesInAABBexcluding(exclude, aabb,
                e2 -> e2 != null && clazz.isAssignableFrom(e2.getClass()))) {
            if (e.getDistance(x, y, z) <= range) {
                result.add((T) e);
            }
        }
        return result;
    }

    /**
     * Drop a special item from an entity (with pickup delay).
     */
    public static void entityDropSpecialItem(Entity entity, ItemStack stack, float offsetY) {
        if (stack.isEmpty()) return;
        EntityItem entityitem = new EntityItem(entity.world,
            entity.posX, entity.posY + (double)offsetY, entity.posZ, stack);
        entityitem.setDefaultPickupDelay();
        entity.world.spawnEntity(entityitem);
    }

    public static Entity getPointedEntity(World world, EntityPlayer player, double range, @Nullable Class<? extends Entity> excludedClass) {
        if (world == null || player == null) return null;
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end = eyes.add(look.x * range, look.y * range, look.z * range);
        Entity pointed = null;
        double closest = range * range;
        AxisAlignedBB searchBox = player.getEntityBoundingBox()
                .expand(look.x * range, look.y * range, look.z * range)
                .grow(1.0D);
        for (Entity entity : world.getEntitiesInAABBexcluding(player, searchBox, entity -> {
            if (entity == null || !entity.canBeCollidedWith()) return false;
            return excludedClass == null || !excludedClass.isAssignableFrom(entity.getClass());
        })) {
            RayTraceResult hit = entity.getEntityBoundingBox().grow(0.3D).calculateIntercept(eyes, end);
            if (hit == null) continue;
            double distance = eyes.squareDistanceTo(hit.hitVec);
            if (distance < closest) {
                pointed = entity;
                closest = distance;
            }
        }
        return pointed;
    }

    public static void makeChampion(EntityLivingBase entity, boolean isBoss) {
        if (entity == null) return;
        // Scale max health
        double healthMultiplier = isBoss ? 3.0 : 2.0;
        entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH)
                .setBaseValue(entity.getMaxHealth() * healthMultiplier);
        entity.setHealth(entity.getMaxHealth());
        // Scale attack damage
        if (entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
            entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE)
                    .setBaseValue(entity.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue() * 1.5);
        }
        // Add fire resistance for boss champions
        if (isBoss) {
            entity.addPotionEffect(new net.minecraft.potion.PotionEffect(
                    net.minecraft.init.MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        }
    }
}
