package thaumcraft.common.items;

import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemZombieBrain extends ItemFood {

    public ItemZombieBrain() {
        super(4, 0.2f, true);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.8f);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayerMP) {
            this.applyWarp((EntityPlayer) entity, world.rand);
        }
        return super.onItemUseFinish(stack, world, entity);
    }

    void applyWarp(EntityPlayer player, Random random) {
        if (random.nextFloat() < 0.1f) {
            Thaumcraft.addStickyWarpToPlayer(player, 1);
        } else {
            Thaumcraft.addWarpToPlayer(player, 1 + random.nextInt(3), true);
        }
    }
}
