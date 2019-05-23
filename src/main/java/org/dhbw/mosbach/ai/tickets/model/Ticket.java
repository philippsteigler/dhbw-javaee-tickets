package org.dhbw.mosbach.ai.tickets.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Ticket {

    public enum Status{
        open,
        closed,
        inProcess
    }

    //Persistent Fields:
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String subject;

    @Column
    private Status status;

    @Column
    private long editorId;

    @OneToMany
    private List<Entry> entries = new ArrayList<>();

    //Constructor:
    public Ticket() { super(); }

    public Ticket (String subject, Status status, long editorId) {
        this.subject = subject;
        this.status = status;
        this.editorId = editorId;
        this.entries = Lists.newArrayList();
    }

    //Accessor Methods:
    public String getSubject() {
        return subject;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getEditorId() {
        return editorId;
    }

    public void setEditorId(long editorId) {
        this.editorId = editorId;
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }
}

