package org.dieschnittstelle.ess.mip.components.shopping.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

// TODO: PAT1: this is the interface to be provided as a rest service if rest service access is used
@RegisterRestClient
@Path("/shoppingCarts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface PurchaseService {

    @POST
    @Path("/{cartId}/purchase")
    void purchaseCartAtTouchpointForCustomer(@PathParam("cartId") long shoppingCartId, @QueryParam("touchPointId") long touchPointId,
                                             @QueryParam("customerId") long customerId) throws ShoppingException;
}
