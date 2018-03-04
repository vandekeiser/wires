package fr.cla.wires.boxes.exampleusage.neuron;

import java.util.function.DoubleUnaryOperator;

//@formatter:off
public interface TransferFunction extends DoubleUnaryOperator {

    DoubleUnaryOperator derivative();

    static TransferFunction linear(double coeff) {
        return new TransferFunction() {
            @Override public double applyAsDouble(double x) {
                return x * coeff;
            }

            @Override public DoubleUnaryOperator derivative() {
                return x -> coeff;
            }
        };
    }

    static TransferFunction sign() {
        return new TransferFunction() {
            @Override public double applyAsDouble(double x) {
                return x > 0.0 ? +1.0 : -1.0;
            }

            @Override public DoubleUnaryOperator derivative() {
                return x -> {
                    throw new UnsupportedOperationException("sign() is not classically derivable");
                };
            }
        };
    }

    static TransferFunction logistic() {
        return new TransferFunction() {
            @Override public double applyAsDouble(double x) {
                return 1.0 / (1.0 + Math.exp(-1.0 * x));
            }

            @Override public DoubleUnaryOperator derivative() {
                return x -> {
                    double y = applyAsDouble(x);
                    return y * (1.0 - y);
                };
            }
        };
    }

}
//@formatter:on