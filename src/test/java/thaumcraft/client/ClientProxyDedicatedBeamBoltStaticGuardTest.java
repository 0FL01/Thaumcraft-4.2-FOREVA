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
        String beamBoreClass = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java");
        String beamWandClass = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java");
        String beamPowerClass = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeamPower.java");
        String beamGolemBossClass = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeamGolemBoss.java");
        String boltClass = readFile("src/main/java/thaumcraft/client/fx/bolt/FXLightningBolt.java");

        assertTrue("ClientProxy beam path should enqueue dedicated FXBeam via ParticleEngine",
                clientProxy.contains("new FXBeam(") && clientProxy.contains("ParticleEngine.addEffect(world"));
        assertTrue("ClientProxy bolt path should enqueue dedicated FXLightningBolt via ParticleEngine",
                clientProxy.contains("new FXLightningBolt(") && clientProxy.contains("ParticleEngine.addEffect(world"));
        assertTrue("Beam pulse packet-driven proxy paths should keep the reference width/blend/reverse shaping for ordinary and golem-boss pulses",
                clientProxy.contains("beam.setBlendMode(GL11.GL_ONE_MINUS_SRC_ALPHA);")
                        && clientProxy.contains("beam.setBeamWidth(2.5F);")
                        && clientProxy.contains("beam.setReverse(true);")
                        && clientProxy.contains("beamA.setBlendMode(GL11.GL_ONE);")
                        && clientProxy.contains("beamA.setBeamWidth(3.0F);")
                        && clientProxy.contains("beamA.setReverse(false);")
                        && clientProxy.contains("beamB.setBlendMode(GL11.GL_ONE);")
                        && clientProxy.contains("beamB.setBeamWidth(1.5F);")
                        && clientProxy.contains("beamB.setReverse(false);"));
        assertFalse("ClientProxy beam path should not keep legacy inline beam fallback loops",
                clientProxy.contains("double dx = tx - x;"));
        assertFalse("ClientProxy bolt path should not keep legacy inline bolt fallback loops",
                clientProxy.contains("double noise = 0.18f;"));
        assertTrue("FXBeam should keep dedicated custom render-path beam logic",
                beamClass.contains("class FXBeam extends Particle")
                        && beamClass.contains("renderImpact(")
                        && beamClass.contains("public void setBlendMode(int blendmode)")
                        && beamClass.contains("public void setBeamWidth(float width)")
                        && beamClass.contains("getBeamTexture()")
                        && beamClass.contains("return 3;"));
        assertTrue("FXBeamBore should keep impact-gated overlay contract for bore beams",
                beamBoreClass.contains("public int impact;")
                        && beamBoreClass.contains("if (this.impact <= 0)")
                        && beamBoreClass.contains("super.renderImpact("));
        assertTrue("FXBeamWand should keep player-sourced endpoint tracking and impact-gated overlay",
                beamWandClass.contains("sourcePos(")
                        && beamWandClass.contains("this.sourceYOffset")
                        && beamWandClass.contains("if (this.impact <= 0)")
                        && beamWandClass.contains("super.updateBeam("));
        assertTrue("FXBeamPower should keep pulse-opacity flare overlay contract",
                beamPowerClass.contains("private float opacity = 0.3F;")
                        && beamPowerClass.contains(".lightmap(240, 240)")
                        && beamPowerClass.contains("addFlareVertex(")
                        && beamPowerClass.contains("this.opacity = 0.8F"));
        assertTrue("FXBeamGolemBoss should keep boss-facing source vector and target-anchored endpoint updates",
                beamGolemBossClass.contains("this.boss.renderYawOffset")
                        && beamGolemBossClass.contains("this.target.prevPosX")
                        && beamGolemBossClass.contains("updateEndpointsFromEntities()"));
        assertTrue("FXLightningBolt should keep jittered segment custom render logic",
                boltClass.contains("class FXLightningBolt extends Particle")
                        && boltClass.contains("buildPath(this.seed + this.particleAge * 31L")
                        && boltClass.contains("new Random(jitterSeed)")
                        && boltClass.contains("buildPath(")
                        && boltClass.contains("return 3;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
