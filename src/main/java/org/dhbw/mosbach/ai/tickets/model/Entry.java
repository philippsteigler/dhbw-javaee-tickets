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
    private String creator;

    @Column
    private String content;

    @Column
    private Date createDate;

    //Constructor:
    public Entry() {
    }

    Entry (String creator, String content, Date createDate) {
        this.creator = creator;
        this.content = content;
        this.createDate = createDate;
    }

    //Accessor Methods:
    public String getCreator() {
        return creator;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateDate() {
        return createDate;
    }
}

