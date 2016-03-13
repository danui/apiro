package com.danui.apiro;

import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles matched requests
 */
public interface Endpoint {
	/**
	 * Handle a request.
	 *
	 * <p>matcher.matches().</p>
	 */
	public void handle(HttpServletRequest req, HttpServletResponse res,
		Matcher matcher);
}
