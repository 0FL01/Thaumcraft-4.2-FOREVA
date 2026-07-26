package thaumcraft.rendering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** CI-visible source guards for the TC4 robe model profile and pose contract. */
public class RobeArmorRenderContractTest {
    private static final String MODEL =
            "src/main/java/thaumcraft/client/renderers/models/gear/ModelRobe.java";
    private static final String VOID_ARMOR =
            "src/main/java/thaumcraft/common/items/armor/ItemVoidRobeArmor.java";
    private static final String CULTIST_ARMOR =
            "src/main/java/thaumcraft/common/items/armor/ItemCultistRobeArmor.java";
    private static final String PROFILE_ROUTE =
            "armorSlot == EntityEquipmentSlot.CHEST || armorSlot == EntityEquipmentSlot.FEET ? this.model1 : this.model2";

    @Test
    public void childScaleAndAdultSneakShouldRemainSeparatePosePaths() throws IOException {
        String model = read(MODEL);
        int childStart = model.indexOf("if (this.isChild) {");
        int adultStart = model.indexOf("} else {", childStart);
        assertTrue(childStart >= 0 && adultStart > childStart);
        String childBranch = model.substring(childStart, adultStart);
        assertTrue(childBranch.contains("GlStateManager.scale(1.5f / f6, 1.5f / f6, 1.5f / f6);")
                && childBranch.contains("GlStateManager.scale(1.0f / f6, 1.0f / f6, 1.0f / f6);"));
        assertFalse(childBranch.contains("isSneak"));
        assertTrue(model.substring(adultStart).contains("if (this.isSneak) {")
                && model.substring(adultStart).contains("GlStateManager.translate(0.0f, 0.2f, 0.0f);"));
    }

    @Test
    public void bothArmorFamiliesShouldUseTheTc4GeometryProfiles() throws IOException {
        String voidArmor = read(VOID_ARMOR);
        String cultistArmor = read(CULTIST_ARMOR);

        assertTrue(voidArmor.contains(PROFILE_ROUTE));
        assertTrue(cultistArmor.contains(PROFILE_ROUTE));
        assertFalse(voidArmor.contains("armorType.ordinal()"));
        assertFalse(cultistArmor.contains("armorType.ordinal()"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
