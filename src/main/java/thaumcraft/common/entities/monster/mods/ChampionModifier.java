package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;

import java.util.HashMap;
import java.util.Map;

public class ChampionModifier {
    public static Map<Integer, IChampionModifierEffect> modifiers = new HashMap<>();

    public static void init() {
        // TODO: register champion modifiers
    }

    public static void applyModifiers(EntityLivingBase entity, int tier) {
        // TODO
    }
}
