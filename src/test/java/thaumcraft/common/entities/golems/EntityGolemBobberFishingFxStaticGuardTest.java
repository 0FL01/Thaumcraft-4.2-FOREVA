package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityGolemBobberFishingFxStaticGuardTest {

    @Test
    public void golemFishingSplashShouldUseEntityStatusAndProxyFx() throws IOException {
        String bobber = readFile("src/main/java/thaumcraft/common/entities/golems/EntityGolemBobber.java");
        String aiFish = readFile("src/main/java/thaumcraft/common/entities/ai/interact/AIFish.java");

        assertTrue("EntityGolemBobber must expose dedicated splash status ids and proxy handoff",
                bobber.contains("public static final byte STATUS_SPLASH_AMBIENT = 16;")
                        && bobber.contains("public static final byte STATUS_SPLASH_NIBBLE = 17;")
                        && bobber.contains("public static final byte STATUS_SPLASH_CATCH = 18;")
                        && bobber.contains("this.world.setEntityState(this, STATUS_SPLASH_AMBIENT);")
                        && bobber.contains("this.world.setEntityState(this, STATUS_SPLASH_NIBBLE);")
                        && bobber.contains("Thaumcraft.proxy.golemFishingSplashFX(this, 0);")
                        && bobber.contains("Thaumcraft.proxy.golemFishingSplashFX(this, 1);")
                        && bobber.contains("Thaumcraft.proxy.golemFishingSplashFX(this, 2);")
                        && !bobber.contains("spawnParticle(")
                        && !bobber.contains("EnumParticleTypes.WATER_SPLASH"));
        assertTrue("AIFish catch resolution must trigger bobber catch splash through entity status instead of vanilla splash particles",
                aiFish.contains("theWorld.setEntityState(this.bobber, EntityGolemBobber.STATUS_SPLASH_CATCH);")
                        && !aiFish.contains("spawnParticle(")
                        && !aiFish.contains("EnumParticleTypes.WATER_SPLASH"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
