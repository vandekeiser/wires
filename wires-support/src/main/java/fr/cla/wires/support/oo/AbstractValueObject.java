package fr.cla.wires.support.oo;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A DDD Value Object: immutable object with no identity (equality defined by a set of fields).
 * (it just doesn't have validation)
 */
public abstract class AbstractValueObject<T extends AbstractValueObject<T>> {

    private final SameTypePolicy sameTypePolicy;
    private final Class<T> type;

    protected AbstractValueObject(Class<T> type) {
        //this(type, SameTypePolicy.SAME_CONCRETE_CLASS);
        //this(type, SameTypePolicy.IS_INSTANCE);
        this(type, SameTypePolicy.CAN_EQUAL);
    }

    protected AbstractValueObject(Class<T> type, SameTypePolicy sameTypePolicy) {
        this.type = requireNonNull(type);
        this.sameTypePolicy = requireNonNull(sameTypePolicy);
    }

    @Override public final boolean equals(Object obj) {
        //An optimization, but also avoids StackOverflows on cyclic object graphs.
        if(obj == this) return true;

        if(! sameTypePolicy.safeIsSameType(this, type, obj)) return false;

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

    /**
     * @see SameTypePolicy.CAN_EQUAL
     */
    protected boolean canEqual(AbstractValueObject<?> that) {
        return true;
    }




    public enum SameTypePolicy {
        /**
         * The most simple, we're sure to respect the Object::equals contract without collaboration from the concrete classes.
         * On the other hand this fails if the class is replaced at load-time by a proxy (eg. by Hibernate).
         * Also of course this isn't as flexible as CAN_EQUAL, as there is now way to still be equal even without adding state.
         */
        SAME_CONCRETE_CLASS {
            @Override boolean isSameType(
                AbstractValueObject<?> thisObj, Class<?> thisObjType,
                AbstractValueObject<?> thatObj, Class<?> thatObjType
            ) {
                return Objects.equals(thisObj.getClass(), thatObj.getClass());
            }
        },

        /**
         * KO if we want to keep AbstractValueObject_PbtTest::equals_should_be_false_for_different_types,
         *  as this doesn't prevent VO1A eq VO1B
         * OK otherwise.
         *
         * This has the advantage of working in the face of Hibernate proxies.
         * It also allows a derived VO to compare equal to its parent while still respecting the Object::equals contract iff:
         * -it adds no state compared to its parent class
         *
         * But is not always fine-grained enough, as it doesn't allow distinguishing:
         *  -derived classes that do add state and should not compare equal to their parent
         *  -derived classes that do not add state and could compare equal to their parent
         * That degree of fine-grained control requires using CAN_EQUAL,
         *  and placing canEqual overrides in coordination with adding state or not.
         *  (this then becomes the responsibility of the derived class)
         *
         *  TODO: test with Hibernate-instrumented class where the concrete class is a load-type proxy
         */
        IS_INSTANCE{
            @Override boolean isSameType(
                AbstractValueObject<?> thisObj, Class<?> thisObjType,
                AbstractValueObject<?> thatObj, Class<?> thatObjType
            ) {
                return
                    thisObjType.isInstance(thatObj)
                    &&
                    thatObjType.isInstance(thisObj)
                ;
            }
        },

        /**
         * The most flexible pattern, initially from the Java bible "Effective Java".
         * https://www.artima.com/lejava/articles/equality.html: nice article about the EJ "canEqual" pattern.
         *
         * This could allow to keep strict concrete class same-type policy when Hibernate modifies our class
         *  but we want to ignore that from an equality point of view.
         *
         * TODO: test with Hibernate-instrumented class where the concrete class is a load-type proxy
         */
        CAN_EQUAL{
            boolean isSameType(
                AbstractValueObject<?> thisObj, Class<?> thisObjType,
                AbstractValueObject<?> thatObj, Class<?> thatObjType
            ) {
                return
                    thisObj.canEqual(thatObj)
                    &&
                    thatObj.canEqual(thisObj)
                ;
            }
        },
        ;

        /**
         * @throws NullPointerException iff thisObj or thisObjType is null (which is impossible from the equals method)
         */
        boolean safeIsSameType(AbstractValueObject<?> thisObj, Class<?> thisObjType, Object thatObj) {
            thisObj = requireNonNull(thisObj);
            thisObjType = requireNonNull(thisObjType);

            if(!(thatObj instanceof AbstractValueObject)) return false;
            AbstractValueObject<?> thatVo = (AbstractValueObject<?>) thatObj;
            Class<?> thatObjType = thatVo.type;

            return isSameType(thisObj, thisObjType, thatVo, thatObjType);
        }

        abstract boolean isSameType(AbstractValueObject<?> thisObj, Class<?> thisObjType, AbstractValueObject<?> thatObj, Class<?> thatObjType);
    }

}
//@formatter:on
