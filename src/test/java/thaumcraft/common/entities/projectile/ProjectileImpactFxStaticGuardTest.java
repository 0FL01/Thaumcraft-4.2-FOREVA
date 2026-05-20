package thaumcraft.common.entities.projectile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ProjectileImpactFxStaticGuardTest {

    @Test
    public void projectileImpactFxSurfacesStayWiredToDedicatedClientPaths() throws IOException {
        String commonProxy = readFile("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String fxWisp = readFile("src/main/java/thaumcraft/client/fx/particles/FXWisp.java");
        String alumentum = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityAlumentum.java");
        String bottleTaint = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityBottleTaint.java");
        String eldritchOrb = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityEldritchOrb.java");

        assertTrue("CommonProxy must keep server-safe throwable FX stubs",
                commonProxy.contains("public void wispFX2(World world, double x, double y, double z, float size, int type, boolean shrink, boolean clip, float gravity)")
                        && commonProxy.contains("public void sparkle(float x, float y, float z, int color)")
                        && commonProxy.contains("public void taintsplosionFX(Entity entity)")
                        && commonProxy.contains("public void bottleTaintBreak(World world, double x, double y, double z)"));
        assertTrue("ClientProxy must route throwable FX through dedicated client particle paths",
                clientProxy.contains("public void wispFX2(World world, double x, double y, double z, float size, int type, boolean shrink, boolean clip, float gravity)")
                        && clientProxy.contains("new FXWisp(world, x, y, z, size, type)")
                        && clientProxy.contains("public void sparkle(float x, float y, float z, int color)")
                        && clientProxy.contains("new FXSparkle(world, x, y, z, 1.5F, color, 6.0F)")
                        && clientProxy.contains("public void taintsplosionFX(Entity entity)")
                        && clientProxy.contains("new FXBreaking(")
                        && clientProxy.contains("entity.posY + world.rand.nextFloat() * entity.height")
                        && clientProxy.contains("Items.SLIME_BALL")
                        && clientProxy.contains("public void bottleTaintBreak(World world, double x, double y, double z)")
                        && clientProxy.contains("SoundEvents.ENTITY_SPLASH_POTION_BREAK"));
        assertTrue("FXWisp must keep the typed ambient constructor used by Alumentum",
                fxWisp.contains("public FXWisp(World world, double x, double y, double z, float size, int type)")
                        && fxWisp.contains("this.blendmode = 771;"));
        assertTrue("EntityAlumentum must restore reference client trail FX",
                alumentum.contains("Thaumcraft.proxy.wispFX2(")
                        && alumentum.contains("Thaumcraft.proxy.sparkle("));
        assertTrue("EntityBottleTaint must restore reference taint splash break FX",
                bottleTaint.contains("Thaumcraft.proxy.taintsplosionFX(this);")
                        && bottleTaint.contains("Thaumcraft.proxy.bottleTaintBreak(this.world, this.posX, this.posY, this.posZ);"));
        assertTrue("EntityEldritchOrb status 16 must restore the reference wisp burst",
                eldritchOrb.contains("Thaumcraft.proxy.wispFX3(")
                        && eldritchOrb.contains("if (this.world.isRemote) {"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
