package org.dieschnittstelle.ess.ser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.dieschnittstelle.ess.utils.Utils.*;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;

public class TouchpointServiceServlet extends HttpServlet {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(TouchpointServiceServlet.class);

    public TouchpointServiceServlet() {
        show("TouchpointServiceServlet: constructor invoked\n");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        logger.info("doGet()");

        // we assume here that GET will only be used to return the list of all
        // touchpoints

        // obtain the executor for reading out the touchpoints
        TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext().getAttribute("touchpointCRUD");
        try {
            // set the status
            response.setStatus(HttpServletResponse.SC_OK);
            // obtain the output stream from the response and write the	 list of
            // touchpoints into the stream
            ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
            // write the object
            oos.writeObject(exec.readAllTouchpoints());
            oos.close();
        } catch (Exception e) {
            String err = "got exception: " + e;
            logger.error(err, e);
            throw new RuntimeException(e);
        }

    }

    /*
     * TODO: SER3 server-side implementation of createNewTouchpoint
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        logger.info("doPost() started");
        // assume POST will only be used for touchpoint creation, i.e. there is
        // no need to check the uri that has been used

        // obtain the executor for reading out the touchpoints from the servlet context using the touchpointCRUD attribute

        try {
            // create an ObjectInputStream from the request's input stream
            ObjectInputStream ois = new ObjectInputStream(request.getInputStream());

            // read an AbstractTouchpoint object from the stream
            AbstractTouchpoint touchpoint = (AbstractTouchpoint) ois.readObject();
            logger.info("doPost(): touchpoint received " + touchpoint);

            // obtain the executor for reading out the touchpoints
            TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext().getAttribute("touchpointCRUD");

            // call the create method on the executor and take its return value
            touchpoint = exec.createTouchpoint(touchpoint);

            // set the response status as successful, using the appropriate
            // constant from HttpServletResponse
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/octet-stream"); // Important for raw bytes
            // then write the object to the response's output stream, using a
            // wrapping ObjectOutputStream
            try (ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream())) {
                // ... and write the object to the stream
                oos.writeObject(touchpoint);
            }

        } catch (Exception e) {
            String err = "got exception: " + e;
            logger.error(err, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }

    }
    /*
     * TODO: SER4 server-side implementation of deleteTouchpoint
     */

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("doDelete() started");
        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing id parameter");
                throw new IllegalArgumentException("Missing ID");
            } else {
                TouchpointCRUDExecutor exec = (TouchpointCRUDExecutor) getServletContext().getAttribute("touchpointCRUD");
                long tId = Long.parseLong(idParam);
                logger.info("Attempting to delete ID: " + tId);
                boolean isDeleted = exec.deleteTouchpoint(tId);
                if (!isDeleted) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Touchpoint not found");
                    return;
                }
                logger.info("Deletion successful: " + isDeleted);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (Exception e) {
            String err = "doDelete(): got exception: " + e;
            logger.error(err, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}
