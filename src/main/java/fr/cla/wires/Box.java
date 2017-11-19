package fr.cla.wires;

import fr.cla.support.functional.Monads;

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
    private final Delay delay;
    private final Agenda agenda;

    protected Box(Delay delay, Time time) {
        this.delay = requireNonNull(delay);
        this.agenda = time.agenda();
        if(agenda == null) throw new AssertionError("Time::agenda broke its promise not to return null!");
    }

    //Don't make private as this is the only alternative to the "Staged Builder"
    protected final <O> void onSignalChanged(Wire<O> observedWire, OnSignalChanged<O> callback) {
        OnSignalChanged<O> _callback = requireNonNull(callback);

        observedWire.onSignalChanged(
            agenda.afterDelay(delay, _callback)
        );
    }

    protected static final <O> Set<Wire<O>> checkNoNulls(Set<Wire<O>> ins) {
        ins = new HashSet<>(ins);
        if(ins.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Detected null wires in " + ins);
        }
        return ins;
    }



    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------VVVVVVVVVVVVVVVV
    //----------(this follows the "Staged Builder" pattern)-------------
    protected final <O, T> OnSignalChangedBuilder_ObservedWireCaptured<O, T> onSignalChanged(Wire<O> observedWire) {
        return new OnSignalChangedBuilder_ObservedWireCaptured<>(observedWire);
    }




    /**
    * Captures the input wire to observe
    */
    protected class OnSignalChangedBuilder_ObservedWireCaptured<O, T> {
        final Wire<O> observedWire;

        private OnSignalChangedBuilder_ObservedWireCaptured(Wire<O> observedWire) {
            this.observedWire = requireNonNull(observedWire);
        }

        public final OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<O, T> set(Wire<T> targetWire) {
            return new OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<>(
                observedWire,
                requireNonNull(targetWire)
            );
        }
    }




    /**
     * Captures the input wire to observe, and the output wire to set
     */
    protected class OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<O, T>
    extends OnSignalChangedBuilder_ObservedWireCaptured<O, T> {
        final Wire<T> targetWire;

        private OnSignalChangedBuilder_ObservedAndTargetWiresCaptured(
            Wire<O> observedWire,
            Wire<T> targetWire
        ) {
            super(observedWire);
            this.targetWire = requireNonNull(targetWire);
        }

        public final OnSignalChangedBuilder_Applying<O, T> toResultOfApplying() {
            return new OnSignalChangedBuilder_Applying<>(
                observedWire, targetWire
            );
        }

        public final OnSignalChangedBuilder_InputsAndOutputCaptured<O, T> from(Collection<Wire<O>> inputs) {
            return new OnSignalChangedBuilder_InputsAndOutputCaptured<>(
                observedWire, targetWire, requireNonNull(inputs)
            );
        }
    }




    /**
     * Enables applying a mapper to 1 or 2 inputs
     */
    protected class OnSignalChangedBuilder_Applying<O, T>
    extends OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<O, T> {
        private OnSignalChangedBuilder_Applying(
            Wire<O> observedWire,
            Wire<T> targetWire
        ) {
            super(observedWire, targetWire);
        }

        /**
         * Applies {@code transformation} to the changed Signal
         */
        public final void transformation(Function<O, T> transformation) {
            Function<O, T> _transformation = requireNonNull(transformation);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                    newIn.map(_transformation)
                )
            );
        }

        /**
         * Applies {@code transformation} to (changed Signal, unchanged Signal).
         * Of course calling this method or the other BiFunction one
         *  only has an impact if {@code transformation} is not symmetrical.
         * @param transformation The function to apply to (changed Wire, unchanged Wire)
         * @param unchangedSecondWire The unchanged Wire, used as the 2nd parameter of {@code transformation}
         */
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

        /**
         * Applies {@code transformation} to (unchanged Signal, changed Signal).
         * Of course calling this method or the other BiFunction one
         *  only has an impact if {@code transformation} is not symmetrical.
         * @param unchangedFirstWire The unchanged Wire, used as the 1st parameter of {@code transformation}
         * @param transformation The function to apply to (unchanged Wire, changed Wire)
         */
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




    /**
     * Captures N homogeneous inputs
     */
    protected class OnSignalChangedBuilder_InputsAndOutputCaptured<O, T>
    extends OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<O, T> {
        final Collection<Wire<O>> allInputs;

        private OnSignalChangedBuilder_InputsAndOutputCaptured(
            Wire<O> observedWire,
            Wire<T> targetWire,
            Collection<Wire<O>> allInputs
        ) {
            super(observedWire, targetWire);
            this.allInputs = new HashSet<>(allInputs);
        }

        public final OnSignalChangedBuilder_Reducing<O, T> map(Function<O, T> mapper) {
            return new OnSignalChangedBuilder_Reducing<>(
                observedWire,
                targetWire,
                allInputs,
                requireNonNull(mapper)
            );
        }

        public final void collect(Collector<Optional<O>, ?, Optional<T>> collector) {
            Collector<Optional<O>, ?, Optional<T>> _collector = requireNonNull(collector);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                    collect(allInputs, _collector)
                )
            );
        }

        private Signal<T> collect(
            Collection<Wire<O>> allInputs,
            Collector<Optional<O>, ?, Optional<T>> collector
        ) {
            return allInputs.stream()
                .map(Wire::getSignal)
                .map(Signal::getValue)
                .collect(collector)
                .map(Signal::of)
                .orElse(Signal.none())
            ;
        }
    }




    /**
     * Enables applying a reduction to N homogeneous inputs
     */
    protected class OnSignalChangedBuilder_Reducing<O, T>
    extends OnSignalChangedBuilder_InputsAndOutputCaptured<O, T> {
        private final Function<O, T> mapper;

        private OnSignalChangedBuilder_Reducing(
            Wire<O> observedWire,
            Wire<T> targetWire,
            Collection<Wire<O>> allInputs,
            Function<O, T> mapper
        ) {
            super(observedWire, targetWire, allInputs);
            this.mapper = requireNonNull(mapper);
        }

        public final void reduce(BinaryOperator<T> reducer, T neutralElement) {
            BinaryOperator<T> _reducer = requireNonNull(reducer);
            T _neutralElement = requireNonNull(neutralElement);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                    mapAndReduce(allInputs, mapper, _reducer, _neutralElement)
                )
            );
        }

        private Signal<T> mapAndReduce(
            Collection<Wire<O>> allInputs,
            Function<O, T> mapper,
            BinaryOperator<T> reducer,
            T neutralElement
        ) {
            return Signal.of(allInputs.stream()
                .map(Wire::getSignal)
                .map(Signal::getValue)
                .map(maybeValue -> maybeValue.map(mapper))
                .reduce(
                    Optional.of(neutralElement),
                    Monads.liftOptional(reducer)
                ).get()
            );
        }
    }
    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------^^^^^^^^^^^^^^^^

}
//@formatter:on
