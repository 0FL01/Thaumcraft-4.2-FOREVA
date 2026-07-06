package thaumcraft.api.aspects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.apache.commons.lang3.text.WordUtils;

public class Aspect {
    String tag;
    Aspect[] components;
    int color;
    private String chatcolor;
    ResourceLocation image;
    int blend;
    public static LinkedHashMap<String, Aspect> aspects = new AspectRegistryMap();
    public static final Aspect AIR = new Aspect("aer", 0xFFFF7E, "e", 1);
    public static final Aspect EARTH = new Aspect("terra", 5685248, "2", 1);
    public static final Aspect FIRE = new Aspect("ignis", 16734721, "c", 1);
    public static final Aspect WATER = new Aspect("aqua", 3986684, "3", 1);
    public static final Aspect ORDER = new Aspect("ordo", 14013676, "7", 1);
    public static final Aspect ENTROPY = new Aspect("perditio", 0x404040, "8", 771);
    public static final Aspect VOID = new Aspect("vacuos", 0x888888, new Aspect[]{AIR, ENTROPY}, 771);
    public static final Aspect LIGHT = new Aspect("lux", 0xFFF663, new Aspect[]{AIR, FIRE});
    public static final Aspect WEATHER = new Aspect("tempestas", 0xFFFFFF, new Aspect[]{AIR, WATER});
    public static final Aspect MOTION = new Aspect("motus", 13487348, new Aspect[]{AIR, ORDER});
    public static final Aspect COLD = new Aspect("gelum", 0xE1FFFF, new Aspect[]{FIRE, ENTROPY});
    public static final Aspect CRYSTAL = new Aspect("vitreus", 0x80FFFF, new Aspect[]{EARTH, ORDER});
    public static final Aspect LIFE = new Aspect("victus", 14548997, new Aspect[]{WATER, EARTH});
    public static final Aspect POISON = new Aspect("venenum", 9039872, new Aspect[]{WATER, ENTROPY});
    public static final Aspect ENERGY = new Aspect("potentia", 0xC0FFFF, new Aspect[]{ORDER, FIRE});
    public static final Aspect EXCHANGE = new Aspect("permutatio", 5735255, new Aspect[]{ENTROPY, ORDER});
    public static final Aspect METAL = new Aspect("metallum", 11908557, new Aspect[]{EARTH, CRYSTAL});
    public static final Aspect DEATH = new Aspect("mortuus", 0x887788, new Aspect[]{LIFE, ENTROPY});
    public static final Aspect FLIGHT = new Aspect("volatus", 0xE7E7D7, new Aspect[]{AIR, MOTION});
    public static final Aspect DARKNESS = new Aspect("tenebrae", 0x222222, new Aspect[]{VOID, LIGHT});
    public static final Aspect SOUL = new Aspect("spiritus", 0xEBEBFB, new Aspect[]{LIFE, DEATH});
    public static final Aspect HEAL = new Aspect("sano", 16723764, new Aspect[]{LIFE, ORDER});
    public static final Aspect TRAVEL = new Aspect("iter", 14702683, new Aspect[]{MOTION, EARTH});
    public static final Aspect ELDRITCH = new Aspect("alienis", 0x805080, new Aspect[]{VOID, DARKNESS});
    public static final Aspect MAGIC = new Aspect("praecantatio", 9896128, new Aspect[]{VOID, ENERGY});
    public static final Aspect AURA = new Aspect("auram", 0xFFC0FF, new Aspect[]{MAGIC, AIR});
    public static final Aspect TAINT = new Aspect("vitium", 0x800080, new Aspect[]{MAGIC, ENTROPY});
    public static final Aspect FLUX = TAINT;
    public static final Aspect SLIME = new Aspect("limus", 129024, new Aspect[]{LIFE, WATER});
    public static final Aspect PLANT = new Aspect("herba", 109568, new Aspect[]{LIFE, EARTH});
    public static final Aspect TREE = new Aspect("arbor", 8873265, new Aspect[]{AIR, PLANT});
    public static final Aspect BEAST = new Aspect("bestia", 10445833, new Aspect[]{MOTION, LIFE});
    public static final Aspect FLESH = new Aspect("corpus", 15615885, new Aspect[]{DEATH, BEAST});
    public static final Aspect UNDEAD = new Aspect("exanimis", 3817472, new Aspect[]{MOTION, DEATH});
    public static final Aspect MIND = new Aspect("cognitio", 16761523, new Aspect[]{FIRE, SOUL});
    public static final Aspect SENSES = new Aspect("sensus", 1038847, new Aspect[]{AIR, SOUL});
    public static final Aspect MAN = new Aspect("humanus", 16766912, new Aspect[]{BEAST, MIND});
    public static final Aspect CROP = new Aspect("messis", 14791537, new Aspect[]{PLANT, MAN});
    public static final Aspect MINE = new Aspect("perfodio", 14471896, new Aspect[]{MAN, EARTH});
    public static final Aspect TOOL = new Aspect("instrumentum", 0x4040EE, new Aspect[]{MAN, ORDER});
    public static final Aspect HARVEST = new Aspect("meto", 15641986, new Aspect[]{CROP, TOOL});
    public static final Aspect WEAPON = new Aspect("telum", 0xC05050, new Aspect[]{TOOL, FIRE});
    public static final Aspect ARMOR = new Aspect("tutamen", 49344, new Aspect[]{TOOL, EARTH});
    public static final Aspect HUNGER = new Aspect("fames", 10093317, new Aspect[]{LIFE, VOID});
    public static final Aspect GREED = new Aspect("lucrum", 15121988, new Aspect[]{MAN, HUNGER});
    public static final Aspect CRAFT = new Aspect("fabrico", 8428928, new Aspect[]{MAN, TOOL});
    public static final Aspect CLOTH = new Aspect("pannus", 15395522, new Aspect[]{TOOL, BEAST});
    public static final Aspect MECHANISM = new Aspect("machina", 0x8080A0, new Aspect[]{MOTION, TOOL});
    public static final Aspect TRAP = new Aspect("vinculum", 10125440, new Aspect[]{MOTION, ENTROPY});

    // TC6 compatibility: 1.12 addons may link these newer aspect constants
    // directly. Keep the gameplay registry strictly TC4.2, but expose TC6
    // names as aliases so optional bridges do not fail with NoSuchFieldError.
    @Deprecated
    public static final Aspect ALCHEMY = MAGIC;
    @Deprecated
    public static final Aspect AVERSION = WEAPON;
    @Deprecated
    public static final Aspect PROTECT = ARMOR;
    @Deprecated
    public static final Aspect DESIRE = GREED;

    public Aspect(String tag, int color, Aspect[] components, ResourceLocation image, int blend) {
        if (aspects.containsKey(tag)) {
            throw new IllegalArgumentException(tag + " already registered!");
        }
        this.tag = tag;
        this.components = components;
        this.color = color;
        this.image = image;
        this.blend = blend;
        aspects.put(tag, this);
    }

    public Aspect(String tag, int color, Aspect[] components) {
        this(tag, color, components, new ResourceLocation("thaumcraft", "textures/aspects/" + tag.toLowerCase() + ".png"), 1);
    }

    public Aspect(String tag, int color, Aspect[] components, int blend) {
        this(tag, color, components, new ResourceLocation("thaumcraft", "textures/aspects/" + tag.toLowerCase() + ".png"), blend);
    }

    public Aspect(String tag, int color, String chatcolor, int blend) {
        this(tag, color, (Aspect[])null, blend);
        this.setChatcolor(chatcolor);
    }

    public int getColor() {
        return this.color;
    }

    public String getName() {
        return WordUtils.capitalizeFully((String)this.tag);
    }

    public String getLocalizedDescription() {
        String description = I18n.translateToLocal((String)("tc.aspect." + this.tag));
        if (description.equals("tc.aspect." + this.tag)) {
            // The 1.12 asset corpus keeps the human-readable aspect descriptions under tc.aspect.help.*.
            String helpDescription = I18n.translateToLocal((String)("tc.aspect.help." + this.tag));
            if (!helpDescription.equals("tc.aspect.help." + this.tag)) {
                description = helpDescription;
            }
        }
        return description;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Aspect[] getComponents() {
        return this.components;
    }

    public void setComponents(Aspect[] components) {
        this.components = components;
    }

    public ResourceLocation getImage() {
        return this.image;
    }

    public static Aspect getAspect(String tag) {
        Aspect aspect = aspects.get(tag);
        return aspect != null ? aspect : getLegacyAspect(tag);
    }

    private static Aspect getLegacyAspect(Object tag) {
        if (!(tag instanceof String)) {
            return null;
        }
        switch ((String) tag) {
            case "alkimia":
                return MAGIC;
            case "aversio":
                return WEAPON;
            case "praemunio":
                return ARMOR;
            case "desiderium":
                return GREED;
            default:
                return null;
        }
    }

    private static final class AspectRegistryMap extends LinkedHashMap<String, Aspect> {
        @Override
        public Aspect get(Object key) {
            Aspect aspect = super.get(key);
            return aspect != null ? aspect : getLegacyAspect(key);
        }

        @Override
        public boolean containsKey(Object key) {
            return super.containsKey(key) || getLegacyAspect(key) != null;
        }
    }

    public int getBlend() {
        return this.blend;
    }

    public void setBlend(int blend) {
        this.blend = blend;
    }

    public boolean isPrimal() {
        return this.getComponents() == null || this.getComponents().length != 2;
    }

    public static ArrayList<Aspect> getPrimalAspects() {
        ArrayList<Aspect> primals = new ArrayList<Aspect>();
        Collection<Aspect> pa = aspects.values();
        for (Aspect aspect : pa) {
            if (!aspect.isPrimal()) continue;
            primals.add(aspect);
        }
        return primals;
    }

    public static ArrayList<Aspect> getCompoundAspects() {
        ArrayList<Aspect> compounds = new ArrayList<Aspect>();
        Collection<Aspect> pa = aspects.values();
        for (Aspect aspect : pa) {
            if (aspect.isPrimal()) continue;
            compounds.add(aspect);
        }
        return compounds;
    }

    public String getChatcolor() {
        return this.chatcolor;
    }

    public void setChatcolor(String chatcolor) {
        this.chatcolor = chatcolor;
    }
}
