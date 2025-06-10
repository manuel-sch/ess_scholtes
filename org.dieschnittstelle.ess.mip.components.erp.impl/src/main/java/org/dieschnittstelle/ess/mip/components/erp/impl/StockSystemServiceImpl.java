package org.dieschnittstelle.ess.mip.components.erp.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystemService;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

@Logged
@ApplicationScoped
public class StockSystemServiceImpl implements StockSystemService {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(StockSystemServiceImpl.class);

    @Inject
    private StockSystem stockSystem;

    @Inject
    private ProductCRUD productCRUD;

    public StockSystemServiceImpl() {
        logger.info("<constructor>: " + this);
    }

    @Override
    public void addToStock(long productId, long pointOfSaleId, int units) {
        AbstractProduct product = productCRUD.readProduct(productId);
        if (product != null) {
            if (!(product instanceof IndividualisedProductItem item)) {
                throw new IllegalArgumentException("Product is not an IndividualisedProductItem");
            }
            stockSystem.addToStock(item, pointOfSaleId, units);
        }
    }

    @Override
    public void removeFromStock(long productId, long pointOfSaleId, int units) {
        AbstractProduct product = productCRUD.readProduct(productId);
        if (product != null) {
            if (!(product instanceof IndividualisedProductItem item)) {
                throw new IllegalArgumentException("Product is not an IndividualisedProductItem");
            }
            stockSystem.removeFromStock(item, pointOfSaleId, units);
        }
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        return pointOfSaleId == -2 ? stockSystem.getAllProductsOnStock() : stockSystem.getProductsOnStock(pointOfSaleId);
    }

    @Override
    public int getUnitsOnStock(long productId, long pointOfSaleId) {
        AbstractProduct product = productCRUD.readProduct(productId);
        if (product == null) {
            return 0;
        }
        if (!(product instanceof IndividualisedProductItem item)) {
            throw new IllegalArgumentException("Product is not an IndividualisedProductItem");
        }
        if (pointOfSaleId == 0) {
            return stockSystem.getTotalUnitsOnStock((IndividualisedProductItem) product);
        } else {
            return stockSystem.getUnitsOnStock((IndividualisedProductItem) product, pointOfSaleId);
        }
    }

    @Override
    public List<Long> getPointsOfSale(long productId) {
        AbstractProduct product = productCRUD.readProduct(productId);
        if (product == null) {
            return List.of();
        }
        if (!(product instanceof IndividualisedProductItem item)) {
            throw new IllegalArgumentException("Product is not an IndividualisedProductItem");
        }
        return stockSystem.getPointsOfSale(item);
    }
}
