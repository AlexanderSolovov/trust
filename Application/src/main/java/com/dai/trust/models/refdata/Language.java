package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="ref_language")
public class Language extends AbstractRefDataEntity {
    
    @Column(name="is_default")
    private boolean isDefault;
    
    @Column(name="item_order")
    private int itemOrder;
    
    @Column
    private boolean ltr;
    
    public Language(){
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }

    public boolean getLtr() {
        return ltr;
    }

    public void setLtr(boolean ltr) {
        this.ltr = ltr;
    }
}
