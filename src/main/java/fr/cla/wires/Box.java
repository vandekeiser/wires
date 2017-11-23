package fr.cla.wires;

import java.util.*;
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
    protected final <O> void onSignalChanged(Wire<O> observedWire, OnSignalChanged<O> callback) {
        OnSignalChanged<O> _callback = requireNonNull(callback);

        observedWire.onSignalChanged(
            agenda.afterDelay(delay, _callback)
        );
    }

    /**
     * Abstract method, so don't call from the constructor (see Effective Java).
     */
    protected abstract Box startup();

    protected static <O> Set<Wire<O>> checkNoNulls(Set<Wire<O>> ins) {
        ins = new HashSet<>(requireNonNull(ins));
        if(ins.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Detected null wires in " + ins);
        }
        return ins;
    }



    //Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------VVVVVVVVVVVVVVVV
    //
    //This follows the "Staged Builder" pattern:
    //    -Every instance of the following protected inner classes is a Builder of some OnSignalChanged callback.
    //    -Each successive Builder has a constructor that captures 1 more intermediary Wire assembly state element than the previous one...
    //    -...except for the terminal void methods, which have the full picture, and call this::onSignalChanged.
    protected final <O, T> ObservedWireCaptured<O, T> onSignalChanged(Wire<O> observedWire) {
        return new ObservedWireCaptured<>(observedWire);
    }




    protected class ObservedWireCaptured<O, T> {
        final Wire<O> observedWire;

        private ObservedWireCaptured(Wire<O> observedWire) {
            this.observedWire = requireNonNull(observedWire);
        }

        public final ObservedAndTargetWiresCaptured<O, T> set(Wire<T> targetWire) {
            return new ObservedAndTargetWiresCaptured<>(
                observedWire,
                requireNonNull(targetWire)
            );
        }
    }




    protected class ObservedAndTargetWiresCaptured<O, T>
    extends ObservedWireCaptured<O, T> {
        final Wire<T> targetWire;

        private ObservedAndTargetWiresCaptured(Wire<O> observedWire,Wire<T> targetWire) {
            super(observedWire);
            this.targetWire = requireNonNull(targetWire);
        }

        public final Applying<O, T> toResultOfApplying() {
            return new Applying<>(
                observedWire, targetWire
            );
        }

        public final InputsAndOutputCaptured<O, T> from(Collection<Wire<O>> inputs) {
            return new InputsAndOutputCaptured<>(
                observedWire, targetWire, requireNonNull(inputs)
            );
        }
    }




    protected class Applying<O, T>
    extends ObservedAndTargetWiresCaptured<O, T> {
        private Applying(Wire<O> observedWire, Wire<T> targetWire) {
            super(observedWire, targetWire);
        }

        public final void transformation(Function<O, T> transformation) {
            Function<O, T> _transformation = requireNonNull(transformation);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                    newIn.map(_transformation)
                )
            );
        }

        public final <P> void transformation(
            BiFunction<O, P, T> transformation,
            Wire<P> unchangedSecondWire
        ) {
            BiFunction<O, P, T> _transformation= requireNonNull(transformation);
            Wire<P> _unchangedSecondWire = requireNonNull(unchangedSecondWire);

            onSignalChanged(observedWire,
                newIn1 -> targetWire.setSignal(
                    Signal.map(newIn1, _unchangedSecondWire.getSignal(), _transformation)
                )
            );
        }

        public final <P> void transformation(
            Wire<P> unchangedFirstWire,
            BiFunction<P, O, T> transformation
        ) {
            Wire<P> _unchangedFirstWire = requireNonNull(unchangedFirstWire);
            BiFunction<P, O, T> _transformation= requireNonNull(transformation);

            onSignalChanged(observedWire,
                newIn2 -> targetWire.setSignal(
                    Signal.map(_unchangedFirstWire.getSignal(), newIn2, _transformation)
                )
            );
        }
    }




    protected class InputsAndOutputCaptured<O, T>
    extends ObservedAndTargetWiresCaptured<O, T> {
        final Collection<Wire<O>> allInputs;

        private InputsAndOutputCaptured(
            Wire<O> observedWire,
            Wire<T> targetWire,
            Collection<Wire<O>> allInputs
        ) {
            super(observedWire, targetWire);
            this.allInputs = new HashSet<>(allInputs);
        }

        public final void collect(Collector<O, ?, T> collector) {
            Collector<O, ?, T> _collector = requireNonNull(collector);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                Wire.collect(allInputs, _collector)
            ));
        }

        public final Reducing<O, T> map(Function<O, T> accumulationValue) {
            return new Reducing<>(
                observedWire,
                targetWire,
                allInputs,
                requireNonNull(accumulationValue)
            );
        }
    }




    protected class Reducing<O, T>
    extends InputsAndOutputCaptured<O, T> {
        private final Function<O, T> accumulationValue;

        private Reducing(
            Wire<O> observedWire,
            Wire<T> targetWire,
            Collection<Wire<O>> allInputs,
            Function<O, T> accumulationValue
        ) {
            super(observedWire, targetWire, allInputs);
            this.accumulationValue = requireNonNull(accumulationValue);
        }

        public final void reduce(BinaryOperator<T> reducer, T neutralElement) {
            BinaryOperator<T> _reducer = requireNonNull(reducer);
            T _neutralElement = requireNonNull(neutralElement);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                    Wire.mapAndReduce(allInputs, accumulationValue, _reducer, _neutralElement)
                )
            );
        }
    }
    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------^^^^^^^^^^^^^^^^

}
//@formatter:on
