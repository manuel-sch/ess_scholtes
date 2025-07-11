package org.dieschnittstelle.ess.entities.crm;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.json.bind.annotation.JsonbTransient;

import java.io.Serializable;

/**
 * the execution of a campaign for some erpCampaignId, some touchpoint, some
 * quantity of units, some start date and some duration
 */
public class CampaignExecution implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5077516349500947392L;

    private AbstractTouchpoint touchpoint;

    private long erpCampaignId;

    private int units;

    private long startDate = System.currentTimeMillis();

    private long duration;

    /**
     * track the units that are left for this execution
     */
    private int unitsLeft;

    public CampaignExecution() {

    }

    public CampaignExecution(AbstractTouchpoint touchpoint, long erpCampaignId,
                             int units, long duration) {
        this.touchpoint = touchpoint;
        this.erpCampaignId = erpCampaignId;
        this.units = units;
        this.duration = duration;
        this.unitsLeft = units;
    }

    /**
     * purchase a given number of units
     */
    public void purchase(int units) {
        if (duration != -1 && System.currentTimeMillis() - startDate > duration) {
            throw new RuntimeException("campaign for " + erpCampaignId
                    + " has expired!");
        }

        if (units > unitsLeft) {
            throw new RuntimeException("number " + units
                    + " of units to be purchased exceeds the number "
                    + unitsLeft + " of units left");
        }

        unitsLeft -= units;
    }

    /**
     * check whether the execution is valid
     */
    @JsonIgnore
    @JsonbTransient
    public boolean isValid() {
        return this.unitsLeft > 0
                && (duration == -1 || (System.currentTimeMillis() - startDate <= duration));
    }

    public String toString() {
        return "<CampaignExecution "
                + this.erpCampaignId
                + " "
                + this.touchpoint
                + " "
                + this.unitsLeft
                + "/"
                + this.units
                + ", "
                + (duration == -1 ? "<no time limit>" : (System.currentTimeMillis()
                - startDate - duration)) + ">";
    }

    public void setTouchpoint(AbstractTouchpoint touchpoint) {
        this.touchpoint = touchpoint;
    }

    public void setErpCampaignId(long erpCampaignId) {
        this.erpCampaignId = erpCampaignId;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setUnitsLeft(int unitsLeft) {
        this.unitsLeft = unitsLeft;
    }

    public AbstractTouchpoint getTouchpoint() {
        return touchpoint;
    }

    public long getErpCampaignId() {
        return erpCampaignId;
    }

    public int getUnits() {
        return units;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getDuration() {
        return duration;
    }

    public int getUnitsLeft() {
        return unitsLeft;
    }

}
