package org.rmt2.api.handler.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.NotFoundException;
import com.RMT2Base;
import com.RMT2Exception;
import com.SystemException;
import com.api.util.RMT2File;
import com.api.util.RMT2String;
import com.api.xml.RMT2XmlUtility;

/**
 * Class that provides common functionality for generating PDF reports using
 * XSL-FO technology Client requests for a report are dispatched by the
 * controller servlet. This is a fully functional implementation. The
 * functioning crux of this class is to intialized various system file path name
 * variables needed for accurately accessing report resources, copy the target
 * report layout file to the user's profile work area, and to replace any
 * variable place holders that may exist in the report layout with concrete data
 * values. Currently, the only variable place holders that should exist in the
 * report layout should pertain to the image directory. Occurrences of the image
 * directory place holder is required to exist as "$IMAGES_DIRECTORY$" in the
 * report layout.
 * 
 * @author rterrell
 * 
 */
public class PdfReportUtility extends RMT2Base {
    private static final Logger logger = Logger.getLogger(PdfReportUtility.class);
    private static final String OS = System.getProperty("os.name");
    private static final String IMAGE_PATH_PLACEHOLDER = "$IMAGES_DIRECTORY$";


    /**
     * The name of the field that represents the input control used to select a
     * list item on the client
     */
    protected static final String ATTRIB_LISTSELECTORID = "selCbx";

    private String reportName;
    private String xslPath;
    private String userWorkArea;
    private String imageDirPath;
    private List parsedRequest;
    private String reportId;

    /** The unqualified command that this handler will process */
    protected String command;

    /** The name of the XSLT file to process */
    protected String xslFileName;

    /** The name of the XML file to process */
    protected String xmlFileName;

    /** The name of the Formatted Object (FO)imtermediate output file to process */
    protected String foFileName;

    protected String pdfFileName;

    private boolean outputAsFile;

    private String xml;

    /**
     * Default constructor.
     */
    private PdfReportUtility() {
        super();
    }

    /**
     * 
     * @param xslFile
     * @param xml
     * @param outpurAsFile
     */
    public PdfReportUtility(String xslFile, String xml, boolean outpurAsFile) {
        this();
        this.outputAsFile = outpurAsFile;
        this.setupReportLayout(xslFile, xml);
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
    public void init() throws SystemException {
        super.init();
        // Get path to user's work area to store report output
        this.userWorkArea = System.getProperty("SerialPath");
        // if (OS.startsWith("Windows")) {
        // this.userWorkArea = System.getProperty("SerialDrive") +
        // this.userWorkArea;
        // }

        // Get image directory
        this.imageDirPath += "..\\images\\"; // might need to reference "images"
        
        // Get path to xslt files.   The results should be the path that is on the classpath
        this.xslPath = System.getProperty("RptXsltPath");
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
    protected String setupReportLayout(String xslFile, String xml) throws SystemException {
        String fileData = null;
        String origRptPath = this.xslPath + "/" + xslFile;
        this.reportName = origRptPath;
        this.reportId = String.valueOf(new java.util.Date().getTime()) + "-" + RMT2String.getTokens(xslFile, ".").get(0);
        String userRptPath = this.userWorkArea + this.reportId + ".xsl";
        this.xslFileName = userRptPath;
        this.xmlFileName = this.userWorkArea + this.reportId + ".xml";
        this.foFileName = this.userWorkArea + this.reportId + ".fo";
        this.pdfFileName = this.userWorkArea + this.reportId + ".pdf";

        // Persist XML data to disk.
        try {
            RMT2File.outputFile(xml, this.xmlFileName);
            this.xml = xml;
        } catch (SystemException e) {
            this.msg = "Error persisting XML report data file to user work area";
            logger.error(this.msg);
            throw new SystemException(this.msg, e);
        }

        // Copy XSL file to user's work area and replace variable place holders
        // in XSL file with static content.
        try {
            // Attempt to locate Report layout using classpath instead of the file system
            InputStream is = RMT2File.getFileInputStream(origRptPath);
            if (is == null) {
        	this.msg = "Report generation failed.   Unable to locate report layout referenced as " + origRptPath;
        	throw new NotFoundException(this.msg);
            }
            // Substitute place holders in the report layout
            fileData = RMT2File.getStreamStringData(is);
            fileData = RMT2String.replaceAll2(fileData, this.imageDirPath, PdfReportUtility.IMAGE_PATH_PLACEHOLDER);
            // Copy report layout to the user's work area.
            RMT2File.outputFile(fileData, this.xslFileName);
            return userRptPath;
        }
        catch (NotFoundException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Builds the report from the input XML data and returns the results as an
     * OutputStream.
     * 
     * @param xslFileName
     *            file name of the XSL script
     * @param xml
     *            the actual XML data to process
     * @param outputAsFile
     *            set to true when persisting the ourput as file. Set to false
     *            to allow report to reside in memory.
     * @return the report as an {@link OutputStream} which could represent data
     *         persisted as a file or in memory.
     * @throws RMT2Exception
     */
    public OutputStream buildReport() throws RMT2Exception {
        // Generate report.
        ByteArrayOutputStream pdf = null;
        try {
            RMT2XmlUtility xsl = RMT2XmlUtility.getInstance();
            xsl.transformXslt(this.xslFileName, this.xmlFileName, this.foFileName);
            pdf = xsl.renderPdf(this.foFileName);
        } catch (SystemException e) {
            this.msg = "Error generating report in PDF format: " + e.getMessage();
            logger.error(this.msg);
            throw new RMT2Exception(this.msg, e);
        }

        RMT2File.outputFile(pdf.toByteArray(), this.pdfFileName);
        return pdf;
    }

    /**
     * @return the reportId
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * @return the pdfFileName
     */
    public String getPdfFileName() {
        return pdfFileName;
    }

}