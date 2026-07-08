package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaintacleAndSwarmVisualParityStaticGuardTest {

    @Test
    public void taintacleModelSegmentsShouldChainVertically() throws IOException {
        String model = read("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintacle.java");

        assertTrue("Root taintacle segment should remain raised from the model origin",
                model.contains("tentacle.rotationPointY = 12.0F;"));
        assertEquals("Child taintacle segments, orb, and terminal bulb should use Y offsets like TC4, not Z offsets",
                3, countOccurrences(model, "rotationPointY = -8.0F;"));
        assertEquals("The old Z-axis chain makes taintacles render lying sideways and must not return",
                0, countOccurrences(model, "rotationPointZ = -8.0F;"));
    }

    @Test
    public void taintSwarmParticlesShouldUseThaumcraftSheetOnCustomLayer() throws IOException {
        String swarm = read("src/main/java/thaumcraft/client/fx/particles/FXSwarm.java");
        String particleEngine = read("src/main/java/thaumcraft/client/fx/ParticleEngine.java");

        assertTrue("FXSwarm should render through the TC ParticleEngine layer-1 thaumcraft sheet like TC4",
                swarm.contains("class FXSwarm extends Particle implements ITCParticle")
                        && particleEngine.contains("particleTexture = new ResourceLocation(\"thaumcraft\", \"textures/misc/particles.png\")")
                        && !swarm.contains("textures/particle/particles.png")
                        && swarm.contains("int frame = 7 + this.particleAge % 8")
                        && swarm.contains("float v0 = 0.25F")
                        && !swarm.contains("buffer.begin(GL11.GL_QUADS")
                        && !swarm.contains("tessellator.draw();")
                        && swarm.contains("public int getFXLayer()")
                        && swarm.contains("public int getTCParticleLayer()")
                        && swarm.contains("return 1;"));
    }

    @Test
    public void taintMobFxRoutesShouldMatchTc4Reference() throws IOException {
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String taintacle = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintacle.java");
        String taintCreeper = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintCreeper.java");
        String taintChicken = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintChicken.java");
        String taintCow = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintCow.java");
        String taintPig = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintPig.java");
        String taintSheep = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintSheep.java");
        String taintVillager = read("src/main/java/thaumcraft/common/entities/monster/EntityTaintVillager.java");

        assertTrue("Taintacle arise should use the dedicated TC4 splatter/digging proxy route",
                commonProxy.contains("public void tentacleAriseFX(Entity entity)")
                        && clientProxy.contains("public void tentacleAriseFX(Entity entity)")
                        && clientProxy.contains("new TaintDiggingFX(")
                        && clientProxy.contains("setBlockPos(blockPos)")
                        && clientProxy.contains("fx.setRBGColorF(0.4F, 0.0F, 0.4F)")
                        && taintacle.contains("Thaumcraft.proxy.tentacleAriseFX(this);")
                        && !taintacle.contains("Thaumcraft.proxy.burst(this.world, this.posX, this.posY, this.posZ, 1.0F);"));
        assertTrue("Taint helper particles should keep TC4 one-particle-per-call radius/velocity behavior",
                clientProxy.contains("float offsetX = MathHelper.sin(angle) * radius;")
                        && clientProxy.contains("motionX = motionX / length * speed * 0.9640000000596046D;")
                        && clientProxy.contains("motionY = motionY / length * speed * 0.9640000000596046D + 0.10000000149011612D;"));
        assertTrue("Taint creeper should keep TC4 client taintsplosion and early sploosh routes",
                taintCreeper.contains("Thaumcraft.proxy.particleCount(100)")
                        && taintCreeper.contains("Thaumcraft.proxy.taintsplosionFX(this)")
                        && taintCreeper.contains("Thaumcraft.proxy.splooshFX(this)")
                        && !taintCreeper.contains("Thaumcraft.proxy.taintLandFX(this)"));
        assertTrue("Tainted animal/villager spawn poofs should use splooshFX, not falling-taint land FX",
                taintChicken.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintCow.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintPig.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintSheep.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintVillager.contains("Thaumcraft.proxy.splooshFX(this)")
                        && !taintChicken.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && !taintCow.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && !taintPig.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && !taintSheep.contains("Thaumcraft.proxy.taintLandFX(this)")
                        && !taintVillager.contains("Thaumcraft.proxy.taintLandFX(this)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
