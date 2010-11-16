package com.udnaes.jerseymockito;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.TreeMapper;

/**
 * 
 * @author Morten Udnæs, 2010
 * 
 */

@SuppressWarnings("deprecation")
public class RestClientUtil {

	private static String sendRequest(String url) {
		byte[] responseBody;
		String httpParms = "";

		try {
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(url + httpParms);

			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK)
				throw new RuntimeException("HTTP Request retuned with error status: " + statusCode);

			responseBody = method.getResponseBody();
			if (responseBody != null)
				return new String(responseBody);
			else
				throw new RuntimeException("HTTP Response was empty...");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}


	public static JsonNode doGet(String url, int port, String context) {
		try {
			String response = sendRequest(url + ":" + port + context);
			return new TreeMapper().readTree(response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
