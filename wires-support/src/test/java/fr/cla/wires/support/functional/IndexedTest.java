package fr.cla.wires.support.functional;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
public class IndexedTest {

    //This test is required to justify the @SuppressWarnings("unchecked") comment in Indexed::indexedOfT
    @Test
    public void should_not_get_classcast_when_calling_equals_on_indexeds_of_different_types() {
        Indexed<String> stringIndexed = Indexed.index(0,"foo");
        Indexed<Boolean> booleanIndexed = Indexed.index(0,true);
        assertThat(
            stringIndexed.equals(booleanIndexed)//shouldn't throw ClassCastException
        ).isFalse();
    }

    //This test is required to justify the @SuppressWarnings("unchecked") comment in Indexed::indexedOfT
    @Test
    public void equals_should_be_true_for_indexeds_of_different_types_but_same_value() {
        //Using new guarantees these are equal, but not the same instance.
        //This is so that the test is stronger.
        Object o = new String("foo");
        String s = new String("foo");

        Indexed<Object> objectSignal = Indexed.index(0, o);
        Indexed<String> stringSignal = Indexed.index(0, s);

        assertThat(
            objectSignal.equals(stringSignal)
        ).isTrue();
    }

}
//@formatter:on
