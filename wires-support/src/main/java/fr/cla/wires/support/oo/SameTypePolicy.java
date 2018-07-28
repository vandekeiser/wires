package fr.cla.wires.support.oo;

import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.*;

public enum SameTypePolicy {

    /**
     *
     */
    SAME_CONCRETE_CLASS {
        @Override <T> boolean isSameType(AbstractValueObject<?> thisObj, Class<T> thisObjType, AbstractValueObject<?> thatObj) {
            return Objects.equals(thisObj.getClass(), thatObj.getClass());
        }
    },

    /**
     *
     */
    IS_INSTANCE{
        @Override <T> boolean isSameType(AbstractValueObject<?> thisObj, Class<T> thisObjType, AbstractValueObject<?> thatObj) {
            return thisObjType.isInstance(thatObj);
        }
    },

    /**
     *
     */
    CAN_EQUAL{
        @Override <T> boolean isSameType(AbstractValueObject<?> thisObj, Class<T> thisObjType, AbstractValueObject<?> thatObj) {
            if(!thisObjType.isInstance(thatObj)) return false;
            return thatObj.canEqual(thisObj);
        }
    },
    ;

    /**
     * @throws NullPointerException iff thisObj is null (which is impossible from the equals method)
     * @throws UnsupportedOperationException iff thisObj is not an instance of AbstractValueObject
     */
    <T> boolean isSameType(Object thisObj, Class<T> thisObjType, Object thatObj) {
        thisObj = requireNonNull(thisObj);

        if(!(thisObj instanceof AbstractValueObject)) throw new UnsupportedOperationException(format(
            "Can only compare types of AbstractValueObjects. Actual: thisObj == %s.",
            thisObj
        ));
        AbstractValueObject<?> thisVo = (AbstractValueObject<?>) thisObj;

        if(!(thatObj instanceof AbstractValueObject)) return false;
        AbstractValueObject<?> thatVo = (AbstractValueObject<?>) thatObj;

        return isSameType(thisVo, thisObjType, thatVo);
    }

    abstract <T> boolean isSameType(AbstractValueObject<?> thisObj, Class<T> thisObjType, AbstractValueObject<?> thatObj);

}
