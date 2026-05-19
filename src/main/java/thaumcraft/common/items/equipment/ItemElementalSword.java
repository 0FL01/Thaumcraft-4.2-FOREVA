package thaumcraft.common.items.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.Thaumcraft;
import thaumcraft.api.IRepairable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.Utils;

import java.util.List;

public class ItemElementalSword extends ItemSword implements IRepairable {

    public ItemElementalSword(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack thaumiumIngot = new ItemStack(ConfigItems.itemResource, 1, 2);
        return repair.isItemEqual(thaumiumIngot) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
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
        super.onUsingTick(stack, entity, count);
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        int ticks = this.getMaxItemUseDuration(stack) - count;

        if (player.motionY < 0.0D) {
            player.motionY /= 1.2D;
            player.fallDistance /= 1.2F;
        }
        player.motionY += 0.08D;
        if (player.motionY > 0.5D) {
            player.motionY = 0.2D;
        }
        if (!player.world.isRemote && player instanceof EntityPlayerMP) {
            Utils.resetFloatCounter((EntityPlayerMP) player);
        }

        List<Entity> nearby = player.world.getEntitiesWithinAABBExcludingEntity(player,
                player.getEntityBoundingBox().grow(2.5D, 2.5D, 2.5D));
        for (Entity target : nearby) {
            if (target instanceof EntityPlayer || !target.isEntityAlive()) {
                continue;
            }
            if (player.getRidingEntity() != null && player.getRidingEntity() == target) {
                continue;
            }
            Vec3d playerPos = new Vec3d(player.posX, player.posY, player.posZ);
            Vec3d targetPos = new Vec3d(target.posX, target.posY, target.posZ);
            double distance = playerPos.distanceTo(targetPos) + 0.1D;
            Vec3d delta = targetPos.subtract(playerPos);
            target.motionX += delta.x / 2.5D / distance;
            target.motionY += delta.y / 2.5D / distance;
            target.motionZ += delta.z / 2.5D / distance;
            target.velocityChanged = true;
        }

        if (player.world.isRemote) {
            int miny = (int) (player.getEntityBoundingBox().minY - 2.0D);
            if (player.onGround) {
                miny = MathHelper.floor(player.getEntityBoundingBox().minY);
            }
            for (int i = 0; i < 5; i++) {
                Thaumcraft.proxy.smokeSpiral(player.world,
                        player.posX,
                        player.getEntityBoundingBox().minY + player.height / 2.0F,
                        player.posZ,
                        1.5F,
                        player.world.rand.nextInt(360),
                        miny,
                        0xDDDDDD);
            }
            if (player.onGround) {
                float yaw = player.world.rand.nextFloat() * 360.0F;
                float mx = -MathHelper.sin(yaw / 180.0F * (float) Math.PI) / 5.0F;
                float mz = MathHelper.cos(yaw / 180.0F * (float) Math.PI) / 5.0F;
                Thaumcraft.proxy.drawGenericParticles(player.world,
                        player.posX,
                        player.getEntityBoundingBox().minY + 0.1F,
                        player.posZ,
                        mx, 0.0D, mz,
                        0.25F, 0.25F, 0.25F, 0.8F,
                        false, 0, 8, -1, 8, 0, 0.8F, 1);
            }
        } else if (ticks == 0 || ticks % 20 == 0) {
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    TCSounds.WIND, SoundCategory.PLAYERS, 0.5F,
                    0.9F + player.world.rand.nextFloat() * 0.2F);
        }

        if (ticks % 20 == 0) {
            stack.damageItem(1, player);
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.world.isRemote && entity.isEntityAlive()) {
            List<Entity> nearby = player.world.getEntitiesWithinAABBExcludingEntity(player,
                    entity.getEntityBoundingBox().grow(1.2D, 1.1D, 1.2D));
            int count = 0;
            for (Entity candidate : nearby) {
                if (!candidate.isEntityAlive() || !(candidate instanceof EntityLivingBase) || candidate == entity) {
                    continue;
                }
                if (candidate instanceof EntityGolemBase && player.getName().equals(((EntityGolemBase) candidate).getOwnerName())) {
                    continue;
                }
                if (candidate instanceof EntityTameable && ((EntityTameable) candidate).isOwner(player)) {
                    continue;
                }
                if (candidate instanceof EntityPlayer && player.getName().equals(((EntityPlayer) candidate).getName())) {
                    continue;
                }
                if (candidate.isOnSameTeam(player)) {
                    continue;
                }
                player.attackTargetEntityWithCurrentItem(candidate);
                count++;
            }
            if (count > 0) {
                player.world.playSound(null, entity.posX, entity.posY, entity.posZ,
                        TCSounds.SWING, SoundCategory.PLAYERS, 1.0F,
                        0.9F + player.world.rand.nextFloat() * 0.2F);
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}
