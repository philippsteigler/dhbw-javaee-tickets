package org.dhbw.mosbach.ai.tickets.view;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

@Named
@SessionScoped
public class GeneralView implements Serializable {

    public String formatDate(Date date){
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);

        return dateFormat.format(date);
    }

}
