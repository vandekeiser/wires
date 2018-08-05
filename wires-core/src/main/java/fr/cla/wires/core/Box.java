package fr.cla.wires.core;

import fr.cla.wires.support.functional.Indexed;
import fr.cla.wires.support.oo.Accumulable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
    private final Signal.WhenCombining combiningPolicy;

    protected Box(
        Clock clock,
        Delay delay,
        Signal.WhenCombining combiningPolicy
    ) {
        this.clock = requireNonNull(clock);
        this.agenda = clock.agenda();
        if(agenda == null) throw new AssertionError(
            "Clock::agenda broke its promise not to return null!"
        );
        this.delay = requireNonNull(delay);
        this.combiningPolicy = requireNonNull(combiningPolicy);
    }

    //Don't make package-private as this is the only alternative to the "Staged Builder"
    protected final <O> void onSignalChanged(Wire<O> observed, OnSignalChanged<O> callback) {
        var cb = requireNonNull(callback);

        observed.onSignalChanged(
            agenda.afterDelay(delay, cb)
        );
    }

    /**
     * Abstract method, so don't call from the constructor (see Effective Java).
     */
    protected abstract Box startup();

    /**
     * @throws NullPointerException if the collection itself or any of its elements are null
     * @return a defensive copy (to help implementors minimize mutability)
     */
    protected static <X> List<X> checkNoNulls(Collection<X> inputs) {
        if(inputs.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("Detected null wires in " + inputs);
        }
        return new ArrayList<>(inputs);
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

    protected final <O> ObservedWireCapturedAndMatchedToOutputOfSameType<O> onSignalChanged2(Wire<O> observed) {
        return new ObservedWireCapturedAndMatchedToOutputOfSameType<>(observed);
    }




    protected class ObservedWireCaptured<O, T> {
        final Wire<O> observed;

        private ObservedWireCaptured(Wire<O> observed) {
            this.observed = requireNonNull(observed);
        }

        public ObservedAndTargetWiresCaptured<O, T> set(Wire<T> target) {
            return new ObservedAndTargetWiresCaptured<>(observed, requireNonNull(target));
        }
    }

    protected class ObservedWireCapturedAndMatchedToOutputOfSameType<O> extends ObservedWireCaptured<O, O> {
        private ObservedWireCapturedAndMatchedToOutputOfSameType(Wire<O> observed) {
            super(observed);
        }

        public final ObservedAndTargetWiresOfSameTypeCaptured<O> set(Wire<O> target) {
            return new ObservedAndTargetWiresOfSameTypeCaptured<>(observed, requireNonNull(target));
        }
    }




    protected class ObservedAndTargetWiresCaptured<O, T> extends ObservedWireCaptured<O, T> {
        final Wire<T> target;

        private ObservedAndTargetWiresCaptured(Wire<O> observed, Wire<T> target) {
            super(observed);
            this.target = requireNonNull(target);
        }

        public Applying<O, T> toResultOfApplying() {
            return new Applying<>(observed, target);
        }

        public final InputsAndOutputCaptured<O, T> from(List<Wire<O>> inputs) {
            return new InputsAndOutputCaptured<>(
                observed, target, requireNonNull(inputs)
            );
        }
    }
    protected class ObservedAndTargetWiresOfSameTypeCaptured<O> extends ObservedAndTargetWiresCaptured<O, O> {
        private ObservedAndTargetWiresOfSameTypeCaptured(Wire<O> observed, Wire<O> target) {
            super(observed, target);
        }

        public final ApplyingToTargetOfSameType<O> toResultOfApplying() {
            return new ApplyingToTargetOfSameType<>(observed, target);
        }

    }




    protected class Applying<O, T> extends ObservedAndTargetWiresCaptured<O, T> {
        private Applying(Wire<O> observed, Wire<T> target) {
            super(observed, target);
        }

    }
    protected class ApplyingToTargetOfSameType<O> extends Applying<O, O> {
        private ApplyingToTargetOfSameType(Wire<O> observed, Wire<O> target) {
            super(observed, target);
        }

        public final void signalValueTransformation(Function<O, O> signalValueTransformation) {
            var f = requireNonNull(signalValueTransformation);

            onSignalChanged(observed,
                newSignal -> target.setSignal(
                    newSignal.map(f)
                )
            );
        }

        public final void signalValuesCombinator(
            BinaryOperator<O> signalValuesCombinator,
            Wire<O> rightWire
        ) {
            var f = requireNonNull(signalValuesCombinator);
            var r = requireNonNull(rightWire);

            onSignalChanged(observed,
                newSignal -> target.setSignal(
                    Signal.combine(newSignal, r.getSignal(), f, combiningPolicy)
                )
            );
        }

        public final void signalValuesCombinator(
            Wire<O> leftWire,
            BinaryOperator<O> signalValuesCombinator
        ) {
            var l = requireNonNull(leftWire);
            var f = requireNonNull(signalValuesCombinator);

            onSignalChanged(observed,
                newSignal -> target.setSignal(
                    Signal.combine(l.getSignal(), newSignal, f, combiningPolicy)
                )
            );
        }
    }




    protected class InputsAndOutputCaptured<O, T> extends ObservedAndTargetWiresCaptured<O, T> {
        final List<Wire<O>> inputs;

        private InputsAndOutputCaptured(
            Wire<O> observed,
            Wire<T> target,
            List<Wire<O>> inputs
        ) {
            super(observed, target);
            this.inputs = new ArrayList<>(inputs);
        }

        public final void collect(Collector<O, ?, T> collector) {
            var c = requireNonNull(collector);

            onSignalChanged(observed,
                newSignal -> target.setSignal(
                    Wire.collect(inputs, c, combiningPolicy)
                )
            );
        }

        public final Reducing<O, T> map(Function<O, T> weight) {
            return new Reducing<>(
                observed, target, inputs,
                requireNonNull(weight)
            );
        }

        public final ReducingIndexed<O, T> mapIndexed(Function<Indexed<O>, T> indexedWeight) {
            return new ReducingIndexed<>(
                observed, target, inputs,
                requireNonNull(indexedWeight)
            );
        }

        public final void collectIndexed(Collector<Indexed<O>, ?, T> collector) {
            var c = requireNonNull(collector);

            onSignalChanged(observed,
                newSignal -> target.setSignal(
                    Wire.collectIndexed(inputs, c, combiningPolicy)
                )
            );
        }
    }




    protected class ReducingIndexed<O, T> extends InputsAndOutputCaptured<O, T> {
        private final Function<Indexed<O>, T> weight;

        private ReducingIndexed(
            Wire<O> observed,
            Wire<T> target,
            List<Wire<O>> inputs,
            Function<Indexed<O>, T> weight
        ) {
            super(observed, target, inputs);
            this.weight = requireNonNull(weight);
        }

        public final void reduce(BinaryOperator<T> accumulator, T identity) {
            var acc = requireNonNull(accumulator);

            onSignalChanged(observed,
                newSignal -> target.setSignal(
                    Wire.mapAndReduceIndexed(inputs, weight, acc, combiningPolicy)
                )
            );
        }
    }




    protected class Reducing<O, T> extends InputsAndOutputCaptured<O, T> {
        private final Function<O, T> weight;

        private Reducing(
            Wire<O> observed,
            Wire<T> target,
            List<Wire<O>> inputs,
            Function<O, T> weight
        ) {
            super(observed, target, inputs);
            this.weight = requireNonNull(weight);
        }

        public final void reduce(BinaryOperator<T> accumulator) {
            var acc = requireNonNull(accumulator);

            onSignalChanged(observed,
                newSignal -> target.setSignal(
                    Wire.mapAndReduce(inputs, weight, acc, combiningPolicy)
                )
            );
        }
    }
    //----------Convenience shortcuts for Boxes that have 1 or 2 inputs, or N homogeneous inputs--------------^^^^^^^^^^^^^^^^

}
//@formatter:on
