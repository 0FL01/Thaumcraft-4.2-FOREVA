package thaumcraft.api.internal;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

/**
 * TC6 internal API compatibility surface backed by the canonical TC4 registries.
 */
public class CommonInternals {

    public static ArrayList<ThaumcraftApi.EntityTags> scanEntities = ThaumcraftApi.scanEntities;
    private static final ThreadLocal<ItemStack> generatedStack = new ThreadLocal<>();
    public static ConcurrentHashMap<Integer, AspectList> objectTags = new ProjectedObjectTagMap();

    public static int generateUniqueItemstackId(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        generatedStack.set(copy);
        return copy.serializeNBT().toString().hashCode();
    }

    private static final class ProjectedObjectTagMap extends ConcurrentHashMap<Integer, AspectList> {
        @Override
        public AspectList put(Integer key, AspectList aspects) {
            ItemStack stack = generatedStack.get();
            generatedStack.remove();
            if (stack != null) {
                ThaumcraftApi.registerObjectTag(stack, aspects);
            }
            return null;
        }
    }
}
