package org.dieschnittstelle.ess.mip.client.apiclients;

import java.util.List;

import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.mip.client.Constants;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;

public class ProductCRUDClient implements ProductCRUD {

    private ProductCRUD serviceProxy;

    public ProductCRUDClient() throws Exception {
        // obtain a proxy specifying the service interface. Let all subsequent methods use the proxy.
        this.serviceProxy = ServiceProxyFactory.getInstance().getProxy(ProductCRUD.class);
    }

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        AbstractProduct created = serviceProxy.createProduct(prod);
//		// as a side-effect we set the id of the created product on the argument before returning
        prod.setId(created.getId());
        return created;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        return serviceProxy.readAllProducts();
    }

    @Override
    public AbstractProduct updateProduct(long productId, AbstractProduct update) {
        return serviceProxy.updateProduct(update.getId(), update);
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        return serviceProxy.readProduct(productID);
    }

    @Override
    public boolean deleteProduct(long productID) {
        return serviceProxy.deleteProduct(productID);
    }

    @Override
    public List<Campaign> getCampaignsForProduct(long productID) {
        return serviceProxy.getCampaignsForProduct(productID);
    }

}
