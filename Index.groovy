package com.bonitasoft.rest.api;

import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.bdm.BusinessObjectDAOFactory;
import org.bonitasoft.web.extension.ResourceProvider;
import org.bonitasoft.web.extension.rest.RestApiResponse;
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder;
import com.bonitasoft.web.extension.rest.RestAPIContext;
import com.bonitasoft.web.extension.rest.RestApiController;

import com.projet.model.PersonneDAO;

import groovy.json.JsonBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Get implements RestApiController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("com.bonitasoft.rest.api");

    @Override
    RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {
		
		// PARAMS
        String id = request.getParameter("id");
        LOGGER.info("id : " + id);

        if (id == null) {
			//EX ERROR
            return buildResponse(responseBuilder, HttpServletResponse.SC_BAD_REQUEST,"""{"error" : "the parameter id is missing"}""");
        }
		
		// SESSION and ENTRY POINT
        APISession apiSession = context.getApiSession();
        APIClient contextApiClient = context.getApiClient();

        // Get APIS
        ProcessAPI processAPI = contextApiClient.getProcessAPI();
        IdentityAPI identityAPI = contextApiClient.getIdentityAPI();
		
		// BDM
		def BusinessObjectDAOFactory daoFactory = new BusinessObjectDAOFactory();
		def PersonneDAO = daoFactory.createDAO(apiSession, PersonneDAO.class);
		def Personnes = PersonneDAO.find(0, 100);
		
		// PROPERTIES
        Properties props = loadProperties("configuration.properties", context.resourceProvider);
        String paramValue = props["myParameterKey"];
		
		// OBJECT RETURN
		def result = ["Test":"Réussi"];

        // BUILD RESPONSE
        return buildResponse(responseBuilder, HttpServletResponse.SC_OK, new JsonBuilder(result).toPrettyString());
    }
	
	/**
     * Build an HTTP response.
     *
     * @param  responseBuilder the Rest API response builder
     * @param  httpStatus the status of the response
     * @param  body the response body
     * @return a RestAPIResponse
     */
    RestApiResponse buildResponse(RestApiResponseBuilder responseBuilder, int httpStatus, Serializable body) {
        return responseBuilder.with {
            withResponseStatus(httpStatus)
            withResponse(body)
            build()
        }
    }

    /**
     * Returns a paged result like Bonita BPM REST APIs.
     * Build a response with content-range data in the HTTP header.
     *
     * @param  responseBuilder the Rest API response builder
     * @param  body the response body
     * @param  p the page index
     * @param  c the number of result per page
     * @param  total the total number of results
     * @return a RestAPIResponse
     */
    RestApiResponse buildPagedResponse(RestApiResponseBuilder responseBuilder, Serializable body, int p, int c, long total) {
        return responseBuilder.with {
            withAdditionalHeader(HttpHeaders.CONTENT_RANGE,"$p-$c/$total");
            withResponse(body)
            build()
        }
    }

    /**
     * Load a property file into a java.util.Properties
     */
    Properties loadProperties(String fileName, ResourceProvider resourceProvider) {
        Properties props = new Properties()
        resourceProvider.getResourceAsStream(fileName).withStream { InputStream s ->
            props.load s
        }
        props
    }
}
