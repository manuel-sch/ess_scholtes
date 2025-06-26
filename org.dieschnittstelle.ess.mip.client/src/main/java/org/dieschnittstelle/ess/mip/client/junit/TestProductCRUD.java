package org.dieschnittstelle.ess.mip.client.junit;

import org.dieschnittstelle.ess.entities.erp.AbstractProduct;
import org.dieschnittstelle.ess.entities.erp.Campaign;
import org.dieschnittstelle.ess.mip.client.apiclients.ProductCRUDClient;
import org.dieschnittstelle.ess.mip.client.apiclients.ServiceProxyFactory;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.stream.Collectors;

import static org.dieschnittstelle.ess.mip.client.Constants.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestProductCRUD {

    private ProductCRUDClient client;

    @Before
    public void prepareContext() throws Exception {
        ServiceProxyFactory.initialise();
        client = new ProductCRUDClient();
    }

    // this is not a test, but a utility method used by the tests
    private List<AbstractProduct> readAll() {
        return client.readAllProducts();
    }

    @Test
    public void a_createIndividualisedProductItem() {
        // we reset the entitie here
        resetEntities();

        List<AbstractProduct> prodlistBefore;
        // read all products
        assertNotNull("product list can be read", prodlistBefore = readAll());

        /* CREATE + READ */
        // create and use the id
        PRODUCT_1.setId(client.createProduct(PRODUCT_1).getId());
        assertEquals("product list is appended on create", 1, client.readAllProducts().size() - prodlistBefore.size());
    }

    @Test
    public void b_readIndividualisedProductItem() {
        // read the products and check whether they are equivalent
        AbstractProduct testProduct = client.readProduct(PRODUCT_1.getId());

        assertNotNull("new product can be read", testProduct);
        assertEquals("new product name is correct", PRODUCT_1.getName(), testProduct.getName());
    }

    @Test
    public void c_updateIndividualisedProductItem() {
        /* UPDATE */
        // change the local name
        PRODUCT_1.setName(PRODUCT_1.getName() + " " + PRODUCT_1.getName());
        // update the product on the server-side
        client.updateProduct(PRODUCT_1.getId(), PRODUCT_1);

        // read out the product and compare the names
        AbstractProduct testProduct = client.readProduct(PRODUCT_1.getId());
        assertEquals("product name is updated correctly", PRODUCT_1.getName(), testProduct.getName());
    }

    @Test
    public void d_deleteIndividualisedProductItem() {
        List<AbstractProduct> prodlistBefore = readAll();
        System.out.println("size before: " + prodlistBefore.size());

        /* DELETE */
        assertTrue("product can be deleted", client.deleteProduct(PRODUCT_1.getId()));
        System.out.println("size after delete: " + readAll().size());

        assertNull("deleted product does not exist anymore", client.readProduct(PRODUCT_1.getId()));
        assertEquals("product list is reduced on delete", prodlistBefore.size() - 1, readAll().size());
    }

    @Test
    public void e_createCampaign() {
        // we need to re-create the deleted product_1 and create two additional products,
        // but the id on product_1 needs to be reset
        PRODUCT_1.setId(0);
        PRODUCT_1.setId(client.createProduct(PRODUCT_1).getId());
        PRODUCT_2.setId(client.createProduct(PRODUCT_2).getId());
        PRODUCT_3.setId(client.createProduct(PRODUCT_3).getId());

        List<AbstractProduct> prodlistBefore = readAll();

        /* CREATE */
        CAMPAIGN_1.setId(client.createProduct(CAMPAIGN_1).getId());
        assertEquals("product list is appended on create for campaigns", 1, client.readAllProducts().size()
                - prodlistBefore.size());
    }

    @Test
    public void f_readCampaign() {
        Campaign createdCampaign = (Campaign) client.readProduct(CAMPAIGN_1.getId());
        assertEquals("campaign contains correct number of bundles", 2, createdCampaign.getBundles().size());
        // make sure that campaign does not use cascade on products (to make clear that we are testing on the first bundle, which is the one we added for PRODUCT_1, we cast to
        // List, which is the type actually used for the bundles)
        assertTrue("campaign is persisted using references to existing products",
                createdCampaign.getBundles().iterator().next().getProduct().getId() == PRODUCT_1
                        .getId());
    }

    // skip this for the time being
//	@Test
//	public void g_idsOnProductsAreAssignedIndependently() {
//		/* PRIMARY KEY ASSIGNMENT */
//		CAMPAIGN_2.setId(client.createProduct(CAMPAIGN_2).getId());
//		assertEquals("id values for campaigns (and other products) are assigned independently from other entities)", 1, CAMPAIGN_2.getId() - CAMPAIGN_1.getId());
//	}

    @Test
    public void g_getCampaignsForProduct() {
        // create the campaign here
        CAMPAIGN_2.setId(client.createProduct(CAMPAIGN_2).getId());
        List<Long> campaignids = client.getCampaignsForProduct(PRODUCT_2.getId())
                .stream()
                .map(Campaign::getId)
                .collect(Collectors.toList());
        assertEquals("retrieved campaigns for product have the correct length", 2, campaignids.size());
        assertTrue("campaign 1 is contained in campaigns", campaignids.contains(CAMPAIGN_1.getId()));
        assertTrue("campaign 2 is contained in campaigns", campaignids.contains(CAMPAIGN_2.getId()));
    }

    @Test
    public void h_deleteCampaign() {
        List<AbstractProduct> prodlistBefore = readAll();

        /* DELETE */
        client.deleteProduct(CAMPAIGN_2.getId());
        assertEquals("product list is reduced by 1 on delete for campaigns", prodlistBefore.size() - 1, readAll().size());

        assertNull("deleted campaign does not exist anymore", client.readProduct(CAMPAIGN_2.getId()));
        assertNotNull("individual products still exist after campaign deletion", client.readProduct(PRODUCT_1.getId()));

        try {
            client.deleteProduct(PRODUCT_3.getId());
            assertEquals("products that were part of campaigns can be deleted after campaign deletion", prodlistBefore.size() - 2, readAll().size());
        } catch (Exception e) {
            fail("Fix this issue: products that were part of campaigns cannot be deleted after campaign deletion.");
        }

    }

}
