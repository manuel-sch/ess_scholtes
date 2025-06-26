package org.dieschnittstelle.ess.mip.client;

import org.dieschnittstelle.ess.entities.crm.*;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.entities.erp.IndividualisedProductItem;
import org.dieschnittstelle.ess.entities.erp.ProductBundle;
import org.dieschnittstelle.ess.entities.erp.ProductType;

/**
 * lorem this class specifies a couple of entities for the domain objects that
 * are used by the client classes
 */
public class Constants {

    /*
     * constants for the objects that are dealt with in the different accessors
     * to the server-side functions
     */

    public static StationaryTouchpoint TOUCHPOINT_1;

    public static StationaryTouchpoint TOUCHPOINT_2;

    public static MobileTouchpoint TOUCHPOINT_3;

    public static IndividualisedProductItem PRODUCT_1;

    public static IndividualisedProductItem PRODUCT_2;

    public static IndividualisedProductItem PRODUCT_3;

    public static Campaign CAMPAIGN_1;

    public static Campaign CAMPAIGN_2;

    public static Customer CUSTOMER_1;

    public static Customer CUSTOMER_2;

    // instantiate the constants
    static {

        Address addr1 = new Address("Luxemburger Strasse", "10", "13353",
                "Berlin");
        TOUCHPOINT_1 = new StationaryTouchpoint(0, "BHT Mensa", addr1);

        Address addr2 = new Address("Leopoldplatz", "1", "13353", "Berlin");
        TOUCHPOINT_2 = new StationaryTouchpoint(0, "U Leopoldplatz", addr2);

        TOUCHPOINT_3 = new MobileTouchpoint("01778896571");
        TOUCHPOINT_3.setName("Mobiler Verkaufsstand");

        PRODUCT_1 = new IndividualisedProductItem("Schrippe", ProductType.ROLL,
                720);

        PRODUCT_2 = new IndividualisedProductItem("Kirschplunder",
                ProductType.PASTRY, 1080);

        PRODUCT_3 = new IndividualisedProductItem("Nussstriezel",
                ProductType.PASTRY, 1080);

        CAMPAIGN_1 = new Campaign("Familienfrühstück");
        CAMPAIGN_1.addBundle(new ProductBundle(PRODUCT_1, 5));
        CAMPAIGN_1.addBundle(new ProductBundle(PRODUCT_2, 2));

        CAMPAIGN_2 = new Campaign("Kaffeerunde");
        CAMPAIGN_2.addBundle(new ProductBundle(PRODUCT_2, 3));
        CAMPAIGN_2.addBundle(new ProductBundle(PRODUCT_3, 1));

        // use a shared address for the two customers
        Address addr3 = new Address("Kopernikusstrasse", "11", "10245",
                "Berlin");

        CUSTOMER_1 = new Customer("Anna", "Musterfrau", Gender.W);
        CUSTOMER_1.setAddress(addr3);
        CUSTOMER_1.setEmail("anna@example.com");

        CUSTOMER_2 = new Customer("Benedikt", "Mustermann", Gender.M);
        CUSTOMER_2.setAddress(addr3);
        CUSTOMER_2.setEmail("bene@example.com");
    }

    // this method resets all ids that might have been assigned to the objects
    // referred to the constants after successful server-side creation
    // note that in order for this to work, ids must have int or long type and must not
    // be defaulted to any value different from 0 (e.g. -1)
    public static void resetEntities() {
        TOUCHPOINT_1.setId(0);
        TOUCHPOINT_1.getAddress().setId(0);
        TOUCHPOINT_2.setId(0);
        TOUCHPOINT_2.getAddress().setId(0);
        TOUCHPOINT_3.setId(0);
        PRODUCT_1.setId(0);
        PRODUCT_2.setId(0);
        PRODUCT_3.setId(0);
        CAMPAIGN_1.setId(0);
        for (ProductBundle bundle : CAMPAIGN_1.getBundles()) {
            bundle.setId(0);
        }
        CAMPAIGN_2.setId(0);
        for (ProductBundle bundle : CAMPAIGN_2.getBundles()) {
            bundle.setId(0);
        }
        CUSTOMER_1.setId(0);
        CUSTOMER_1.getAddress().setId(0);
        CUSTOMER_2.setId(0);
        CUSTOMER_2.getAddress().setId(0);
    }

    // this creates unique ids for products as long as we do not have
    // server-side product creation available
    private static int ID_SEQUENCE = 0;

    public static int nextId() {
        return ++ID_SEQUENCE;
    }

}
