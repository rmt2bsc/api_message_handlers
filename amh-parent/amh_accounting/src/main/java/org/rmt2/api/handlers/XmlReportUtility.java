package org.rmt2.api.handlers;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.api.constants.RMT2ServletConst;
import com.api.security.RMT2TagQueryBean;
import com.api.util.HostCompanyManager;
import com.api.util.RMT2File;
import com.api.util.RMT2String;
import com.api.util.RMT2Utility;
import com.api.web.ICommand;
import com.api.web.security.RMT2SessionBean;
import com.api.xml.RMT2XmlUtility;

/**
 * Abstract class that provides common functionality for generating PDF reports
 * using XSL-FO technology Client requests for a report are dispatched by the
 * controller servlet. This is a fully functional implementation of
 * AbstractActionHandler class and the ICommand interface. The functioning crux
 * of this class is the discovery of the current user, to ensure that the user's
 * profile work area is created, to intialized various system file path name
 * variables needed for accurately accessing report resources, copy the target
 * report layout file to the user's profile work area, and to replace any
 * variable place holders that may exist in the report layout with concrete data
 * values. Currently, the only variable place holders that should exist in the
 * report layout should pertain to the image directory. Occurrences of the image
 * directory place holder is required to exist as "$IMAGES_DIRECTORY$" in the
 * report layout.
 * <p>
 * As mentioned previously, this abstract class is fully functional and it
 * operates in two clinet/server passess. The first pass deals with gathering
 * client data from the request object, building selection criteria that may be
 * needed, storing the client data onto the session via the RMT2TagQueryBean,
 * and displaying the "Wait Please..." JSP page. The second pass deals with
 * gathering the report data from some external data provider that is to be used
 * along with the report layout file. If needed, the selection criteria and data
 * stored on the session from the first pass can be used to filter the results
 * of the data gathered from the external data provider. Lastly, the report is
 * generated. The first and second passes are associated with the commands
 * "prepare" and "generate", respectively. The handler for the prepare command
 * is buildSearchCriteria() and the handler for the generate command is run().
 * <p>
 * When implementing this class, minimally, the developer should code the
 * following methods: receiveClientData, prepareReport, and sendClientData. If
 * the descendent desires to use a different polling service and/or command than
 * what is specified in the SystemParms properties file, be sure to invoke
 * setPollService() and setPollCommand(), respectively, before the "generate"
 * command is handled. Preferably the init() method is a good place to override
 * the system defaults.
 * <p>
 * <b>The Implementation Strategy</b> Here is a list of methods that should be
 * implemented or overriden at the descendent: <blockquote>
 * <ul>
 * <li><b>{@link #init(ServletContext, HttpServletRequest)}</b> - Override
 * this method to ensure that the base datasource name is set.</li>
 * <li><b>{@link #processRequest(HttpServletRequest, HttpServletResponse, String)}</b> -
 * Override as the driver only if there are additional commands to process other
 * than "prepare" and "generate".</li>
 * <li><b>{@link #receiveClientData()}</b> - Implement to gather data from
 * the request object in order to build selection criteria.</li>
 * <li><b>{@link #initializeReport()}</b> - Implement to prepare the
 * environment to for report generation. See method documentation.</li>
 * <li><b>{@link #sendClientData()}</b> - Implement to perform the report
 * transformation, generation, and rendering.</li>
 * </ul>
 * </blockquote>
 * <p>
 * <b>Note: </b> When overriding any of the methods mentioned above, be sure to
 * call the super script of that method for completeness.
 * 
 * @author rterrell
 * 
 */
public abstract class XmlReportUtility extends AbstractActionHandler implements ICommand {
    private static final String IMAGE_PATH_PLACEHOLDER = "$IMAGES_DIRECTORY$";

    private static final String COMMAND_PRINT = "print";

    private static final String COMMAND_GENERATE = "generate";

    /**
     * The name of the field that represents the input control used to select a
     * list item on the client
     */
    protected static final String ATTRIB_LISTSELECTORID = "selCbx";

    private String PollingUrlPrefix;

    private String pollPageAction;

    private String webAppCtx;

    private String reportName;

    private String pollService;

    private String pollCommand;

    private String appPath;

    private String xslPath;

    private String userWorkArea;

    private String imageDirPath;

    private Logger logger;

    private List parsedRequest;

    private String reportId;

    /** The unqualified command that this handler will process */
    protected String command;

    /** The name of the XSLT file to process */
    protected String xslFileName;

    /** The name of the XML file to process */
    protected String xmlFileName;

    /** The name of the imtermediate output file to process */
    protected String outFileName;

    /**
     * Default constructor.
     */
    public XmlReportUtility() {
        super();
    }

    /**
     * Constructor responsible for initializing this object's servlet context
     * and Http Request objects
     * 
     * @param _context
     *            The servlet context passed by the servlet
     * @param _request
     *            The Http Servle Request object passed by the servlet
     * @throws SystemException
     */
    public XmlReportUtility(String reportDefFile, Request _request) throws SystemException {
        this();
        this.init(_context, _request);
    }

    /**
     * Determines and initializes file system path names so that resources such
     * as the user's profile work area, image directory, report layout, and the
     * report output file may be accurately located.
     * <p>
     * <b>Note:</b> It is important to overide this method so that the
     * descendent's implementation contains logic to set the base data source
     * name. Neglecting to follow this protocol will result in a runtime error
     * when ancestor script(s) attempt to obtain the datasource for this object.
     * 
     * @param _context
     *            the servet context
     * @param _request
     *            the http servlet request
     * @throws SystemException
     */
    protected void init(Context _context, Request _request) throws SystemException {
        super.init(_context, _request);
        logger = Logger.getLogger("XmlReportUtility");
        String temp = null;

        // Initialize base view to the null table since a datasource
        // is required to successfully instantiate this class.
        this.setBaseView("NulltableView");
        RMT2SessionBean userSession = (RMT2SessionBean) this.getSession().getAttribute(RMT2ServletConst.SESSION_BEAN);
        // Get path to user's work area to store report output
        this.userWorkArea = userSession.getWorkAreaPath();

        this.appPath = this.session.getContext().getRealPath("");
        temp = System.getProperty(HttpSystemPropertyConfig.PROPNAME_IMG_DIR);
        this.imageDirPath = this.session.getContext().getRealPath(temp);
        this.imageDirPath += "\\";
        
        // Get path to xslt files.   The results should be the path that is on the classpath
        this.xslPath = System.getProperty(HttpSystemPropertyConfig.PROPNAME_XSLT_PATH);
        
        // Get Servlet context
        this.webAppCtx = RMT2Utility.getWebAppContext(_request);
        // Get command
        if (parsedRequest.size() >= 3) {
            this.command = (String) parsedRequest.get(2);
        }
        // Get name of report to run.
        if (parsedRequest.size() >= 2) {
            this.reportName = (String) parsedRequest.get(1);
        }
        // Get report file name extension
        this.reportName += System.getProperty(HttpSystemPropertyConfig.PROPNAME_RPT_FILE_EXT);
        // Set the polling service that will trigger the action handler to generate the report.
        // This could be overidden.
        this.pollService = "unsecureRequestProcessor/" + (String) parsedRequest.get(0) + "." + (String) parsedRequest.get(1);
        // Set default command for generating report.
        this.pollCommand = "generate";
    }

    /**
     * Copies the target report layout file to the user's profile work area
     * which was created somewhere in the file system druing login.
     * <p>
     * This process also replaces any image directory place holders in the
     * report layout with the actual data values.
     * 
     * @return Path name of report in user's profile work area.
     * @throws SystemException
     */
    protected String setupReportLayout() throws SystemException {
        String fileData = null;
        String origRptPath = this.xslPath + "/" + this.reportName;
        this.reportId = String.valueOf(new java.util.Date().getTime());
        String userRptPath = this.userWorkArea + "/" + this.reportId + ".xsl";
        this.xslFileName = userRptPath;
        this.xmlFileName = this.getUserWorkArea() + "\\" + this.reportId + ".xml";
        this.outFileName = this.getUserWorkArea() + "\\" + this.reportId + ".fo";
        try {
            // Attempt to locate Report layout using classpath instead of the file system
            InputStream is = RMT2File.getFileInputStream(origRptPath);
            if (is == null) {
        	this.msg = "Report generation failed.   Unable to locate report layout referenced as " + origRptPath;
        	throw new NotFoundException(this.msg);
            }
            // Substitute place holders in the report layout
            fileData = RMT2File.getStreamStringData(is);
            fileData = RMT2String.replaceAll2(fileData, this.imageDirPath, XmlReportUtility.IMAGE_PATH_PLACEHOLDER);
            // Copy report layout to the user's work area.
            RMT2File.outputFile(fileData, this.xslFileName);
            return userRptPath;
        }
        catch (NotFoundException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Processes the client's request to prepare and run a report using request,
     * response, and command.
     * 
     * @param request
     *            The HttpRequest object
     * @param response
     *            The HttpResponse object
     * @param command
     *            Comand issued by the client.
     * @Throws SystemException
     *             when an error needs to be reported.
     */
    public void processRequest(Request request, Response response, String command) throws ActionHandlerException {
        this.parsedRequest = RMT2String.getTokens(command, ".");
        super.processRequest(request, response, command);
        if (this.command.equalsIgnoreCase(XmlReportUtility.COMMAND_PRINT)) {
            this.buildSearchCriteria();
        }
        if (this.command.equalsIgnoreCase(XmlReportUtility.COMMAND_GENERATE)) {
            this.run();
        }
    }

    /**
     * Drives the process of builing report selection criteria.
     * <p>
     * The selection criteria can be used to allow server-side processes to
     * retrieve data filtered by the selection criteria or send the selection
     * criteria to the client to be utilized accordingly. For the latter, the
     * selection criteria is stored in the session object as
     * {@link RMT2TagQueryBean} and can be obtained using the following
     * constant, <b>{@link RMT2ServletConst}</b>.REPORT_BEAN. The user's
     * selection criteria input can also be stored in <b>RMT2TagQueryBean</b>
     * via the custom object property which the client is responsible for
     * prooperly casting.
     * <p>
     * This method invokes receiveClientData to receive user input. The methods
     * doPostCustomInitialization and postBuildCustomClientCriteria are also
     * called to allow the user to add more data to the query object and to
     * apply custom selection criteria to the report, respectively. The
     * developer is responsible for passing any required data to the client via
     * the request and session objects
     * 
     * @throws ActionHandlerException
     */
    public void buildSearchCriteria() throws ActionHandlerException {
        String sql = null;
        Object customObj = this.doCustomInitialization();
        try {
            this.query = new RMT2TagQueryBean();
        }
        catch (Exception e) {
            // Do nothing
        }
        this.receiveClientData();
        this.query.setCustomObj(customObj);
        this.query.setWhereClause(null);
        try {
            sql = this.buildClientSelectionCriteria(false);
        }
        catch (Exception e) {
            sql = null;
        }
        this.query.setWhereClause(sql);
        this.doPostCustomInitialization(this.query, RMT2ServletConst.SEARCH_MODE_NEW);
        this.getSession().setAttribute(RMT2ServletConst.REPORT_BEAN, this.query);

        // Set the URL that will serve as the polling page's action command

        String pollPageURL = this.buildPollingServiceUrl();
        if (pollPageURL == null || pollPageURL.trim().length() <= 0) {
            this.msg = "User must supply a action for the polling JSP page in order to run selected report";
            this.logger.log(Level.ERROR, this.msg);
            throw new ActionHandlerException(this.msg);
        }
        this.request.setAttribute(RMT2ServletConst.RESPONSE_HREF, pollPageURL);
        return;
    }

    /**
     * Sets the Polling Page's form action property.
     * 
     * @param action
     *            The action name
     */
    public void setPollPageAction(String action) {
        this.pollPageAction = action;
    }

    /**
     * Executes the actual report and sends the results to the client by
     * invoking prepareReport and sendClientData in the order specified. Also,
     * the xml data file is created at this point. This method is best invoked
     * as a response to the "generate" command.
     * 
     * @throws ActionHandlerException
     */
    public void run() throws ActionHandlerException {
        this.query = (RMT2TagQueryBean) this.getSession().getAttribute(RMT2ServletConst.REPORT_BEAN);
        String xml = this.initializeReport();

        // Persist XML data to disk.
        try {
            xml = this.createWellFormedData(xml);
            RMT2File.outputFile(xml, this.xmlFileName);
        }
        catch (SystemException e) {
            this.msg = "Error createing XML report data file";
            logger.log(Level.ERROR, this.msg);
            throw new ActionHandlerException(this.msg);
        }
        this.sendClientData();
    }

    /**
     * Builds the URL for the polling service that displays the "Wait Please..."
     * JSP page.
     * 
     * @return URL as a String.
     */
    private String buildPollingServiceUrl() {
        String url = null;
        try {
            url = System.getProperty(HttpSystemPropertyConfig.PROPNAME_POLL_PAGE);

        }
        catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage());
            return null;
        }
        url += "?clientAction=" + this.pollCommand;
        url += "&pollService=" + this.pollService;
        // url = this.webAppCtx + url;
        return url;
    }

    /**
     * Retrieves the host company data using an instance of
     * {@linkHostCompanyManager}.
     * 
     * @param dao
     *            Object that that is connected to some external data provider.
     *            For this implementation, dao is expected to be of type
     *            {@link HostCompanyManager} which possesses functionality to
     *            obtain details of the company that provided the service.
     * @return Data in the form of XML.
     * @throws ActionHandlerException
     */
    protected String fetchHostCompany(Object dao) throws ActionHandlerException {
        if (dao == null) {
            this.msg = "Failed to obtain host company data since DAO parameter is invalid";
            logger.log(Level.ERROR, this.msg);
            throw new ActionHandlerException(this.msg);
        }
        if (dao instanceof HostCompanyManager) {
            HostCompanyManager company = (HostCompanyManager) dao;
            return company.getHostCompanyInfo();
        }
        else {
            this.msg = "Failed to obtain host company data since DAO parameter is not an instance of HostCompanyManager";
            logger.log(Level.ERROR, this.msg);
            throw new ActionHandlerException(this.msg);
        }
    }

    /**
     * Ensures that XML data is well formed by enclosing data with start and
     * ending xml tags using {@link RMT2XmlUtility.XML_TAG_START} and
     * {@link RMT2XmlUtility.XML_TAG_END}.
     * 
     * @param data
     *            XML data
     * @return Well Formed XML data as a String
     */
    protected String createWellFormedData(String data) {
        return RMT2XmlUtility.XML_TAG_START + data + RMT2XmlUtility.XML_TAG_END;
    }

    /**
     * Gets the user's profile work area file system path.
     * 
     * @return Path name.
     */
    protected String getUserWorkArea() {
        return this.userWorkArea;
    }

    /**
     * Sets the service that the polling service will use to trigger the clients
     * command.
     * <p>
     * The poll service is synonymous to the command mapping prefix of the the
     * actual command.
     * 
     * @param value
     *            The fully-qualified class name of the action handler.
     */
    protected void setPollService(String value) {
        this.pollService = value;
    }

    /**
     * Prepare the environment for running the report. This method is call prior
     * to the actual generation of the report from the run() method. Listed are
     * the minimal basic steps to follow when implementing this method:
     * <p>
     * <blockquote>
     * <ul>
     * <li>Create and/or indentify the user's profile work area by invoking
     * {@link XmlReportUtility#setupReportLayout()}.</li>
     * <li>Identify and assign the file name to the XSL, XML, and XSL-FO files
     * used to generate the report.</li>
     * <li>Fetch and combine the report data from all mandatory sources into
     * one XML data stream.</li>
     * <li>Ensure the XML data is well formed by calling
     * {@link XmlReportUtility#createWellFormedData(String)}.</li>
     * <li>Persist the XML data to a file located in the user's profile work
     * area using {@link RMT2Utility#outputFile(String, String)}.</li>
     * </ul>
     * </blockquote>
     * 
     * @return String of xml data
     * @throws ActionHandlerException
     */
    protected abstract String initializeReport() throws ActionHandlerException;

    /**
     * Sets the next command the polling service will trigger.
     * 
     * @param value
     *            The name of the action
     */
    protected void setPollCommand(String value) {
        this.pollCommand = value;
    }

    /**
     * No Action
     */
    public void add() throws ActionHandlerException {
        return;
    }

    /**
     * No Action
     */
    public void edit() throws ActionHandlerException {
        return;
    }

    /**
     * No Action
     */
    public void save() throws ActionHandlerException {
        return;
    }

    /**
     * No Action
     */
    public void delete() throws ActionHandlerException {
        return;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

}