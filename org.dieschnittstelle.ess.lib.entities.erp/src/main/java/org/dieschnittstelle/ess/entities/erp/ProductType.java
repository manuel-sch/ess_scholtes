package org.dieschnittstelle.ess.entities.erp;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductType {

    BREAD, ROLL, PASTRY;

    @JsonCreator
    public static ProductType deserialise(String pt) {
        return ProductType.valueOf(ProductType.class, pt);
    }
}
