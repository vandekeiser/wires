package fr.cla.wires.core;

import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.ArrayDeque;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A discretized time unit.
 */
public final class Tick extends AbstractValueObject<Tick> {
    public static final Tick ZERO = new Tick(0L);

    private final long tick;

    private Tick(long tick) {
        super(Tick.class);
        if(tick < 0) throw new AssertionError("tick must be >= 0, was: " + tick);
        this.tick = tick;
    }

    @Override
    protected List<Object> equalityCriteria() {
        //Bad idea to use Java9 List.of here since in an equals method fields could potentially be legally null.
        // In Tick it can't happen but I don't want to copy-paste List.of by mistake..
        return singletonList(tick);
    }

    /**
     * @throws Tick.OverflowException if the addition overflows long //TODO test
     */
    //Don't need to do any checks other than overflow here,
    // since Delay::duration guarantees duration is >0 and Delay is final
    public Tick plus(Delay delay) {
        long newTick;
        try {
            newTick = Math.addExact(tick, (long)delay.duration()); //throws ArithmeticException if overflows long
        } catch(ArithmeticException overflow) {
            throw new Tick.OverflowException(this, delay, overflow);
        }
        return new Tick(newTick);
    }

    public static Tick number(long number) {
        if(number < 0) throw new IllegalArgumentException("Tick number must be >= 0, was: " + number);
        return new Tick(number);
    }

    @Override
    public String toString() {
        return String.valueOf(tick);
    }

    /**
     * This should not happen under normal circumstances, since Tick.tick is long:
     * Long.MAX_VALUE == 2^63-1 == 9_223_372_036_854_775_807.
     *
     * Java note: could maybe have imagined making this one a checked exception for once,
     *  but they don't work with java.util.function's @FunctionalInterfaces,
     *  and I don't want to write/depend-on checked @FunctionalInterfaces for just that.
     */
    public static final class OverflowException extends RuntimeException {
        private final Tick currentTick;
        private final Delay attemptedDelay;
        private final ArithmeticException overflow;

        OverflowException(Tick currentTick, Delay attemptedDelay, ArithmeticException overflow) {
            super(
                formatMessage(currentTick, attemptedDelay),
                overflow //Do propagate as "Caused by: " in the stacktrace!
            );
            //Don't validate since this is an exception constructor! Propagate whatever context is known as is.
            this.currentTick = currentTick;
            this.attemptedDelay = attemptedDelay;
            this.overflow = overflow;
        }

        private static String formatMessage(Tick currentTick, Delay attemptedDelay) {
            return String.format(
                "Tick overflow! currentTick: %s, attemptedDelay: %s",
                currentTick, attemptedDelay
            );
        }

        public Tick getCurrentTick() { return currentTick; }
        public Delay getAttemptedDelay() { return attemptedDelay; }
        public ArithmeticException getOverflow() { return overflow; }
    }




    /**
     * Remembers the callbacks to call at a given Tick.
     */
    static final class Queue {
        private final Tick tick;
        //Run callbacks in FIFO order
        private final java.util.Queue<Runnable> todo = new ArrayDeque<>();

        Queue(Tick tick) {
            this.tick = requireNonNull(tick);
        }

        <V> void thenCall(OnSignalChanged<V> callback, Signal<V> signal) {
            var _callback = requireNonNull(callback);
            var _signal = requireNonNull(signal);

            todo.add(() -> _callback.accept(_signal));
        }

        void runAll() {
            todo.forEach(Runnable::run);
        }

        @Override public String toString() {
            return String.format(
                "{tick: %s, todo:%s}",
                tick, todo
            );
        }
    }

}
//@formatter:on