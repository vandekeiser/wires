package fr.cla.wires;

import java.util.Collection;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A DDD Value Object: immutable object with no identity (equality defined by a set of fields).
 * (it just doesn't have validation)
 */
public abstract class AbstractValueObject<T extends AbstractValueObject<T>> {
    
    private final Class<T> type;

    protected AbstractValueObject(Class<T> type) {
        this.type = requireNonNull(type);
    }

    @Override public final boolean equals(Object obj) {
        //An optimization, but also avoids StackOverflows on cyclic object graphs.
        if(obj == this) return true;

        if(obj == null) return false;
        if(! type.isInstance(obj)) return false;
        T that = type.cast(obj);
        
        return Objects.equals(
            this.attributesToIncludeInEqualityCheck(),
            that.attributesToIncludeInEqualityCheck()
        );
    }

    @Override public final int hashCode() {
        return Objects.hash(attributesToIncludeInEqualityCheck());
    }

    @Override public String toString() {
        return String.format(
            "%s@%s%s",
            getClass().getSimpleName(),
            Integer.toHexString(System.identityHashCode(this)),
            attributesToIncludeInEqualityCheck()
        );
    }

    //Should be a Set, but that would require more boilerplate in concrete classes than Arrays::asList.
    //Doesn't matter since equality will still be defined correctly if a field is included twice.
    protected abstract Collection<Object> attributesToIncludeInEqualityCheck();

}
