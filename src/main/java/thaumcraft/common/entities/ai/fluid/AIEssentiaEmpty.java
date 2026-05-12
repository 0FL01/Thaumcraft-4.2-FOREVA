package thaumcraft.common.entities.ai.fluid;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.tiles.TileEssentiaReservoir;
import thaumcraft.common.tiles.TileJarFillable;

public class AIEssentiaEmpty extends EntityAIBase {

    private EntityGolemBase theGolem;
    private int jarX;
    private int jarY;
    private int jarZ;
    private World theWorld;

    public AIEssentiaEmpty(EntityGolemBase golem) {
        this.theGolem = golem;
        this.theWorld = golem.world;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos home = this.theGolem.getHomePosition();
        if (!this.theGolem.getNavigator().noPath()
            || this.theGolem.essentia == null
            || this.theGolem.essentiaAmount == 0) {
            return false;
        }
        BlockPos jarloc = GolemHelper.findJarWithRoom(this.theGolem);
        if (jarloc == null) {
            return false;
        }
        if (this.theGolem.getDistanceSq(jarloc.getX() + 0.5, jarloc.getY() + 0.5, jarloc.getZ() + 0.5) > 4.0) {
            return false;
        }
        this.jarX = jarloc.getX();
        this.jarY = jarloc.getY();
        this.jarZ = jarloc.getZ();
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    @Override
    public void startExecuting() {
        TileEntity tile = this.theWorld.getTileEntity(new BlockPos(this.jarX, this.jarY, this.jarZ));
        if (tile == null) return;

        if (tile instanceof TileJarFillable) {
            TileJarFillable jar = (TileJarFillable) tile;
            this.theGolem.essentiaAmount = jar.addToContainer(this.theGolem.essentia, this.theGolem.essentiaAmount);
            if (this.theGolem.essentiaAmount == 0) {
                this.theGolem.essentia = null;
            }
            this.theWorld.playSound(null, this.theGolem.getPosition(),
                net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("game.neutral.swim")),
                net.minecraft.util.SoundCategory.NEUTRAL, 0.2f,
                1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.3f);
            this.theGolem.updateCarried();
            this.theWorld.markBlockRangeForRenderUpdate(this.jarX, this.jarY, this.jarZ, this.jarX, this.jarY, this.jarZ);
            return;
        }

        if (tile instanceof TileEssentiaReservoir) {
            TileEssentiaReservoir trans = (TileEssentiaReservoir) tile;
            if (trans.getSuctionAmount(trans.facing) > 0
                && (trans.getSuctionType(trans.facing) == null
                    || trans.getSuctionType(trans.facing) == this.theGolem.essentia)) {
                int added = trans.addEssentia(this.theGolem.essentia, this.theGolem.essentiaAmount, trans.facing);
                if (added > 0) {
                    this.theGolem.essentiaAmount -= added;
                    if (this.theGolem.essentiaAmount == 0) {
                        this.theGolem.essentia = null;
                    }
                    this.theWorld.playSound(null, this.theGolem.getPosition(),
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("game.neutral.swim")),
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.2f,
                        1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.3f);
                    this.theGolem.updateCarried();
                    this.theWorld.markBlockRangeForRenderUpdate(this.jarX, this.jarY, this.jarZ, this.jarX, this.jarY, this.jarZ);
                }
            }
            return;
        }

        if (tile instanceof IEssentiaTransport) {
            IEssentiaTransport trans = (IEssentiaTransport) tile;
            for (Integer side : GolemHelper.getMarkedSides(this.theGolem, tile, (byte) -1)) {
                EnumFacing dir = EnumFacing.VALUES[side % EnumFacing.VALUES.length];
                if (!trans.canInputFrom(dir)) continue;
                if (trans.getSuctionAmount(dir) <= 0) continue;
                if (trans.getSuctionType(dir) != null && trans.getSuctionType(dir) != this.theGolem.essentia) continue;
                int added = trans.addEssentia(this.theGolem.essentia, this.theGolem.essentiaAmount, dir);
                if (added > 0) {
                    this.theGolem.essentiaAmount -= added;
                    if (this.theGolem.essentiaAmount == 0) {
                        this.theGolem.essentia = null;
                    }
                    this.theWorld.playSound(null, this.theGolem.getPosition(),
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("game.neutral.swim")),
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.2f,
                        1.0f + (this.theWorld.rand.nextFloat() - this.theWorld.rand.nextFloat()) * 0.3f);
                    this.theGolem.updateCarried();
                    this.theWorld.markBlockRangeForRenderUpdate(this.jarX, this.jarY, this.jarZ, this.jarX, this.jarY, this.jarZ);
                    break;
                }
            }
        }
    }

    @Override
    public void resetTask() {}
}
