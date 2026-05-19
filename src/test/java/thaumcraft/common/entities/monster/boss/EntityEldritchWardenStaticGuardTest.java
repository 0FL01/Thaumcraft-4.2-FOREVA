package thaumcraft.common.entities.monster.boss;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntityEldritchWardenStaticGuardTest {

    @Test
    public void eldritchWardenShouldKeepReferenceSwimAndSpawnStateContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java");

        assertTrue(source.contains("this.getNavigator() instanceof PathNavigateGround"));
        assertTrue(source.contains("((PathNavigateGround) this.getNavigator()).setCanSwim(true);"));
        assertTrue(source.contains("if (this.getSpawnTimer() == 150) {"));
        assertFalse(source.contains("if (!this.world.isRemote && this.getSpawnTimer() == 150) {"));
        assertTrue("Warden spawn visuals must keep smoke spiral client path during spawn timer",
                source.contains("if (this.getSpawnTimer() > 0) {")
                        && source.contains("Thaumcraft.proxy.smokeSpiral(this.world, this.posX"));
        assertTrue("Warden sonic attack branch must keep PacketFXSonic broadcast baseline",
                source.contains("new PacketFXSonic(this.getEntityId())")
                        && source.contains("new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), this.posX, this.posY, this.posZ, 32.0)"));
        assertTrue("Warden field frenzy must keep block-arc/sparkle FX broadcasts",
                source.contains("new PacketFXBlockArc(pos.getX(), pos.getY(), pos.getZ(), this.getEntityId())")
                        && source.contains("new PacketFXBlockSparkle(pos.getX(), pos.getY(), pos.getZ(), 0x800080)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
