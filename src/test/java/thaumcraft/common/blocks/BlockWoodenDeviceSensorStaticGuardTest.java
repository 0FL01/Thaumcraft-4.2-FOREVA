package thaumcraft.common.blocks;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class BlockWoodenDeviceSensorStaticGuardTest {

    @Test
    public void blockWoodenDeviceShouldKeepSensorActivationAndToneHooks() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");

        assertTrue(source.contains("if (te instanceof TileSensor)"));
        assertTrue(source.contains("sensor.changePitch();"));
        assertTrue(source.contains("sensor.triggerNote(worldIn, pos.getX(), pos.getY(), pos.getZ(), true);"));
        assertTrue(source.contains("if (state.getValue(TYPE) == 1)"));
        assertTrue(source.contains("((TileSensor) te).updateTone();"));
    }

    @Test
    public void blockWoodenDeviceShouldKeepSensorRedstonePowerContracts() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");

        assertTrue(source.contains("public boolean canProvidePower(IBlockState state)"));
        assertTrue(source.contains("public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)"));
        assertTrue(source.contains("public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)"));
        assertTrue(source.contains("((TileSensor) tile).redstoneSignal > 0 ? 15 : 0"));
        assertTrue(source.contains("if (meta == 3)"));
        assertTrue(source.contains("return side == EnumFacing.UP ? 15 : 0;"));
        assertTrue(source.contains("return 15;"));
    }

    @Test
    public void blockWoodenDeviceShouldKeepNoteEventPlaybackPath() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");

        assertTrue(source.contains("public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)"));
        assertTrue(source.contains("if (id <= 4)"));
        assertTrue(source.contains("Math.pow(2.0D, (param - 12) / 12.0D)"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_HARP"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_BASEDRUM"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_SNARE"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_HAT"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_BASS"));
        assertTrue(source.contains("EnumParticleTypes.NOTE"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
