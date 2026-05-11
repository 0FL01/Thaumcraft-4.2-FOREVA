package thaumcraft.api.damagesource;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class DamageSourceThaumcraft
extends DamageSource {
    public static DamageSource taint = new DamageSourceThaumcraft("taint").setDamageBypassesArmor().setMagicDamage();
    public static DamageSource tentacle = new DamageSourceThaumcraft("tentacle");
    public static DamageSource swarm = new DamageSourceThaumcraft("swarm");
    public static DamageSource dissolve = new DamageSourceThaumcraft("dissolve").setDamageBypassesArmor();
    private boolean isUnblockable = false;
    private boolean isDamageAllowedInCreativeMode = false;
    private float hungerDamage = 0.3f;
    private boolean fireDamage;
    private boolean projectile;
    private boolean difficultyScaled;
    private boolean magicDamage = false;
    private boolean explosion = false;

    protected DamageSourceThaumcraft(String par1Str) {
        super(par1Str);
    }

    public static DamageSource causeSwarmDamage(EntityLivingBase par0EntityLiving) {
        return new EntityDamageSource("swarm", (Entity)par0EntityLiving);
    }

    public static DamageSource causeTentacleDamage(EntityLivingBase par0EntityLiving) {
        return new EntityDamageSource("tentacle", (Entity)par0EntityLiving);
    }
}
