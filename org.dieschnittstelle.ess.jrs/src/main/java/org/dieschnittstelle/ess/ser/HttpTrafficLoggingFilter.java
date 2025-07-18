package org.dieschnittstelle.ess.ser;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import static org.dieschnittstelle.ess.utils.Utils.show;

/**
 * example of a filter, taken from Crawford/Kaplan, JEE Design Patterns, 2003
 *
 * @author joern
 */
public class HttpTrafficLoggingFilter implements Filter {

    /**
     * the logger
     */
    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(HttpTrafficLoggingFilter.class);

    /**
     * the config passed with the init method
     */
    private FilterConfig config;

    /**
     * constructor for lifecycle logging
     */
    public HttpTrafficLoggingFilter() {
        show("HttpTrafficLoggingFilter: constructor invoked\n");
    }

    @Override
    public void destroy() {
        logger.info("destroy()");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        System.out.println("\n\n%%%%%%%%%%%%%%% start request processing %%%%%%%%%%%%%%%\n");

        show("HttpTrafficLoggingFilter: doFilter() invoked\n");

        logger.info("doFilter(): " + request + ", " + response + ", " + chain);

        // obtain the servlet context
        ServletContext sc = config.getServletContext();

        // log the request
        logger.info("request is:\n" + logRequest((HttpServletRequest) request));

        // continue filtering
        chain.doFilter(request, response);

        System.out.println("\n\n%%%%%%%%%%%%%%% request processing done %%%%%%%%%%%%%%%\n");
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("init(): " + config);

        this.config = config;
    }

    /**
     * static method for logging a request
     * <p>
     * this also exemplifies access to the attributes, parameters and headers of
     * a request
     */
    public static String logRequest(HttpServletRequest request) {
        StringBuffer buf = new StringBuffer();


        // access the attributes
        buf.append("Request Properties:");
        buf.append("\nMethod: " + request.getMethod());
        buf.append("\nProtocol: " + request.getProtocol());
        buf.append("\nQueryString: " + request.getQueryString());
        buf.append("\nRequestURL: " + request.getRequestURL());
        buf.append("\nRequestURI: " + request.getRequestURI());
        buf.append("\nLocalName: " + request.getLocalName());
        buf.append("\nLocalAddr: " + request.getLocalAddr());
        buf.append("\nLocalPort: " + request.getLocalPort());
        buf.append("\nContextPath: " + request.getContextPath());
        buf.append("\nServletPath: " + request.getServletPath());
        buf.append("\nPathInfo: " + request.getPathInfo());
        buf.append("\nRealPath: "
                + request.getServletContext().getRealPath(
                request.getServletPath()));
        buf.append("\nContentType: " + request.getContentType());

        buf.append("\nParameters:");

        // access the parameters
        for (Enumeration<String> e = request.getParameterNames(); e
                .hasMoreElements(); ) {
            String name = e.nextElement();
            String[] vals = request.getParameterValues(name);
            buf.append("\n\t" + name + "=" + Arrays.toString(vals));
        }

        // access the headers
        buf.append("\nRequest Header:");

        for (Enumeration<String> e = request.getHeaderNames(); e
                .hasMoreElements(); ) {
            String name = e.nextElement();
            String value = request.getHeader(name);
            buf.append("\n\t" + name + "=" + value);
        }

        buf.append("\nAttributes:");
        for (Enumeration<String> e = request.getAttributeNames(); e
                .hasMoreElements(); ) {
            String name = e.nextElement();
            Object value = request.getAttribute(name);
            buf.append("\n\t" + name + "=" + value + " of type "
                    + (value == null ? "<null type>" : value.getClass()));
        }

        return buf.toString();
    }

}
