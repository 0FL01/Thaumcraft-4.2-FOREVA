package thaumcraft.common.entities.projectile;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityPrimalArrowCoreContractsStaticGuardTest {

    @Test
    public void primalArrowKeepsReferenceLaunchAndSpawnPayloadContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/projectile/EntityPrimalArrow.java");

        assertTrue("EntityPrimalArrow must keep reference coordinate and typed launch constructors",
                source.contains("public EntityPrimalArrow(World world, double x, double y, double z)")
                         && source.contains("public EntityPrimalArrow(World world, EntityLivingBase shooter, float velocity, int type)")
                         && source.contains("this.setArrowType(type);")
                         && source.contains("this.shoot(-MathHelper.sin(yaw) * MathHelper.cos(pitch), -MathHelper.sin(pitch),"));
        assertTrue("EntityPrimalArrow must keep reference-like spawn payload for motion, rotation, type and shooter id",
                source.contains("buf.writeDouble(this.motionX);")
                        && source.contains("buf.writeFloat(this.rotationYaw);")
                        && source.contains("buf.writeByte(this.getArrowType());")
                        && source.contains("buf.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : this.shootingEntityId);")
                        && source.contains("this.motionX = buf.readDouble();")
                        && source.contains("this.prevRotationPitch = this.rotationPitch;")
                         && source.contains("this.setArrowType(buf.readByte());")
                         && source.contains("this.shootingEntityId = buf.readInt();"));
        assertTrue("EntityPrimalArrow must keep TC4 damage, lifetime, water drag, and no-pickup contracts",
                source.contains("this.setDamage(2.1D);")
                        && source.contains("if (type == 3) return damage * 1.5D;")
                        && source.contains("if (type == 4 || type == 5) return damage * 0.8D;")
                        && source.contains("this.timeInGround >= 100")
                        && source.contains("this.motionX *= 4.0D / 3.0D;")
                        && source.contains("this.motionY = this.motionY * 4.0D / 3.0D + 1.0D / 60.0D;")
                        && source.contains("this.pickupStatus = PickupStatus.DISALLOWED;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
