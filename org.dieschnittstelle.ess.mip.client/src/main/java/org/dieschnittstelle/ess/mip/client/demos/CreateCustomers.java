package org.dieschnittstelle.ess.mip.client.demos;

import org.dieschnittstelle.ess.mip.client.apiclients.CustomerCRUDClient;
import org.dieschnittstelle.ess.mip.client.apiclients.ServiceProxyFactory;

import static org.dieschnittstelle.ess.mip.client.Constants.CUSTOMER_1;
import static org.dieschnittstelle.ess.mip.client.Constants.CUSTOMER_2;
import static org.dieschnittstelle.ess.utils.Utils.show;

public class CreateCustomers {

    public static void main(String[] args) {
        ServiceProxyFactory.initialise();

        try {
            CustomerCRUDClient customerCRUD = new CustomerCRUDClient();
            // this is for testing simplest, non polymorphic access to the rest api, therefore we remove the adress
            customerCRUD.createCustomer(CUSTOMER_1);
            customerCRUD.createCustomer(CUSTOMER_2);
            show("read created customer for id " + CUSTOMER_1.getId() + ": " + customerCRUD.readCustomer(CUSTOMER_1.getId()));
            show("read created customer for id " + CUSTOMER_2.getId() + ": " + customerCRUD.readCustomer(CUSTOMER_2.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
