package fr.cla.wires;

import fr.cla.Accumulable;
import fr.cla.Mutable;

import java.util.EnumSet;
import java.util.HashSet;
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

    //Don't do the startup in the constructor to not let "this" escape through the method ref,
    // so that the Box is "properly constructed".
    //protected ReduceHomogeneousInputs<O, T, B> startup() {
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

    protected abstract Collector<Optional<O>, ?, Optional<T>> collection();


//    protected final Collector<Optional<O>, ?, Optional<T>> collection() {
//        return collection(binaryOperator());
//    }
//
//    protected abstract BinaryOperator<O> binaryOperator();
//
//    private Collector<Optional<Boolean>, ?, Optional<Boolean>> collection(
//        BinaryOperator<Boolean> binaryOperator
//    ) {
//        return new Collector<Optional<Boolean>, Accumulable<O>, Optional<Boolean>>() {
//            @Override public Supplier<Accumulable<O>> supplier() {
//                return () -> Accumulable.initiallyUnset(binaryOperator);
//            }
//
//            @Override public BiConsumer<Accumulable<O>, Optional<O>> accumulator() {
//                return Accumulable::accumulate;
//            }
//
//            @Override public BinaryOperator<Accumulable<O>> combiner() {
//                return Accumulable::combine;
//            }
//
//            @Override public Function<Accumulable<O>, Optional<O>> finisher() {
//                return Mutable::current;
//            }
//
//            @Override public Set<Characteristics> characteristics() {
//                return EnumSet.of(UNORDERED);
//            }
//        };
//    }

    protected static <O> Set<Wire<O>> checkNoNulls(Set<Wire<O>> ins) {
        ins = new HashSet<>(requireNonNull(ins));
        if(ins.stream().anyMatch(w -> w == null)) {
            throw new NullPointerException("Detected null wires in " + ins);
        }
        return ins;
    }

}
//@formatter:on
