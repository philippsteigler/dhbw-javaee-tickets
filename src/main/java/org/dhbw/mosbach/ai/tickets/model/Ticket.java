package org.dhbw.mosbach.ai.tickets.model;

import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ticket {

    public enum Status{
        open,
        closed,
        inProcess
    }

    //Persistente Attribute:
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String subject;

    @Column
    private Status status;

    @Column
    private long editorId;

    @Column
    private long customerId;

    @OneToMany(fetch = FetchType.EAGER)

    private List<Entry> entries = new ArrayList<>();

    //Konstruktor:
    public Ticket() { super(); }

    public Ticket (String subject, Status status, long editorId, long customerId) {
        this.subject = subject;
        this.status = status;
        this.editorId = editorId;
        this.customerId = customerId;
        this.entries = Lists.newArrayList();
    }

    //Zugriffsmethoden:
    public String getSubject() {
        return subject;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatusToOpen() {
        status = Status.open;
    }

    public void setStatusToClose() {
        status = Status.closed;
    }

    public void setStatusToInProcess() {
        status = Status.inProcess;
    }

    public void setEditorId(long editorId){
        this.editorId = editorId;
    }

    public long getEditorId() {
        return editorId;
    }

    public long getId() {
        return id;
    }

    public long getCustomerId() {
        return customerId;

    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public List<Entry> getEntries() { return entries; }
}

