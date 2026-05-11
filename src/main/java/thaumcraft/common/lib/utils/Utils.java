package thaumcraft.common.lib.utils;

public class Utils {

    public static void generateVisEffect(int dim, int x, int y, int z, int x2, int y2, int z2, int color) {
    }

    public static void setPrivateFinalValue(Class<?> cls, Object instance, Object value, String... fieldNames) {
    }

    public static byte pack(boolean[] bits) {
        byte v = 0;
        for (int i = 0; i < Math.min(bits.length, 8); i++) {
            if (bits[i]) v |= (1 << i);
        }
        return v;
    }

    public static boolean[] unpack(byte v) {
        boolean[] bits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = (v & (1 << i)) != 0;
        }
        return bits;
    }
}
