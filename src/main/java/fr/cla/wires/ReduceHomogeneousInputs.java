package fr.cla.wires;

import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//TODO: refact as extending CollectHomogeneousInputs (reduction->collector)
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
            .from(this.ins)
            .map(mapping())
            .reduce(reduction(), neutralElement())
        ;
    }

    protected abstract Function<O,T> mapping();
    protected abstract T neutralElement();
    protected abstract BinaryOperator<T> reduction();

}
//@formatter:on
