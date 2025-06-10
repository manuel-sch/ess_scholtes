package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.PointOfSale;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

@Logged
@ApplicationScoped
@Transactional
public class StockItemCRUDImpl implements StockItemCRUD {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(StockItemCRUDImpl.class);

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    private EntityManager em;


    @Override
    public StockItem createStockItem(StockItem item) {
        IndividualisedProductItem product = item.getProduct();
        if (product != null && product.getId() == 0) {
            product = em.getReference(IndividualisedProductItem.class, product.getId());
            item.setProduct(product);
        }
        em.persist(item);
        return item;
    }

    @Override
    public StockItem readStockItem(IndividualisedProductItem prod, PointOfSale pos) {
        try {
            String queryContent = "SELECT stockItem FROM StockItem stockItem WHERE stockItem.product.id = :prodId AND stockItem.pos.id = :posId";
            TypedQuery<StockItem> query = em.createQuery(queryContent, StockItem.class);
            query.setParameter("prodId", prod.getId()).setParameter("posId", pos.getId());
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public StockItem updateStockItem(StockItem item) {
        item = em.merge(item);
        return item;
    }

    @Override
    public List<StockItem> readStockItemsForProduct(IndividualisedProductItem prod) {
        try {
            String queryContent = "SELECT stockItem FROM StockItem stockItem WHERE stockItem.product.id = :prodId";
            TypedQuery<StockItem> query = em.createQuery(queryContent, StockItem.class);
            query.setParameter("prodId", prod.getId());
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<StockItem> readStockItemsForPointOfSale(PointOfSale pos) {
        try {
            String queryContent = "SELECT stockItem FROM StockItem stockItem WHERE stockItem.pos.id = :posId";
            TypedQuery<StockItem> query = em.createQuery(queryContent, StockItem.class);
            query.setParameter("posId", pos.getId());
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
