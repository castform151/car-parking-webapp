package com.group.CarParking.Util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Cookie[] cookies = request.getCookies();
    var vv = request.getRequestURI();
    if (vv.equals("/register") || vv.equals("/index.html") || vv.equals("/")) {
      return true;
    }
    if(cookies == null){
      response.sendRedirect("/register?logout=0");
      return true;
    }
    boolean isAdmin = false;
    System.out.println(vv);
    if (cookies != null)
      for (int i = 0; i < cookies.length; i++) {
        if (cookies[i].getName().equals("admin") && cookies[i].getValue().equals("true")) {
          isAdmin = true;
        }
        System.out.println(cookies[i].getName() + "=" + cookies[i].getValue());
      }
    if (vv.startsWith("/admin") && !isAdmin) {
      response.sendError(403);
      return true;
    }
    if (vv.startsWith("/dashboard") && isAdmin) {
      response.sendRedirect("/admin");
      return true;
    }
    if (cookies.length <= 1)
      response.sendError(403);
    return cookies.length > 1;
  }

}
