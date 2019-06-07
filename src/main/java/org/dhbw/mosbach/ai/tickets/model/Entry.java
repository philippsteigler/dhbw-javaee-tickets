package org.dhbw.mosbach.ai.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Entry {

    //Persistent Fields:
    @Id
    @GeneratedValue
    private long id;

    @Column
    private long customer_id;

    @Column
    private String content;

    @Column
    private Date createDate;

    //Constructor:
    public Entry() { super(); }

    public Entry (long customer_id, String content, Date createDate) {
        this.customer_id = customer_id;
        this.content = content;
        this.createDate = createDate;
    }

    //Accessor Methods:
    public long getCustomerID() {
        return customer_id;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateDate() {
        return createDate;
    }
}

