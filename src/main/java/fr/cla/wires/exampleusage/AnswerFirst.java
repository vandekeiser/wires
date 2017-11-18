package fr.cla.wires.exampleusage;

import fr.cla.wires.Box;
import fr.cla.wires.Delay;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;

public class AnswerFirst extends Box {

    private AnswerFirst(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Time time) {
        this(in1, in2, out, time, DEFAULT_DELAY);
    }

    private AnswerFirst(Wire<Boolean> in1, Wire<Boolean> in2, Wire<Boolean> out, Time time, Delay delay) {
        super(delay, time);
        //Warning not to let this escape if we end up making this thread-safe
        this.<Boolean, Boolean>onSignalChanged(in1).set(out).toResultOf(this::answerFirst, in2);
        this.<Boolean, Boolean>onSignalChanged(in2).set(out).toResultOf(in1, this::answerFirst);
    }


    private boolean answerFirst(boolean b1, boolean b2) {
        return b1;
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

        public AnswerFirst time(Time time) {
            return new AnswerFirst(in1, in2, out, time);
        }
    }

}
