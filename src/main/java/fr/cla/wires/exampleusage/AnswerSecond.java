package fr.cla.wires.exampleusage;

import fr.cla.wires.Agenda;
import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Wire;

import static java.util.Objects.requireNonNull;

public class AnswerSecond extends Box {
    private final Wire<Boolean> in1, in2, out;

    private AnswerSecond(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Agenda agenda) {
        this(in1, in2, out, agenda, DEFAULT_DELAY);
    }

    private AnswerSecond(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Agenda agenda, Delay delay) {
        super(delay, agenda);
        this.in1 = requireNonNull(in1);
        this.in2 = requireNonNull(in2);
        this.out = requireNonNull(out);

        this.<Boolean, Boolean>onSignalChanged(in1).set(out).toResultOf(this::answerSecond, in2);
        this.<Boolean, Boolean>onSignalChanged(in2).set(out).toResultOf(in1, this::answerSecond);
    }


    private boolean answerSecond(boolean b1, boolean b2) {
        return b2;
    }

    public static Builder in1(Wire<Boolean> in1) {
        return new Builder(in1);
    }

    public static class Builder {
        private Wire<Boolean> in1, in2, out;

        public Builder(Wire<Boolean> in) {
            this.in1 = in;
        }

        public Builder in2(Wire<Boolean> in2) {
            this.in2 = in2;
            return this;
        }

        public Builder out(Wire<Boolean> out) {
            this.out = out;
            return this;
        }

        public AnswerSecond agenda(Agenda agenda) {
            return new AnswerSecond(in1, in2, out, agenda);
        }
    }

}
