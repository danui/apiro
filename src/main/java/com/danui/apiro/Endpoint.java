package com.danui.apiro;

import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles matched requests
 *
 * @author Wong H'sien Jin
 */
public interface Endpoint {
    /**
     * Handle a request.
     *
     * @param req Request
     *
     * @param res Response
     *
     * @param matcher Matcher that was used to match req.getPathInfo() to this
     *     Endpoint. Therefore matcher.matches() is will be true.
     *     Implementations can read path parameters, if any, by using
     *     matcher.group().
     */
    public void handle(HttpServletRequest req, HttpServletResponse res,
        Matcher matcher);
}
