package fr.cla.wires;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
public abstract class ReduceHomogeneousInputs<O, T> extends Box {

    private final Set<Wire<O>> ins;
    private final Wire<T> out;

    protected ReduceHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Time time) {
        this(ins, out, time, DEFAULT_DELAY);
    }

    protected ReduceHomogeneousInputs(Set<Wire<O>> ins, Wire<T> out, Time time, Delay delay) {
        super(delay, time);
        this.ins = requireNonNull(ins);
        this.out = requireNonNull(out);
    }

    //Don't do the startup in the constructor to not let "this" escape through the method ref,
    // so that the Box is "properly constructed".
    //protected ReduceHomogeneousInputs<O, T, B> startup() {
    protected ReduceHomogeneousInputs<O, T> startup() {
        ins.forEach(this::startup);
        return this;
    }

    private void startup(Wire<O> in) {
        this.<O, T>onSignalChanged(in)
            .set(out)
            .withInputs(this.ins)
            .withMapping(mapping())
            .withReduction(reduction(), neutralElement())
        ;
    }

    protected abstract Function<O,T> mapping();
    protected abstract T neutralElement();
    protected abstract BinaryOperator<T> reduction();

    protected static <O> Set<Wire<O>> checkNoNulls(Set<Wire<O>> ins) {
        ins = new HashSet<>(requireNonNull(ins));
        if(ins.stream().anyMatch(w -> w == null)) {
            throw new NullPointerException("Detected null wires in " + ins);
        }
        return ins;
    }

}
//@formatter:on
