package thaumcraft.common.entities.golems;

public enum EnumGolemType {
    STRAW(10, 1, 0, 0, 0.38, false, 1, 75, 0),
    WOOD(20, 4, 1, 6, 0.35, false, 1, 75, 1),
    TALLOW(20, 8, 2, 9, 0.33, false, 2, 75, 2),
    CLAY(25, 8, 2, 9, 0.33, true, 1, 100, 2),
    FLESH(15, 4, 1, 6, 0.35, false, 2, 40, 1),
    STONE(30, 16, 3, 12, 0.32, true, 1, 100, 3),
    IRON(35, 32, 4, 15, 0.31, true, 1, 125, 4),
    THAUMIUM(40, 32, 4, 15, 0.32, true, 2, 100, 4);

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
        return values()[Math.floorMod(id, values().length)];
    }
}
