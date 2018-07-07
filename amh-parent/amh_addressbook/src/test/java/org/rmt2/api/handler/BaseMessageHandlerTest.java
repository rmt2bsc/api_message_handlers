package org.rmt2.api.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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
public class BaseMessageHandlerTest {

    private static String APP_CONFIG_FILENAME;
    protected JaxbUtil jaxb;
    protected PersistenceClient mockPersistenceClient;
    

    @Before
    public void setUp() throws Exception {
        String curDir = RMT2File.getCurrentDirectory();
        APP_CONFIG_FILENAME = curDir + "/src/test/resources/config/TestAddressBook-AppServerConfig.xml";
        SystemConfigurator appConfig = new SystemConfigurator();
        appConfig.start(APP_CONFIG_FILENAME);
        
        try {
            this.jaxb = SystemConfigurator.getJaxb(ConfigConstants.JAXB_CONTEXNAME_DEFAULT);
        }
        catch (Exception e) {
            this.jaxb = new JaxbUtil(MessagingConstants.JAXB_RMT2_PKG);
        }
        
        PowerMockito.mockStatic(Rmt2OrmClientFactory.class);
        this.mockPersistenceClient = Mockito.mock(PersistenceClient.class);
        when(Rmt2OrmClientFactory.createOrmClientInstance(any(String.class))).thenReturn(this.mockPersistenceClient);
        return;
    }
   
}

