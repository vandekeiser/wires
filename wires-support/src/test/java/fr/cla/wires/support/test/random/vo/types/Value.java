package fr.cla.wires.support.test.random.vo.types;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.Arrays;

//@formatter:off
public enum Value {
    V1,
    V2,
//    V3,
//    V4,
    ;


    private static final Value[] VALUES_PLUS_NULL = append(Value.values(), null);

    private static Value[] append(Value[] xs, Value x) {
        Value[] all = Arrays.copyOf(xs, xs.length + 1);
        all[xs.length] = x;
        return all;
    }

    public static Value random(SourceOfRandomness rnd) {
        return VALUES_PLUS_NULL[rnd.nextInt(VALUES_PLUS_NULL.length)];
    }

    public static int numberOfPossibleValues() {
        return VALUES_PLUS_NULL.length;
    }
}
//@formatter:on