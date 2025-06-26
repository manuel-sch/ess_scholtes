package org.dieschnittstelle.ess.mip.components.crm.api;

public class CrmException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 205789836734861691L;

    public CrmException() {
        super();
    }

    public CrmException(Throwable cause) {
        super(cause);
    }

    public CrmException(String msg) {
        super(msg);
    }

    public CrmException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
