package com.danui.apiro;

import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApiroTest {

    private HttpServletRequest newRequest(final String pathInfo) {
        HttpServletRequest result = mock(HttpServletRequest.class);
        when(result.getPathInfo()).thenReturn(pathInfo);
        return result;
    }

    private HttpServletResponse newResponse() {
        HttpServletResponse result = mock(HttpServletResponse.class);
        return result;
    }

    private static class Ep implements Endpoint {
        public HttpServletRequest req;
        public HttpServletResponse res;
        public Matcher matcher;
        @Override
        public void handle(HttpServletRequest req, HttpServletResponse res,
            Matcher matcher) {

            this.req = req;
            this.res = res;
            this.matcher = matcher;
        }
        public boolean wasCalled() {
            return req != null;
        }
    }

    @Test
    public void testReturnFalseWhenNoRoutes() {
        Router router = new Router();
        HttpServletRequest req = newRequest("/doc");
        HttpServletResponse res = newResponse();
        assertFalse(router.dispatch(req, res));
    }

    @Test
    public void testRoutesToMatchedEndpoint() {
        Router router = new Router();
        Ep epDocWrong = new Ep();
        Ep epDocCorrect = new Ep();
        router.on("/docwrong/?", epDocWrong);
        router.on("/doccorrect/?", epDocCorrect);
        HttpServletRequest req = newRequest("/doccorrect/");
        HttpServletResponse res = newResponse();
        assertTrue(router.dispatch(req, res));
        assertTrue(epDocCorrect.wasCalled());
        assertFalse(epDocWrong.wasCalled());
    }

    /**
     * Test routing with path params. The Endpoint should receive each path
     * parameter as a matched group.
     */
    @Test
    public void testPathParams() {
        Router router = new Router();
        Ep epDoc = new Ep();
        router.on("/doc/(S[0-9]{6})/page/([0-9]{1,})?", epDoc);
        HttpServletRequest req = newRequest("/doc/S123456/page/42");
        HttpServletResponse res = newResponse();
        assertTrue(router.dispatch(req, res));
        assertTrue(epDoc.wasCalled());
        assertEquals("S123456", epDoc.matcher.group(1));
        assertEquals("42", epDoc.matcher.group(2));
    }

    /**
     * Test routing with path params that are named. The Endpoint should receive
     * each path parameter as a matched named group.
     */
    @Test
    public void testNamedPathParams() {
        Router router = new Router();
        Ep epDoc = new Ep();
        router.on("/doc/(?<id>[^/]+)/page/(?<pg>[0-9]{1,})/?", epDoc);
        HttpServletRequest req = newRequest("/doc/S123456/page/42");
        HttpServletResponse res = newResponse();
        assertTrue(router.dispatch(req, res));
        assertTrue(epDoc.wasCalled());
        assertEquals("S123456", epDoc.matcher.group("id"));
        assertEquals("42", epDoc.matcher.group("pg"));
    }

}
