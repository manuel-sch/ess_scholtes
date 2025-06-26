package org.dieschnittstelle.ess.ser;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static org.dieschnittstelle.ess.utils.Utils.show;

/**
 * checks whether the gui servlet is accessed by a user agent that accepts html
 *
 * @author kreutel
 */
public class TouchpointServiceServletFilter implements Filter {

    protected static Logger logger = org.apache.logging.log4j.LogManager
            .getLogger(TouchpointServiceServletFilter.class);

    public TouchpointServiceServletFilter() {
        show("TouchpointServiceServletFilter: constructor invoked\n");
    }


    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        show("TouchpointServiceServletFilter: doFilter() invoked\n");

        // check whether we have a an accept-language header that will be set by the browser but not by the apache http client.
        // otherwise reject the request
        String acceptLanguageHeader = ((HttpServletRequest) request)
                .getHeader("accept-language");
        logger.info("got accept-language header: " + acceptLanguageHeader);

        // we do quite a brute force string match
        if (acceptLanguageHeader == null || acceptLanguageHeader.isBlank()) {
            chain.doFilter(request, response);
        } else {
            // if we do find the forbidden header, we block access
            ((HttpServletResponse) response)
                    .setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}
