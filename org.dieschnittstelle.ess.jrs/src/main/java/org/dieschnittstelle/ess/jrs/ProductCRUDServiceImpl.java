package org.dieschnittstelle.ess.jrs;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.entities.GenericCRUDExecutor;
import org.dieschnittstelle.ess.entities.crm.StationaryTouchpoint;
import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;

import java.util.List;

/*
 * TODO JRS2: implementieren Sie hier die im Interface deklarierten Methoden
 */

public class ProductCRUDServiceImpl implements IProductCRUDService {

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ProductCRUDServiceImpl.class);

    private GenericCRUDExecutor<AbstractProduct> crud;

    public ProductCRUDServiceImpl(@Context ServletContext servletContext, @Context HttpServletRequest request) {
        logger.info("<constructor>: " + servletContext + "/" + request);
        this.crud = (GenericCRUDExecutor<AbstractProduct>) servletContext.getAttribute("productCRUD");

        logger.debug("read out the productCRUD from the servlet context: " + this.crud);
    }

    @Override
    public AbstractProduct createProduct(AbstractProduct product) {
        return this.crud.createObject(product);
    }

    @Override
    public List<AbstractProduct> readAllProducts() {
        return this.crud.readAllObjects();
    }

    @Override
    public AbstractProduct updateProduct(long id, AbstractProduct product) {
        product.setId(id);
        return this.crud.updateObject(product);
    }

    @Override
    public boolean deleteProduct(long id) {
        return this.crud.deleteObject(id);
    }

    @Override
    public AbstractProduct readProduct(long id) {
        AbstractProduct product = this.crud.readObject(id);
        if (product != null) {
            return product;
        } else {
            throw new NotFoundException("The touchpoint with id " + id + " does not exist!");
        }
    }

}
