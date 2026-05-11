package thaumcraft.codechicken.lib.vec;

import thaumcraft.codechicken.lib.vec.ITransformation;

public class IrreversibleTransformationException
extends RuntimeException {
    public ITransformation t;

    public IrreversibleTransformationException(ITransformation t) {
        this.t = t;
    }

    @Override
    public String getMessage() {
        return "The following transformation is irreversible:\n" + this.t;
    }
}

