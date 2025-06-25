package org.dieschnittstelle.ess.mip.client.shopping;

import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.shopping.ShoppingCartItem;
import org.dieschnittstelle.ess.mip.client.apiclients.ServiceProxyFactory;
import org.dieschnittstelle.ess.mip.client.apiclients.ShoppingCartClient;
import org.dieschnittstelle.ess.mip.components.crm.api.CustomerTracking;
import org.dieschnittstelle.ess.mip.components.shopping.api.PurchaseService;
import org.dieschnittstelle.ess.mip.components.shopping.api.ShoppingException;
import org.dieschnittstelle.ess.entities.crm.AbstractTouchpoint;
import org.dieschnittstelle.ess.entities.crm.Customer;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCartService;

public class PurchaseServiceClient implements ShoppingBusinessDelegate {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(PurchaseServiceClient.class);

    private ShoppingCartClient shoppingCartClient;
    private PurchaseService purchaseServiceProxy;

    private AbstractTouchpoint touchpoint;
    private Customer customer;

    /*
     * TODO PAT1: use an appropriate proxy for the server-side purchase service
     *  Note that touchpoint and customer need to be stored locally
     *  before purchase() is invoked. For accessing shopping cart data use a local ShoppingCartClient
     *  in this case and access the shopping cart using the provided getter method
     */

    public PurchaseServiceClient() {
        /* instantiate the proxy using the ServiceProxyFactory (see the other client classes) */
        try {
            shoppingCartClient = new ShoppingCartClient();
            purchaseServiceProxy = ServiceProxyFactory.getInstance().getProxy(PurchaseService.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* TODO: implement the following methods s */

    @Override
    public void setTouchpoint(AbstractTouchpoint touchpoint) {
        this.touchpoint = touchpoint;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void addProduct(AbstractProduct product, int units) {
        this.shoppingCartClient.addItem(new ShoppingCartItem(product.getId(), units, product instanceof Campaign));
    }

    @Override
    public void purchase() throws ShoppingException {
        this.purchaseServiceProxy.purchaseCartAtTouchpointForCustomer(shoppingCartClient.getShoppingCartEntityId(), customer.getId(), touchpoint.getId());
    }

}
