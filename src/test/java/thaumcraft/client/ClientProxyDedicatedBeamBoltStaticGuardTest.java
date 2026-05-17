package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClientProxyDedicatedBeamBoltStaticGuardTest {

    @Test
    public void clientProxyUsesDedicatedBeamAndBoltFxClasses() throws IOException {
        String clientProxy = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String beamClass = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeam.java");
        String boltClass = readFile("src/main/java/thaumcraft/client/fx/bolt/FXLightningBolt.java");

        assertTrue("ClientProxy beam path should enqueue dedicated FXBeam via ParticleEngine",
                clientProxy.contains("new FXBeam(") && clientProxy.contains("ParticleEngine.addEffect(world"));
        assertTrue("ClientProxy bolt path should enqueue dedicated FXLightningBolt via ParticleEngine",
                clientProxy.contains("new FXLightningBolt(") && clientProxy.contains("ParticleEngine.addEffect(world"));
        assertFalse("ClientProxy beam path should not keep legacy inline beam fallback loops",
                clientProxy.contains("double dx = tx - x;"));
        assertFalse("ClientProxy bolt path should not keep legacy inline bolt fallback loops",
                clientProxy.contains("double noise = 0.18f;"));
        assertTrue("FXBeam should keep beam-line emission logic",
                beamClass.contains("class FXBeam extends Particle")
                        && beamClass.contains("EnumParticleTypes.REDSTONE")
                        && beamClass.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("FXLightningBolt should keep jittered segment emission logic",
                boltClass.contains("class FXLightningBolt extends Particle")
                        && boltClass.contains("new Random(this.seed + this.particleAge * 31L)")
                        && boltClass.contains("EnumParticleTypes.CRIT_MAGIC"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
