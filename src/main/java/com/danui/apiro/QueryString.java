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
     * Parse a query string
     *
     * @param qs Query String (e.g. from req.getQueryString())
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

    /**
     * Is the query string empty?
     *
     * @return True if there are no query parameters.
     */
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
     * For example
     *
     *     if (qs.cond("timestamp", "!year", "!month", "!day")) {
     *         // process using timestamp
     *     }
     *     else if (qs.cond("!timestamp", "year", "month", "day")) {
     *         // process using year month day
     *     }
     *     else if (qs.cond("!timestamp", "!year", "!month", "!day")) {
     *         // process using some default setting
     *     }
     *     else {
     *         throw new BadRequestException(
     *             "Specify 'timestamp' or 'year,month,day' or nil")
     *     }
     *
     * @param keys Variable length number of key conditions.
     *
     * @return True when all key conditions evaluate to true.
     */
    public boolean cond(String... keys) {
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
     * Has a particular query string parameter been specified?
     *
     * @param key Query string parameter key.
     *
     * @return True if 'key' exists.
     */
    public boolean has(String key) {
        return map.containsKey(key);
    }

    /**
     * Does a particular query string parameter have a value?
     *
     * @param key Query string parameter key.
     *
     * @return True if 'key' exists and has a value.
     */
    public boolean hasValue(String key) {
        return has(key) && (get(key) != null);
    }

    /**
     * Is a particular query string parameter value a string?
     *
     * @param key Query string parameter key.
     *
     * @return True if value of key is a string. False if it is not or does not
     *     exist.
     */
    public boolean isString(String key) {
        return hasValue(key);
    }

    /**
     * Is a particular query string parameter value an integer?
     *
     * @param key Query string parameter key.
     *
     * @return True if value of key is an integer. False if it is not or does
     *     not exist.
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
     * Is a particular query string parameter value a long?
     *
     * @param key Query string parameter key.
     *
     * @return True if value of key is a long. False if it is not or does
     *     not exist.
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
     * Is a particular query string parameter value a boolean?
     *
     * @param key Query string parameter key.
     *
     * @return True if value of key is a boolean. False if it is not or does
     *     not exist.
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
     * Get value of a parameter as a string.
     *
     * @param key Query string parameter key.
     *
     * @return String value of key.
     *
     * @throws MissingParameterException when 'key' does not match any query
     *     parameter.
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
     * Get value of a parameter as an integer.
     *
     * @param key Query string parameter key.
     *
     * @return Integer value of key.
     *
     * @throws MissingParameterException when 'key' does not match any query
     *     parameter.
     *
     * @throws WrongParameterTypeException when the value at 'key' is not an
     *     Integer.
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
     * Get value of a parameter as a long.
     *
     * @param key Query string parameter key.
     *
     * @return Long value of key.
     *
     * @throws MissingParameterException when 'key' does not match any query
     *     parameter.
     *
     * @throws WrongParameterTypeException when the value at 'key' is not a
     *     Long.
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
     * Get value of a parameter as a boolean.
     *
     * @param key Query string parameter key.
     *
     * @return Boolean value of key.
     *
     * @throws MissingParameterException when 'key' does not match any query
     *     parameter.
     *
     * @throws WrongParameterTypeException when the value at 'key' is not a
     *     Boolean.
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
     * Get flag
     *
     * Flag is raised when it exist and has no value, or if the value specified
     * is true.
     *
     * Flag is not raised when it does not exist or if the value provided if
     * false.
     *
     * @param key Query string parameter key.
     *
     * @return True if flag is raised.
     *
     * @throws WrongParameterTypeException when a value is present at 'key' but
     *     it is not a Boolean.
     */
    public boolean getFlag(String key) throws WrongParameterTypeException {
        try {
            if (!has(key)) return false;
            if (!hasValue(key)) return true;
            return getBoolean(key);
        } catch (MissingParameterException e) {
            throw new RuntimeException(
                "BUG MissingParameterException should never occur");
        }
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
