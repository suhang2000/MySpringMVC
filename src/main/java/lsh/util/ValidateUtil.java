package lsh.util;

import java.util.Collection;
import java.util.Map;

/**
 * ValidateUtil
 * check if the given object is null or not
 */
public class ValidateUtil {
    /**
     * Object is null or not
     *
     * @param obj Object
     * @return is null or not
     */
    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    /**
     * String is null or ""
     *
     * @param obj String
     * @return is null or not
     */
    public static boolean isEmpty(String obj) {
        return (obj == null || "".equals(obj));
    }

    /**
     * Array is null or empty
     *
     * @param obj Array
     * @return is null or empty
     */
    public static boolean isEmpty(Object[] obj) {
        return obj == null || obj.length == 0;
    }

    /**
     * Collection is null or empty
     *
     * @param obj Collection
     * @return is null or empty
     */
    public static boolean isEmpty(Collection<?> obj) {
        return obj == null || obj.isEmpty();
    }

    /**
     * Map is null or empty
     *
     * @param obj Map
     * @return is null or empty
     */
    public static boolean isEmpty(Map<?, ?> obj) {
        return obj == null || obj.isEmpty();
    }

    /**
     * Object is null or not
     *
     * @param obj Object
     * @return is null
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * String is null or ""
     *
     * @param obj String
     * @return is null or ""
     */
    public static boolean isNotEmpty(String obj) {
        return !isEmpty(obj);
    }

    /**
     * Array is null or empty
     *
     * @param obj Array
     * @return is null or empty
     */
    public static boolean isNotEmpty(Object[] obj) {
        return !isEmpty(obj);
    }

    /**
     * Collection is null or empty
     *
     * @param obj Collection
     * @return is null or empty
     */
    public static boolean isNotEmpty(Collection<?> obj) {
        return !isEmpty(obj);
    }

    /**
     * Map is null or empty
     *
     * @param obj Map
     * @return is null or empty
     */
    public static boolean isNotEmpty(Map<?, ?> obj) {
        return !isEmpty(obj);
    }
}
