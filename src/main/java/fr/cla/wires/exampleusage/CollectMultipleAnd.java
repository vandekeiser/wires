package fr.cla.wires.exampleusage;

import fr.cla.wires.*;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class CollectMultipleAnd extends CollectHomogeneousInputs<Boolean, Boolean> {

    private CollectMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    private CollectMultipleAnd(Set<Wire<Boolean>> ins, Wire<Boolean> out, Time time, Delay delay) {
        super(ins, out, time, delay);
    }


    private boolean and(boolean b1, boolean b2) {
        return b1 && b2;
    }

    @Override protected CollectMultipleAnd startup() {
        super.startup();
        return this;
    }

    @Override protected Collector<Optional<Boolean>, ?, Optional<Boolean>> collection() {
        return new Collector<Optional<Boolean>, MutableBoolean, Optional<Boolean>>() {
            @Override public Supplier<MutableBoolean> supplier() {
                return MutableBoolean::initiallyUnset;
            }

            @Override public BiConsumer<MutableBoolean, Optional<Boolean>> accumulator() {
                return MutableBoolean::and;
            }

            @Override public BinaryOperator<MutableBoolean> combiner() {
                return MutableBoolean::and;
            }

            @Override public Function<MutableBoolean, Optional<Boolean>> finisher() {
                return MutableBoolean::current;
            }

            @Override public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    public static Builder ins(Set<Wire<Boolean>> ins) {
        return new Builder(checkNoNulls(ins));
    }

    public static class Builder {
        private Set<Wire<Boolean>> ins;
        private Wire<Boolean> out;

        private Builder(Set<Wire<Boolean>> ins) {
            this.ins = requireNonNull(ins);
        }

        public Builder out(Wire<Boolean> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public CollectMultipleAnd time(Time time) {
            Time _time = requireNonNull(time);
            return new CollectMultipleAnd(ins, out, _time).startup();
        }
    }

}
//@formatter:on
