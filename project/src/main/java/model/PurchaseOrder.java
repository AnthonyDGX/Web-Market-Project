/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;

/**
 *
 * @author adagneau
 */
public class PurchaseOrder {
    private int ORDER_NUM;
    private int CUSTOMER_ID;
    private int QUANTITY;
    
    public PurchaseOrder(int ORDER_NUM, int CUSTOMER_ID, int QUANTITY){
        this.CUSTOMER_ID = CUSTOMER_ID;
        this.ORDER_NUM = ORDER_NUM;
        this.QUANTITY = QUANTITY;
    }

    public int getORDER_NUM() {
        return ORDER_NUM;
    }

    public void setORDER_NUM(int ORDER_NUM) {
        this.ORDER_NUM = ORDER_NUM;
    }

    public int getCUSTOMER_ID() {
        return CUSTOMER_ID;
    }

    public void setCUSTOMER_ID(int CUSTOMER_ID) {
        this.CUSTOMER_ID = CUSTOMER_ID;
    }

    public int getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(int QUANTITY) {
        this.QUANTITY = QUANTITY;
    }
    
}
