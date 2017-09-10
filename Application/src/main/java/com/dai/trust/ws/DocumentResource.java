package com.dai.trust.ws;

import com.dai.trust.common.FileUtility;
import com.dai.trust.common.RolesConstants;
import com.dai.trust.models.document.FileInfo;
import com.dai.trust.services.document.DocumentService;
import com.dai.trust.ws.filters.Authorized;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Provides methods to manage documents and related files.
 */
@Path("{langCode: [a-zA-Z]{2}}/doc")
public class DocumentResource extends AbstractResource {

    private static final Logger logger = LogManager.getLogger(DocumentResource.class.getName());

    public DocumentResource() {
        super();
    }

    /**
     * Saves provided document and attached file
     *
     * @param langCode Language code for localization
     * @param inputStream File input stream
     * @param fileDetail File details object.
     * @param body Form data body for the file
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json; charset=UTF-8")
    @Path(value = "upload")
    @Authorized(roles = {RolesConstants.MANAGE_APPLICATIONS, RolesConstants.MANAGE_OWNERS, RolesConstants.MANAGE_RIGHTS})
    public String upload(
            @PathParam(value = LANG_CODE) String langCode,
            @FormDataParam("file") FormDataBodyPart body,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        try {
            DocumentService service = new DocumentService();
            FileInfo fileInfo = service.saveFile(inputStream, fileDetail, body);
            if (fileInfo != null) {
                return "{\"id\": \"" + fileInfo.getId() + "\"}";
            }
            return "{id: null}";
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }

    /**
     * Returns file
     *
     * @param langCode Language code for localization
     * @param id File id
     * @return
     */
    @GET
    @Path(value = "{a:getfile|getFile}/{id}")
    @Authorized(roles = RolesConstants.VIEWING)
    public Response getFile(@PathParam(value = LANG_CODE) String langCode, @PathParam(value = "id") String id) {
        return getMedia(langCode, id, false);
    }

    private Response getMedia(String langCode, String id, boolean asAttachment) {
        try {
            DocumentService service = new DocumentService();
            FileInfo fileInfo = service.getFileInfo(id);
            if (fileInfo != null) {
                String fileName = fileInfo.getId();
                if(fileName != null && fileName.length() > 8){
                    fileName = fileName.substring(0, 8);
                }
                String attachment = asAttachment ? "attachment; " : "";
                ResponseBuilder response = Response.ok((Object) service.getFile(fileInfo));
                response.header("Content-Disposition", attachment + "filename=\"" + fileName + "." + FileUtility.getFileExtension(fileInfo.getOriginalFileName()) + "\"");
                response.header("Content-Type", fileInfo.getMediaType());
                return response.build();
            }
            return Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
            throw processException(e, langCode);
        }
    }
}
