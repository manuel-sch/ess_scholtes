package org.dieschnittstelle.ess.mip.client.demos;

import org.dieschnittstelle.ess.mip.client.TotalUsecase;
import org.dieschnittstelle.ess.mip.client.apiclients.ServiceProxyFactory;

public class PrepareCampaigns {

    public static void main(String[] args) {
        ServiceProxyFactory.initialise();

        TotalUsecase uc;
        try {
            uc = new TotalUsecase();
            uc.prepareCampaigns();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
