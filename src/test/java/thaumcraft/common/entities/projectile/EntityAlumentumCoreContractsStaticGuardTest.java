package thaumcraft.common.entities.projectile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityAlumentumCoreContractsStaticGuardTest {

    @Test
    public void alumentumKeepsReferenceExplosionAndEyeHeightContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityAlumentum.java");
        String item = readFile("src/main/java/thaumcraft/common/items/ItemResource.java");

        assertTrue("EntityAlumentum must keep mobGriefing-gated impact explosion with 1.66F power",
                source.contains("boolean griefing = this.world.getGameRules().getBoolean(\"mobGriefing\");")
                        && source.contains("this.world.createExplosion(null, this.posX, this.posY, this.posZ, 1.66f, griefing);"));
        assertTrue("EntityAlumentum must keep reference low eye-height contract",
                source.contains("public float getEyeHeight()")
                        && source.contains("return 0.1F;"));
        assertTrue("EntityAlumentum must keep reference gravity baseline while launch speed is driven by ItemResource",
                source.contains("protected float getGravityVelocity() { return 0.03f; }")
                        && item.contains("projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.75F, 1.0F);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
