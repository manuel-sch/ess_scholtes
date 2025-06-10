package org.dieschnittstelle.ess.mip.components.erp.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.mip.components.erp.api.StockSystem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.PointOfSaleCRUD;
import org.dieschnittstelle.ess.mip.components.erp.crud.impl.StockItemCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Logged
@ApplicationScoped
public class StockSystemImpl implements StockSystem {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(StockSystemImpl.class);

    @Inject
    private StockItemCRUD stockItemCRUD;

    @Inject
    private PointOfSaleCRUD pointOfSaleCRUD;

    @Override
    public void addToStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        PointOfSale pointOfSale = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        StockItem stockItem = stockItemCRUD.readStockItem(product, pointOfSale);
        if (stockItem == null) {
            stockItemCRUD.createStockItem(new StockItem(product, pointOfSale, units));
        } else {
            stockItem.setUnits(stockItem.getUnits() + units);
            stockItemCRUD.updateStockItem(stockItem);
        }
    }

    @Override
    public void removeFromStock(IndividualisedProductItem product, long pointOfSaleId, int units) {
        PointOfSale pointOfSale = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        StockItem stockItem = stockItemCRUD.readStockItem(product, pointOfSale);
        if (stockItem != null) {
            stockItem.setUnits(stockItem.getUnits() - units);
            stockItemCRUD.updateStockItem(stockItem);
        }
    }

    @Override
    public List<IndividualisedProductItem> getProductsOnStock(long pointOfSaleId) {
        PointOfSale pointOfSale = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        return stockItemCRUD.readStockItemsForPointOfSale(pointOfSale).stream().map(StockItem::getProduct).collect(Collectors.toList());
    }

    @Override
    public List<IndividualisedProductItem> getAllProductsOnStock() {
        return pointOfSaleCRUD.readAllPointsOfSale().stream().flatMap(pos -> stockItemCRUD.readStockItemsForPointOfSale(pos).stream()).map(StockItem::getProduct).distinct().collect(Collectors.toList());
    }

    @Override
    public int getUnitsOnStock(IndividualisedProductItem product, long pointOfSaleId) {
        PointOfSale pointOfSale = pointOfSaleCRUD.readPointOfSale(pointOfSaleId);
        StockItem stockItem = stockItemCRUD.readStockItem(product, pointOfSale);
        if (stockItem == null) {
            return 0;
        }
        return stockItem.getUnits();
    }

    @Override
    public int getTotalUnitsOnStock(IndividualisedProductItem product) {
        List<StockItem> stockItems = stockItemCRUD.readStockItemsForProduct(product);
        return stockItems == null ? 0 : stockItems.stream().mapToInt(StockItem::getUnits).sum();
    }

    @Override
    public List<Long> getPointsOfSale(IndividualisedProductItem product) {
        return stockItemCRUD.readStockItemsForProduct(product).stream().map(stockItem -> stockItem.getPos().getId()).collect(Collectors.toList());
    }
}
