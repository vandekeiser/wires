package fr.cla.wires.core.boxes;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.support.oo.Accumulable;
import fr.cla.wires.support.oo.Mutable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off
/**
 * TODO: javadoc in same style as CollectHomogeneousInputsToOutputOfSameType
 * A Box that has N inputs, but all of the same type of {@code Signal}.
 * @param <O> O like "observed".
 *           The type of {@code Signal} that transits on the observed {@code Wire}s.
 *           This means this Box's input is N {@code Wire}s of type {@code O}.
 * @param <T> T like "target".
 *           The type of {@code Signal} that transits on the target {@code Wire}
 *           This means this Box's output is 1 {@code Wire} of type {@code T}.
 */
public abstract class CollectHomogeneousInputs<O, T>
extends Box {

    private final List<Wire<O>> ins;
    private final Wire<T> out;

    protected CollectHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected CollectHomogeneousInputs(List<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.ins = checkNoNulls(ins);
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
    protected CollectHomogeneousInputs<O, T> startup() {
        ins.forEach(this::startup);
        return this;
    }

    /**
     * The DSL implemented by the "Staged Builder" pattern translates:
     * {@code
     *      onSignalChanged(in)
     *          .set(out)
     *          .from(ins)
     *          .collect(collector())
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(in,
     *          newIn -> out.setSignal(
     *              collect(ins, collector())
     *          )
     *      );
     * }
     */
    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .from(ins)
            .collect(collector())
        ;
    }

    private Collector<O, ?, T> collector() {
        return new AccumulableCollector<>(
            accumulationValue(), accumulator()
        );
    }

    protected abstract Function<O, T> accumulationValue();
    protected abstract BinaryOperator<T> accumulator();




    private static class AccumulableCollector<O, T>
    implements Collector<O, Accumulable<O, T>, T> {
        private final Function<O, T> accumulationValue;
        private final BinaryOperator<T> accumulator;

        private AccumulableCollector(
            Function<O, T> accumulationValue,
            BinaryOperator<T> accumulator
        )  {
            this.accumulationValue = requireNonNull(accumulationValue);
            this.accumulator = requireNonNull(accumulator);
        }

        @Override public Supplier<Accumulable<O, T>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                accumulationValue, accumulator
            );
        }

        @Override public BiConsumer<Accumulable<O, T>, O> accumulator() {
            return Accumulable::accumulate;
        }

        @Override public BinaryOperator<Accumulable<O, T>> combiner() {
            return Accumulable::combine;
        }

        @Override public Function<Accumulable<O, T>, T> finisher() {
            return Mutable::get;
        }

        @Override public Set<Characteristics> characteristics() {
            //TODO some collectors might not be UNORDERED
            return EnumSet.of(UNORDERED);
        }
    }

}
//@formatter:on
