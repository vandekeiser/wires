package fr.cla.wires;

/**
 * This should not happen under normal circumstances, since Tick and Delay use long.
 * (Long.MAX_VALUE == 2^63-1 == 9_223_372_036_854_775_807)
 */
public final class TickOverflowException extends RuntimeException {

    //Unused fields, but useful for debug
    private final long currentTick;
    private final Delay attemptedDelay;
    private final ArithmeticException overflow;

    TickOverflowException(long currentTick, Delay attemptedDelay, ArithmeticException overflow) {
        super(
            formatMessage(currentTick, attemptedDelay),
            overflow //Do propagate as "Caused by: " in the stacktrace!
        );
        //Don't validate since this is an exception constructor! Propagate whatever context is known as is.
        this.currentTick = currentTick;
        this.attemptedDelay = attemptedDelay;
        this.overflow = overflow;
    }

    private static String formatMessage(long currentTick, Delay attemptedDelay) {
        return String.format(
            "Tick overflow! currentTick: %d, attemptedDelay: %s",
            currentTick, attemptedDelay
        );
    }

}
