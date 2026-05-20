package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityGolemClientFeedbackStaticGuardTest {

    @Test
    public void golemAndTrunkClientFeedbackShouldKeepReferenceStatusBranches() throws IOException {
        String golem = read("src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java");
        String trunk = read("src/main/java/thaumcraft/common/entities/golems/EntityTravelingTrunk.java");

        assertTrue("EntityGolemBase status 5 must keep healing timer and client-side max-health refresh based on type/decoration",
                golem.contains("else if (id == 5) {")
                        && golem.contains("this.healing = 5;")
                        && golem.contains("this.refreshClientHealingState();")
                        && golem.contains("private void refreshClientHealingState()")
                        && golem.contains("this.getGolemDecoration().contains(\"H\") ? 5 : 0;")
                        && golem.contains("this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH)")
                        && golem.contains(".setBaseValue(this.getGolemType().health + bonus);"));

        assertTrue("EntityTravelingTrunk status 18 must keep lid kick plus positive heart feedback particles",
                trunk.contains("if (id == 17) {")
                        && trunk.contains("} else if (id == 18) {")
                        && trunk.contains("this.lidrot = 0.15F;")
                        && trunk.contains("this.showHeartsOrSmokeFX(true);")
                        && trunk.contains("private void showHeartsOrSmokeFX(boolean positive)")
                        && trunk.contains("net.minecraft.util.EnumParticleTypes.HEART")
                        && trunk.contains("net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL")
                        && trunk.contains("this.world.spawnParticle("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
