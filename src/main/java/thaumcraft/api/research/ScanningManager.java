package thaumcraft.api.research;

import java.util.ArrayList;
import java.util.List;

public class ScanningManager {
    private static final List<IScanThing> things = new ArrayList<>();

    public static void addScannableThing(IScanThing thing) {
        things.add(thing);
    }
}
