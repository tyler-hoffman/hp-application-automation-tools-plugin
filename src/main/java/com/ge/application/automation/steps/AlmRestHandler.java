package com.ge.application.automation.steps;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.hp.application.automation.tools.rest.RestClient;
import com.hp.application.automation.tools.sse.sdk.ResourceAccessLevel;
import com.hp.application.automation.tools.sse.sdk.Response;

/**
 * Abstract class for making rest calls
 * @param <T> Return type for getResult
 */
public abstract class AlmRestHandler<T>{
	
	/**
	 * Get the string to be appended to the rest url generated
	 * by con.buildEntityCollectionUrl(). 
	 * e.g. "tests/5"
	 * @return Returns the request.
	 */
	public abstract String getRequest();
	
	/**
	 * Extract data from xml response
	 * @param xml Xml response from alm
	 * @return Returns needed data from xml
	 * @throws JAXBException
	 */
	public abstract T parseXml(String xml) throws JAXBException;
	
	/**
	 * Make a call to the rest api and extract the needed data
	 * @param con RestConnector to use
	 * @param logger Logger
	 * @return Returns output from parseXml
	 */
	//public T getResult(RestConnector con, PrintStream logger) {
	public T getResult(RestClient con, PrintStream logger) {
		T output = null;
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("Accept", "application/xml");
		
		//String requestUrl = //con.buildEntityCollectionUrl(getRequest()); 
		String requestUrl = con.buildRestRequest(getRequest());
		
		// get xml from alm rest api
		String responseXml = null;
		try {
			Response response = con.httpGet(requestUrl, "", requestHeaders, ResourceAccessLevel.PRIVATE);
			responseXml = response.toString();
		} catch (Exception e) {
			e.printStackTrace(logger);
		}
		
		// attempt to parse the response
		if (responseXml != null) {
			try {
				output = parseXml(responseXml);
			} catch (JAXBException e) {
				logger.println("An error occurred while retrieving results from ALM.");
				logger.println("Did you enter the proper credentials in the ALM build step?");
				e.printStackTrace(logger);
				logger.println("URL: " + requestUrl);
				logger.println(responseXml);
			}
		}
		
		return output;
	}
	
}
