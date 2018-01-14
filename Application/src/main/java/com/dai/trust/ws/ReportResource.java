package com.dai.trust.ws;

import com.dai.trust.common.RolesConstants;
import com.dai.trust.services.report.ReportsService;
import com.dai.trust.ws.filters.Authorized;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides methods to provide and manage reference data tables from the client.
 */
@Path("{langCode: [a-zA-Z]{2}}/report")
public class ReportResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(ReportResource.class.getName());

    public ReportResource() {
        super();
    }

    /**
     * Returns adjudication form
     *
     * @param langCode Language code for localization
     * @param id Property id
     * @return
     */
    @GET
    @Authorized(roles = RolesConstants.MANAGE_RIGHTS)
    @Path(value = "{a:adjudicationform|adjudicationForm}/{id}")
    public Response getAdjudicationForm(@PathParam(value = LANG_CODE) final String langCode, @PathParam(value = "id") String id) {
        try {
            ReportsService reportService = new ReportsService();
            final JasperPrint report = reportService.getAdjudicationForms(langCode, id);
            return writeReport(report, "AdjudicationForm", langCode);
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }

    /**
     * Returns certificate (title)
     *
     * @param langCode Language code for localization
     * @param id Property id
     * @return
     */
    @GET
    @Authorized(roles = RolesConstants.GENERATE_TITLE)
    @Path(value = "{a:certificate|certificate}/{id}")
    public Response getCertificate(@PathParam(value = LANG_CODE) final String langCode, @PathParam(value = "id") String id) {
        try {
            ReportsService reportService = new ReportsService();
            final JasperPrint report = reportService.getCertificate(langCode, id);
            return writeReport(report, "Certificate", langCode);
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
    
    /**
     * Returns transaction sheet for property
     *
     * @param langCode Language code for localization
     * @param id Property id
     * @return
     */
    @GET
    @Authorized(roles = RolesConstants.MANAGE_RIGHTS)
    @Path(value = "{a:transactionsheet|transactionSheet}/{id}")
    public Response getTransactionSheet(@PathParam(value = LANG_CODE) final String langCode, @PathParam(value = "id") String id) {
        try {
            ReportsService reportService = new ReportsService();
            final JasperPrint report = reportService.getTransactionSheet(langCode, id);
            return writeReport(report, "TransactionSheet", langCode);
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }

    private Response writeReport(final JasperPrint report, String reportName, final String langCode) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    JasperExportManager.exportReportToPdfStream(report, output);
                    output.flush();
                } catch (Exception ex) {
                    throw processException(ex, langCode);
                }
            }
        };

        Response.ResponseBuilder response = Response.ok(stream);
        response.header("Content-disposition", "inline; inline; filename=\"" + reportName + ".pdf\"");
        response.header("Content-Type", "application/pdf");
        return response.build();
    }
}
