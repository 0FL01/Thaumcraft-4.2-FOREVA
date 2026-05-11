package thaumcraft.api.wands;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.apache.logging.log4j.LogManager;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class FocusUpgradeType {
    public static FocusUpgradeType[] types = new FocusUpgradeType[20];
    public short id;
    public ResourceLocation icon;
    public String name;
    public String text;
    public AspectList aspects;
    public static FocusUpgradeType potency = new FocusUpgradeType(0, new ResourceLocation("thaumcraft", "textures/foci/potency.png"), "focus.upgrade.potency.name", "focus.upgrade.potency.text", new AspectList().add(Aspect.WEAPON, 1));
    public static FocusUpgradeType frugal = new FocusUpgradeType(1, new ResourceLocation("thaumcraft", "textures/foci/frugal.png"), "focus.upgrade.frugal.name", "focus.upgrade.frugal.text", new AspectList().add(Aspect.HUNGER, 1));
    public static FocusUpgradeType treasure = new FocusUpgradeType(2, new ResourceLocation("thaumcraft", "textures/foci/treasure.png"), "focus.upgrade.treasure.name", "focus.upgrade.treasure.text", new AspectList().add(Aspect.GREED, 1));
    public static FocusUpgradeType enlarge = new FocusUpgradeType(3, new ResourceLocation("thaumcraft", "textures/foci/enlarge.png"), "focus.upgrade.enlarge.name", "focus.upgrade.enlarge.text", new AspectList().add(Aspect.TRAVEL, 1));
    public static FocusUpgradeType alchemistsfire = new FocusUpgradeType(4, new ResourceLocation("thaumcraft", "textures/foci/alchemistsfire.png"), "focus.upgrade.alchemistsfire.name", "focus.upgrade.alchemistsfire.text", new AspectList().add(Aspect.ENERGY, 1).add(Aspect.SLIME, 1));
    public static FocusUpgradeType alchemistsfrost = new FocusUpgradeType(5, new ResourceLocation("thaumcraft", "textures/foci/alchemistsfrost.png"), "focus.upgrade.alchemistsfrost.name", "focus.upgrade.alchemistsfrost.text", new AspectList().add(Aspect.COLD, 1).add(Aspect.TRAP, 1));
    public static FocusUpgradeType architect = new FocusUpgradeType(6, new ResourceLocation("thaumcraft", "textures/foci/architect.png"), "focus.upgrade.architect.name", "focus.upgrade.architect.text", new AspectList().add(Aspect.CRAFT, 1));
    public static FocusUpgradeType extend = new FocusUpgradeType(7, new ResourceLocation("thaumcraft", "textures/foci/extend.png"), "focus.upgrade.extend.name", "focus.upgrade.extend.text", new AspectList().add(Aspect.EXCHANGE, 1));
    public static FocusUpgradeType silktouch = new FocusUpgradeType(8, new ResourceLocation("thaumcraft", "textures/foci/silktouch.png"), "focus.upgrade.silktouch.name", "focus.upgrade.silktouch.text", new AspectList().add(Aspect.GREED, 1));

    public FocusUpgradeType(int id, ResourceLocation icon, String name, String text, AspectList aspects) {
        this.id = (short)id;
        this.icon = icon;
        this.name = name;
        this.text = text;
        this.aspects = aspects;
        if (id < types.length && types[id] != null) {
            LogManager.getLogger((String)"THAUMCRAFT").fatal("Focus Upgrade id " + id + " already occupied. Ignoring.");
            return;
        }
        if (id >= types.length) {
            FocusUpgradeType[] temp = new FocusUpgradeType[id + 1];
            System.arraycopy(types, 0, temp, 0, types.length);
            types = temp;
        }
        FocusUpgradeType.types[id] = this;
    }

    public String getLocalizedName() {
        return I18n.translateToLocal((String)this.name);
    }

    public String getLocalizedText() {
        return I18n.translateToLocal((String)this.text);
    }

    public boolean equals(Object obj) {
        if (obj instanceof FocusUpgradeType) {
            return this.id == ((FocusUpgradeType)obj).id;
        }
        return false;
    }
}

