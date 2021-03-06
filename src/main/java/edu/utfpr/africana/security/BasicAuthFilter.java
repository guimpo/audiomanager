package edu.utfpr.africana.security;

import edu.utfpr.africana.dao.UsuarioDao;
import edu.utfpr.africana.model.Usuario;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "BasicAuthFilter", urlPatterns = {"/api/plano", "/api/plano/*"})
public class BasicAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        
        System.out.println((char)27 + "[33m" + "basicAuthFilter");
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
             
        Cookie[] cookies = httpRequest.getCookies();
        String cookieName = "credentials";
        String cookieValue = "";
        
        if(cookies == null) {
            System.out.println((char)27 + "[33m" + "cookie null");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            chain.doFilter(httpRequest,httpResponse);
            return;
        }
        
        for(Cookie c : cookies) {
            if(cookieName.equals(c.getName())) {
                cookieValue = c.getValue();
            }
        }
        
        if(cookieValue.equalsIgnoreCase("")) {
            System.out.println((char)27 + "[33m" + "cookieValue empty");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            chain.doFilter(httpRequest,httpResponse);
        } else {
            String basicAuthString = new String(Base64.getDecoder().decode(cookieValue));
            System.out.println((char)27 + "[33m" + basicAuthString);
            Usuario user = new UsuarioDao().getUsuarioByEmail(basicAuthString.split(":")[0]);
            httpRequest.setAttribute("user", user);
            chain.doFilter(httpRequest,httpResponse);
        }
    }

    @Override
    public void destroy() { }
}
