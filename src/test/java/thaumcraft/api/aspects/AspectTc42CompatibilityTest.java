package thaumcraft.api.aspects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AspectTc42CompatibilityTest {

    private static final String[] TC6_EXTRA_TAGS = new String[]{
            "alkimia", "aversio", "praemunio", "desiderium"
    };

    @Test
    public void registryMustRemainStrictlyThaumcraft42() {
        Set<String> tags = registeredTags();
        Set<String> iteratedKeys = iteratedKeys();

        assertEquals("TC4.2 exposes exactly 48 gameplay aspects", 48, Aspect.aspects.size());
        assertEquals("Aspect tags should be unique", Aspect.aspects.size(), tags.size());
        for (String extra : TC6_EXTRA_TAGS) {
            assertFalse("TC6 tag must not be an iterated registry key: " + extra, iteratedKeys.contains(extra));
            assertFalse("TC6 tag must not be a registered gameplay aspect: " + extra, tags.contains(extra));
        }

        assertComponents(Aspect.WEAPON, Aspect.TOOL, Aspect.FIRE);
        assertComponents(Aspect.ARMOR, Aspect.TOOL, Aspect.EARTH);
    }

    @Test
    public void tc6AspectApiNamesMustAliasCanonicalTc42Aspects() {
        assertSame(Aspect.MAGIC, Aspect.ALCHEMY);
        assertSame(Aspect.WEAPON, Aspect.AVERSION);
        assertSame(Aspect.ARMOR, Aspect.PROTECT);
        assertSame(Aspect.GREED, Aspect.DESIRE);

        assertSame(Aspect.MAGIC, Aspect.getAspect("alkimia"));
        assertSame(Aspect.WEAPON, Aspect.getAspect("aversio"));
        assertSame(Aspect.ARMOR, Aspect.getAspect("praemunio"));
        assertSame(Aspect.GREED, Aspect.getAspect("desiderium"));

        assertSame("Public registry lookup should also bridge legacy TC6 tags", Aspect.MAGIC, Aspect.aspects.get("alkimia"));
        assertTrue("Public registry containsKey should reserve legacy TC6 tags", Aspect.aspects.containsKey("alkimia"));
        assertFalse("Legacy lookup must not add TC6 tags to iteration", iteratedKeys().contains("alkimia"));
        assertEquals("Legacy lookup must not change the visible registry size", 48, Aspect.aspects.values().size());
    }

    @Test
    public void legacyAspectNbtMustReadAsCanonicalAndSkipUnknown() {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        list.appendTag(aspectTag("alkimia", 4));
        list.appendTag(aspectTag("unknown_future_aspect", 99));
        root.setTag("Aspects", list);

        AspectList aspects = new AspectList();
        aspects.readFromNBT(root);

        assertEquals(1, aspects.size());
        assertEquals(4, aspects.getAmount(Aspect.MAGIC));
        assertEquals(0, aspects.getAmount(null));
    }

    @Test
    public void aspectAssetsMustMatchFortyEightTc42Aspects() throws IOException {
        Set<String> tags = registeredTags();

        Path textureDir = Paths.get("src/main/resources/assets/thaumcraft/textures/aspects");
        Set<String> icons;
        try (Stream<Path> paths = Files.list(textureDir)) {
            icons = paths
                    .filter(path -> path.getFileName().toString().endsWith(".png"))
                    .map(path -> path.getFileName().toString().replaceFirst("\\.png$", ""))
                    .filter(name -> !name.startsWith("_"))
                    .collect(Collectors.toSet());
        }
        assertEquals(48, icons.size());
        assertTrue(icons.containsAll(tags));

        String lang = new String(Files.readAllBytes(Paths.get("src/main/resources/assets/thaumcraft/lang/en_us.lang")), StandardCharsets.UTF_8);
        Set<String> helpKeys = new HashSet<>();
        Matcher matcher = Pattern.compile("^tc\\.aspect\\.help\\.([a-z_]+)=", Pattern.MULTILINE).matcher(lang);
        while (matcher.find()) {
            helpKeys.add(matcher.group(1));
        }
        assertEquals(48, helpKeys.size());
        assertTrue(helpKeys.containsAll(tags));

        for (String extra : TC6_EXTRA_TAGS) {
            assertFalse(icons.contains(extra));
            assertFalse(helpKeys.contains(extra));
        }
    }

    private static Set<String> registeredTags() {
        Set<String> tags = new HashSet<>();
        for (Aspect aspect : Aspect.aspects.values()) {
            tags.add(aspect.getTag());
        }
        return tags;
    }

    private static Set<String> iteratedKeys() {
        return new HashSet<>(Aspect.aspects.keySet());
    }

    private static void assertComponents(Aspect aspect, Aspect first, Aspect second) {
        Aspect[] components = aspect.getComponents();
        assertEquals(2, components.length);
        assertSame(first, components[0]);
        assertSame(second, components[1]);
    }

    private static NBTTagCompound aspectTag(String key, int amount) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("key", key);
        tag.setInteger("amount", amount);
        return tag;
    }
}
