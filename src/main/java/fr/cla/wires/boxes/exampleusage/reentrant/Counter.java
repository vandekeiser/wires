package fr.cla.wires.boxes.exampleusage.reentrant;

import fr.cla.wires.*;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.boxes.exampleusage
 */
public final class Counter extends Box {

    private static final long 
        DEFAULT_INITIAL = 0L,
        DEFAULT_STEP = 1L
    ;
    private final long initial;
    private final Wire<Long> step, out;

    private Counter(long initial, long step, Wire<Long> out, Clock clock) {
        this(initial, step, out, clock, DEFAULT_DELAY);
    }
    
    private Counter(long initial, long step, Wire<Long> out, Clock clock, Delay delay) {
        super(clock, delay);
        this.out = requireNonNull(out);
        this.initial = validateInitial(initial);
        this.step = Wire.make();
        this.step.setSignal(Signal.of(validateStep(step)));
    }

    /**
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * The DSL implemented by the "Staged Builder" pattern translates:
     * {@code
     *      onSignalChanged(out)
     *          .set(out)
     *          .toResultOfApplying()
     *          .transformation(this::add, step)
     *      ;
     * }
     * to the less linear:
     * {@code
     *      onSignalChanged(out,
     *          newOut -> out.setSignal(
     *              Signal.map(newOut, step.getSignal(), this::add)
     *          )
     *      );
     * }
     */
    @Override protected Counter startup() {
        this.<Long, Long>onSignalChanged(out)
            .set(out)
            .toResultOfApplying()
            .transformation(this::add, step)
        ;
        out.setSignal(Signal.of(initial)); //Do this after registering the transformation, so that this change is visible. 
        return this;
    }
    
    private long add(long counter, long step) {
        long newCounter;
        try {
            newCounter = Math.addExact(counter, step); //throws ArithmeticException if overflows long
        } catch(ArithmeticException overflow) {
            throw new Counter.OverflowException(counter, step, overflow);
        }
        return newCounter;
    }

    private static long validateInitial(long initial) {
        //Initial negative value could sometimes make sense, so don't forbid that.
        return initial;
    }
    
    private static long validateStep(long step) {
        //Negative step could sometimes make sense, so don't forbid that.
        return step;
    }
    
    public static Builder out(Wire<Long> out) {
        return new Builder(out);
    }


    
    
    public static class Builder {
        private Wire<Long> out;
        private long step = DEFAULT_STEP, initial = DEFAULT_INITIAL;

        private Builder(Wire<Long> out) {
            this.out = requireNonNull(out);
        }

        public Builder step(long step) {
            this.step = validateStep(step);
            return this;
        }
        
        public Builder initial(long initial) {
            this.initial = validateInitial(initial);
            return this;
        }

        public Counter time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new Counter(initial, step, out, _clock).startup();
        }
        
    }

    
    
    
    public static final class OverflowException extends RuntimeException {
        private final long currentCounter, attemptedStep;
        private final ArithmeticException overflow;

        OverflowException(long currentCounter, long attemptedStep, ArithmeticException overflow) {
            super(
                formatMessage(currentCounter, attemptedStep),
                overflow //Do propagate as "Caused by: " in the stacktrace!
            );
            //Don't validate since this is an exception constructor! Propagate whatever context is known as is.
            this.currentCounter = currentCounter;
            this.attemptedStep = attemptedStep;
            this.overflow = overflow;
        }

        private static String formatMessage(long currentCounter, long attemptedStep) {
            return String.format(
                "Counter overflow! currentCounter: %s, attemptedStep: %s",
                currentCounter, attemptedStep
            );
        }

        public ArithmeticException getOverflow() { return overflow; }
        public long getAttemptedStep() { return attemptedStep; }
        public long getCurrentCounter() { return currentCounter; }
    }
    
}
//@formatter:on
