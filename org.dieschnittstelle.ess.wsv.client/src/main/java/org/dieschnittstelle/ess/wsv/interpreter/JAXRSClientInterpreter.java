package org.dieschnittstelle.ess.wsv.interpreter;


import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.entity.ByteArrayEntity;

import org.dieschnittstelle.ess.utils.Http;
import org.dieschnittstelle.ess.wsv.interpreter.json.JSONObjectSerialiser;
import org.dieschnittstelle.ess.wsv.interpreter.json.ObjectMappingException;


/*
 * TODO WSV1: implement this class such that the crud operations declared on ITouchpointCRUDService in .ess.wsv can be successfully called from the class AccessRESTServiceWithInterpreter in the .esa.wsv.client project
 */
public class JAXRSClientInterpreter implements InvocationHandler {

    // use a logger
    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(JAXRSClientInterpreter.class);

    // declare a baseurl
    private String baseurl;

    // declare a common path segment
    private String commonPath;

    // use our own implementation JSONObjectSerialiser
    private JSONObjectSerialiser jsonSerialiser = new JSONObjectSerialiser();

    // use an attribute that holds the serviceInterface (useful, e.g. for providing a toString() method)
    private Class serviceInterface;

    // use a constructor that takes an annotated service interface and a baseurl. the implementation should read out the path annotation,
    // we assume we produce and consume json, i.e. the @Produces and @Consumes annotations will not be considered here
    public JAXRSClientInterpreter(Class serviceInterface, String baseurl) {

        // implement the constructor!
        this.serviceInterface = serviceInterface;
        this.baseurl = baseurl;
        Path path = (Path) serviceInterface.getAnnotation(Path.class);
        this.commonPath = (path != null) ? path.value() : "";

        logger.info("<constructor>: " + serviceInterface + " / " + baseurl + " / " + commonPath);
    }

    // implement this method interpreting jax-rs annotations on the meth argument
    @Override
    public Object invoke(Object proxy, Method meth, Object[] args) throws Throwable {


        // check whether we handle the toString method and give some appropriate return value
        Object defaultObjectMethodAnswer = handleObjectDefaultMethods(proxy, meth, args);
        if (defaultObjectMethodAnswer != null) return defaultObjectMethodAnswer;

        // use a default http client
        HttpClient client = Http.createSyncClient();

        // Create the requestUrl using baseurl and commonpath (further segments may be added if the method has an own @Path annotation)
        String requestUrl = buildRequestUrl(meth);

        // a value that needs to be sent via the http request body
        Object requestBodyData = null;

        // check whether we have method arguments - only consider pathparam annotations (if any) on the first argument here -
        //  if no args are passed, the value of args is null!
        //  if no pathparam annotation is present assume that the argument value is passed via the body of the http request
        if (args != null && args.length > 0) {
            if (meth.getParameterAnnotations()[0].length > 0 && meth.getParameterAnnotations()[0][0].annotationType() == PathParam.class) {
                // handle PathParam on the first argument - do not forget that in this case we might have a second argument providing a requestBodyData
                // if we have a path param, we need to replace the corresponding pattern in the requestUrl with the parameter value
                Parameter firstParameter = meth.getParameters()[0];
                if (firstParameter.isAnnotationPresent(PathParam.class)) {
                    {
                        String name = firstParameter.getAnnotation(PathParam.class).value();
                        String value = args[0].toString();
                        requestUrl = requestUrl.replace("{" + name + "}", value);
                    }
                }
                if (args.length > 1) {
                    requestBodyData = args[1];
                }

            } else {
                // if we do not have a path param, we assume the argument value will be sent via the body of the request
                requestBodyData = args[0];
            }
        }

        // declare a HttpUriRequest variable
        HttpUriRequest request = null;

        // check which of the http method annotation is present and instantiate request accordingly passing the requestUrl
        request = buildRequestFromMethod(meth, requestUrl);

        // add a header on the request declaring that we accept json (for header names, you can use the constants declared in jakarta.ws.rs.core.HttpHeaders, for content types use the constants from jakarta.ws.rs.core.MediaType;)
        request.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

        if (requestBodyData != null) {
            attachRequestBody(request, requestBodyData);
            request.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }

        logger.info("invoke(): executing request: " + request);

        // then send the request to the server and get the response
        HttpResponse response = client.execute(request);

        logger.info("invoke(): received response: " + response);

        // check the response code
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            // declare a variable for the return value
            Object returnValue = null;

            // convert the resonse body to a java object of an appropriate type considering the return type of the method as returned by getGenericReturnType() and set the object as value of returnValue
            try (InputStream is = response.getEntity().getContent()) {
                Type returnType = meth.getGenericReturnType();
                returnValue = jsonSerialiser.readObject(is, returnType);
                logger.info("invoke(): returning value: " + returnValue);
                // and return the return value
                return returnValue;
            }
        } else {
            throw new RuntimeException("Got unexpected status from server: " + response.getStatusLine());
        }
    }

    private Object handleObjectDefaultMethods(Object proxy, Method meth, Object[] args) {
        if (meth.getDeclaringClass() == Object.class) {
            switch (meth.getName()) {
                case "toString":
                    return "Proxy for: " + serviceInterface.getName() + " -> " + baseurl + commonPath;
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "equals":
                    return proxy == args[0];
                default:
                    throw new UnsupportedOperationException("Object method not handled: " + meth);
            }
        }
        return null;
    }

    private HttpRequestBase buildRequestFromMethod(Method meth, String requestUrl) {
        HttpRequestBase httpRequest = null;
        String httpMethod = getHttpMethod(meth);
        switch (httpMethod) {
            case "GET":
                httpRequest = new HttpGet(requestUrl);
                break;
            case "POST":
                httpRequest = new HttpPost(requestUrl);
                break;
            case "PUT":
                httpRequest = new HttpPut(requestUrl);
                break;
            case "DELETE":
                httpRequest = new HttpDelete(requestUrl);
                break;
            default:
                httpRequest = new HttpGet(requestUrl);
                break;
        }
        return httpRequest;
    }

    private String buildRequestUrl(Method meth) {
        String requestUrl = baseurl + commonPath;

        // check whether we have a path annotation and append the requestUrl (path params will be handled when looking at the method arguments)
        if (meth.isAnnotationPresent(Path.class)) {
            requestUrl += meth.getAnnotation(Path.class).value();
        }
        return requestUrl;
    }

    private String getHttpMethod(Method method) {
        if (method.isAnnotationPresent(GET.class)) return "GET";
        if (method.isAnnotationPresent(POST.class)) return "POST";
        if (method.isAnnotationPresent(PUT.class)) return "PUT";
        if (method.isAnnotationPresent(DELETE.class)) return "DELETE";
        throw new UnsupportedOperationException("Unsupported HTTP method");
    }

    private void attachRequestBody(HttpRequest request, Object requestBodyData) {
        // if a body shall be sent, convert the requestBodyData to json, create an entity from it and set it on the request
        if (requestBodyData == null) return;
        try {
            // use a ByteArrayOutputStream for writing json
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // write the object to the stream using the jsonSerialiser
            jsonSerialiser.writeObject(requestBodyData, baos);

            // if we need to send the method argument in the request body we need to declare an entity
            // create an ByteArrayEntity from the stream's content, assiging it to requestBodyDataAsJson
            ByteArrayEntity requestBodyDataAsJson = new ByteArrayEntity(baos.toByteArray(), ContentType.APPLICATION_JSON);

            // set the entity on the request, which must be cast to HttpEntityEnclosingRequest
            ((HttpEntityEnclosingRequest) request).setEntity(requestBodyDataAsJson); // Setze den Body
        } catch (ObjectMappingException e) {
            logger.error("Serialisierung des Request-Bodys fehlgeschlagen", e);
            throw new RuntimeException("Fehler beim Serialisieren des Request-Bodys", e);
        }

    }
}