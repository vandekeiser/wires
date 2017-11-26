package fr.cla.wires;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A box connecting wires.
 * Each box has a specific discrete Delay:
 *  changes to input wires are propagated to output wires after this delay.
 */
public abstract class Box {

    protected static final Delay DEFAULT_DELAY = Delay.of(1);

    protected final Clock clock;
    protected final Delay delay;
    private final Clock.Agenda agenda;

    protected Box(Clock clock, Delay delay) {
        this.clock = requireNonNull(clock);
        this.agenda = clock.agenda();
        if(agenda == null) throw new AssertionError(
            "Clock::agenda broke its promise not to return null!"
        );
        this.delay = requireNonNull(delay);
    }

    //Don't make package-private as this is the only alternative to the "Staged Builder"
    protected final <O> void onSignalChanged(Wire<O> observed, OnSignalChanged<O> callback) {
        OnSignalChanged<O> _callback = requireNonNull(callback);

        observed.onSignalChanged(
            agenda.afterDelay(delay, _callback)
        );
    }

    /**
     * Abstract method, so don't call from the constructor (see Effective Java).
     */
    protected abstract Box startup();

    protected static <O> Set<Wire<O>> checkNoNulls(Set<Wire<O>> inputs) {
        inputs = new HashSet<>(requireNonNull(inputs));
        if(inputs.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Detected null wires in " + inputs);
        }
        return inputs;
    }



    //Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------VVVVVVVVVVVVVVVV
    //
    //This follows the "Staged Builder" pattern:
    //    -Every instance of the following protected inner classes is a Builder of some OnSignalChanged callback.
    //    -Each successive Builder has a constructor that captures 1 more intermediary Wire assembly state element than the previous one...
    //    -...except for the terminal void methods, which have the full picture, and call this::onSignalChanged.
    protected final <O, T> ObservedWireCaptured<O, T> onSignalChanged(Wire<O> observed) {
        return new ObservedWireCaptured<>(observed);
    }




    protected class ObservedWireCaptured<O, T> {
        final Wire<O> observed;

        private ObservedWireCaptured(Wire<O> observed) {
            this.observed = requireNonNull(observed);
        }

        public final ObservedAndTargetWiresCaptured<O, T> set(Wire<T> target) {
            return new ObservedAndTargetWiresCaptured<>(observed, requireNonNull(target));
        }
    }




    protected class ObservedAndTargetWiresCaptured<O, T> extends ObservedWireCaptured<O, T> {
        final Wire<T> target;

        private ObservedAndTargetWiresCaptured(Wire<O> observed,Wire<T> target) {
            super(observed);
            this.target = requireNonNull(target);
        }

        public final Applying<O, T> toResultOfApplying() {
            return new Applying<>(observed, target);
        }

        public final InputsAndOutputCaptured<O, T> from(Collection<Wire<O>> inputs) {
            return new InputsAndOutputCaptured<>(
                observed, target, requireNonNull(inputs)
            );
        }
    }




    protected class Applying<O, T> extends ObservedAndTargetWiresCaptured<O, T> {
        private Applying(Wire<O> observed, Wire<T> target) {
            super(observed, target);
        }

        public final void transformation(Function<O, T> transformation) {
            Function<O, T> _transformation = requireNonNull(transformation);

            onSignalChanged(observed,
                newIn -> target.setSignal(
                    newIn.map(_transformation)
                )
            );
        }

        public final <P> void transformation(
            BiFunction<O, P, T> transformation,
            Wire<P> unchangedSecond
        ) {
            BiFunction<O, P, T> _transformation= requireNonNull(transformation);
            Wire<P> _unchangedSecond = requireNonNull(unchangedSecond);

            onSignalChanged(observed,
                newIn1 -> target.setSignal(
                    Signal.map(newIn1, _unchangedSecond.getSignal(), _transformation)
                )
            );
        }

        public final <P> void transformation(
            Wire<P> unchangedFirst,
            BiFunction<P, O, T> transformation
        ) {
            Wire<P> _unchangedFirst = requireNonNull(unchangedFirst);
            BiFunction<P, O, T> _transformation= requireNonNull(transformation);

            onSignalChanged(observed,
                newIn2 -> target.setSignal(
                    Signal.map(_unchangedFirst.getSignal(), newIn2, _transformation)
                )
            );
        }
    }




    protected class InputsAndOutputCaptured<O, T> extends ObservedAndTargetWiresCaptured<O, T> {
        final Collection<Wire<O>> inputs;

        private InputsAndOutputCaptured(
            Wire<O> observed,
            Wire<T> target,
            Collection<Wire<O>> inputs
        ) {
            super(observed, target);
            this.inputs = new HashSet<>(inputs);
        }

        public final void collect(Collector<O, ?, T> collector) {
            Collector<O, ?, T> _collector = requireNonNull(collector);

            onSignalChanged(observed,
                newIn -> target.setSignal(
                    Wire.collect(inputs, _collector)
                )
            );
        }

        public final Reducing<O, T> map(Function<O, T> accumulationValue) {
            return new Reducing<>(
                observed, target, inputs,
                requireNonNull(accumulationValue)
            );
        }
    }




    protected class Reducing<O, T> extends InputsAndOutputCaptured<O, T> {
        private final Function<O, T> accumulationValue;

        private Reducing(
            Wire<O> observed,
            Wire<T> target,
            Collection<Wire<O>> inputs,
            Function<O, T> accumulationValue
        ) {
            super(observed, target, inputs);
            this.accumulationValue = requireNonNull(accumulationValue);
        }

        public final void reduce(BinaryOperator<T> accumulator, T identity) {
            BinaryOperator<T> _accumulator = requireNonNull(accumulator);
            T _identity = requireNonNull(identity);

            onSignalChanged(observed,
                newIn -> target.setSignal(
                    Wire.mapAndReduce(inputs, accumulationValue, _accumulator, _identity)
                )
            );
        }
    }
    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------^^^^^^^^^^^^^^^^

}
//@formatter:on
