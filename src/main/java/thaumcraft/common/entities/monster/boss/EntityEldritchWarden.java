package thaumcraft.common.entities.monster.boss;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.ai.combat.AIAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.EntityCultist;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.network.fx.PacketFXSonic;

public class EntityEldritchWarden extends EntityThaumcraftBoss implements net.minecraft.entity.IRangedAttackMob, thaumcraft.api.entities.IEldritchMob {
    public static final String[] TITLES = {"Aphoom-Zhah", "Basatan", "Chaugnar Faugn", "Mnomquah", "Nyogtha", "Oorn", "Shaikorth", "Rhan-Tegoth", "Rhogog", "Shudde M'ell", "Vulthoom", "Yag-Kosha", "Yibb-Tstll", "Zathog", "Zushakon"};
    private static final DataParameter<Integer> TITLE = EntityDataManager.createKey(EntityEldritchWarden.class, DataSerializers.VARINT);
    private boolean fieldFrenzy = false;
    private int fieldFrenzyCounter = 0;
    private boolean lastBlast = false;
    public float armLiftL = 0.0F;
    public float armLiftR = 0.0F;

    public EntityEldritchWarden(net.minecraft.world.World world) {
        super(world);
        if (this.getNavigator() instanceof PathNavigateGround) {
            ((PathNavigateGround) this.getNavigator()).setCanSwim(true);
        }
        this.setSize(1.5F, 3.5F);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AILongRangeAttack(this, 3.0, 1.0, 20, 40, 24.0f));
        this.tasks.addTask(3, new AIAttackOnCollide(this, EntityLivingBase.class, 1.1, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new net.minecraft.entity.ai.EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, thaumcraft.common.entities.monster.EntityCultist.class, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(TITLE, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        this.spawnTimer = 150;
        this.setTitle(this.rand.nextInt(TITLES.length));
        this.setAbsorptionAmount(this.getAbsorptionAmount() + (float) (this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 0.66D));
        return super.onInitialSpawn(difficulty, livingdata);
    }

    private void setTitle(int title) {
        this.dataManager.set(TITLE, Math.max(0, Math.min(title, TITLES.length - 1)));
    }

    public String getTitle() {
        return TITLES[this.dataManager.get(TITLE)];
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setByte("title", this.dataManager.get(TITLE).byteValue());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setTitle(compound.getByte("title"));
    }

    @Override
    public int getTotalArmorValue() {
        return super.getTotalArmorValue() + 4;
    }

    @Override
    public float getEyeHeight() {
        return 3.1F;
    }

    @Override
    protected void updateAITasks() {
        if (this.fieldFrenzyCounter == 0) {
            super.updateAITasks();
        }
        if (this.hurtResistantTime <= 0 && this.ticksExisted % 25 == 0) {
            int maxWard = (int) (this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 0.66D);
            if (this.getAbsorptionAmount() < (float) maxWard) {
                this.setAbsorptionAmount(this.getAbsorptionAmount() + 1.0F);
            }
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        if (this.rand.nextFloat() > 0.2F) {
            EntityEldritchOrb blast = new EntityEldritchOrb(this.world, this);
            this.lastBlast = !this.lastBlast;
            this.world.setEntityState(this, this.lastBlast ? (byte) 16 : (byte) 15);
            int rr = this.lastBlast ? 90 : 180;
            double xx = MathHelper.cos((this.rotationYaw + (float) rr) % 360.0F / 180.0F * (float) Math.PI) * 0.5F;
            double yy = 0.13D;
            double zz = MathHelper.sin((this.rotationYaw + (float) rr) % 360.0F / 180.0F * (float) Math.PI) * 0.5F;
            blast.setPosition(blast.posX - xx, blast.posY - yy, blast.posZ - zz);
            double d0 = target.posX + target.motionX - this.posX;
            double d1 = target.posY - this.posY - (double) (target.height / 2.0F);
            double d2 = target.posZ + target.motionZ - this.posZ;
            blast.shoot(d0, d1, d2, 1.0F, 2.0F);
            this.playSound(TCSounds.EGATTACK, 2.0F, 1.0F + this.rand.nextFloat() * 0.1F);
            if (!this.world.isRemote) {
                this.world.spawnEntity(blast);
            }
        } else if (this.canEntityBeSeen(target)) {
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXSonic(this.getEntityId()),
                    new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0));
            target.addVelocity(-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * 1.5F, 0.1D, MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * 1.5F);
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 400, 0));
            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 400, 0));
            if (target instanceof EntityPlayer) {
                Thaumcraft.addWarpToPlayer((EntityPlayer) target, 3 + this.world.rand.nextInt(3), true);
            }
            this.playSound(TCSounds.EGSCREECH, 4.0F, 1.0F + this.rand.nextFloat() * 0.1F);
        }
    }

    @Override
    public void setSwingingArms(boolean swinging) {}

    @Override protected net.minecraft.util.SoundEvent getAmbientSound() { return thaumcraft.common.lib.TCSounds.EGIDLE; }
    @Override protected net.minecraft.util.SoundEvent getDeathSound() { return thaumcraft.common.lib.TCSounds.EGDEATH; }

    @Override
    public void onUpdate() {
        if (this.getSpawnTimer() == 150) {
            this.world.setEntityState(this, (byte) 18);
        }
        super.onUpdate();
        if (this.world.isRemote) {
            if (this.armLiftL > 0.0F) {
                this.armLiftL -= 0.05F;
            }
            if (this.armLiftR > 0.0F) {
                this.armLiftR -= 0.05F;
            }
            double x = this.posX + (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            double z = this.posZ + (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            Thaumcraft.proxy.wispFXEG(this.world, x, this.posY + 0.25D * (double) this.height, z, this);
            if (this.getSpawnTimer() > 0) {
                float he = Math.max(1.0F, this.height * ((150 - this.getSpawnTimer()) / 150.0F));
                for (int i = 0; i < 33; i++) {
                    Thaumcraft.proxy.smokeSpiral(this.world, this.posX, this.getEntityBoundingBox().minY + he / 2.0F, this.posZ,
                            he, this.rand.nextInt(360), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, 0x22112F);
                }
            }
            return;
        }
        this.fillEldritchField();
        if (this.fieldFrenzyCounter > 0) {
            if (this.fieldFrenzyCounter == 150) {
                this.teleportHome();
            }
            this.performFieldFrenzy();
        }
    }

    private void fillEldritchField() {
        int y = MathHelper.floor(this.posY);
        for (int l = 0; l < 4; ++l) {
            int x = MathHelper.floor(this.posX + (double) ((float) (l % 2 * 2 - 1) * 0.25F));
            int z = MathHelper.floor(this.posZ + (double) ((float) (l / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos pos = new BlockPos(x, y, z);
            if (this.world.isAirBlock(pos)) {
                this.world.setBlockState(pos, ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 11), 3);
                this.world.scheduleUpdate(pos, ConfigBlocks.blockAiry, 250 + this.rand.nextInt(150));
            }
        }
    }

    private void performFieldFrenzy() {
        if (this.fieldFrenzyCounter < 121 && this.fieldFrenzyCounter % 10 == 0) {
            this.world.setEntityState(this, (byte) 17);
            double radius = (double) (150 - this.fieldFrenzyCounter) / 8.0D;
            int d = 1 + this.fieldFrenzyCounter / 8;
            int x = MathHelper.floor(this.posX);
            int y = MathHelper.floor(this.posY);
            int z = MathHelper.floor(this.posZ);
            for (int q = 0; q < 180 / d; ++q) {
                double radians = Math.toRadians(q * 2 * d);
                int dx = (int) (radius * Math.cos(radians));
                int dz = (int) (radius * Math.sin(radians));
                BlockPos pos = new BlockPos(x + dx, y, z + dz);
                if (!this.world.isAirBlock(pos) || !this.world.isSideSolid(pos.down(), net.minecraft.util.EnumFacing.UP, false)) {
                    continue;
                }
                this.world.setBlockState(pos, ConfigBlocks.blockAiry.getDefaultState().withProperty(BlockAiry.TYPE, 11), 3);
                this.world.scheduleUpdate(pos, ConfigBlocks.blockAiry, 250 + this.rand.nextInt(150));
                if (this.rand.nextFloat() < 0.3F) {
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketFXBlockArc(pos.getX(), pos.getY(), pos.getZ(), this.getEntityId()),
                            new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                } else {
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketFXBlockSparkle(pos.getX(), pos.getY(), pos.getZ(), 0x800080),
                            new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 32.0));
                }
            }
            this.world.playSound(null, this.posX, this.posY, this.posZ, TCSounds.ZAP, SoundCategory.HOSTILE, 1.0F, 0.9F + this.rand.nextFloat() * 0.1F);
        }
        --this.fieldFrenzyCounter;
    }

    protected void teleportHome() {
        if (!this.hasHome()) {
            return;
        }
        BlockPos home = this.getHomePosition();
        EnderTeleportEvent event = new EnderTeleportEvent(this, home.getX(), home.getY(), home.getZ(), 0.0F);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        double oldX = this.posX;
        double oldY = this.posY;
        double oldZ = this.posZ;
        if (!this.tryTeleportNearHome(this.world, event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
            this.setPosition(oldX, oldY, oldZ);
            return;
        }
        this.world.playSound(null, oldX, oldY, oldZ, net.minecraft.init.SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
        this.playSound(net.minecraft.init.SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
    }

    private boolean tryTeleportNearHome(World world, double x, double y, double z) {
        int baseX = MathHelper.floor(x);
        int baseY = MathHelper.floor(y);
        int baseZ = MathHelper.floor(z);
        if (!world.isBlockLoaded(new BlockPos(baseX, baseY, baseZ))) {
            return false;
        }
        for (int tries = 20; tries > 0; --tries) {
            int tx = baseX + this.rand.nextInt(8) - this.rand.nextInt(8);
            int tz = baseZ + this.rand.nextInt(8) - this.rand.nextInt(8);
            BlockPos pos = new BlockPos(tx, baseY, tz);
            if (!world.getBlockState(pos.down()).getMaterial().blocksMovement() || world.getBlockState(pos).getMaterial().blocksMovement()) {
                continue;
            }
            this.setPosition((double) tx + 0.5D, (double) baseY + 0.1D, (double) tz + 0.5D);
            if (world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                return true;
            }
        }
        this.setPosition((double) baseX + 0.5D, (double) baseY + 0.1D, (double) baseZ + 0.5D);
        return world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()
            && world.getBlockState(new BlockPos(baseX, baseY - 1, baseZ)).getMaterial() != Material.AIR;
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        if (this.fieldFrenzyCounter > 0 || source == net.minecraft.util.DamageSource.OUT_OF_WORLD || source == net.minecraft.util.DamageSource.MAGIC) {
            return false;
        }
        boolean damaged = super.attackEntityFrom(source, amount);
        if (!this.world.isRemote && damaged && !this.fieldFrenzy && this.getAbsorptionAmount() <= 0.0F) {
            this.fieldFrenzy = true;
            this.fieldFrenzyCounter = 150;
        }
        return damaged;
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        return entityIn instanceof EntityEldritchGuardian || super.isOnSameTeam(entityIn);
    }

    @Override
    public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
        if (cls == EntityEldritchGuardian.class) {
            return false;
        }
        return super.canAttackClass(cls);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 15) {
            this.armLiftL = 0.5F;
        } else if (id == 16) {
            this.armLiftR = 0.5F;
        } else if (id == 17) {
            this.armLiftL = 0.9F;
            this.armLiftR = 0.9F;
        } else {
            super.handleStatusUpdate(id);
        }
    }
}
