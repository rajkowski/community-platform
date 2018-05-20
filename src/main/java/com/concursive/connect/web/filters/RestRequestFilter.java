package com.concursive.connect.web.filters;

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.security.auth.login.LoginException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * Validates REST requests
 */
public class RestRequestFilter implements Filter {

  private static Log LOG = LogFactory.getLog(RestRequestFilter.class);

  public void init(FilterConfig config) throws ServletException {
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
    HttpServletRequest hsRequest = (HttpServletRequest) request;
    HttpServletResponse hsResponse = (HttpServletResponse) response;

    // Determine the requested path
    String contextPath = hsRequest.getContextPath();
    String uri = hsRequest.getRequestURI();
    String requestedPath = uri.substring(contextPath.length());
    String resource = uri.substring(contextPath.length());

    LOG.debug("Requested Path is: " + requestedPath);

    String apiKey = ((HttpServletRequest) request).getHeader("X-API-Key");
    if (StringUtils.isEmpty(apiKey)) {
      apiKey = request.getParameter("key");
    }
    if (StringUtils.isEmpty(apiKey)) {
      LOG.debug("Invalid key");
      hsResponse.sendError(401, "Unauthorized");
      return;
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug(((HttpServletRequest) request).getMethod() + " " + resource);
    }

    boolean isAuthorized = checkAuthorization(request);
    if (isAuthorized) {
      chain.doFilter(request, hsResponse);
    } else {
      doUnauthorized(hsResponse);
    }
  }

  private boolean checkAuthorization(ServletRequest servletRequest) {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null) {
      return false;
    }
    StringTokenizer st = new StringTokenizer(authHeader);
    if (!st.hasMoreTokens()) {
      return false;
    }
    String basic = st.nextToken();
    if (!basic.equalsIgnoreCase("Basic")) {
      LOG.debug("Client must use BASIC authentication");
      return false;
    }
    try {
      String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
      int p = credentials.indexOf(":");
      if (p == -1) {
        LOG.debug("Client did not specify credentials");
        return false;
      }
      String email = credentials.substring(0, p).trim();
      String password = credentials.substring(p + 1).trim();
      // Attempt the login
      try {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
          throw new LoginException("An email and password are required");
        }
        // @todo Verify email and password
        User user = UserUtils.createGuestUser();
        request.setAttribute(Constants.REST_USER, user);
        return true;
      } catch (LoginException e) {
        LOG.debug("Credentials are invalid");
      }
    } catch (UnsupportedEncodingException e) {
      LOG.error("UnsupportedEncodingException: " + e.getMessage());
    }
    return false;
  }

  private void doUnauthorized(ServletResponse servletResponse) throws IOException {
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    response.setHeader("WWW-Authenticate", "Basic realm=\"Protected\"");
    response.sendError(401, "Unauthorized");
  }
}