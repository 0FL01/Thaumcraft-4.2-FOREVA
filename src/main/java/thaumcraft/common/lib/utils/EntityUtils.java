package thaumcraft.common.lib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.mods.ChampionModifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityUtils {
    public static final IAttribute CHAMPION_MOD =
            new RangedAttribute(null, "tc.mobmod", -2.0D, -2.0D, 100.0D)
                    .setDescription("Champion modifier")
                    .setShouldWatch(true);
    public static final AttributeModifier CHAMPION_HEALTH =
            new AttributeModifier(UUID.fromString("a62bef38-48cc-42a6-ac5e-ef913841c4fd"), "Champion health buff", 30.0D, 0);
    public static final AttributeModifier CHAMPION_DAMAGE =
            new AttributeModifier(UUID.fromString("a340d2db-d881-4c25-ac62-f0ad14cd63b0"), "Champion damage buff", 2.0D, 2);
    public static final AttributeModifier BOLDBUFF =
            new AttributeModifier(UUID.fromString("4b1edd33-caa9-47ae-a702-d86c05701037"), "Bold speed boost", 0.3D, 1);
    public static final AttributeModifier MIGHTYBUFF =
            new AttributeModifier(UUID.fromString("7163897f-07f5-49b3-9ce4-b74beb83d2d3"), "Mighty damage boost", 3.0D, 2);

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

    private static IAttributeInstance ensureChampionModAttribute(EntityLivingBase entity) {
        if (!(entity instanceof EntityMob)) {
            return null;
        }
        IAttributeInstance instance = entity.getEntityAttribute(CHAMPION_MOD);
        if (instance != null) {
            return instance;
        }
        try {
            return entity.getAttributeMap().registerAttribute(CHAMPION_MOD);
        } catch (IllegalArgumentException ignored) {
            return entity.getEntityAttribute(CHAMPION_MOD);
        }
    }

    public static void makeChampion(EntityLivingBase entity, boolean persist) {
        if (!(entity instanceof EntityMob)) {
            return;
        }
        EntityMob mob = (EntityMob) entity;
        int type = mob.world.rand.nextInt(ChampionModifier.mods.length);
        if (mob instanceof EntityCreeper) {
            type = 0;
        }

        IAttributeInstance modai = ensureChampionModAttribute(mob);
        if (modai == null) {
            return;
        }
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.applyModifier(ChampionModifier.mods[type].attributeMod);

        if (!(mob instanceof EntityThaumcraftBoss)) {
            IAttributeInstance maxHealth = mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.removeModifier(CHAMPION_HEALTH);
                maxHealth.applyModifier(CHAMPION_HEALTH);
            }
            IAttributeInstance attack = mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (attack != null) {
                attack.removeModifier(CHAMPION_DAMAGE);
                attack.applyModifier(CHAMPION_DAMAGE);
            }
            mob.heal(25.0F);
            mob.setCustomNameTag(ChampionModifier.mods[type].getModNameLocalized() + " " + mob.getName());
        } else {
            ((EntityThaumcraftBoss) mob).generateName();
        }

        if (persist) {
            mob.enablePersistence();
        }

        switch (type) {
            case 0:
                IAttributeInstance speed = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                if (speed != null) {
                    speed.removeModifier(BOLDBUFF);
                    speed.applyModifier(BOLDBUFF);
                }
                break;
            case 3:
                IAttributeInstance damage = mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                if (damage != null) {
                    damage.removeModifier(MIGHTYBUFF);
                    damage.applyModifier(MIGHTYBUFF);
                }
                break;
            case 5:
                IAttributeInstance maxHealth = mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                if (maxHealth != null) {
                    int bonus = (int) maxHealth.getBaseValue() / 2;
                    mob.setHealth(mob.getHealth() + (float) bonus);
                }
                break;
            default:
                break;
        }
    }
}
