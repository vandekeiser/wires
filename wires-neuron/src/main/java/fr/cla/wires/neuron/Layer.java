package fr.cla.wires.neuron;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.core.boxes.CollectIndexedHomogeneousInputs;
import fr.cla.wires.support.functional.Indexed;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

public abstract class Layer
extends CollectIndexedHomogeneousInputs<Double, Double, Long>    
implements GroupOfUnits{

    public static final Double DEFAULT_THRESHOLD = 1.0;

    private final double threshold;
    private final List<Double> weigths;

    protected Layer(List<Wire<Double>> ins, Wire<Double> out, double threshold, List<Double> weigths, Clock clock) {
        this(ins, out, threshold, weigths, clock, DEFAULT_DELAY);
    }

    protected Layer(List<Wire<Double>> ins, Wire<Double> out, double threshold, List<Double> weigths, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
        this.threshold = threshold;
        this.weigths = new ArrayList<>(weigths);
    }

    protected Layer(List<Wire<Double>> ins, List<Wire<Double>> outs, double threshold, List<Double> weigths, Clock clock, Delay delay) {
        super(ins, outs, clock, delay);
        this.threshold = threshold;
        this.weigths = new ArrayList<>(weigths);
    }

    @Override
    protected Function<Indexed<Double>, Double> accumulationValue() {
        return indexed -> {
            int index = indexed.getIndex();
            double value = indexed.getValue();
            return value * weigths.get(index);
        };
    }

    @Override
    protected BinaryOperator<Double> accumulator() {
        return Double::sum;
    }

    @Override
    protected UnaryOperator<Double> finisher() {
        return potential -> potential > threshold ? 1.0 : 0.0;
    }

    @Override protected Layer startup() {
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
