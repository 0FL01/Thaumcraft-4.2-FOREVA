package thaumcraft.api.entities;

/** Stable numeric identifiers used by {@link IGolemInfo}. */
public final class GolemIds {
    public static final byte CORE_NONE = -1;
    public static final byte CORE_FILL = 0;
    public static final byte CORE_EMPTY = 1;
    public static final byte CORE_GATHER = 2;
    public static final byte CORE_HARVEST = 3;
    public static final byte CORE_GUARD = 4;
    public static final byte CORE_DECANTING = 5;
    public static final byte CORE_ALCHEMY = 6;
    public static final byte CORE_CHOP = 7;
    public static final byte CORE_USE = 8;
    public static final byte CORE_BUTCHER = 9;
    public static final byte CORE_SORTING = 10;
    public static final byte CORE_FISHING = 11;

    public static final int TYPE_STRAW = 0;
    public static final int TYPE_WOOD = 1;
    public static final int TYPE_TALLOW = 2;
    public static final int TYPE_CLAY = 3;
    public static final int TYPE_FLESH = 4;
    public static final int TYPE_STONE = 5;
    public static final int TYPE_IRON = 6;
    public static final int TYPE_THAUMIUM = 7;

    public static final int UPGRADE_AIR = 0;
    public static final int UPGRADE_EARTH = 1;
    public static final int UPGRADE_FIRE = 2;
    public static final int UPGRADE_WATER = 3;
    public static final int UPGRADE_ORDER = 4;
    public static final int UPGRADE_ENTROPY = 5;

    private GolemIds() {
    }
}
