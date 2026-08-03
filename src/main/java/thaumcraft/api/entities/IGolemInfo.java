package thaumcraft.api.entities;

/**
 * Read-only information exposed by a Thaumcraft golem.
 *
 * <p>Core, golem type, and upgrade identifiers use the stable numeric values
 * declared in {@link GolemIds}. Core, type, and upgrade values are suitable for
 * client-side display. Advanced status is an immutable spawn snapshot.</p>
 */
public interface IGolemInfo {
    /** Returns the installed core ID, or {@link GolemIds#CORE_NONE}. */
    byte getCore();

    /** Returns one of the {@code TYPE_*} IDs declared in {@link GolemIds}. */
    int getGolemTypeId();

    /** Returns whether this is an advanced golem. */
    boolean isAdvancedGolem();

    /** Returns the number of installed upgrades with the supplied stable ID. */
    int getUpgradeAmount(int upgradeId);
}
