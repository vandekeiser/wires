package fr.cla.wires.boxes.exampleusage.neuron.perceptron;

import fr.cla.wires.Clock;
import fr.cla.wires.Wire;
import fr.cla.wires.boxes.exampleusage.neuron.Neuron;

import java.util.List;

public class FeedForwardNeuron extends Neuron {

    private FeedForwardNeuron(List<Wire<Double>> ins, Wire<Double> out, double threshold, List<Double> weigths, Clock clock) {
        super(ins, out, threshold, weigths, clock);
    }

}
