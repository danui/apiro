package com.danui.apiro;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP Query String parser
 *
 * @author Wong H'sien Jin
 */
public class QueryString {

    /**
     * @param qs Query String
     *
     * @return QueryString object.
     */
    public static QueryString parse(String qs) {
        QueryString result = new QueryString();
        if (qs == null) {
            return result;
        }
        if (qs.length() == 0) {
            return result;
        }
        String[] parts = qs.split("[&]");
        for (int i = 0; i < parts.length; ++i) {
            if (parts[i].length() == 0) continue;
            String[] kv = parts[i].split("[=]");
            String k = decodeUriComponent(kv[0]);
            if (kv.length == 2) {
                String v = decodeUriComponent(kv[1]);
                result.put(k,v);
            } else if (kv.length == 1) {
                result.put(k,null);
            } else {
                throw new IllegalArgumentException(
                    "Bad query string: " + qs);
            }
        }
        return result;
    }

    private final Map<String,String> map = new HashMap<>();

    // Private constructor. Please use QueryString::parse.
    private QueryString() {
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Verify that all key conditions are met.
     *
     * A Key Condition is specified as a key name, such as "foo", or as
     * a negated key name, such as "!foo". A negated form evaluates to
     * true when the negated key is not in the set of query string
     * parameters.
     *
     * @param keys Variable length number of key conditions.
     *
     * @return True when all key conditions evaluate to true.
     */
    public boolean has(String... keys) {
        for (String i : Arrays.asList(keys)) {
            if (i.startsWith("!")) {
                if (map.containsKey(i.substring(1))) {
                    return false;
                }
            } else {
                if (!map.containsKey(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return True if 'key' exists and has a value.
     */
    public boolean hasValue(String key) {
        return has(key) && (get(key) != null);
    }

    /**
     * @return True if value of key is a string.
     */
    public boolean isString(String key) {
        return hasValue(key);
    }

    /**
     * @return True if value of key is an integer.
     */
    public boolean isInteger(String key) {
        if (!has(key)) return false;
        try {
            Integer.valueOf(get(key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return True if value of key is a long.
     */
    public boolean isLong(String key) {
        if (!has(key)) return false;
        try {
            Long.valueOf(get(key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return True if value of key is a boolean.
     */
    public boolean isBoolean(String key) {
        String val;
        if (null == (val = get(key))) {
            return false;
        }
        if (val.equalsIgnoreCase("true")) {
            return true;
        }
        if (val.equalsIgnoreCase("false")) {
            return true;
        }
        return false;
    }

    /**
     * @return String value of key.
     */
    public String getString(String key)
        throws MissingParameterException {

        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        String val = get(key);
        if (val == null) {
            throw new MissingParameterException(
                "Query parameter '"+key+"' is not set");
        }
        return val;
    }

    /**
     * @return Integer value of key.
     */
    public int getInteger(String key) throws MissingParameterException,
        WrongParameterTypeException {

        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        String val = get(key);
        if (val == null) {
            throw new MissingParameterException(
                "Query parameter '"+key+"' is not set");
        }
        try {
            return Integer.valueOf(val);
        } catch (Exception e) {
            throw new WrongParameterTypeException(String.format(
                "Value '%s' for key '%s' is not an Integer",
                val, key));
        }
    }

    /**
     * @return Long value of key.
     */
    public long getLong(String key) throws MissingParameterException,
        WrongParameterTypeException {

        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        String val = get(key);
        if (val == null) {
            throw new MissingParameterException(
                "Query parameter '"+key+"' is not set");
        }
        try {
            return Long.valueOf(val);
        } catch (Exception e) {
            throw new WrongParameterTypeException(String.format(
                "Value '%s' for key '%s' is not a Long",
                val, key));
        }
    }

    /**
     * @return Boolean value of key.
     */
    public boolean getBoolean(String key)
        throws MissingParameterException, WrongParameterTypeException {

        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        String val = get(key);
        if (val == null) {
            throw new MissingParameterException(
                "Query parameter '"+key+"' is not set");
        }
        if (val.equalsIgnoreCase("true")) {
            return true;
        }
        if (val.equalsIgnoreCase("false")) {
            return false;
        }
        throw new WrongParameterTypeException(String.format(
            "Value '%s' for key '%s' is not a Boolean",
            val, key));
    }

    /**
     * Has a flag key.
     *
     * @return False if key does not exist; True if key exists but has
     *     no value; otherwise the value of getBoolean(key).
     *
     */
    public boolean hasFlag(String key)
        throws MissingParameterException, WrongParameterTypeException {

        if (!has(key)) return false;
        if (!hasValue(key)) return true;
        return getBoolean(key);
    }

    private void put(String key, String val) {
        map.put(key,  val);
    }

    private String get(String key) {
        return map.get(key);
    }

    private static String decodeUriComponent(String component) {
        try {
            return URLDecoder.decode(component, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
