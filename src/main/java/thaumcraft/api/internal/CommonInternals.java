package thaumcraft.api.internal;

import java.util.ArrayList;
import thaumcraft.api.ThaumcraftApi;

/**
 * TC6 internal API compatibility surface backed by the canonical TC4 registries.
 */
public class CommonInternals {

    public static ArrayList<ThaumcraftApi.EntityTags> scanEntities = ThaumcraftApi.scanEntities;
}
