package fr.cla.wires.support.functional;

import java.util.Objects;

//@formatter:off
/**
 * A value combined with an index, indicating its position in an ordered sequence.
 *
 * @param <T> The type of the indexed value.
 */
public final class Indexed<T> {

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (! (obj instanceof Indexed)) return false;
        Indexed<?> that = (Indexed<?>) obj;
        return Objects.equals(index, that.index) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, value);
    }

    @Override
    public String toString() {
        return String.format("{ index: %d, value: %s }", index, value);
    }

}
//@formatter:on
