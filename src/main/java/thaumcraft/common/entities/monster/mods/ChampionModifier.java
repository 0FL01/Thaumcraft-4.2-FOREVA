package thaumcraft.common.entities.monster.mods;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class ChampionModifier {
    public String name = "";
    public String displayName = "";
    public int id = 0;
    public int type = 0;
    public IChampionModifierEffect effect = null;
    public net.minecraft.entity.ai.attributes.AttributeModifier attributeMod;
    public static ChampionModifier[] mods = new ChampionModifier[]{
        new ChampionModifier(0, "bold", "Bold", -1, new ChampionModBold(), java.util.UUID.fromString("40289aa1-907f-4ac6-ad79-e6681efe2cbc")),
        new ChampionModifier(1, "spine", "Spined", 2, new ChampionModSpined(), java.util.UUID.fromString("365eead5-3f15-42a8-9e68-36100faef945")),
        new ChampionModifier(2, "armor", "Armored", 2, new ChampionModArmored(), java.util.UUID.fromString("4e23758d-348e-42a8-8de6-08ae0a59033c")),
        new ChampionModifier(3, "mighty", "Mighty", -1, new ChampionModMighty(), java.util.UUID.fromString("6d2ffe79-f034-4a06-b288-e1916c21e385")),
        new ChampionModifier(4, "grim", "Grim", 1, new ChampionModGrim(), java.util.UUID.fromString("0f23321e-f921-4246-90b8-21ef202de224")),
        new ChampionModifier(5, "warded", "Warded", 0, new ChampionModWarded(), java.util.UUID.fromString("b622c4d8-abc6-4db7-b3ee-5cf71b8e5286")),
        new ChampionModifier(6, "warp", "Warped", 1, new ChampionModWarp(), java.util.UUID.fromString("107da049-af7a-4409-989a-6de23c8fe036")),
        new ChampionModifier(7, "undying", "Undying", 0, new ChampionModUndying(), java.util.UUID.fromString("cb9484d3-6255-4893-a4f2-3ecc375692ee")),
        new ChampionModifier(8, "fiery", "Fiery", 1, new ChampionModFire(), java.util.UUID.fromString("6b567fdf-9245-48f5-8314-f93fe5db1427")),
        new ChampionModifier(9, "sickly", "Sickly", 1, new ChampionModSickly(), java.util.UUID.fromString("b5718868-9ab0-424c-af1f-8b35e836b46e")),
        new ChampionModifier(10, "venomous", "Venomous", 1, new ChampionModPoison(), java.util.UUID.fromString("ab9a132e-c619-4c0a-a103-10cbbcfba1a2")),
        new ChampionModifier(11, "vampiric", "Vampiric", 1, new ChampionModVampire(), java.util.UUID.fromString("3412251e-af81-4c3c-93ba-2e1c33b049ea")),
        new ChampionModifier(12, "infested", "Infested", 2, new ChampionModInfested(), java.util.UUID.fromString("9c577fbe-ddbc-4ea2-a661-770ea775f43b"))
    };
    public static net.minecraft.entity.ai.attributes.AttributeModifier ATTRIBUTE_MOD_NONE = new net.minecraft.entity.ai.attributes.AttributeModifier(
        java.util.UUID.fromString("1e645a3d-9115-4807-a61c-705172839f87"), "normal", 1.0, 0);

    public ChampionModifier(int id, String name, String displayName, int type, IChampionModifierEffect effect, java.util.UUID iud) {
        this.name = name;
        this.displayName = displayName;
        this.id = id;
        this.type = type;
        this.effect = effect;
        this.attributeMod = new net.minecraft.entity.ai.attributes.AttributeModifier(iud, name, (double)(id + 2), 0);
    }

    public String getModNameLocalized() {
        String key = "champion.mod." + this.name;
        String translated = net.minecraft.util.text.translation.I18n.translateToLocal(key);
        return key.equals(translated) ? this.displayName : translated;
    }
}
