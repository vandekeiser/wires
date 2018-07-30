package fr.cla.wires.support.pbt.examplevos;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.Arrays;
import java.util.List;

//@formatter:off
public class VO1B extends VO1 {

    private final Value y;

    public VO1B(Value y, Value x) {
        super(x);
        this.y = y;
    }

    public static VO1B random(SourceOfRandomness rand) {
        return new VO1B(
            Value.random(rand),
            Value.random(rand)
        );
    }

    @Override
    protected List<Object> equalityCriteria() {
        return Arrays.asList(super.x, y);
    }

    @Override
    protected boolean canEqual(AbstractValueObject<?> that) {
        return that instanceof VO1B;
    }

}
//@formatter:on