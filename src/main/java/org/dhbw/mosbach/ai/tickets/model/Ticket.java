package org.dhbw.mosbach.ai.tickets.model;

import com.google.common.collect.Sets;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Ticket {

    //Persistent Fields:
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String subject;

    @Column
    private String status;

    @Column
    private long editorId;

    @OneToMany
    private Set<Entry> entries = Sets.newHashSet();

    //Constructor:
    public Ticket() {
    }

    Ticket (String subject, String status, long editorId) {
        this.subject = subject;
        this.status = status;
        this.editorId = editorId;
    }

    //Accessor Methods:
    public String getSubject() {
        return subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getEditorId() {
        return editorId;
    }

    public void setEditorId(long editorId) {
        this.editorId = editorId;
    }
}

