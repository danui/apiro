package com.apiro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class Route {

    private final Pattern pattern;
    private final Endpoint endpoint;

    Route(String regex, Endpoint endpoint) {
        this.pattern = Pattern.compile(regex);
        this.endpoint = endpoint;
    }

    /**
     * Handle request if it matches the regex.
     *
     * On a match, forwards request, response, and matched matcher to endpoint.
     *
     * @return True if request was matched and handled.
     */
    boolean handle(HttpServletRequest req, HttpServletResponse res) {
        Matcher matcher = pattern.matcher(req.getPathInfo());
        if (matcher.matches()) {
            endpoint.handle(req, res, matcher);
            return true;
        }
        return false;
    }
}
