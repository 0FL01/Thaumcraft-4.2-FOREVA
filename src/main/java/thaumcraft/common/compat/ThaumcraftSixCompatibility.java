package thaumcraft.common.compat;

import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.aspects.AspectEventProxy;
import thaumcraft.api.aspects.AspectRegistryEvent;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

/**
 * Central lifecycle hooks for the Thaumcraft 6 binary compatibility shims.
 *
 * <p>The shim classes intentionally live in the TC6 API/common package names
 * that addons link against. This class keeps their initialization visible in
 * one place, while the package-compatible classes keep the exact descriptors.</p>
 */
public final class ThaumcraftSixCompatibility {

    private ThaumcraftSixCompatibility() {
    }

    public static void initBlockAliases() {
        BlocksTC.init();
    }

    public static void initItemAliases() {
        ItemsTC.init();
    }

    public static void postAspectRegistryEvent() {
        // TC6 addons subscribe during preInit and expect this event after core
        // aspect tags exist, but before research uses addon-provided tags.
        AspectRegistryEvent aspectRegistryEvent = new AspectRegistryEvent();
        aspectRegistryEvent.register = new AspectEventProxy();
        MinecraftForge.EVENT_BUS.post(aspectRegistryEvent);
    }
}
