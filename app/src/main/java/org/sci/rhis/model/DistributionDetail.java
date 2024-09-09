package org.sci.rhis.model;

public class DistributionDetail {
    private String itemName;
    private int quantity;

    public DistributionDetail(String itemName, int quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }
}
