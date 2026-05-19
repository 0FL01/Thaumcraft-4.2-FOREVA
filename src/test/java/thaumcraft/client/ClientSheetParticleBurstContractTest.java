package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientSheetParticleBurstContractTest {

    @Test
    public void stage8eSheetParticleBurstRoutesClientOnlyFallbacksThroughProxy() throws IOException {
        String commonProxy = read("src/main/java/thaumcraft/common/CommonProxy.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String genericFx = read("src/main/java/thaumcraft/client/fx/particles/FXGeneric.java");
        String elementalSword = read("src/main/java/thaumcraft/common/items/equipment/ItemElementalSword.java");
        String blockCrystal = read("src/main/java/thaumcraft/common/blocks/BlockCrystal.java");
        String tileMirror = read("src/main/java/thaumcraft/common/tiles/TileMirror.java");
        String entityDart = read("src/main/java/thaumcraft/common/entities/projectile/EntityDart.java");
        String fireBat = read("src/main/java/thaumcraft/common/entities/monster/EntityFireBat.java");

        assertTrue("CommonProxy and ClientProxy must expose the explicit-count generic sheet particle overload",
                commonProxy.contains("float red, float green, float blue, float alpha,")
                        && commonProxy.contains("int count) {")
                        && clientProxy.contains("float red, float green, float blue, float alpha,")
                        && clientProxy.contains("int count) {"));
        assertTrue("FXGeneric must support reverse frame playback for vanilla-sheet migrations",
                genericFx.contains("Math.abs(this.particleInc)")
                        && genericFx.contains("if (this.particleInc < 0)")
                        && genericFx.contains("this.numParticles - 1 - frame"));
        assertTrue("Elemental Sword, BlockCrystal, TileMirror, Dart, and FireBat must route their client-only fallback particles through proxy generic sheet FX",
                elementalSword.contains("Thaumcraft.proxy.drawGenericParticles(player.world")
                        && !elementalSword.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && blockCrystal.contains("Thaumcraft.proxy.drawGenericParticles(worldIn")
                        && !blockCrystal.contains("EnumParticleTypes.SPELL_MOB")
                        && tileMirror.contains("Thaumcraft.proxy.drawGenericParticles(this.world")
                        && !tileMirror.contains("EnumParticleTypes.SPELL_MOB")
                        && entityDart.contains("Thaumcraft.proxy.drawGenericParticles(this.world")
                        && !entityDart.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && fireBat.contains("Thaumcraft.proxy.drawGenericParticles(")
                        && !fireBat.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && !fireBat.contains("EnumParticleTypes.FLAME"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
