package fr.cla.wires.exampleusage;

import fr.cla.Accumulable;
import fr.cla.Mutable;
import fr.cla.wires.CollectHomogeneousInputs;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

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

    protected Collector<Optional<Boolean>, ?, Optional<Boolean>> collection() {
        return collection(this::and);
    }

    protected Collector<Optional<Boolean>, ?, Optional<Boolean>> collection(
        BinaryOperator<Boolean> accumulator
    ) {
        return new Collector<Optional<Boolean>, Accumulable<Boolean>, Optional<Boolean>>() {
            @Override public Supplier<Accumulable<Boolean>> supplier() {
                return () -> Accumulable.initiallyUnset(accumulator);
            }

            @Override public BiConsumer<Accumulable<Boolean>, Optional<Boolean>> accumulator() {
                return Accumulable::accumulate;
            }

            @Override public BinaryOperator<Accumulable<Boolean>> combiner() {
                return Accumulable::combine;
            }

            @Override public Function<Accumulable<Boolean>, Optional<Boolean>> finisher() {
                return Mutable::current;
            }

            @Override public Set<Characteristics> characteristics() {
                return EnumSet.of(UNORDERED);
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
