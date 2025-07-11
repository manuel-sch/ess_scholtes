package org.dieschnittstelle.ess.mip.client.apiclients;

import java.util.ArrayList;
import java.util.List;

import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

public class StockSystemClient implements StockSystem {

    private StockSystemService serviceProxy;

    public StockSystemClient() throws Exception {
        // remove the comments and complete the implementation
        this.serviceProxy = ServiceProxyFactory.getInstance().getProxy(StockSystemService.class);
    }

    // uncomment the commented sections from all the following methods and remove the default return statements

    @Override
    public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        this.serviceProxy.addToStock(product.getId(), pointOfSaleId, units);
    }

    @Override
    public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId,
                                int units) {
        this.serviceProxy.removeFromStock(product.getId(), pointOfSaleId, units);
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        return this.serviceProxy.getProductsOnStock(pointOfSaleId);
    }

    @Override
    public List<IndividualisedProductItem> getAllProductsOnStock() {
        return this.serviceProxy.getProductsOnStock(-2);
    }

    @Override
    public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
        return this.serviceProxy.getUnitsOnStock(product.getId(), pointOfSaleId);
    }

    @Override
    public int getTotalUnitsOnStock(IndividualisedProductItem product) {
        return this.serviceProxy.getUnitsOnStock(product.getId(), 0);
    }

    @Override
    public List<Long> getPointsOfSale(IndividualisedProductItem product) {
        return this.serviceProxy.getPointsOfSale(product.getId());
    }


}
