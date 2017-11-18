package fr.cla.wires;

import java.util.function.BiFunction;
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

    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs--------------VVVVVVVVVVVVVVVV
    // (call onSignalChanged() directly if you have 3 or more inputs)

    /**
     * @param <I> The type of input to read on @{code observedWire}
     * @param <O> The type of output to write on @{code targetWire}
     */
    protected <I, O> OnSignalChangedBuilder<I, O> onSignalChanged(Wire<I> observedWire) {
        return new OnSignalChangedBuilder<>(observedWire);
    }

    protected class OnSignalChangedBuilder<I, O> {
        private Wire<I> observedWire;
        private Wire<O> targetWire;

        OnSignalChangedBuilder(Wire<I> observedWire) {
            this.observedWire = requireNonNull(observedWire);
        }

        public OnSignalChangedBuilder<I, O> set(Wire<O> targetWire) {
            this.targetWire = requireNonNull(targetWire);
            return this;
        }

        /**
         * Applies {@code transformation} to the changed Signal
         */
        public void toResultOf(Function<I, O> transformation) {
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
        public <J> void toResultOf(
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
        public <J> void toResultOf(
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
    }
    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs--------------^^^^^^^^^^^^^^^^

}
