package truetyper;

public class Formatter {
    public static float[] getFormatted(char c) {
        int[] outrgba = null;
        switch (c) {
            case '0': {
                outrgba = new int[]{0, 0, 0, 0, 255};
                break;
            }
            case '1': {
                outrgba = new int[]{0, 0, 170, 255};
                break;
            }
            case '2': {
                outrgba = new int[]{0, 170, 0, 255};
                break;
            }
            case '3': {
                outrgba = new int[]{0, 170, 170, 255};
                break;
            }
            case '4': {
                outrgba = new int[]{170, 0, 0, 255};
                break;
            }
            case '5': {
                outrgba = new int[]{170, 0, 170, 255};
                break;
            }
            case '6': {
                outrgba = new int[]{255, 170, 0, 255};
                break;
            }
            case '7': {
                outrgba = new int[]{170, 170, 170, 255};
                break;
            }
            case '8': {
                outrgba = new int[]{85, 85, 85, 255};
                break;
            }
            case '9': {
                outrgba = new int[]{85, 85, 255, 255};
                break;
            }
            case 'a': {
                outrgba = new int[]{85, 255, 85, 255};
                break;
            }
            case 'b': {
                outrgba = new int[]{85, 255, 255, 255};
                break;
            }
            case 'c': {
                outrgba = new int[]{255, 85, 85, 255};
                break;
            }
            case 'd': {
                outrgba = new int[]{85, 255, 255, 255};
                break;
            }
            case 'e': {
                outrgba = new int[]{255, 255, 85, 255};
                break;
            }
            case 'f': {
                outrgba = new int[]{255, 255, 255, 255};
                break;
            }
            default: {
                outrgba = new int[]{255, 255, 255, 255};
            }
        }
        float[] outfloat = new float[outrgba.length];
        for (int i = 0; i < outrgba.length; ++i) {
            outfloat[i] = outrgba[i] > 0 ? (float)(outrgba[i] / 255) : 0.0f;
        }
        return outfloat;
    }
}

