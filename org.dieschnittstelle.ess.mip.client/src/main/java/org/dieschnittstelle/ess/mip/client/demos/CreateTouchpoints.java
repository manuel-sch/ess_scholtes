package org.dieschnittstelle.ess.mip.client.demos;

import org.dieschnittstelle.ess.mip.client.TotalUsecase;
import org.dieschnittstelle.ess.mip.client.apiclients.ServiceProxyFactory;

public class CreateTouchpoints {

    public static void main(String[] args) {
        ServiceProxyFactory.initialise();

        try {
            TotalUsecase uc = new TotalUsecase();
            uc.createTouchpoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
