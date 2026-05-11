package thaumcraft.common.entities.golems;

public enum EnumGolemType {
    WOOD(20, 6, 1, 0, 0.15, false, 0, 200, 7),
    IRON(30, 10, 2, 2, 0.2, false, 1, 100, 12),
    CLAY(15, 6, 1, 0, 0.2, false, 1, 150, 6),
    STRAW(12, 4, 0, -1, 0.25, true, 0, 100, 5),
    FLESH(25, 8, 2, 0, 0.2, false, 1, 50, 10),
    TALLOW(10, 4, 0, 0, 0.3, true, 0, 50, 8),
    STONE(40, 12, 3, 3, 0.15, false, 1, 200, 15),
    THAUMIUM(50, 10, 3, 2, 0.25, false, 2, 50, 20);

    public final int health;
    public final int carry;
    public final int strength;
    public final int armor;
    public final double speed;
    public final boolean fireResist;
    public final int upgrades;
    public final int regenDelay;
    public final int visCost;

    EnumGolemType(int health, int carry, int strength, int armor, double speed, boolean fireResist, int upgrades, int regenDelay, int visCost) {
        this.health = health;
        this.carry = carry;
        this.strength = strength;
        this.armor = armor;
        this.speed = speed;
        this.fireResist = fireResist;
        this.upgrades = upgrades;
        this.regenDelay = regenDelay;
        this.visCost = visCost;
    }

    public static EnumGolemType getType(int id) {
        return values()[id % values().length];
    }
}
