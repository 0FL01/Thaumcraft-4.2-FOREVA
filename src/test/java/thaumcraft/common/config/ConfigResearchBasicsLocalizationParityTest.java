package thaumcraft.common.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigResearchBasicsLocalizationParityTest {

    private static final String LANG_DIR = "src/main/resources/assets/thaumcraft/lang/";
    private static final String[] LOCALIZED_BASICS = {
            "ca_es.lang:26993474859b979b7440dc76dc1ba7216a0a5fd0264003bff3e38a3c88268774",
            "cs_cz.lang:6f9e0074af942bb0573456e964f4dd2c8b6cfc7bdee7f28949a3809d02657ba1",
            "es_uy.lang:65455ff741530cac123c6af23018f63d3e7224f1c0d119412d514f244b767d38",
            "es_ve.lang:65455ff741530cac123c6af23018f63d3e7224f1c0d119412d514f244b767d38",
            "et_ee.lang:0b4a4ddc21de219ad5e0d5a2e5886ff5d93473b3218a7f97534590d9b9471c31",
            "fi_fi.lang:cfd2815467e8c20deaacd7ed93ed87cc2509a16b550d92052ade5282564ebade",
            "it_it.lang:b251945f99ba5e0ebbe42c18b73fd9a53cb92f061dec1eb00aac885ddf27dea0",
            "ja_jp.lang:13a82667406ee65a3b4de77ed190265a2206c3ec13145768a7c4e5d740bacf81",
            "ko_kr.lang:a02df01317a1ab8fb4f3f5509fec321869dda4490ce49bf82fdd0fa4061bb23b",
            "nl_nl.lang:a514c8ceae9f329fec3fa4c0bdd7ebc9d9d2995205b5e884a531d30dfae8aa0a",
            "pt_br.lang:615cd68374fe51b9c28a6e61bb78c6df7fd2418de370680e7d586f67972d0c93",
            "pt_pt.lang:e4257f2c2eb537f931a5a5a83eb27a0488062474d156577090c94fa7daf9119c",
            "sv_se.lang:374081a22373f8712e0d90e111990f7bda1ad5ebe968be487e221d787f3b1b8e"
    };

    private static final Set<String> TC4_ASPECT_KEYS = new HashSet<String>(Arrays.asList((
            "primal unknown aer terra ignis aqua ordo perditio vacuos praecantatio auram vitium lux "
                    + "potentia motus tempestas victus mortuus volatus tenebrae limus herba arbor spiritus "
                    + "humanus bestia exanimis cognitio sensus messis meto metallum perfodio instrumentum "
                    + "telum gelum vitreus sano iter venenum alienis tutamen fames fabrico lucrum pannus "
                    + "machina vinculum permutatio corpus").split(" ")));

    @Test
    public void translatedBasicsSubsetsShouldMatchTc4UnderLowercaseLocaleNames() throws Exception {
        for (String expected : LOCALIZED_BASICS) {
            String[] parts = expected.split(":", 2);
            Path locale = Paths.get(LANG_DIR + parts[0]);
            assertEquals("Unexpected localized BASICS corpus for " + parts[0], parts[1], sha256(Files.readAllBytes(locale)));
        }
    }

    @Test
    public void englishAspectDescriptionsShouldMatchTc4() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(LANG_DIR + "en_us.lang"), StandardCharsets.UTF_8);
        StringBuilder selected = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            int equals = line.indexOf('=');
            if (equals < 0 || !line.startsWith("tc.aspect.")) {
                continue;
            }
            String key = line.substring("tc.aspect.".length(), equals);
            if (TC4_ASPECT_KEYS.contains(key)) {
                selected.append(line).append('\n');
                count++;
            }
        }
        assertEquals(50, count);
        assertEquals("bfe9bcf8f6a5f04dd01bda5609ccfec5a01921ac651b0d12de330611e3b9a025",
                sha256(selected.toString().getBytes(StandardCharsets.UTF_8)));
    }

    private static String sha256(byte[] data) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(data);
        StringBuilder result = new StringBuilder();
        for (byte value : digest) {
            result.append(String.format("%02x", value & 0xff));
        }
        return result.toString();
    }
}
