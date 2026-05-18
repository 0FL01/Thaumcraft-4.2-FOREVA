package thaumcraft.client.fx.bolt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.client.fx.WRVector3;

public class FXLightningBoltCommon {
    public static final float speed = 3.0f;
    public static final int fadetime = 20;

    public final List<Segment> segments = new ArrayList<>();
    public WRVector3 start;
    public WRVector3 end;
    public Map<Integer, Integer> splitparents = new HashMap<>();
    public float multiplier;
    public float length;
    public int numsegments0;
    public int increment;
    public int type = 0;
    public boolean nonLethal = false;
    public long seed;
    public int particleAge;
    public int particleMaxAge;

    private int numsplits;
    private boolean finalized;
    private final Random rand;
    private final World world;

    public FXLightningBoltCommon(World world, WRVector3 start, WRVector3 end, long seed) {
        this.world = world;
        this.start = start;
        this.end = end;
        this.seed = seed;
        this.rand = new Random(seed);
        this.numsegments0 = 1;
        this.increment = 1;
        this.length = this.end.copy().sub(this.start).length();
        this.particleMaxAge = 3 + this.rand.nextInt(3) - 1;
        this.multiplier = 1.0f;
        this.particleAge = -((int) (this.length * speed));
        this.segments.add(new Segment(this.start, this.end));
    }

    public FXLightningBoltCommon(World world, Entity detonator, Entity target, long seed) {
        this(world, new WRVector3(detonator), new WRVector3(target), seed);
    }

    public FXLightningBoltCommon(World world, Entity detonator, Entity target, long seed, int speed) {
        this(world, new WRVector3(detonator), new WRVector3(target.posX, target.posY + target.getEyeHeight() - 0.7f, target.posZ), seed);
        this.increment = speed;
        this.multiplier = 0.4f;
    }

    public FXLightningBoltCommon(World world, TileEntity detonator, Entity target, long seed) {
        this(world, new WRVector3(detonator), new WRVector3(target), seed);
    }

    public FXLightningBoltCommon(World world, TileEntity detonator, double x, double y, double z, long seed) {
        this(world, new WRVector3(detonator), new WRVector3(x, y, z), seed);
    }

    public FXLightningBoltCommon(World world, double x1, double y1, double z1, double x2, double y2, double z2, long seed, int duration, float multi) {
        this(world, new WRVector3(x1, y1, z1), new WRVector3(x2, y2, z2), seed);
        this.particleMaxAge = duration + this.rand.nextInt(duration) - duration / 2;
        this.multiplier = multi;
    }

    public FXLightningBoltCommon(World world, double x1, double y1, double z1, double x2, double y2, double z2, long seed, int duration, float multi, int speed) {
        this(world, new WRVector3(x1, y1, z1), new WRVector3(x2, y2, z2), seed);
        this.particleMaxAge = duration + this.rand.nextInt(duration) - duration / 2;
        this.multiplier = multi;
        this.increment = speed;
    }

    public void setMultiplier(float m) {
        this.multiplier = m;
    }

    public void fractal(int splits, float amount, float splitchance, float splitlength, float splitangle) {
        if (this.finalized) {
            return;
        }
        List<Segment> oldsegments = new ArrayList<>(this.segments);
        this.segments.clear();
        Segment prev = null;
        for (Segment segment : oldsegments) {
            prev = segment.prev;
            WRVector3 subsegment = segment.diff.copy().scale(1.0f / (float) splits);
            BoltPoint[] newpoints = new BoltPoint[splits + 1];
            WRVector3 startpoint = segment.startpoint.point;
            newpoints[0] = segment.startpoint;
            newpoints[splits] = segment.endpoint;

            for (int i = 1; i < splits; i++) {
                WRVector3 randoff = WRVector3.getPerpendicular(segment.diff).rotate(this.rand.nextFloat() * 360.0f, segment.diff);
                randoff.scale((this.rand.nextFloat() - 0.5f) * amount);
                WRVector3 basepoint = startpoint.copy().add(subsegment.copy().scale(i));
                newpoints[i] = new BoltPoint(basepoint, randoff);
            }

            for (int i = 0; i < splits; i++) {
                Segment next = new Segment(newpoints[i], newpoints[i + 1], segment.light, segment.segmentno * splits + i, segment.splitno);
                next.prev = prev;
                if (prev != null) {
                    prev.next = next;
                }

                if (i != 0 && this.rand.nextFloat() < splitchance) {
                    WRVector3 splitrot = WRVector3.xCrossProduct(next.diff).rotate(this.rand.nextFloat() * 360.0f, next.diff);
                    WRVector3 diff = next.diff.copy().rotate((this.rand.nextFloat() * 0.66f + 0.33f) * splitangle, splitrot).scale(splitlength);
                    ++this.numsplits;
                    this.splitparents.put(this.numsplits, next.splitno);
                    Segment split = new Segment(newpoints[i], new BoltPoint(newpoints[i + 1].basepoint, newpoints[i + 1].offsetvec.copy().add(diff)), segment.light / 2.0f, next.segmentno, this.numsplits);
                    split.prev = prev;
                    this.segments.add(split);
                }
                prev = next;
                this.segments.add(next);
            }

            if (segment.next != null) {
                segment.next.prev = prev;
            }
        }
        this.numsegments0 *= splits;
    }

    public void defaultFractal() {
        this.fractal(2, this.length * this.multiplier / 8.0f, 0.7f, 0.1f, 45.0f);
        this.fractal(2, this.length * this.multiplier / 12.0f, 0.5f, 0.1f, 50.0f);
        this.fractal(2, this.length * this.multiplier / 17.0f, 0.5f, 0.1f, 55.0f);
        this.fractal(2, this.length * this.multiplier / 23.0f, 0.5f, 0.1f, 60.0f);
        this.fractal(2, this.length * this.multiplier / 30.0f, 0.0f, 0.0f, 0.0f);
        this.fractal(2, this.length * this.multiplier / 34.0f, 0.0f, 0.0f, 0.0f);
        this.fractal(2, this.length * this.multiplier / 40.0f, 0.0f, 0.0f, 0.0f);
    }

    private void calculateCollisionAndDiffs() {
        HashMap<Integer, Integer> lastactivesegment = new HashMap<>();
        Collections.sort(this.segments, new SegmentSorter());

        int lastsplitcalc = 0;
        int lastactiveseg = 0;
        for (Segment segment : this.segments) {
            if (segment.splitno > lastsplitcalc) {
                lastactivesegment.put(lastsplitcalc, lastactiveseg);
                lastsplitcalc = segment.splitno;
                Integer parentSplit = this.splitparents.get(segment.splitno);
                lastactiveseg = lastactivesegment.get(parentSplit);
                if (lastactiveseg == 0 && parentSplit != null && parentSplit == 0) {
                    lastactiveseg = lastactivesegment.getOrDefault(0, 0);
                }
            }
            lastactiveseg = segment.segmentno;
        }

        lastactivesegment.put(lastsplitcalc, lastactiveseg);
        lastsplitcalc = 0;
        lastactiveseg = lastactivesegment.getOrDefault(0, 0);

        Iterator<Segment> iterator = this.segments.iterator();
        while (iterator.hasNext()) {
            Segment segment = iterator.next();
            if (lastsplitcalc != segment.splitno) {
                lastsplitcalc = segment.splitno;
                lastactiveseg = lastactivesegment.getOrDefault(segment.splitno, lastactiveseg);
            }
            if (segment.segmentno > lastactiveseg) {
                iterator.remove();
                continue;
            }
            segment.calcEndDiffs();
        }
    }

    public void finalizeBolt() {
        if (this.finalized) {
            return;
        }
        this.finalized = true;
        this.calculateCollisionAndDiffs();
        Collections.sort(this.segments, new SegmentLightSorter());
    }

    public void onUpdate() {
        this.particleAge += this.increment;
        if (this.particleAge > this.particleMaxAge) {
            this.particleAge = this.particleMaxAge;
        }
    }

    public class SegmentSorter implements Comparator<Segment> {
        @Override
        public int compare(Segment o1, Segment o2) {
            int comp = Integer.compare(o1.splitno, o2.splitno);
            if (comp == 0) {
                return Integer.compare(o1.segmentno, o2.segmentno);
            }
            return comp;
        }
    }

    public class SegmentLightSorter implements Comparator<Segment> {
        @Override
        public int compare(Segment o1, Segment o2) {
            return Float.compare(o2.light, o1.light);
        }
    }

    public class Segment {
        public BoltPoint startpoint;
        public BoltPoint endpoint;
        public WRVector3 diff;
        public Segment prev;
        public Segment next;
        public WRVector3 nextdiff;
        public WRVector3 prevdiff;
        public float sinprev;
        public float sinnext;
        public float light;
        public int segmentno;
        public int splitno;

        public Segment(BoltPoint start, BoltPoint end, float light, int segmentnumber, int splitnumber) {
            this.startpoint = start;
            this.endpoint = end;
            this.light = light;
            this.segmentno = segmentnumber;
            this.splitno = splitnumber;
            this.calcDiff();
        }

        public Segment(WRVector3 start, WRVector3 end) {
            this(new BoltPoint(start, new WRVector3(0.0, 0.0, 0.0)), new BoltPoint(end, new WRVector3(0.0, 0.0, 0.0)), 1.0f, 0, 0);
        }

        public void calcDiff() {
            this.diff = this.endpoint.point.copy().sub(this.startpoint.point);
        }

        public void calcEndDiffs() {
            WRVector3 thisdiffnorm;
            if (this.prev != null) {
                WRVector3 prevdiffnorm = this.prev.diff.copy().normalize();
                thisdiffnorm = this.diff.copy().normalize();
                this.prevdiff = thisdiffnorm.add(prevdiffnorm).normalize();
                this.sinprev = (float) Math.sin(WRVector3.anglePreNorm(thisdiffnorm, prevdiffnorm.scale(-1.0f)) / 2.0f);
            } else {
                this.prevdiff = this.diff.copy().normalize();
                this.sinprev = 1.0f;
            }

            if (this.next != null) {
                WRVector3 nextdiffnorm = this.next.diff.copy().normalize();
                thisdiffnorm = this.diff.copy().normalize();
                this.nextdiff = thisdiffnorm.add(nextdiffnorm).normalize();
                this.sinnext = (float) Math.sin(WRVector3.anglePreNorm(thisdiffnorm, nextdiffnorm.scale(-1.0f)) / 2.0f);
            } else {
                this.nextdiff = this.diff.copy().normalize();
                this.sinnext = 1.0f;
            }
        }

        @Override
        public String toString() {
            return this.startpoint.point.toString() + " " + this.endpoint.point.toString();
        }
    }

    public class BoltPoint {
        public WRVector3 point;
        public WRVector3 basepoint;
        public WRVector3 offsetvec;

        public BoltPoint(WRVector3 basepoint, WRVector3 offsetvec) {
            this.point = basepoint.copy().add(offsetvec);
            this.basepoint = basepoint;
            this.offsetvec = offsetvec;
        }
    }
}
