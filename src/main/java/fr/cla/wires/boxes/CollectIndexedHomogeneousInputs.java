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

import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off
/**
 * Take the index of observed Wires into account for neural networks (use a weight matrix).
 * @param <O> The type of Signal that transits on observed Wires, same as in Box
 * @param <T> The type of Signal that transits on target Wires, same as in Box
 * @param <I> The type of index for observed Wires
 */
public abstract class CollectIndexedHomogeneousInputs<O, T, I>
extends Box {

    private final List<Wire<O>> ins;
    private final List<Wire<T>> outs;

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
        this(ins, singletonList(out), clock, delay);
    }

    protected CollectIndexedHomogeneousInputs(List<Wire<O>> ins, List<Wire<T>> outs, Clock clock, Delay delay) {
        super(clock, delay);
        this.ins = checkNoNulls(ins);
        this.outs = checkNoNulls(outs);
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

    private CollectIndexedHomogeneousInputs<O, T, I> startup(Wire<O> in) {
        outs.forEach(out -> startup(in, out));
        return this;
    }

    private void startup(Wire<O> in, Wire<T> out) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .collectIndexed(collector())
        ;
    }

    private Collector<Indexed<O>, ?, T> collector() {
        return new CollectIndexedHomogeneousInputs.AccumulableCollector<>(
           accumulationValue(), accumulator(), combiner(), finisher()
        );
    }

    protected abstract Function<Indexed<O>, T> accumulationValue();
    protected abstract BiFunction<T, Indexed<O>, T> accumulator();
    protected abstract BinaryOperator<T> combiner();
    protected abstract Function<T, T> finisher();




    private static class AccumulableCollector<O, T>
    implements Collector<Indexed<O>, Accumulable<T, Indexed<O>>, T> {
        private final Function<Accumulable<T, Indexed<O>>, T> getAccumulated = Mutable::get;
        private final Function<Indexed<O>, T> accumulationValue;
        private final BiFunction<T, Indexed<O>, T> accumulator;
        private final BinaryOperator<T> combiner;
        private final Function<T, T> finisher;

        private AccumulableCollector(
            Function<Indexed<O>, T> accumulationValue,
            BiFunction<T, Indexed<O>, T> accumulator,
            BinaryOperator<T> combiner,
            Function<T, T> finisher
        )  {
            this.accumulationValue = requireNonNull(accumulationValue);
            this.accumulator = requireNonNull(accumulator);
            this.combiner = requireNonNull(combiner);
            this.finisher = requireNonNull(finisher);
        }

        @Override public Supplier<Accumulable<T, Indexed<O>>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                accumulationValue, accumulator, combiner
            );
        }

        @Override public BiConsumer<Accumulable<T, Indexed<O>>, Indexed<O>> accumulator() {
            return Accumulable::accumulate;
        }

        @Override public BinaryOperator<Accumulable<T, Indexed<O>>> combiner() {
            return Accumulable::combine;
        }

        @Override public Function<Accumulable<T, Indexed<O>>, T> finisher() {
            return getAccumulated.andThen(finisher);
        }

        @Override public Set<Characteristics> characteristics() {
            //TODO some collectors might not be UNORDERED
            return EnumSet.of(UNORDERED);
        }
    }

}
//@formatter:on
