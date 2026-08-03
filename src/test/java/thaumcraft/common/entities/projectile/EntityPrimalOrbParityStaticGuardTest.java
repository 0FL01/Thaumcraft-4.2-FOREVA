package thaumcraft.common.entities.projectile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityPrimalOrbParityStaticGuardTest {

    @Test
    public void primalFocusAndOrbKeepReferenceImpactTerrainAndFeedbackContracts() throws IOException {
        String orb = read("src/main/java/thaumcraft/common/entities/projectile/EntityPrimalOrb.java");
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusPrimal.java");
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String wisp = read("src/main/java/thaumcraft/client/fx/particles/FXWisp.java");

        assertTrue(orb.contains("this.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, 0.5F, 1.0F);"));
        assertTrue(orb.contains("result.typeOfHit == RayTraceResult.Type.BLOCK && this.isInsideOfMaterial(Material.WATER)")
                && orb.contains("this.rand.nextInt(100) <= specialChance")
                && orb.contains("impactPos = new BlockPos(result.hitVec)"));
        assertTrue(orb.contains("Thaumcraft.proxy.wispFX4(")
                && orb.contains("Thaumcraft.proxy.wispFX2(")
                && orb.contains("Thaumcraft.proxy.wispFX3(")
                && orb.contains("for (int type = 0; type < 6; ++type)"));
        assertTrue(orb.contains("int x = (int) this.posX;")
                && orb.contains("if (!this.rand.nextBoolean()")
                && orb.contains("if (!this.world.isAirBlock(bp.down()))"));
        assertTrue(focus.contains("return 0xA5A1C1;")
                && focus.contains("player.isHandActive() ? player.getActiveHand() : EnumHand.MAIN_HAND"));
        assertTrue(commonProxy.contains("public void wispFX4(")
                && clientProxy.contains("new FXWisp(world, x, y, z, target, type)")
                && wisp.contains("public FXWisp(World world, double x, double y, double z, Entity target, int type)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
