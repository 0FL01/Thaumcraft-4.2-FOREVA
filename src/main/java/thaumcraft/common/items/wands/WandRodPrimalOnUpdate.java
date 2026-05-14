package thaumcraft.common.items.wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.IWandRodOnUpdate;

import java.util.ArrayList;

public class WandRodPrimalOnUpdate implements IWandRodOnUpdate {

    private final Aspect aspect;
    private final ArrayList<Aspect> primals;

    public WandRodPrimalOnUpdate(Aspect aspect) {
        this.aspect = aspect;
        this.primals = null;
    }

    public WandRodPrimalOnUpdate() {
        this.aspect = null;
        this.primals = Aspect.getPrimalAspects();
    }

    @Override
    public void onUpdate(ItemStack itemstack, EntityPlayer player) {
        if (!(itemstack.getItem() instanceof ItemWandCasting) || player == null) return;
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        if (this.aspect != null) {
            if (player.ticksExisted % 200 == 0 && wand.getVis(itemstack, this.aspect) < wand.getMaxVis(itemstack) / 10) {
                wand.addVis(itemstack, this.aspect, 1, true);
            }
            return;
        }
        if (player.ticksExisted % 50 == 0 && this.primals != null) {
            ArrayList<Aspect> candidates = new ArrayList<>();
            for (Aspect primal : this.primals) {
                if (wand.getVis(itemstack, primal) < wand.getMaxVis(itemstack) / 10) {
                    candidates.add(primal);
                }
            }
            if (!candidates.isEmpty()) {
                wand.addVis(itemstack, candidates.get(player.world.rand.nextInt(candidates.size())), 1, true);
            }
        }
    }
}
