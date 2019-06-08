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
    private String content;

    @Column
    private long editorId;

    @Column
    private long customerId;

    @OneToMany(fetch = FetchType.EAGER)

    private List<Entry> entries = new ArrayList<>();

    //Constructor:
    public Ticket() { super(); }

    public Ticket (String subject, Status status, String content, long editorId, long customerId) {
        this.subject = subject;
        this.status = status;
        this.content = content;
        this.editorId = editorId;
        this.customerId = customerId;
        this.entries = Lists.newArrayList();
    }

    //Accessor Methods:
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

    public String getContent() { return content; }

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

