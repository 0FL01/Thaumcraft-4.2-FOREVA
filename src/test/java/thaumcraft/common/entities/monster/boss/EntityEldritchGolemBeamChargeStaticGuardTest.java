package thaumcraft.common.entities.monster.boss;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityEldritchGolemBeamChargeStaticGuardTest {

    @Test
    public void eldritchGolemShouldKeepHeadlessBeamChargeFxContracts() throws IOException {
        String entity = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java");
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("Proxy surface must expose dedicated spark and scaled vent helper overloads for Eldritch Golem beam-charge FX",
                commonProxy.contains("public void spark(float x, float y, float z, float size, float red, float green, float blue, float alpha)")
                        && commonProxy.contains("public void drawVentParticles(World world, double x, double y, double z,")
                        && commonProxy.contains("double mx, double my, double mz, int color, float scale)")
                        && clientProxy.contains("public void spark(float x, float y, float z, float size, float red, float green, float blue, float alpha)")
                        && clientProxy.contains("new FXSpark(world, x, y, z, size)")
                        && clientProxy.contains("fx.setAlphaF(alpha);")
                        && clientProxy.contains("public void drawVentParticles(World world, double x, double y, double z,")
                        && clientProxy.contains("double mx, double my, double mz, int color, float scale)")
                        && clientProxy.contains("fx.setScale(scale);"));
        assertTrue("EntityEldritchGolem must restore the headless beam-charge client FX branch and arcing status update handling",
                entity.contains("private int arcing = 0;")
                        && entity.contains("private int ax = 0;")
                        && entity.contains("private int ay = 0;")
                        && entity.contains("private int az = 0;")
                        && entity.contains("if (this.world.isRemote) {")
                        && entity.contains("this.rotationPitch = 0.0F;")
                        && entity.contains("Thaumcraft.proxy.spark(")
                        && entity.contains("Thaumcraft.proxy.drawVentParticles(")
                        && entity.contains("Thaumcraft.proxy.arcLightning(")
                        && entity.contains("this.world.setEntityState(this, (byte) 19);")
                        && entity.contains("} else if (id == 19) {")
                        && entity.contains("this.arcing = 8 + this.rand.nextInt(5);")
                        && entity.contains("TCSounds.JACOBS"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
