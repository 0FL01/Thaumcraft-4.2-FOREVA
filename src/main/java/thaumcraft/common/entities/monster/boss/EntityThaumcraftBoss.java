package thaumcraft.common.entities.monster.boss;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;

public class EntityThaumcraftBoss extends EntityMob {
    private final BossInfoServer bossInfo;

    public EntityThaumcraftBoss(net.minecraft.world.World world) {
        super(world);
        this.bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.95);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (amount > 35.0f) amount = 35.0f;
        if (amount > 0) {
            // TODO: enrage mechanics
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // TODO: boss scaling per player count
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean isNonBoss() { return false; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        super.dropFewItems(wasRecentlyHit, looting);
    }
}
