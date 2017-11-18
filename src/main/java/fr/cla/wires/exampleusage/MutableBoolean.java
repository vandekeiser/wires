package fr.cla.wires.exampleusage;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class MutableBoolean {

    private Optional<Boolean> maybeBoolean;

    public MutableBoolean(Optional<Boolean> b) {
        this.maybeBoolean = requireNonNull(b);
    }

    public static MutableBoolean initiallyUnset() {
        return new MutableBoolean(Optional.empty());
    }

    public Optional<Boolean> current() {
        if(maybeBoolean == null) throw new AssertionError();
        return maybeBoolean;
    }

    public void and(Optional<Boolean> maybeBoolean) {
        if(this.maybeBoolean.isPresent() && maybeBoolean.isPresent()) {
            boolean thisb = this.maybeBoolean.get(), thatb = maybeBoolean.get();
            this.maybeBoolean = Optional.of(_and(thisb, thatb));
        } else if(maybeBoolean.isPresent()) {
            this.maybeBoolean = maybeBoolean;
        }
    }

    public MutableBoolean and(MutableBoolean that) {
        if(!this.maybeBoolean.isPresent()) return initiallyUnset();
        if(!that.maybeBoolean.isPresent()) return initiallyUnset();
        boolean thisb = this.maybeBoolean.get(), thatb = that.maybeBoolean.get();
        return new MutableBoolean(Optional.of(_and(thisb, thatb)));
    }

    private static boolean _and(boolean thisBool, boolean thatBool) {
        return thisBool && thatBool;
    }

}
