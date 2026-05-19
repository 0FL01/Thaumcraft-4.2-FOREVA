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
        String blockCandle = read("src/main/java/thaumcraft/common/blocks/BlockCandle.java");
        String blockJar = read("src/main/java/thaumcraft/common/blocks/BlockJar.java");
        String blockWoodenDevice = read("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");
        String tileMirror = read("src/main/java/thaumcraft/common/tiles/TileMirror.java");
        String tileArcaneFurnace = read("src/main/java/thaumcraft/common/tiles/TileArcaneFurnace.java");
        String entityDart = read("src/main/java/thaumcraft/common/entities/projectile/EntityDart.java");
        String frostShard = read("src/main/java/thaumcraft/common/entities/projectile/EntityFrostShard.java");
        String fireBat = read("src/main/java/thaumcraft/common/entities/monster/EntityFireBat.java");
        String inhabitedZombie = read("src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java");
        String watcher = read("src/main/java/thaumcraft/common/entities/monster/EntityWatcher.java");
        String thaumicSlime = read("src/main/java/thaumcraft/common/entities/monster/EntityThaumicSlime.java");
        String taintacleGiant = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityTaintacleGiant.java");
        String eldritchGolem = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchGolem.java");
        String thaumcraftBoss = read("src/main/java/thaumcraft/common/entities/monster/boss/EntityThaumcraftBoss.java");

        assertTrue("CommonProxy and ClientProxy must expose the explicit-count generic sheet particle overload",
                commonProxy.contains("float red, float green, float blue, float alpha,")
                        && commonProxy.contains("int count) {")
                        && clientProxy.contains("float red, float green, float blue, float alpha,")
                        && clientProxy.contains("int count) {"));
        assertTrue("FXGeneric must support reverse frame playback for vanilla-sheet migrations",
                genericFx.contains("Math.abs(this.particleInc)")
                        && genericFx.contains("if (this.particleInc < 0)")
                        && genericFx.contains("this.numParticles - 1 - frame"));
        assertTrue("ClientProxy taint/slime breaking helpers must use the slime-ball reference sprite baseline",
                clientProxy.contains("Items.SLIME_BALL")
                        && !clientProxy.contains("Items.SNOWBALL"));
        assertTrue("Elemental Sword, Crystal, Candle, Jar, WoodenDevice sensor, Mirror, ArcaneFurnace, Dart, FireBat, and InhabitedZombie must route their client-only fallback particles through proxy generic sheet FX",
                elementalSword.contains("Thaumcraft.proxy.drawGenericParticles(player.world")
                        && !elementalSword.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && blockCrystal.contains("Thaumcraft.proxy.drawGenericParticles(worldIn")
                        && !blockCrystal.contains("EnumParticleTypes.SPELL_MOB")
                        && blockCandle.contains("Thaumcraft.proxy.drawGenericParticles(worldIn")
                        && !blockCandle.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && !blockCandle.contains("EnumParticleTypes.FLAME")
                        && blockJar.contains("Thaumcraft.proxy.drawGenericParticles(worldIn")
                        && !blockJar.contains("EnumParticleTypes.SPELL_MOB")
                        && blockWoodenDevice.contains("Thaumcraft.proxy.drawGenericParticles(worldIn")
                        && !blockWoodenDevice.contains("EnumParticleTypes.NOTE")
                        && tileMirror.contains("Thaumcraft.proxy.drawGenericParticles(this.world")
                        && !tileMirror.contains("EnumParticleTypes.SPELL_MOB")
                        && tileArcaneFurnace.contains("Thaumcraft.proxy.drawGenericParticles(this.world")
                        && !tileArcaneFurnace.contains("EnumParticleTypes.LAVA")
                        && entityDart.contains("Thaumcraft.proxy.drawGenericParticles(this.world")
                        && !entityDart.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && fireBat.contains("Thaumcraft.proxy.drawGenericParticles(")
                        && !fireBat.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && !fireBat.contains("EnumParticleTypes.FLAME")
                        && inhabitedZombie.contains("Thaumcraft.proxy.drawGenericParticles(this.world")
                        && !inhabitedZombie.contains("EnumParticleTypes.EXPLOSION_NORMAL"));
        assertTrue("FrostShard, Watcher, ThaumicSlime, TaintacleGiant, EldritchGolem, and ThaumcraftBoss must route their remaining client-only fallback particles through dedicated proxy FX paths",
                frostShard.contains("Thaumcraft.proxy.sparkle(")
                        && frostShard.contains("Thaumcraft.proxy.boreDigFx(")
                        && !frostShard.contains("EnumParticleTypes.BLOCK_CRACK")
                        && watcher.contains("Thaumcraft.proxy.drawGenericParticles(")
                        && !watcher.contains("EnumParticleTypes.WATER_BUBBLE")
                        && thaumicSlime.contains("Thaumcraft.proxy.slimeJumpFX(this, sizeSqrt)")
                        && !thaumicSlime.contains("EnumParticleTypes.SLIME")
                        && taintacleGiant.contains("Thaumcraft.proxy.drawGenericParticles(")
                        && !taintacleGiant.contains("EnumParticleTypes.VILLAGER_ANGRY")
                        && eldritchGolem.contains("Thaumcraft.proxy.boreDigFx(")
                        && !eldritchGolem.contains("EnumParticleTypes.BLOCK_CRACK")
                        && thaumcraftBoss.contains("Thaumcraft.proxy.drawGenericParticles(")
                        && !thaumcraftBoss.contains("EnumParticleTypes.VILLAGER_ANGRY"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
