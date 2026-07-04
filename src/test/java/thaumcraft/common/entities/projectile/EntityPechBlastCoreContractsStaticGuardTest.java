package thaumcraft.common.entities.projectile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityPechBlastCoreContractsStaticGuardTest {

    @Test
    public void pechBlastKeepsReferenceLaunchConstructorsAndSpeedContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityPechBlast.java");
        String focusSource = readFile("src/main/java/thaumcraft/common/items/wands/foci/FocusPech.java");

        assertTrue("EntityPechBlast must keep reference coordinate constructor payload contract",
                source.contains("public EntityPechBlast(World world, double x, double y, double z, int strength, int duration, boolean nightshade)")
                        && source.contains("super(world, x, y, z);")
                        && source.contains("this.strength = strength;")
                        && source.contains("this.duration = duration;")
                        && source.contains("this.nightshade = nightshade;"));
        assertTrue("FocusPech must keep explicit 1.5F launch speed baseline for 1.12 throwable parity",
                focusSource.contains("blast.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);"));
    }

    @Test
    public void pechBlastKeepsReferenceClientFxParity() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityPechBlast.java");
        String focusSource = readFile("src/main/java/thaumcraft/common/items/wands/foci/FocusPech.java");

        assertTrue("EntityPechBlast must keep reference client trail FX",
                source.contains("if (this.world.isRemote)")
                        && source.contains("for (int i = 0; i < 3; ++i)")
                        && source.contains("Thaumcraft.proxy.wispFX2(")
                        && source.contains("(this.posX + this.prevPosX) / 2.0D")
                        && source.contains("Thaumcraft.proxy.sparkle("));
        assertTrue("EntityPechBlast must keep reference client impact burst FX",
                source.contains("for (int i = 0; i < 9; ++i)")
                        && source.contains("Thaumcraft.proxy.wispFX3(")
                        && source.contains("offsetX * 8.0F")
                        && source.contains("0.3F,")
                        && source.contains("0.02F"));
        assertTrue("FocusPech must keep reference focus color and wand-derived potency/extend",
                focusSource.contains("return 0x229944;")
                        && focusSource.contains("wand.getFocusPotency(wandStack)")
                        && focusSource.contains("wand.getFocusExtend(wandStack)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
