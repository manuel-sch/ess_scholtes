package org.dieschnittstelle.ess.mip.components.shopping.cart.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.*;


import org.dieschnittstelle.ess.entities.shopping.ShoppingCartItem;
import org.apache.logging.log4j.Logger;
import org.dieschnittstelle.ess.mip.components.shopping.cart.api.ShoppingCart;
import org.dieschnittstelle.ess.utils.interceptors.Logged;

/**
 * provides shopping cart functionality
 * <p>
 * note that this class is, at the same time, annotated as an entity class for supporting a RESTful handling
 * of shopping cart functionality
 * <p>
 * note that instances of this class EITHER behave as stateful ejbs OR as entities
 */
@Entity
@Logged
public class ShoppingCartEntity implements ShoppingCart {

    @Id
    @GeneratedValue
    private long id;

    // track the time when we were lastUpdated - also this is used for entity usage
    private long lastUpdated;

    protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ShoppingCartEntity.class);

    @OneToMany(cascade = CascadeType.ALL)
    private List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>();

    public ShoppingCartEntity() {
        logger.info("<constructor>: " + this);
        this.lastUpdated = System.currentTimeMillis();
    }

    public void addItem(ShoppingCartItem product) {
        // check whether we already have a bundle for the given product
        boolean bundleUpdate = false;
        for (ShoppingCartItem item : items) {
            if (item.getErpProductId() == product.getErpProductId()) {
                item.setUnits(item.getUnits() + product.getUnits());
                bundleUpdate = true;
                break;
            }
        }
        if (!bundleUpdate) {
            this.items.add(product);
        }

        this.lastUpdated = System.currentTimeMillis();
    }

    public List<ShoppingCartItem> getItems() {
        return this.items;
    }

    // entity: access the id

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    // lifecycle ejb logging: jboss complains about usage of default transaction attribute (REQUIRED), hence we explicitly set allowed values

    @PostConstruct
    public void beginn() {
        logger.info("@PostConstruct");
    }

    @PreDestroy
    public void abschluss() {
        logger.info("@PreDestroy");
    }

    // lifecycle entity logging:
    @PostLoad
    public void onPostLoad() {
        logger.info("@PostLoad: " + this);
    }

    @PostPersist
    public void onPostPersist() {
        logger.info("@PostPersist: " + this);
    }

    @PostRemove
    public void onPostRemove() {
        logger.info("@PostRemove: " + this);
    }

    @PostUpdate
    public void onPostUpdate() {
        logger.info("@PostUpdate: " + this);
    }

    @PrePersist
    public void onPrePersist() {
        logger.info("@PrePersist: " + this);
    }

    @PreRemove
    public void onPreRemove() {
        logger.info("@PreRemove: " + this);
    }

    @PreUpdate
    public void onPreUpdate() {
        logger.info("@PreUpdate: " + this);
    }


}
