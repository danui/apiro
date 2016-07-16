package com.danui.apiro;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Routers route incoming HTTP Servlet request-response pairs to a destination
 * endpoint, if any so matches.
 *
 * @author Wong H'sien Jin
 */
public class Router {
    private final ArrayList<Route> routes = new ArrayList<>();

    /**
     * Bind endpoint to router.
     *
     * @param regex Regular expression that would be used to match requests for
     *     'endpoint'.
     *
     * @param endpoint Endpoint to route request when the regular expression
     *     matches.
     *
     */
    public void on(String regex, Endpoint endpoint) {
        routes.add(new Route(regex, endpoint));
    }

    /**
     * Dispatch request-response to a matching endpoint.
     *
     * @param req Servlet request.
     *
     * @param res Servlet response.
     *
     * @return True if the request was handled.
     */
    public boolean dispatch(HttpServletRequest req, HttpServletResponse res) {
        for (Route route : routes) {
            if (route.handle(req, res)) {
                return true;
            }
        }
        return false;
    }
}
