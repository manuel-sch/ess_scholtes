package org.dieschnittstelle.ess.mip.components.erp.crud.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.erp.StockItem;
import org.dieschnittstelle.ess.mip.components.erp.crud.api.ProductCRUD;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

import java.util.List;

import static org.dieschnittstelle.ess.utils.Utils.show;

@Logged
@ApplicationScoped
@Transactional
public class ProductCRUDImpl implements ProductCRUD {
    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ProductCRUDImpl.class);

    @Inject
    @EntityManagerProvider.ERPDataAccessor
    private EntityManager em;

    public ProductCRUDImpl() {
        show("ProductCRUDImpl readProProductCRUDImplduct");
    }

    @Override
    public AbstractProduct createProduct(AbstractProduct prod) {
        em.persist(prod);
        return prod;
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        List<AbstractProduct> allProducts = em.createQuery("SELECT product FROM AbstractProduct product", AbstractProduct.class).getResultList();
        return allProducts;
    }

    @Override
    public AbstractProduct updateProduct(long productID, AbstractProduct update) {
        update.setId(productID);
        update = em.merge(update);
        return update;
    }

    @Override
    public AbstractProduct readProduct(long productID) {
        show("ProductCRUDImpl readProduct" + productID);
        return em.find(AbstractProduct.class, productID);
    }

    @Override
    public boolean deleteProduct(long productID) {
        em.remove(em.find(AbstractProduct.class, productID));
        return true;
    }

    @Override
    public List<Campaign> getCampaignsForProduct(long productID) {
        try {
            String queryContent = "SELECT DISTINCT campaign FROM Campaign campaign JOIN campaign.bundles bundle WHERE bundle.product.id = :productId";
            TypedQuery<Campaign> query = em.createQuery(queryContent, Campaign.class);
            query.setParameter("productId", productID);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
