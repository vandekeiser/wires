package fr.cla.wires.neuron.perceptron;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Wire;
import fr.cla.wires.neuron.Layer;
import fr.cla.wires.support.oo.Accumulable;

import java.util.List;

public class FeedForwardLayer extends Layer {

    protected FeedForwardLayer(List<Wire<Double>> ins, Wire<Double> out, double threshold, List<Double> weigths, Clock clock) {
        super(ins, out, threshold, weigths, clock);
    }

}
