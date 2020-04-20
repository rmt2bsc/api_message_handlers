package org.rmt2.api.handlers.transaction.receipts;

import com.RMT2Exception;

/**
 * Handles payment email confirmation errors.
 * 
 * @author Roy Terrell
 * 
 */
public class PaymentEmailConfirmationException extends RMT2Exception {
    private static final long serialVersionUID = -1884703323759924257L;

    public PaymentEmailConfirmationException() {
        super();
    }

    public PaymentEmailConfirmationException(String msg) {
        super(msg);
    }

    public PaymentEmailConfirmationException(Exception e) {
        super(e);
    }

    public PaymentEmailConfirmationException(String msg, Exception e) {
        super(msg, e);
    }
}
