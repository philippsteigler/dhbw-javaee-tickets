package org.dhbw.mosbach.ai.tickets.view;

import org.dhbw.mosbach.ai.tickets.beans.SecurityBean;
import org.dhbw.mosbach.ai.tickets.beans.TicketCustomerBean;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class CustomerView implements Serializable {

    @Inject
    private SecurityBean securityBean;

    @Inject
    private TicketCustomerBean ticketCustomerBean;

    //render button if current customer is creator of selected  ticket
    public boolean renderButton() {

        //Compare current userId with ticket side customerId
        return ticketCustomerBean.getCurrentTicket().getCustomerId() == securityBean.getUser().getId();
    }
}
