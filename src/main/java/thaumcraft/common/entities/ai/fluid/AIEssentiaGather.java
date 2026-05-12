package thaumcraft.common.entities.ai.fluid;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileEssentiaReservoir;
import thaumcraft.common.tiles.TileJarFillable;

public class AIEssentiaGather extends EntityAIBase {

    private EntityGolemBase theGolem;
    private double crucX;
    private double crucY;
    private double crucZ;
    private World theWorld;
    private long delay = 0L;
    int start = 0;

    public AIEssentiaGather(EntityGolemBase golem) {
        this.theGolem = golem;
        this.theWorld = golem.world;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.theGolem.getNavigator().noPath() || this.delay > System.currentTimeMillis()) {
            return false;
        }
        BlockPos home = this.theGolem.getHomePosition();
        EnumFacing facing = EnumFacing.VALUES[this.theGolem.homeFacing % EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        if (this.theGolem.getDistanceSq(cX + 0.5, cY + 0.5, cZ + 0.5) > 6.0) {
            return false;
        }
        this.start = 0;
        TileEntity te = this.theWorld.getTileEntity(new BlockPos(cX, cY, cZ));
        if (te != null) {
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport etrans = (IEssentiaTransport) te;
                if ((te instanceof TileJarFillable || te instanceof TileEssentiaReservoir
                    || etrans.canOutputTo(facing))
                    && etrans.getEssentiaAmount(facing) > 0
                    && (this.theGolem.essentiaAmount == 0
                        || (this.theGolem.essentia == null
                            || this.theGolem.essentia.equals(etrans.getEssentiaType(facing))
                            || this.theGolem.essentia.equals(etrans.getEssentiaType(EnumFacing.UP)))
                        && this.theGolem.essentiaAmount < this.theGolem.getCarryLimit())) {
                    this.delay = System.currentTimeMillis() + 1000L;
                    this.start = 0;
                    return true;
                }
            } else {
                this.start = -1;
                int prevTot = -1;
                for (int a = 5; a >= 0; --a) {
                    te = this.theWorld.getTileEntity(new BlockPos(cX, cY + a, cZ));
                    if (te == null || !(te instanceof TileAlembic)) continue;
                    TileAlembic ta = (TileAlembic) te;
                    if (this.theGolem.essentiaAmount != 0
                        && (this.theGolem.essentia != null && !this.theGolem.essentia.equals(ta.aspect)
                            || this.theGolem.essentiaAmount >= this.theGolem.getCarryLimit())
                        || ta.amount <= prevTot) continue;
                    this.delay = System.currentTimeMillis() + 1000L;
                    this.start = a;
                    prevTot = ta.amount;
                }
                if (this.start >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    @Override
    public void startExecuting() {
        BlockPos home = this.theGolem.getHomePosition();
        EnumFacing facing = EnumFacing.VALUES[this.theGolem.homeFacing % EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        TileEntity te = this.theWorld.getTileEntity(new BlockPos(cX, cY + this.start, cZ));
        if (te != null && te instanceof IEssentiaTransport) {
            IEssentiaTransport ta = (IEssentiaTransport) te;
            EnumFacing dir = facing;
            if (te instanceof TileAlembic || te instanceof TileJarFillable) {
                dir = EnumFacing.UP;
            }
            if (te instanceof TileEssentiaReservoir) {
                dir = ((TileEssentiaReservoir) te).facing;
            }
            if (ta.getEssentiaAmount(dir) == 0) return;
            if (ta.canOutputTo(dir) && ta.getEssentiaAmount(dir) > 0
                && (this.theGolem.essentiaAmount == 0
                    || (this.theGolem.essentia == null
                        || this.theGolem.essentia.equals(ta.getEssentiaType(dir))
                        || this.theGolem.essentia.equals(ta.getEssentiaType(EnumFacing.UP)))
                    && this.theGolem.essentiaAmount < this.theGolem.getCarryLimit())) {
                Aspect a = ta.getEssentiaType(dir);
                if (a == null) {
                    a = ta.getEssentiaType(EnumFacing.UP);
                }
                int qq = ta.getEssentiaAmount(dir);
                if (te instanceof TileEssentiaReservoir) {
                    qq = ((TileEssentiaReservoir) te).containerContains(a);
                }
                int am = Math.min(qq, this.theGolem.getCarryLimit() - this.theGolem.essentiaAmount);
                this.theGolem.essentia = a;
                int taken = ta.takeEssentia(a, am, dir);
                if (taken > 0) {
                    this.theGolem.essentiaAmount += taken;
                    this.theWorld.playSound(null, this.theGolem.getPosition(),
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("game.neutral.swim")),
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.05f,
                        1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.3f);
                    this.theGolem.updateCarried();
                } else {
                    this.theGolem.essentia = null;
                }
                this.delay = System.currentTimeMillis() + 100L;
            }
        }
    }

    @Override
    public void resetTask() {}
}
