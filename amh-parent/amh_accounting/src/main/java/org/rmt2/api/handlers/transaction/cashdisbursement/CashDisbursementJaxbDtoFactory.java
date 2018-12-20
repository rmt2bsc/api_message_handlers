package org.rmt2.api.handlers.transaction.cashdisbursement;

import org.dto.XactDto;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.jaxb.XactType;

/**
 * A factory for converting cash disbursement transaction related JAXB objects to DTO and
 * vice versa.
 * 
 * @author rterrell
 *
 */
public class CashDisbursementJaxbDtoFactory {

    /**
     * 
     */
    public CashDisbursementJaxbDtoFactory() {
       
    }

    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final XactDto createXactDtoInstance(XactType jaxbObj) {
        XactDto dto = TransactionJaxbDtoFactory.createXactDtoInstance(jaxbObj);
        
        return dto;
    }
}
