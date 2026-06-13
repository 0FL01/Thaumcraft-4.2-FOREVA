package thaumcraft.common.tiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.List;

public class TileEldritchObelisk extends TileThaumcraft implements ITickable {
    private int counter = 0;

    @Override
    public void update() {
        if (this.world == null || this.pos == null) {
            return;
        }

        List<EntityLivingBase> list = EntityUtils.getEntitiesInRange(
                this.getWorld(),
                this.pos.getX() + 0.5D,
                this.pos.getY(),
                this.pos.getZ() + 0.5D,
                null,
                EntityLivingBase.class,
                6.0D);

        if (!this.world.isRemote && this.counter % 20 == 0 && list != null && !list.isEmpty()) {
            for (Entity e : list) {
                if (!(e instanceof IEldritchMob) || !(e instanceof EntityLivingBase) || ((EntityLivingBase) e).isPotionActive(MobEffects.RESISTANCE)) {
                    continue;
                }
                try {
                    ((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 0, true, true));
                    ((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 0, true, true));
                } catch (Exception ignored) {
                }
            }
        }

        if (this.world.isRemote && list != null && !list.isEmpty()) {
            for (Entity e : list) {
                if (!(e instanceof IEldritchMob) || !(e instanceof EntityLivingBase)) {
                    continue;
                }
                Thaumcraft.proxy.wispFXEG(
                        this.getWorld(),
                        this.pos.getX() + 0.5D,
                        this.pos.getY() + 1.0D + this.world.rand.nextFloat() * 3.0F,
                        this.pos.getZ() + 0.5D,
                        e);
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 5, this.pos.getZ() + 1);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 20736.0;
    }
}
