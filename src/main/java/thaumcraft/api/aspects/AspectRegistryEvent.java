package thaumcraft.api.aspects;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Thaumcraft 6 compatibility event.
 *
 * Fossils and Archeology 8.0.6 compiles its optional Thaumcraft bridge against
 * this class and subscribes to it during preInit. Without this shim the bridge
 * fails classloading before it can register any Fossils aspect tags.
 */
public class AspectRegistryEvent extends Event {
    public AspectEventProxy register;
}
