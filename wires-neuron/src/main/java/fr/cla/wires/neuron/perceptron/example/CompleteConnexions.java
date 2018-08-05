package fr.cla.wires.neuron.perceptron.example;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.support.functional.Indexed;
import fr.cla.wires.support.oo.Accumulable;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class CompleteConnexions<I, O>
extends Box {

    private final List<Wire<I>> ins;
    private final List<Wire<O>> outs;
    private final Collector<Indexed<I>, ?, O> collector;

    protected CompleteConnexions(List<Wire<I>> ins, List<Wire<O>> outs, Collector<Indexed<I>, ?, O> collector, Clock clock) {
        this(ins, outs, collector, clock, DEFAULT_DELAY);
    }

    protected CompleteConnexions(List<Wire<I>> ins, List<Wire<O>> outs, Collector<Indexed<I>, ?, O> collector, Clock clock, Delay delay) {
        super(clock, delay, Accumulable.WhenCombining.ABSENT_WINS);
        this.ins = checkNoNulls(ins);
        this.outs = checkNoNulls(outs);
        this.collector = requireNonNull(collector);
    }

    @Override
    protected CompleteConnexions<I, O> startup() {
        ins.forEach(this::startup);
        return this;
    }

    private void startup(Wire<I> in) {
        outs.forEach(out -> this.startup(in, out));
    }

    private void startup(Wire<I> in, Wire<O> out) {
        this.<I, O>onSignalChanged(in)
            .set(out)
            .from(ins)
            .collectIndexed(collector)
        ;
    }

    static <I, O> Builder<I, O> ins(List<Wire<I>> ins) {
        return new Builder<>(ins);
    }




    public static class Builder<I, O> {
        private List<Wire<I>> ins;
        private List<Wire<O>> outs;
        private Function<Indexed<I>, O> weight;
        private BinaryOperator<O> accumulator;
        private UnaryOperator<O> finisher;
        private Clock clock;

        private Builder(List<Wire<I>> ins) {
            this.ins = checkNoNulls(ins);
        }

        public Builder<I, O> outs(List<Wire<O>> outs) {
            this.outs = checkNoNulls(outs);
            return this;
        }

        public Builder<I, O> weight(Function<Indexed<I>, O> weight) {
            this.weight = requireNonNull(weight);
            return this;
        }

        public Builder<I, O> accumulator(BinaryOperator<O> accumulator) {
            this.accumulator = requireNonNull(accumulator);
            return this;
        }

        public Builder<I, O> finisher(UnaryOperator<O> finisher) {
            this.finisher = requireNonNull(finisher);
            return this;
        }

        public Builder<I, O> clock(Clock clock) {
            this.clock = requireNonNull(clock);
            return this;
        }

        public CompleteConnexions<I, O> delay(Delay delay) {
            Delay _delay = requireNonNull(delay);
            Collector<Indexed<I>, ?, O> collector = Accumulable.collector(
                weight, accumulator, finisher
            );
            return new CompleteConnexions<>(ins, outs, collector, clock, _delay).startup();
        }
    }

}
//@formatter:on