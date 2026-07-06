package thaumcraft.api.aspects;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;

/**
 * Thaumcraft 6 aspect-registration facade backed by this port's TC4-style API.
 *
 * Keeping this proxy tiny and delegating to ThaumcraftApi preserves the TC6
 * compatibility surface without duplicating the object tag registry logic.
 */
public class AspectEventProxy {

    public void registerObjectTag(ItemStack item, AspectList aspects) {
        ThaumcraftApi.registerObjectTag(item, aspects);
    }

    public void registerObjectTag(ItemStack item, int[] meta, AspectList aspects) {
        ThaumcraftApi.registerObjectTag(item, meta, aspects);
    }

    public void registerObjectTag(String oreDict, AspectList aspects) {
        ThaumcraftApi.registerObjectTag(oreDict, aspects);
    }

    public void registerComplexObjectTag(ItemStack item, AspectList aspects) {
        ThaumcraftApi.registerComplexObjectTag(item, aspects);
    }

    public void registerComplexObjectTag(String oreDict, AspectList aspects) {
        ThaumcraftApi.registerComplexObjectTag(oreDict, aspects);
    }
}
