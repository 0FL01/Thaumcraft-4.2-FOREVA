package thaumcraft.common.entities.projectile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityDartCoreContractsStaticGuardTest {

    @Test
    public void entityDartKeepsReferenceSpawnDataAndTrajectoryContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityDart.java");

        assertTrue("EntityDart must keep spawn payload serialization for motion and rotation",
                source.contains("buf.writeDouble(this.motionX);")
                        && source.contains("buf.writeDouble(this.motionY);")
                        && source.contains("buf.writeDouble(this.motionZ);")
                        && source.contains("buf.writeFloat(this.rotationYaw);")
                        && source.contains("buf.writeFloat(this.rotationPitch);")
                        && source.contains("this.motionX = buf.readDouble();")
                        && source.contains("this.rotationPitch = buf.readFloat();"));
        assertTrue("EntityDart must keep target-aware constructor trajectory setup",
                source.contains("public EntityDart(net.minecraft.world.World world, net.minecraft.entity.EntityLivingBase shooter,")
                        && source.contains("float lead = (float) horizontal * 0.2F;")
                        && source.contains("this.shoot(dx, dy + (double) lead, dz, velocity, inaccuracy);"));
        assertTrue("EntityDart must keep one-shot client smoke burst on first update tick",
                source.contains("if (this.first && this.world.isRemote)")
                        && source.contains("Thaumcraft.proxy.drawGenericParticles(this.world")
                        && source.contains("false, 0, 8, -1, 8, 0, 0.65F, 1")
                        && source.contains("this.first = false;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
