package fr.cla.wires.support.functional;

import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.Arrays;
import java.util.List;

//@formatter:off
/**
 * A value combined with an index, indicating its position in an ordered sequence.
 *
 * @param <T> The type of the indexed value.
 */
public final class Indexed<T> extends AbstractValueObject<Indexed<T>> {

    /**
     * Combine an index and a value into an indexed value.
     * @param index The index of the value.
     * @param value The value indexed.
     * @param <T> The type of the value.
     * @return The indexed value.
     */
    public static <T> Indexed<T> index(int index, T value) {
        return new Indexed<>(index, value);
    }

    private final int index;
    private final T value;

    private Indexed(int index, T value) {
        super(indexedOfT());
        this.index = index;
        this.value = value;
    }

    /**
     * @return The indexed value.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return The value indexed.
     */
    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("{ index: %d, value: %s }", index, value);
    }

    @Override
    protected List<Object> equalityCriteria() {
        return Arrays.asList(this.index, this.value);
    }

    private static <T> Class<Indexed<T>> indexedOfT() {
        Class<?> unbounded = Indexed.class;

        //Doesn't matter, as this is only used in AbstractValueObject::equals, for the isInstance check.
        //This unchecked cast means that Indexed of all types are compared together without ClassCastException,
        // but this doesn't matter because Indexed with equal values and indices should be equal.
        //This is proved by IndexedTest::should_not_get_classcast_when_calling_equals_on_indexeds_of_different_types
        // and IndexedTest::equals_should_be_true_for_indexeds_of_different_types_but_same_value_and_index
        @SuppressWarnings("unchecked")
        Class<Indexed<T>> indexedOfT = (Class<Indexed<T>>) unbounded;

        return indexedOfT;
    }

}
//@formatter:on
