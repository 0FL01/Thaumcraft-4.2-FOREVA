package thaumcraft.api.aspects;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Thaumcraft 6 compatibility event.
 *
 * Addons compiled against the TC6 API subscribe to this event during preInit.
 * Posting it after core aspect tags are ready lets them attach their object and
 * entity tags without linking directly to this port's TC4-style registries.
 */
public class AspectRegistryEvent extends Event {
    public AspectEventProxy register;
}
