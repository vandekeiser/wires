package fr.cla.wires.support.oo;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A DDD Value Object: immutable object with no identity (equality defined by a set of fields).
 * (it just doesn't have validation)
 */
public abstract class AbstractValueObject<T extends AbstractValueObject<T>> {
    
    private final Class<T> type;

    protected AbstractValueObject(Class<T> type) {
        this.type = requireNonNull(type);
    }

    /**
     * Precises the Object::equals contract by saying that
     * 2 AbstractValueObject of different concrete type are never equal.
     */
    @Override public final boolean equals(Object obj) {
        //An optimization, but also avoids StackOverflows on cyclic object graphs.
        if(obj == this) return true;

        //KO, AbstractValueObject_PbtTest would fail as this doesn't prevent VO1A eq VO1B
        //if(! type.isInstance(obj)) return false;
        if(obj == null) return false;
        if(! Objects.equals(this.getClass(), obj.getClass())) return false;

        T that = type.cast(obj);
        return Objects.equals(
            this.equalityCriteria(),
            that.equalityCriteria()
        );
    }

    @Override public final int hashCode() {
        return Objects.hash(
            equalityCriteria()
        );
    }

    @Override public String toString() {
        return String.format(
            "%s@%s%s",
            getClass().getSimpleName(),
            Integer.toHexString(System.identityHashCode(this)),
            equalityCriteria()
        );
    }

    protected abstract List<Object> equalityCriteria();

}
//@formatter:on