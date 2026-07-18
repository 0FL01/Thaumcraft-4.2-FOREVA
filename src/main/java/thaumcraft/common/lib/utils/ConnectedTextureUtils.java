package thaumcraft.common.lib.utils;

import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public final class ConnectedTextureUtils {
    private static final int[] CONNECTED_TEXTURE_REF_BY_ID = new int[]{
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            4, 4, 5, 5, 4, 4, 5, 5, 17, 17, 22, 26, 17, 17, 22, 26, 16, 16, 20, 20, 16, 16, 28, 28, 21, 21, 46, 42, 21, 21, 43, 38,
            4, 4, 5, 5, 4, 4, 5, 5, 9, 9, 30, 12, 9, 9, 30, 12, 16, 16, 20, 20, 16, 16, 28, 28, 25, 25, 45, 37, 25, 25, 40, 32,
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            0, 0, 6, 6, 0, 0, 6, 6, 3, 3, 19, 15, 3, 3, 19, 15, 1, 1, 18, 18, 1, 1, 13, 13, 2, 2, 23, 31, 2, 2, 27, 14,
            4, 4, 5, 5, 4, 4, 5, 5, 17, 17, 22, 26, 17, 17, 22, 26, 7, 7, 24, 24, 7, 7, 10, 10, 29, 29, 44, 41, 29, 29, 39, 33,
            4, 4, 5, 5, 4, 4, 5, 5, 9, 9, 30, 12, 9, 9, 30, 12, 7, 7, 24, 24, 7, 7, 10, 10, 8, 8, 36, 35, 8, 8, 34, 11
    };

    private ConnectedTextureUtils() {
    }

    public static int getTextureIndex(BlockPos pos, int side, Predicate<BlockPos> connected) {
        boolean[] bitMatrix = new boolean[8];
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (side == 0 || side == 1) {
            bitMatrix[0] = connected.test(new BlockPos(x - 1, y, z - 1));
            bitMatrix[1] = connected.test(new BlockPos(x, y, z - 1));
            bitMatrix[2] = connected.test(new BlockPos(x + 1, y, z - 1));
            bitMatrix[3] = connected.test(new BlockPos(x - 1, y, z));
            bitMatrix[4] = connected.test(new BlockPos(x + 1, y, z));
            bitMatrix[5] = connected.test(new BlockPos(x - 1, y, z + 1));
            bitMatrix[6] = connected.test(new BlockPos(x, y, z + 1));
            bitMatrix[7] = connected.test(new BlockPos(x + 1, y, z + 1));
        } else if (side == 2 || side == 3) {
            bitMatrix[0] = connected.test(new BlockPos(x + (side == 2 ? 1 : -1), y + 1, z));
            bitMatrix[1] = connected.test(new BlockPos(x, y + 1, z));
            bitMatrix[2] = connected.test(new BlockPos(x + (side == 3 ? 1 : -1), y + 1, z));
            bitMatrix[3] = connected.test(new BlockPos(x + (side == 2 ? 1 : -1), y, z));
            bitMatrix[4] = connected.test(new BlockPos(x + (side == 3 ? 1 : -1), y, z));
            bitMatrix[5] = connected.test(new BlockPos(x + (side == 2 ? 1 : -1), y - 1, z));
            bitMatrix[6] = connected.test(new BlockPos(x, y - 1, z));
            bitMatrix[7] = connected.test(new BlockPos(x + (side == 3 ? 1 : -1), y - 1, z));
        } else if (side == 4 || side == 5) {
            bitMatrix[0] = connected.test(new BlockPos(x, y + 1, z + (side == 5 ? 1 : -1)));
            bitMatrix[1] = connected.test(new BlockPos(x, y + 1, z));
            bitMatrix[2] = connected.test(new BlockPos(x, y + 1, z + (side == 4 ? 1 : -1)));
            bitMatrix[3] = connected.test(new BlockPos(x, y, z + (side == 5 ? 1 : -1)));
            bitMatrix[4] = connected.test(new BlockPos(x, y, z + (side == 4 ? 1 : -1)));
            bitMatrix[5] = connected.test(new BlockPos(x, y - 1, z + (side == 5 ? 1 : -1)));
            bitMatrix[6] = connected.test(new BlockPos(x, y - 1, z));
            bitMatrix[7] = connected.test(new BlockPos(x, y - 1, z + (side == 4 ? 1 : -1)));
        }

        int mask = 0;
        for (int i = 0; i < bitMatrix.length; i++) {
            if (bitMatrix[i]) {
                mask |= 1 << i;
            }
        }
        return getTextureIndex(mask);
    }

    public static int getTextureIndex(int mask) {
        return mask >= 0 && mask < CONNECTED_TEXTURE_REF_BY_ID.length
                ? CONNECTED_TEXTURE_REF_BY_ID[mask]
                : 0;
    }
}
