package thaumcraft.codechicken.lib.vec;

public abstract class ITransformation<Vector, Transformation extends ITransformation> {
    public abstract void apply(Vector var1);

    public abstract Transformation at(Vector var1);

    public abstract Transformation with(Transformation var1);

    public Transformation merge(Transformation next) {
        return null;
    }

    public boolean isRedundant() {
        return false;
    }

    public abstract Transformation inverse();

    public Transformation $plus$plus(Transformation t) {
        return this.with(t);
    }
}

