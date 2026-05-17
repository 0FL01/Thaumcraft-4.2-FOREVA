package thaumcraft.client.fx.beams;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXBeamGolemBoss extends FXBeam {

    public FXBeamGolemBoss(World world, EntityLivingBase source, Entity target,
                           float red, float green, float blue, int age) {
        super(world,
                source.posX,
                source.posY + source.getEyeHeight(),
                source.posZ,
                target.posX,
                target.posY + target.height * 0.5D,
                target.posZ,
                red, green, blue,
                age, true, 18);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.world == null || !this.world.isRemote || !this.isAlive()) {
            return;
        }
        double mx = (this.sourceX + this.targetX) * 0.5D;
        double my = (this.sourceY + this.targetY) * 0.5D;
        double mz = (this.sourceZ + this.targetZ) * 0.5D;
        this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, mx, my, mz, 0.0D, 0.0D, 0.0D);
    }
}
