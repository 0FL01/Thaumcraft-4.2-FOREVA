package thaumcraft.common.entities.monster.boss;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityCultist;
import thaumcraft.common.entities.monster.EntityCultistCleric;
import thaumcraft.common.entities.monster.EntityCultistKnight;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileBanner;

public class EntityCultistPortal extends EntityThaumcraftBoss {

    private int stage = 0;
    private int stageCounter = 200;
    public int pulse = 0;

    public EntityCultistPortal(World world) {
        super(world);
        this.noClip = true;
        this.enablePersistence();
        this.setSize(1.5f, 3.0f);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("stage", this.stage);
        nbt.setInteger("stageCounter", this.stageCounter);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.stage = nbt.getInteger("stage");
        if (nbt.hasKey("stageCounter")) {
            this.stageCounter = nbt.getInteger("stageCounter");
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(500.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0D);
    }

    @Override
    public float getEyeHeight() {
        return 0.0F;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0D;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender() {
        return 0xF000F0;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public void move(net.minecraft.entity.MoverType type, double x, double y, double z) {
        // No movement — stationary boss
    }

    @Override
    protected void collideWithNearbyEntities() {
        // No collision knockback
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return false;
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote) {
            if (this.stageCounter > 0) {
                this.stageCounter--;

                // Stage 0: place banners + loot crates
                if (this.stageCounter == 160 && this.stage == 0) {
                    this.world.setEntityState(this, (byte) 16);
                    for (int a = 2; a < 6; a++) {
                        EnumFacing dir = EnumFacing.byIndex(a);
                        BlockPos bannerPos = new BlockPos(
                            (int)this.posX - dir.getXOffset() * 6,
                            (int)this.posY,
                            (int)this.posZ + dir.getZOffset() * 6);
                        this.world.setBlockState(bannerPos,
                            ConfigBlocks.blockWoodenDevice.getStateFromMeta(8), 3);

                        TileEntity te = this.world.getTileEntity(bannerPos);
                        if (te instanceof TileBanner) {
                            int face = 0;
                            switch (a) {
                                case 2:
                                    face = 8;
                                    break;
                                case 3:
                                    face = 0;
                                    break;
                                case 4:
                                    face = 12;
                                    break;
                                case 5:
                                    face = 4;
                                    break;
                                default:
                                    break;
                            }
                            ((TileBanner) te).setFacing((byte) face);
                        }
                        PacketHandler.INSTANCE.sendToAllAround(
                            new PacketFXBlockArc(bannerPos.getX(), bannerPos.getY(), bannerPos.getZ(), this.getEntityId()),
                            new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                        this.playSound(TCSounds.WANDFAIL, 1.0F, 1.0F);
                    }
                }

                // Stage 0: loot crates
                if (this.stageCounter > 20 && this.stageCounter < 150 && this.stage == 0 && this.stageCounter % 13 == 0) {
                    int a = (int)this.posX + this.rand.nextInt(5) - this.rand.nextInt(5);
                    int b = (int)this.posZ + this.rand.nextInt(5) - this.rand.nextInt(5);
                    if (a != (int)this.posX && b != (int)this.posZ && this.world.isAirBlock(new BlockPos(a, (int)this.posY, b))) {
                        this.world.setEntityState(this, (byte) 16);
                        float rr = this.world.rand.nextFloat();
                        int md = rr < 0.05F ? 2 : (rr < 0.2F ? 1 : 0);
                        this.world.setBlockState(new BlockPos(a, (int)this.posY, b),
                            ConfigBlocks.blockLootCrate.getStateFromMeta(md), 3);
                        PacketHandler.INSTANCE.sendToAllAround(
                            new PacketFXBlockArc(a, (int)this.posY, b, this.getEntityId()),
                            new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
                        this.playSound(TCSounds.WANDFAIL, 1.0F, 1.0F);
                    }
                }
            } else if (this.world.getClosestPlayerToEntity(this, 48.0) != null) {
                // Spawn phase: advance stage
                this.world.setEntityState(this, (byte) 16);
                switch (this.stage) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        this.stageCounter = 15 + this.rand.nextInt(10 - this.stage) - this.stage;
                        this.spawnMinions();
                        break;
                    case 12:
                        this.stageCounter = 50 + this.getTiming() * 2 + this.rand.nextInt(50);
                        this.spawnBoss();
                        break;
                    default:
                        int t = this.getTiming();
                        this.stageCounter = t + this.rand.nextInt(5 + t / 3);
                        this.spawnMinions();
                        break;
                }
                this.stage++;
            } else {
                // No players nearby — wait
                this.stageCounter = 30 + this.rand.nextInt(30);
            }

            // Regenerate HP while stage < 12
            if (this.stage < 12) {
                this.heal(1.0F);
            }
        }

        // Client-side pulse animation
        if (this.pulse > 0) {
            this.pulse--;
        }
    }

    private int getTiming() {
        java.util.List<EntityCultist> l = EntityUtils.getEntitiesInRange(this.world, this.posX, this.posY, this.posZ, this, EntityCultist.class, 32.0);
        return l.size() * 20;
    }

    private void spawnMinions() {
        EntityCultist cultist = this.rand.nextFloat() > 0.33F
            ? new EntityCultistKnight(this.world)
            : new EntityCultistCleric(this.world);

        cultist.setLocationAndAngles(
            this.posX + (double)this.rand.nextFloat() - (double)this.rand.nextFloat(),
            this.posY + 0.25D,
            this.posZ + (double)this.rand.nextFloat() - (double)this.rand.nextFloat(),
            0.0F, 0.0F);
        cultist.onInitialSpawn(this.world.getDifficultyForLocation(this.getPosition()), null);
        cultist.playLivingSound();
        cultist.setHomePosAndDistance(this.getPosition(), 32);
        this.world.spawnEntity(cultist);
        this.playSound(TCSounds.WANDFAIL, 1.0F, 1.0F);

        if (this.stage > 12) {
            this.attackEntityFrom(DamageSource.MAGIC, 5 + this.rand.nextInt(5));
        }
    }

    private void spawnBoss() {
        DifficultyInstance difficulty = this.world.getDifficultyForLocation(this.getPosition());
        EntityCultistLeader cultist = new EntityCultistLeader(this.world);
        cultist.setLocationAndAngles(
            this.posX + (double)this.rand.nextFloat() - (double)this.rand.nextFloat(),
            this.posY + 0.25D,
            this.posZ + (double)this.rand.nextFloat() - (double)this.rand.nextFloat(),
            0.0F, 0.0F);
        cultist.onInitialSpawn(difficulty, null);
        cultist.setHomePosAndDistance(this.getPosition(), 32);
        cultist.playLivingSound();
        this.world.spawnEntity(cultist);
        this.playSound(TCSounds.WANDFAIL, 1.0F, 1.0F);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.getDistanceSq(player) < 3.0D && player.attackEntityFrom(
                DamageSource.causeIndirectDamage(this, this), 8.0F)) {
            this.playSound(TCSounds.ZAP, 1.0F,
                (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F + 1.0F);
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.75F;
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 540;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return TCSounds.MONOLITH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return TCSounds.ZAP;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TCSounds.SHOCK;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        EntityUtils.entityDropSpecialItem(this, new ItemStack(ConfigItems.itemEldritchObject, 1, 3), this.height / 2.0F);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 16) {
            this.pulse = 10;
            this.playLivingSound();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public void addPotionEffect(PotionEffect effect) {
        // Immune to all potion effects
    }

    @Override
    protected void onDeathUpdate() {
        if (!this.world.isRemote && this.deathTime == 0) {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 2.0F, false);
        }
        super.onDeathUpdate();
    }
}
