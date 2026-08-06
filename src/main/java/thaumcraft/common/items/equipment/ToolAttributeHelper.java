package thaumcraft.common.items.equipment;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.ArrayList;
import java.util.List;

final class ToolAttributeHelper {

    private ToolAttributeHelper() {
    }

    static Multimap<String, AttributeModifier> withMainHandDamage(
            Multimap<String, AttributeModifier> inherited, EntityEquipmentSlot slot, double damage) {
        if (slot != EntityEquipmentSlot.MAINHAND) {
            return inherited;
        }
        Multimap<String, AttributeModifier> modifiers = LinkedHashMultimap.create(inherited);
        List<AttributeModifier> damageModifiers = new ArrayList<AttributeModifier>(
                modifiers.removeAll(SharedMonsterAttributes.ATTACK_DAMAGE.getName()));
        if (damageModifiers.size() != 1) {
            throw new IllegalStateException("Expected one inherited attack damage modifier");
        }
        AttributeModifier inheritedDamage = damageModifiers.get(0);
        modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                new AttributeModifier(inheritedDamage.getID(), inheritedDamage.getName(), damage,
                        inheritedDamage.getOperation()));
        return modifiers;
    }
}
