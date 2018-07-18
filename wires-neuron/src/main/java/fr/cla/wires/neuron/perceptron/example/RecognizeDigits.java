package fr.cla.wires.neuron.perceptron.example;

import fr.cla.wires.core.Box;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.support.functional.Indexed;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

//@formatter:off
public class RecognizeDigits extends Box {
    private static final long NB_SEGMENTS = 7, NB_DIGITS = 10;;
    private final List<Wire<Double>> segments, digits, hiddens;
    private final CompleteConnexions<Double, Double> segments2Hiddens, hiddens2Digits;
    private final double threshold;
    private final List<Double> segments2HiddensWeigths, hiddens2DigitsWeigths;

    protected RecognizeDigits(
        Clock clock, Delay delay,
        double threshold,
        List<Double> segments2HiddensWeigths, List<Double> hiddens2DigitsWeigths
    ) {
        super(clock, delay);
        this.threshold = threshold;
        this.segments2HiddensWeigths = new ArrayList<>(segments2HiddensWeigths);
        this.hiddens2DigitsWeigths = new ArrayList<>(hiddens2DigitsWeigths);
        this.segments = wires(NB_SEGMENTS);
        this.digits = wires(NB_DIGITS);
        this.hiddens = wires(NB_DIGITS);
        this.segments2Hiddens =  perceptronConnexions(segments, hiddens, this::segments2HiddensWeigths);
        this.hiddens2Digits =  perceptronConnexions(hiddens, digits, this::hiddens2DigitsWeigths);
    }

    private CompleteConnexions<Double, Double> perceptronConnexions(
        List<Wire<Double>> x, List<Wire<Double>> y, Supplier<List<Double>> weigths
    ) {
        return CompleteConnexions.<Double, Double>
            ins(x)
            .outs(y)
            .accumulationValue(weightedInput(weigths))
            .accumulator(sumWeightedInput())
            .finisher(potential2Signal())
            .clock(clock)
            .delay(delay)
        ;
    }

    protected Function<Indexed<Double>, Double> weightedInput(Supplier<List<Double>> weigths) {
        return indexed -> {
            int index = indexed.getIndex();
            double value = indexed.getValue();
            return value * weigths.get().get(index);
        };
    }

    protected BinaryOperator<Double> sumWeightedInput() {
        return Double::sum;
    }

    protected UnaryOperator<Double> potential2Signal() {
        return potential -> potential > threshold ? 1.0 : 0.0;
    }


    private static List<Wire<Double>> wires(long size) {
        return Stream.generate(()->Wire.<Double>make()).limit(size).collect(toList());
    }


    @Override protected RecognizeDigits startup() {
        segments2Hiddens.startup();
        hiddens2Digits.startup();
        return this;
    }

    //Indirections to later vary the weigths
    private List<Double> segments2HiddensWeigths() {
        return segments2HiddensWeigths;
    }
    private List<Double> hiddens2DigitsWeigths() {
        return hiddens2DigitsWeigths;
    }

}
//@formatter:on
