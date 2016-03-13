package com.apiro;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Router {
    private final ArrayList<Route> routes = new ArrayList<>();

    public void on(String regex, Endpoint endpoint) {
        routes.add(new Route(regex, endpoint));
    }

    /**
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
