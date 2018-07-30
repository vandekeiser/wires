package fr.cla.wires.support.pbt.examplevos;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.List;

import static java.util.Collections.singletonList;

//@formatter:off
public class VO1 extends AbstractValueObject<VO1> {

    protected final Value x;

    public VO1(Value x) {
        super(VO1.class);
        this.x = x;
    }

    public static VO1 random(SourceOfRandomness rand) {
        return new VO1(Value.random(rand));
    }

    @Override
    protected List<Object> equalityCriteria() {
        return singletonList(x);
    }

    @Override
    protected boolean canEqual(AbstractValueObject<?> that) {
        return that instanceof VO1;
    }

}
//@formatter:on