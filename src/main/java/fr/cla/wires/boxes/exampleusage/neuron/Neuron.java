package fr.cla.wires.boxes.exampleusage.neuron;

import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.ReduceHomogeneousInputs;

import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * Start trying to implement a neural network on top of Boxes and Wires.
 * (move to a separate Maven module once it reaches a sufficient size)
 */
public class Neuron extends ReduceHomogeneousInputs<Double, Double> {

    private Neuron(List<Wire<Double>> ins, Wire<Double> out, Clock clock) {
        this(ins, out, clock, DEFAULT_DELAY);
    }

    private Neuron(List<Wire<Double>> ins, Wire<Double> out, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
    }

    @Override protected Function<Double, Double> accumulationValue() {
        //TODO use weights
        return Function.identity();
    }
    @Override protected Double identity() {
        return 0.0;
    }
    @Override protected BinaryOperator<Double> accumulator() {
        return this::plus;
    }

    private double plus(double d1, double d2) {
        return d1 + d2;
    }

    /**
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * @implNote The contract for overriders is to call super.startup(), return this:
     * This method is only not marked final as a convenience to allow covariant return.
     *
     * @return this Box, started.
     */
    @Override protected Neuron startup() {
        super.startup();
        return this;
    }

    public static Builder ins(List<Wire<Double>> ins) {
        return new Builder(checkNoNulls(ins));
    }




    public static class Builder {
        private List<Wire<Double>> ins;
        private Wire<Double> out;

        private Builder(List<Wire<Double>> ins) {
            this.ins = checkNoNulls(ins);
        }

        public Builder out(Wire<Double> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public Neuron time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new Neuron(ins, out, _clock).startup();
        }
    }

}
//@formatter:on
