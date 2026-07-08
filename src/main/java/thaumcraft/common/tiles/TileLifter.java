package thaumcraft.common.tiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

import java.util.List;

public class TileLifter extends TileThaumcraft implements ITickable {
    private int counter = 0;
    public int rangeAbove = 0;
    public boolean requiresUpdate = true;
    public boolean lastPowerState = false;

    @Override
    public void update() {
        if (this.world == null) {
            return;
        }

        this.counter++;
        if (this.requiresUpdate || this.counter % 100 == 0) {
            this.lastPowerState = this.gettingPower();
            this.requiresUpdate = false;

            int max = 10;
            int count = 1;
            while (this.world.getBlockState(this.pos.down(count)).getBlock() == ConfigBlocks.blockLifter
                    && !this.world.isBlockPowered(this.pos.down(count))) {
                count++;
                max += 10;
            }

            this.rangeAbove = 0;
            while (this.rangeAbove < max
                    && !this.world.getBlockState(this.pos.up(1 + this.rangeAbove)).isOpaqueCube()) {
                this.rangeAbove++;
            }
        }

        if (this.rangeAbove > 0 && !this.gettingPower()) {
            AxisAlignedBB bb = new AxisAlignedBB(
                    this.pos.getX(),
                    this.pos.getY() + 1,
                    this.pos.getZ(),
                    this.pos.getX() + 1,
                    this.pos.getY() + 1 + this.rangeAbove,
                    this.pos.getZ() + 1);
            List<Entity> targets = this.world.getEntitiesWithinAABB(Entity.class, bb);
            if (!targets.isEmpty()) {
                for (Entity e : targets) {
                    if (!(e instanceof EntityItem) && !e.canBeCollidedWith() && !(e instanceof AbstractHorse)) {
                        continue;
                    }
                    if (Thaumcraft.proxy.isShiftKeyDown()) {
                        if (e.motionY < 0.0D) {
                            e.motionY *= 0.9D;
                        }
                    } else if (e.motionY < 0.35D) {
                        e.motionY += 0.1D;
                    }
                    e.fallDistance = 0.0F;
                }
            }
        }
    }

    public boolean gettingPower() {
        return this.world != null
                && (this.world.isBlockPowered(this.pos) || this.world.isBlockPowered(this.pos.up()));
    }
}
