package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

import java.util.ArrayList;

public class AIEmptyDrop extends EntityAIBase {
    private EntityGolemBase theGolem;
    int count = 0;

    public AIEmptyDrop(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        BlockPos home = theGolem.getHomePosition();
        ArrayList<Byte> matchingColors = theGolem.getColorsMatching(theGolem.getCarried());
        for (byte color : matchingColors) {
            ArrayList<BlockPos> mc = GolemHelper.getMarkedBlocksAdjacentToGolem(theGolem.world, theGolem, color);
            for (BlockPos cc : mc) {
                if (cc.equals(home)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && this.shouldExecute();
    }

    @Override
    public void resetTask() {}

    @Override
    public void updateTask() {
        --this.count;
        super.updateTask();
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        BlockPos home = theGolem.getHomePosition();
        ArrayList<Byte> matchingColors = theGolem.getColorsMatching(theGolem.getCarried());
        for (byte color : matchingColors) {
            ArrayList<BlockPos> mc = GolemHelper.getMarkedBlocksAdjacentToGolem(theGolem.world, theGolem, color);
            for (BlockPos cc : mc) {
                if (cc.equals(home)) continue;
                if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) break;
                EntityItem item = new EntityItem(theGolem.world, theGolem.posX,
                    theGolem.posY + (double)(theGolem.height / 2.0F), theGolem.posZ,
                    theGolem.getCarried().copy());
                if (item != null) {
                    double distance = theGolem.getDistance(cc.getX() + 0.5, cc.getY() + 0.5, cc.getZ() + 0.5);
                    item.motionX = ((double)cc.getX() + 0.5 - theGolem.posX) * (distance / 3.0);
                    item.motionY = 0.1 + ((double)cc.getY() + 0.5 - (theGolem.posY + (double)(theGolem.height / 2.0F))) * (distance / 3.0);
                    item.motionZ = ((double)cc.getZ() + 0.5 - theGolem.posZ) * (distance / 3.0);
                    item.setPickupDelay(10);
                    theGolem.world.spawnEntity(item);
                    theGolem.setCarried(ItemStack.EMPTY);
                    theGolem.startActionTimer();
                }
                break;
            }
        }
        theGolem.updateCarried();
    }
}
