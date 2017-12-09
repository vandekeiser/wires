package fr.cla.wires.boxes.exampleusage.neuron;

import fr.cla.support.functional.Indexed;
import fr.cla.wires.Clock;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.CollectIndexedHomogeneousInputs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

//@formatter:off
//WIP
//Later use Arrays::parallelPrefix (as a path toward using the GPU)
/**
 * Start trying to implement a neural network on top of Boxes and Wires.
 * (move to a separate Maven module once it reaches a sufficient size)
 */
public class Neuron extends CollectIndexedHomogeneousInputs<Double, Double, Long> {

    public static final Double DEFAULT_THRESHOLD = 1.0;

    private final double threshold;
    private final List<Double> weigths;

    protected Neuron(List<Wire<Double>> ins, Wire<Double> out, double threshold, List<Double> weigths, Clock clock) {
        this(ins, out, threshold, weigths, clock, DEFAULT_DELAY);
    }

    protected Neuron(List<Wire<Double>> ins, Wire<Double> out, double threshold, List<Double> weigths, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
        this.threshold = threshold;
        this.weigths = new ArrayList<>(weigths);
    }

    protected Neuron(List<Wire<Double>> ins, List<Wire<Double>> outs, double threshold, List<Double> weigths, Clock clock, Delay delay) {
        super(ins, outs, clock, delay);
        this.threshold = threshold;
        this.weigths = new ArrayList<>(weigths);
    }

    @Override protected Function<Indexed<Double>, Double> accumulationValue() {
        return indexed -> {
            int index = indexed.getIndex();
            double value = indexed.getValue();
            return value * weigths.get(index);
        };
    }

    @Override
    protected BinaryOperator<Double> accumulator() {
        return this::plus;
    }

    @Override
    protected Function<Double, Double> finisher() {
        return potential -> potential > threshold ? 1.0 : 0.0;
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
        private double threshold;
        private List<Double> weigths;

        private Builder(List<Wire<Double>> ins) {
            this.ins = checkNoNulls(ins);
        }

        public Builder out(Wire<Double> out) {
            this.out = requireNonNull(out);
            return this;
        }

        public Builder threshold(double threshold) {
            this.threshold = validateThreshold(threshold);
            return this;
        }

        public Builder weigths(List<Double> weigths) {
            this.weigths = validateWeigths(weigths);
            return this;
        }

        public Neuron time(Clock clock) {
            Clock _clock = requireNonNull(clock);
            return new Neuron(ins, out, threshold, weigths, _clock).startup();
        }
    }

    private static double validateThreshold(double threshold) {
        //TODO
         return threshold;
    }

    private static List<Double> validateWeigths(List<Double> weigths) {
        //TODO
        return checkNoNulls(weigths);
    }

}
//@formatter:on
