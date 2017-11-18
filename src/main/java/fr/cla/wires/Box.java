package fr.cla.wires;

import fr.cla.Monads;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public abstract class Box {

    protected static final Delay DEFAULT_DELAY = Delay.of(1);
    private final Delay delay;
    private final Agenda agenda;

    protected Box(Delay delay, Time time) {
        this.delay = requireNonNull(delay);
        this.agenda = time.agenda();
        if(agenda == null) throw new AssertionError("Time::agenda promised not to return null!");
    }

    protected <V> void onSignalChanged(Wire<V> observedWire, OnSignalChanged<V> callback) {
        OnSignalChanged<V> _callback = requireNonNull(callback);

        observedWire.onSignalChanged(
            agenda.afterDelay(delay, _callback)
        );
    }

    protected <I, O> OnSignalChangedBuilder_observedWireCaptured<I, O> onSignalChanged(Wire<I> observedWire) {
        return new OnSignalChangedBuilder_observedWireCaptured<>(observedWire);
    }

    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------VVVVVVVVVVVVVVVV
    protected class OnSignalChangedBuilder_observedWireCaptured<I,O> {
        protected Wire<I> observedWire;

        OnSignalChangedBuilder_observedWireCaptured(Wire<I> observedWire) {
            this.observedWire = requireNonNull(observedWire);
        }

        public OnSignalChangedBuilder_observedAndTargetWiresCaptured<I, O> set(Wire<O> targetWire) {
            return new OnSignalChangedBuilder_observedAndTargetWiresCaptured<>(
                observedWire,
                requireNonNull(targetWire)
            );
        }
    }

    protected class OnSignalChangedBuilder_observedAndTargetWiresCaptured<I, O>
    extends OnSignalChangedBuilder_observedWireCaptured<I, O>{
        protected Wire<O> targetWire;

        OnSignalChangedBuilder_observedAndTargetWiresCaptured(Wire<I> observedWire, Wire<O> targetWire) {
            super(observedWire);
            this.targetWire = requireNonNull(targetWire);
        }

        /**
         * Applies {@code transformation} to the changed Signal
         */
        public void toResultOfApplying(Function<I, O> transformation) {
            Function<I, O> _transformation = requireNonNull(transformation);

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
        public <J> void toResultOfApplying(
            BiFunction<I, J, O> transformation,
            Wire<J> unchangedSecondWire
        ) {
            BiFunction<I, J, O> _transformation= requireNonNull(transformation);
            Wire<J> _unchangedSecondWire = requireNonNull(unchangedSecondWire);

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
        public <J> void toResultOfApplying(
            Wire<J> unchangedFirstWire,
            BiFunction<J, I, O> transformation
        ) {
            Wire<J> _unchangedFirstWire = requireNonNull(unchangedFirstWire);
            BiFunction<J, I, O> _transformation= requireNonNull(transformation);

            onSignalChanged(observedWire,
                newIn2 -> targetWire.setSignal(
                    Signal.map(_unchangedFirstWire.getSignal(), newIn2, _transformation)
                )
            );
        }

        public OnSignalChangedBuilderAll_Multiple<I, O> toResultOfReducing(Collection<Wire<I>> allInputs) {
            return new OnSignalChangedBuilderAll_Multiple<>(observedWire, targetWire, allInputs);
        }
    }

    protected class OnSignalChangedBuilderAll_Multiple<I, O>
    extends OnSignalChangedBuilder_observedAndTargetWiresCaptured<I, O>{
        protected Collection<Wire<I>> allInputs;

        OnSignalChangedBuilderAll_Multiple(Wire<I> observedWire, Wire<O> targetWire, Collection<Wire<I>> allInputs) {
            super(observedWire, targetWire);
            this.allInputs = new HashSet<>(allInputs);
        }

        public OnSignalChangedBuilderAll_Reducing<I, O> withMapping(Function<I, O> mapper) {
            return new OnSignalChangedBuilderAll_Reducing<>(
                observedWire,
                targetWire,
                allInputs,
                requireNonNull(mapper)
            );
        }
    }


    protected class OnSignalChangedBuilderAll_Reducing<I, O>
    extends OnSignalChangedBuilderAll_Multiple<I, O>{
        private Function<I, O> mapper;

        OnSignalChangedBuilderAll_Reducing(
            Wire<I> observedWire,
            Wire<O> targetWire,
            Collection<Wire<I>> allInputs,
            Function<I, O> mapper
        ) {
            super(observedWire, targetWire, allInputs);
            this.mapper = requireNonNull(mapper);
        }

        public void withReduction(BinaryOperator<O> reducer, O neutralElement) {
            BinaryOperator<O> _reducer = requireNonNull(reducer);
            O _neutralElement = requireNonNull(neutralElement);

            onSignalChanged(observedWire,
                newIn -> targetWire.setSignal(mapAndReduce(allInputs, mapper, _reducer, _neutralElement))
            );
        }

        private Signal<O> mapAndReduce(Collection<Wire<I>> allInputs, Function<I, O> mapper, BinaryOperator<O> reducer, O neutralElement) {
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
