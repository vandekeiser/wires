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



    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------VVVVVVVVVVVVVVVV
    //----------(this follows the "Staged Builder" pattern)-------------
    protected final <O, T> OnSignalChangedBuilder_ObservedWireCaptured<O, T> onSignalChanged(Wire<O> observedWire) {
        return new OnSignalChangedBuilder_ObservedWireCaptured<>(observedWire);
    }




    /**
    * Stage 1: Capture the input wire to observe.
    */
    protected class OnSignalChangedBuilder_ObservedWireCaptured<O, T> {
        final Wire<O> observedWire;

        private OnSignalChangedBuilder_ObservedWireCaptured(Wire<O> observedWire) {
            this.observedWire = requireNonNull(observedWire);
        }

        /**
         * @return goto Stage 2: next, capture the input wire to observe.
         */
        public final OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<O, T> set(Wire<T> targetWire) {
            return new OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<>(
                observedWire,
                requireNonNull(targetWire)
            );
        }
    }




    /**
     * Stage 2: Captures the input wire to observe, and the output wire to set.
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

        /**
         * @return goto Stage 3.1: next, apply a transformation to the 1 Wire,
         *  maybe (depending on what is called next)
         *  taking into account a 2nd Wire depending on whether this Box has 1 or 2 input wires.
         */
        public final OnSignalChangedBuilder_Applying<O, T> toResultOfApplying() {
            return new OnSignalChangedBuilder_Applying<>(
                observedWire, targetWire
            );
        }

        /**
         * @return goto Stage 3.2: next, capture N homogeneous input Wires
         *  to apply a transformation (reduce / collect) to.
         */
        public final OnSignalChangedBuilder_InputsAndOutputCaptured<O, T> from(Collection<Wire<O>> inputs) {
            return new OnSignalChangedBuilder_InputsAndOutputCaptured<>(
                observedWire, targetWire, requireNonNull(inputs)
            );
        }
    }




    /**
     * Stage 3.1: Enables applying a transformation to 1 or 2 inputs.
     */
    protected class OnSignalChangedBuilder_Applying<O, T>
    extends OnSignalChangedBuilder_ObservedAndTargetWiresCaptured<O, T> {
        private OnSignalChangedBuilder_Applying(Wire<O> observedWire, Wire<T> targetWire) {
            super(observedWire, targetWire);
        }

        /**
        * Stage 3.1.1: Apply the transformation to just the changed input.
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
         * Stage 3.1.2: Apply the transformation to (the changed input, the other input).
         *
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
         * Stage 3.1.3: Apply the transformation to (the other input, the changed input).
         *
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
     * Stage 3.2: Captures N homogeneous inputs.
     *  Next, apply a transformation (reduce / collect) to the N input Wires
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

        /**
         * Stage 4.1: Collection inputs->output is now fully specified, register it.
         */
        public final void collect(Collector<Optional<O>, ?, Optional<T>> collector) {
            Collector<Optional<O>, ?, Optional<T>> _collector = requireNonNull(collector);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                Signal.collect(allInputs, _collector)
            ));
        }

        /**
         * Specify the accumulationValue to apply to inputs for reduction.
         * @return goto Stage 4.2: next, specify the reducer to apply.
         */
        public final OnSignalChangedBuilder_Reducing<O, T> map(Function<O, T> accumulationValue) {
            return new OnSignalChangedBuilder_Reducing<>(
                observedWire,
                targetWire,
                allInputs,
                requireNonNull(accumulationValue)
            );
        }
    }




    /**
     * Stage 4.2: Specify the accumulationValue to apply to inputs for reduction.
     *  That function will be mapped to inputs before reduce.
     */
    protected class OnSignalChangedBuilder_Reducing<O, T>
    extends OnSignalChangedBuilder_InputsAndOutputCaptured<O, T> {
        private final Function<O, T> accumulationValue;

        private OnSignalChangedBuilder_Reducing(
            Wire<O> observedWire,
            Wire<T> targetWire,
            Collection<Wire<O>> allInputs,
            Function<O, T> accumulationValue
        ) {
            super(observedWire, targetWire, allInputs);
            this.accumulationValue = requireNonNull(accumulationValue);
        }

        /**
         * Stage 4.2.1: Now that the reduction itself is known,
         *  reduction inputs->output is now fully specified, register it.
         */
        public final void reduce(BinaryOperator<T> reducer, T neutralElement) {
            BinaryOperator<T> _reducer = requireNonNull(reducer);
            T _neutralElement = requireNonNull(neutralElement);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(
                    Signal.mapAndReduce(allInputs, accumulationValue, _reducer, _neutralElement)
                )
            );
        }
    }
    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------^^^^^^^^^^^^^^^^

}
//@formatter:on
