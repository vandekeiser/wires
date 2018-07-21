package fr.cla.wires.support.oo.ddd;

import fr.cla.wires.support.oo.AbstractValueObject;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.*;

//@formatter:off
public class AbstractValueObjectTest {

    @Test
    public void should_not_get_classcast_when_calling_equals_on_different_types() {
        ValueObject1 vo1 = new ValueObject1("foo");
        ValueObject2 vo2 = new ValueObject2(33);
        Assertions.assertThat(
            vo1.equals(vo2)//shouldn't throw ClassCastException
        ).isFalse();
    }

    private static class ValueObject1 extends AbstractValueObject<ValueObject1> {
        private final String value;

        ValueObject1(String value) {
            super(ValueObject1.class);
            this.value = value;
        }

        @Override
        protected List<Object> equalityCriteria() {
            return singletonList(value);
        }
    }

    private static class ValueObject2 extends AbstractValueObject<ValueObject1> {
        private final long value;

        ValueObject2(long value) {
            super(ValueObject1.class);
            this.value = value;
        }

        @Override
        protected List<Object> equalityCriteria() {
            return singletonList(value);
        }
    }

}
//@formatter:on