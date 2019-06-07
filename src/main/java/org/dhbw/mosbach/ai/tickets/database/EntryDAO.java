package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Entry;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named("entryDAO")
@Dependent
public class EntryDAO extends BaseDAO<Entry, Long> {
    private static final long serialVersionUID = 8175577162824097554L;

    public EntryDAO()
    {
        super();
    }
}
