package org.rmt2.api.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.rmt2.constants.MessagingConstants;

import com.api.config.ConfigConstants;
import com.api.config.SystemConfigurator;
import com.api.persistence.PersistenceClient;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;
import com.api.xml.jaxb.JaxbUtil;

/**
 * Base class for testing the API handlers.
 * <p>
 * 
 * @author royterrell
 *
 */
public class BaseAccountingMessageHandlerTest {
    private static String LOGGER_CONFIG_PATH;
    public static String API_ERROR_MESSAGE;
    public static String VALIDATION_ERROR_MESSAGE;
    protected JaxbUtil jaxb;
    protected PersistenceClient mockPersistenceClient;
    

    @Before
    public void setUp() throws Exception {
        // Setup Logging environment
        String curDir = RMT2File.getCurrentDirectory();
        LOGGER_CONFIG_PATH = curDir + "/src/test/resources/config/log4j.properties";
        PropertyConfigurator.configure(LOGGER_CONFIG_PATH);

        try {
            this.jaxb = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        } catch (Exception e) {
            this.jaxb = new JaxbUtil(MessagingConstants.JAXB_RMT2_PKG);
        }

        PowerMockito.mockStatic(Rmt2OrmClientFactory.class);
        PowerMockito.mockStatic(SystemConfigurator.class);
        this.mockPersistenceClient = Mockito.mock(PersistenceClient.class);
        when(Rmt2OrmClientFactory.createOrmClientInstance(any(String.class)))
                .thenReturn(this.mockPersistenceClient);
        when(SystemConfigurator.getJaxb(any(String.class))).thenReturn(this.jaxb);
        return;
    }
   
}

