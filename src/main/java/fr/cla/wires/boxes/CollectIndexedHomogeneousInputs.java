package fr.cla.wires.boxes;

import fr.cla.support.functional.Indexed;
import fr.cla.support.oo.Accumulable;
import fr.cla.support.oo.Mutable;
import fr.cla.wires.Box;
import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off

/**
 * Try this abstraction to take index into account for neural networks (use weigth matrix).
 * @param <O> Same as in Box
 * @param <T> Same as in Box
 * @param <I> Same as in Box
 */
public abstract class CollectIndexedHomogeneousInputs<O, T, I>
extends Box {

    private final List<Wire<O>> ins;
    private final Wire<T> out;

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.ins = checkedNoNulls(ins);
        this.out = requireNonNull(out);
    }

    /**
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * @implNote The contract for overriders is to call super.startup(), return this:
     * This method is only not marked final as a convenience to allow covariant return.
     *
     * @return this Box, started.
     */
    protected CollectIndexedHomogeneousInputs<O, T, I> startup() {
        ins.forEach(this::startup);
        return this;
    }

    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .collectIndexed(collector())
        ;
    }

    private Collector<Indexed<O>, ?, T> collector() {
        //TODO
        /*return new CollectIndexedHomogeneousInputs.AccumulableCollector<>(
           accumulationValue(), accumulator(), combiner()
        );*/
        return null;
    }

    protected abstract Function<Indexed<O>, T> accumulationValue();
    //can this not be BiFunction<T, T, T>???
    //pr debusquer le pb ecrire une boite Bool->Int
    protected abstract BiFunction<T, O, T> accumulator();
    protected abstract BinaryOperator<T> combiner();




    private static class AccumulableCollector<O, T>
    implements Collector<O, Accumulable<T, O>, T> {
        private final Function<O, T> accumulationValue;
        private final BiFunction<T, O, T> accumulator;
        private final BinaryOperator<T> combiner;

        private AccumulableCollector(
            Function<O, T> accumulationValue,
            BiFunction<T, O, T> accumulator,
            BinaryOperator<T> combiner
        )  {
            this.accumulationValue = requireNonNull(accumulationValue);
            this.accumulator = requireNonNull(accumulator);
            this.combiner = requireNonNull(combiner);
        }

        @Override public Supplier<Accumulable<T, O>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                accumulationValue, accumulator, combiner
            );
        }

        @Override public BiConsumer<Accumulable<T, O>, O> accumulator() {
            return Accumulable::accumulate;
        }

        @Override public BinaryOperator<Accumulable<T, O>> combiner() {
            return Accumulable::combine;
        }

        @Override public Function<Accumulable<T, O>, T> finisher() {
            return Mutable::get;
        }

        @Override public Set<Characteristics> characteristics() {
            //TODO some collectors might not be UNORDERED
            return EnumSet.of(UNORDERED);
        }
    }

}
//@formatter:on
