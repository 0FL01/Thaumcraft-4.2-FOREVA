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
        assertTrue(source.contains("if (te instanceof TileArcanePressurePlate)"));
        assertTrue(source.contains("plate.setting = (byte) ((plate.setting + 1) % 3);"));
        assertTrue(source.contains("if (!worldIn.isRemote && state.getValue(TYPE) == 3)"));
        assertTrue(source.contains("if (!worldIn.isRemote && state.getValue(TYPE) == 2)"));
        assertTrue(source.contains("setStateIfMobInteractsWithPlate(worldIn, pos);"));
        assertTrue(source.contains("if (meta == 1)"));
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
        assertTrue(source.contains("if (meta == 4 || meta == 6 || meta == 7)"));
        assertTrue(source.contains("list.add(new ItemStack(this, 1, 6));"));
        assertTrue(source.contains("list.add(new ItemStack(this, 1, 7));"));
        assertTrue(source.contains("world.scheduleUpdate(pos, this, tickRate(world));"));
        assertTrue(source.contains("if (shouldPress && !pressed)"));
        assertTrue(source.contains("if (!shouldPress && pressed)"));
    }

    @Test
    public void blockWoodenDeviceShouldKeepNoteEventPlaybackPath() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java");

        assertTrue(source.contains("public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)"));
        assertTrue(source.contains("boolean silentSensorEvent = id == -1 || id == 255;"));
        assertTrue(source.contains("if (silentSensorEvent || id >= 0 && id <= 4)"));
        assertTrue(source.contains("if (!silentSensorEvent)"));
        assertTrue(source.contains("if (worldIn.isRemote && silentSensorEvent)"));
        assertTrue(source.contains("((TileSensor) tile).redstoneSignal = 10;"));
        assertTrue(source.contains("Math.pow(2.0D, (param - 12) / 12.0D)"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_HARP"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_BASEDRUM"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_SNARE"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_HAT"));
        assertTrue(source.contains("SoundEvents.BLOCK_NOTE_BASS"));
        assertTrue(source.contains("Thaumcraft.proxy.drawGenericParticles(worldIn"));
        assertTrue(source.contains("false, 64, 1, 1, 6, 0, 0.75F, 1"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
