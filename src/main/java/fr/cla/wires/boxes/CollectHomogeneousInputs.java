package fr.cla.wires.boxes;

import fr.cla.support.oo.Accumulable;
import fr.cla.support.oo.Mutable;
import fr.cla.wires.Box;
import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;
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

    private final Set<Wire<O>> ins;
    private final Wire<T> out;

    protected CollectHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    protected CollectHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.ins = requireNonNull(ins);
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

    private Collector<Optional<O>, ?, Optional<T>> collector() {
        return collector(accumulationValue(), accumulator(), combiner());
    }

    protected abstract Function<O, T> accumulationValue();
    protected abstract BiFunction<T, O, T> accumulator();
    protected abstract BinaryOperator<T> combiner();




    private Collector<Optional<O>, ?, Optional<T>> collector(
        Function<O, T> accumulationValue,
        BiFunction<T, O, T> accumulator,
        BinaryOperator<T> combiner
    ) {
        return new Collector<Optional<O>, Accumulable<T, O>, Optional<T>>() {
            @Override public Supplier<Accumulable<T, O>> supplier() {
                Function<O, T> _accumulationValue = requireNonNull(accumulationValue);
                BiFunction<T, O, T> _accumulator = requireNonNull(accumulator);
                BinaryOperator<T> _combiner = requireNonNull(combiner);

                return () -> Accumulable.initiallyEmpty(
                    _accumulationValue, _accumulator, _combiner
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
                //TODO some collectors might not be UNORDERED
                return EnumSet.of(UNORDERED);
            }
        };
    }

}
//@formatter:on
