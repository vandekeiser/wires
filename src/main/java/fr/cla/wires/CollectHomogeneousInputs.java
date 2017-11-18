package fr.cla.wires;

import fr.cla.support.oo.Accumulable;
import fr.cla.support.oo.Mutable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off
public abstract class CollectHomogeneousInputs<O, T> extends Box {

    private final Set<Wire<O>> ins;
    private final Wire<T> out;

    protected CollectHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    protected CollectHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Time time, Delay delay) {
        super(delay, time);
        this.ins = requireNonNull(ins);
        this.out = requireNonNull(out);
    }

    /**
     * Don't do the startup in the constructor to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     * Implementation note:
     *     The contract of overriders is to call super.startup(), return this:
     *     this method is only not marked final as a convenience to allow covariant return.
     * @return this Box, started.
     */
    protected CollectHomogeneousInputs<O, T> startup() {
        ins.forEach(this::startup);
        return this;
    }

    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .withInputs(this.ins)
            .withCollection(collection())
        ;
    }

    protected final Collector<Optional<O>, ?, Optional<T>> collection() {
        return collection(accumulatorConstructor(), accumulator(), combiner());
    }

    protected abstract Function<O, T> accumulatorConstructor();
    protected abstract BiFunction<T, O, T> accumulator();
    protected abstract BinaryOperator<T> combiner();

    private Collector<Optional<O>, ?, Optional<T>> collection(
        Function<O, T> accumulationValue,
        BiFunction<T, O, T> accumulator,
        BinaryOperator<T> combiner
    ) {
        return new Collector<Optional<O>, Accumulable<T, O>, Optional<T>>() {
            @Override public Supplier<Accumulable<T, O>> supplier() {
                return () -> Accumulable.initiallyUnset(
                    accumulationValue, accumulator, combiner
                );
            }

            @Override public BiConsumer<Accumulable<T, O>, Optional<O>> accumulator() {
                return Accumulable::accumulate;
            }

            @Override public BinaryOperator<Accumulable<T, O>> combiner() {
                return Accumulable::combine;
            }

            @Override public Function<Accumulable<T, O>, Optional<T>> finisher() {
                return Mutable::current;
            }

            @Override public Set<Characteristics> characteristics() {
                //TODO some collections might not be UNORDERED
                return EnumSet.of(UNORDERED);
            }
        };
    }

    protected static <O> Set<Wire<O>> checkNoNulls(Set<Wire<O>> ins) {
        ins = new HashSet<>(requireNonNull(ins));
        if(ins.stream().anyMatch(w -> w == null)) {
            throw new NullPointerException("Detected null wires in " + ins);
        }
        return ins;
    }

}
//@formatter:on
