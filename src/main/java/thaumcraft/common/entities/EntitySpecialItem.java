package thaumcraft.common.entities;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySpecialItem extends EntityItem {

    public EntitySpecialItem(World world) {
        super(world);
        setSize(0.25F, 0.25F);
    }

    public EntitySpecialItem(World world, double x, double y, double z) {
        this(world, x, y, z, ItemStack.EMPTY);
    }

    public EntitySpecialItem(World world, double x, double y, double z, ItemStack stack) {
        super(world);
        setSize(0.25F, 0.25F);
        setPosition(x, y, z);
        setItem(stack);
        this.rotationYaw = (float) (Math.random() * 360.0D);
        this.motionX = Math.random() * 0.20000000298023224D - 0.10000000149011612D;
        this.motionY = 0.20000000298023224D;
        this.motionZ = Math.random() * 0.20000000298023224D - 0.10000000149011612D;
    }

    @Override
    public void onUpdate() {
        if (this.motionY > 0.0D) {
            this.motionY *= 0.8999999761581421D;
        }
        this.motionY += 0.03999999910593033D;
        super.onUpdate();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isExplosion()) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }
}
