package org.dieschnittstelle.ess.mip.components.shopping.cart.api;

import org.dieschnittstelle.ess.entities.shopping.ShoppingCartItem;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * Created by master on 20.02.17.
 */
@Path("/shoppingcarts")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface ShoppingCartService {

    @POST
    public long createNewCart();

    @POST
    @Path("/{cartId}")
    public void addItem(@PathParam("cartId") long cartId, ShoppingCartItem product);

    @GET
    @Path("/{cartId}")
    public List<ShoppingCartItem> getItems(@PathParam("cartId") long cartId);

    @DELETE
    @Path("/{cartId}")
    public boolean deleteCart(@PathParam("cartId") long cartId);

}
