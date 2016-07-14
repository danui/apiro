package com.danui.apiro;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class QueryStringTest {

    @Test
    public void testNull() {
        QueryString qs = QueryString.parse(null);
        assertTrue(qs.isEmpty());
    }

    @Test
    public void testEmptyString() {
        QueryString qs = QueryString.parse("");
        assertTrue(qs.isEmpty());
    }

    @Test
    public void testEmptyComponents() {
        QueryString qs = QueryString.parse("&");
        assertTrue(qs.isEmpty());
    }

    @Test
    public void testSingleKeyValue() throws Exception {
        QueryString qs = QueryString.parse("x=y");
        assertFalse(qs.isEmpty());
        assertTrue(qs.has("x"));
        assertTrue(qs.hasValue("x"));
        assertEquals("y", qs.getString("x"));
    }

    @Test
    public void testKeyWithoutValue() throws Exception {
        QueryString qs = QueryString.parse("x");
        assertFalse(qs.isEmpty());
        assertTrue(qs.has("x"));
        assertFalse(qs.hasValue("x"));
    }

    @Test
    public void testMultipleKeys() throws Exception {
        int numPairs = 5;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numPairs; ++i) {
            if (i > 0) sb.append("&");
            sb.append("k"+i);
            sb.append("=");
            sb.append("v"+i);
        }
        QueryString qs = QueryString.parse(sb.toString());
        assertFalse(qs.isEmpty());
        for (int i = 0; i < numPairs; ++i) {
            assertTrue(qs.has("k"+i));
            assertTrue(qs.hasValue("k"+i));
            assertEquals("v"+i, qs.getString("k"+i));
        }
    }

    @Test
    public void testHas() throws Exception {
        QueryString qs = QueryString.parse("a&b&c");

        // Single
        assertFalse(qs.has("!a"));
        assertFalse(qs.has("x"));
        assertTrue(qs.has("!x"));
        assertTrue(qs.has("a"));

        // Multiple
        assertFalse(qs.has("!a", "b", "c"));
        assertFalse(qs.has("a", "x"));
        assertTrue(qs.has("a", "!x"));
        assertTrue(qs.has("a", "b", "c"));
    }

    @Test
    public void testIsString() throws Exception {
        QueryString qs = QueryString.parse("subject=cat&nullsubject");
        assertTrue(qs.isString("subject"));
        assertFalse(qs.isString("nullsubject"));
    }

    @Test
    public void testIsInteger() throws Exception {
        QueryString qs = QueryString.parse(
            "t0=0&t1=1&t2=-1&f0=1.3&f1=abc&f2=%20&f3=21474836470");
        assertTrue(qs.isInteger("t0"));
        assertTrue(qs.isInteger("t1"));
        assertTrue(qs.isInteger("t2"));
        assertFalse(qs.isInteger("f0"));
        assertFalse(qs.isInteger("f1"));
        assertFalse(qs.isInteger("f2"));
        assertFalse(qs.isInteger("f3"));
    }

    @Test
    public void testIsLong() throws Exception {
        QueryString qs = QueryString.parse(
            "t0=0&t1=1&t2=-1&f0=1.3&f1=abc&f2=%20&t3=21474836470");
        assertTrue(qs.isLong("t0"));
        assertTrue(qs.isLong("t1"));
        assertTrue(qs.isLong("t2"));
        assertTrue(qs.isLong("t3"));
        assertFalse(qs.isLong("f0"));
        assertFalse(qs.isLong("f1"));
        assertFalse(qs.isLong("f2"));
    }

    @Test
    public void testIsBoolean() throws Exception {
        int idx;
        List<String> trueCases = Arrays.asList("true", "false", "True",
            "False", "TRUE", "trUE", "FaLSe");
        List<String> falseCases = Arrays.asList("t", "f", "yes", "no",
            "abc", "F", "T", "0", "1");
        StringBuilder sb = new StringBuilder();
        sb.append("x");
        idx = 0;
        for (String val : trueCases) {
            sb.append(String.format("&t%d=%s", idx, val));
            idx += 1;
        }
        idx = 0;
        for (String val : falseCases) {
            sb.append(String.format("&f%d=%s", idx, val));
            idx += 1;
        }
        QueryString qs = QueryString.parse(sb.toString());
        idx = 0;
        for (String val : trueCases) {
            assertTrue(String.format("%s is a boolean", val),
                qs.isBoolean("t"+idx));
            idx += 1;
        }
        idx = 0;
        for (String val : falseCases) {
            assertFalse(String.format("%s is not a boolean", val),
                qs.isBoolean("f"+idx));
            idx += 1;
        }
    }

    @Test
    public void testGetString() throws Exception {
        QueryString qs = QueryString.parse("k=v&subject=cat&x");
        assertEquals("cat", qs.getString("subject"));
    }

    @Test
    public void testGetInteger() throws Exception {
        QueryString qs = QueryString.parse(
            "t0=0&t1=1&t2=-1&f0=1.3&f1=abc&f2=%20&f3=21474836470");
        assertEquals(0, qs.getInteger("t0"));
        assertEquals(1, qs.getInteger("t1"));
        assertEquals(-1, qs.getInteger("t2"));
    }

    @Test
    public void testGetLong() throws Exception {
        QueryString qs = QueryString.parse(
            "t0=0&t1=1&t2=-1&f0=1.3&f1=abc&f2=%20&t3=21474836470");
        assertEquals(0L, qs.getLong("t0"));
        assertEquals(1L, qs.getLong("t1"));
        assertEquals(-1L, qs.getLong("t2"));
        assertEquals(21474836470L, qs.getLong("t3"));
    }

    @Test
    public void testGetBoolean() throws Exception {
        for (String val : Arrays.asList("true", "TRUE", "True")) {
            QueryString qs = QueryString.parse("key="+val);
            assertTrue(val+" is true", qs.getBoolean("key"));
        }
        for (String val : Arrays.asList("false", "FALSE", "False")) {
            QueryString qs = QueryString.parse("key="+val);
            assertFalse(val+" is false", qs.getBoolean("key"));
        }
    }

    @Test
    public void testGetFlag() throws Exception {
        for (String val : Arrays.asList("true", "TRUE", "True")) {
            QueryString qs = QueryString.parse("key="+val);
            assertTrue(val+" is true", qs.getFlag("key"));
        }
        for (String val : Arrays.asList("false", "FALSE", "False")) {
            QueryString qs = QueryString.parse("key="+val);
            assertFalse(val+" is false", qs.getFlag("key"));
        }
        // ...
        {
            QueryString qs = QueryString.parse(
                "opt1&opt3=true&opt4=false");
            assertTrue(qs.getFlag("opt1"));
            assertFalse(qs.getFlag("opt2"));
            assertTrue(qs.getFlag("opt3"));
            assertFalse(qs.getFlag("opt4"));
        }

    }
}
