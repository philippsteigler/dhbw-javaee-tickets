package org.dhbw.mosbach.ai.tickets.view;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;

@Named
@ViewScoped
public class GeneralView implements Serializable {

    //formatieren des Zeitstempels in ein schönes lesbares Format
    public String formatDate(Date date){
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);

        return dateFormat.format(date);
    }

    // Für die Präsentation wird auf Deutsch gerendert
    public String statusToString(String status) {
        switch (status) {
            case "open": return "Offen";
            case "closed": return "Geschlossen";
            case "inProcess": return "In Bearbeitung";
            default: return "";
        }
    }
}
