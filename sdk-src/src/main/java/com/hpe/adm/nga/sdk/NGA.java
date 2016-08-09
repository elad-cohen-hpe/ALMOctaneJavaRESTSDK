package com.hpe.adm.nga.sdk;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.hpe.adm.nga.sdk.authorisation.Authorisation;
import com.hpe.adm.nga.sdk.exception.NgaException;
import com.hpe.adm.nga.sdk.attachments.AttachmentList;
import com.hpe.adm.nga.sdk.metadata.Metadata;
import com.hpe.adm.nga.sdk.model.ErrorModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This class represents the main NGA builder
 *
 * @author Moris Oz
  */
public class NGA {

	//Constants
	private static final String SITE_ADMIN_DOMAIN_FORMAT= "/api/siteadmin/";
	private static final String SHARED_SPACES_DOMAIN_FORMAT= "%s/api/shared_spaces/%s/"; 
	private static final String WORKSPACES_DOMAIN_FORMAT= "workspaces/%s/"; 
	private static final String METADATA_DOMAIN_FORMAT= "metadata";
	private static final String ATTACHMENT_LIST_DOMAIN_FORMAT = "attachments";
	
	
	//private memebers
	private HttpRequestFactory requestFactory = null;
	private String urlDomain = "";
	public String idsharedSpaceId = null;
	private long workSpaceId = 0;
	
	// functions
	public NGA(HttpRequestFactory reqFactory,String domain,String sharedSpaceId,long workId ) {
		
		requestFactory = reqFactory;
		urlDomain = domain;
		idsharedSpaceId = sharedSpaceId;
		workSpaceId = workId;
		
		
	}

	/**
	   * EntityList Getter
	   * 
	   * @param entityName - Entity Name
	   * @return  A new EntityList object that list of entities 
	   */
	public EntityList entityList(String entityName) {
		
		String entityListDomain =  getBaseDomainFormat() + entityName; 
		return new EntityList(requestFactory,entityListDomain);
	}
	

	/**
	   * Metadata Getter
	   * 
	   * @return  A new Metadata object that hold the metadata
	   */
	public Metadata metadata() {
		String metadataDomain =  getBaseDomainFormat()+METADATA_DOMAIN_FORMAT;
		return new Metadata(requestFactory,metadataDomain) ;
	}

	/**
	   * AttachmentList Getter
	   * 
	   * @return  A new AttachmentList object that hold the Attachments
	   */
	public AttachmentList AttachmentList() {
		
		String attachmentListDomain =  getBaseDomainFormat()+ATTACHMENT_LIST_DOMAIN_FORMAT; 
		return new AttachmentList(requestFactory,attachmentListDomain);
	}
	
	/**
	 * get the base domain based on workSpaceId and idsharedSpaceId
	 * @return base domain
	 */
	protected String getBaseDomainFormat(){
		
		
		String baseDomain = urlDomain + SITE_ADMIN_DOMAIN_FORMAT;
		
		if (idsharedSpaceId!=null && !idsharedSpaceId.isEmpty())
		{
			baseDomain = String.format(SHARED_SPACES_DOMAIN_FORMAT,urlDomain, idsharedSpaceId);
			
			if (workSpaceId!=0)
				baseDomain = baseDomain + String.format(WORKSPACES_DOMAIN_FORMAT,String.valueOf(workSpaceId));
		}
			
		return baseDomain;
		
	}
	
	/**
	 * This class is in charge on the builder functionality 
	 *
	 * @author Moris Oz
	  */
	public static class Builder {

		
		//Constants
		private static final String OAUTH_AUTH_URL = "/authentication/sign_in";
		private static final String SET_COOKIE = "set-cookie";
		private static final String HPSSO_COOKIE_CSRF = "HPSSO_COOKIE_CSRF";
		private static final String LWSSO_COOKIE_KEY = "LWSSO_COOKIE_KEY";
		private static final String HPSSO_HEADER_CSRF = "HPSSO_HEADER_CSRF";
		private static final String HPE_CLIENT_TYPE = "HPECLIENTTYPE";
		private static final String HPE_MQM_UI ="HPE_MQM_UI";
		private static final String LOGGER_REQUEST_FORMAT = "Request: %s - %s - %s";
		private static final String LOGGER_RESPONSE_FORMAT = "Response: %d - %s - %s";

		
		//Private
		private Logger logger = LogManager.getLogger(NGA.class.getName());
		private HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		private String lwssoValue = "";
		private String hppsValue = "";
		private HttpRequestFactory requestFactory = null;
		private String urlDomain = "";
		public String idsharedSpaceId = null;
		private long workSpaceId = 0;
		private final Authorisation authorisation;
		
		
		//Functions
		
		/** Creates a new Builder object 
		 * 
		 * @param authorisation - hold the details of Authorisation
		 */
		public Builder(Authorisation authorisation)  {
	    	this.authorisation = authorisation;
		}
		
		/**
		 *  Setter of the sharedSpace id ( UUID )
		 * @param ssID - sharedSpace id
		 * @return Builder object with new sharedSpace id
		 */
		public Builder sharedSpace(UUID ssID) {
			
			idsharedSpaceId = ssID.toString();
			return this;
		}
		
		/**
		 * Setter of the sharedSpace id ( long )
		 * @param ssID - sharedSpace id
		 * @return Builder object with new sharedSpace id
		 */
		public Builder sharedSpace(long ssID) {
			
			idsharedSpaceId = String.valueOf(ssID);
			return this;
		}
		/**
		 * Setter of the workSpace id
		 * @param lId - workSpace id
		 * @return Builder object with new workSpace id
		 */
		public Builder workSpace(long lId) {
			
			workSpaceId = lId;
			return this;
		}
		
		/**
		 * Setter of the Server domain name and port
		 * @param domain - domain name
		 * @param port - port number 
		 * @return Builder object with new Server domain info
		 */
		public Builder Server(String domain, int port) {
			
			urlDomain = domain + ":" + String.valueOf(port);
			
			return this;
		}
		/**
		 *  Setter of the Server domain name 
		 * @param domain - domain name
		 * @return Builder object with new Server domain name
		 */
		public Builder Server(String domain) {
			
			urlDomain = domain;
			
			return this;
		}
		
		
		/**
		 * The main build procedure which create the NGA objects,based on this steps :
		 * 1. Build an HTTP request
		 * 2. Handle response - Initialize Cookies keys
		 * 3. Create a new NGA objects.
		 *  
		 * @return a new NGA object
		 * @throws RuntimeException
		 */
		public NGA build() throws RuntimeException{
			
			NGA objNga = null;
			
			requestFactory = HTTP_TRANSPORT
                .createRequestFactory(new HttpRequestInitializer() {

                    @Override
                    public void initialize(HttpRequest request) {

                        request.setUnsuccessfulResponseHandler(new HttpUnsuccessfulResponseHandler() {
                            @Override
                            public boolean handleResponse(HttpRequest httpRequest, HttpResponse httpResponse, boolean b) throws IOException {
                                if (httpResponse.getStatusCode() == HttpStatusCodes.STATUS_CODE_UNAUTHORIZED) {
                                    return refreshToken();
                                }

                                return false;
                            }

                            /**
                             * refresh the LWSSO token and retry
                             * @return true if token is renewed and need retry and false otherwise
                             */
                            private boolean refreshToken() {
                                logger.debug("Session is expired, renew token and retry.");
                                GenericUrl genericUrl = new GenericUrl(urlDomain + OAUTH_AUTH_URL);
                                try{
                                    // clear lwssoValue for re-login
                                    lwssoValue = "";
                                    logger.debug("Login to renew token.");
                                    HttpRequest httpRequest = requestFactory.buildPostRequest(genericUrl, null);
                                    logger.debug(String.format(LOGGER_REQUEST_FORMAT,httpRequest.getRequestMethod(),urlDomain + OAUTH_AUTH_URL,httpRequest.getHeaders().toString()));
                                    HttpResponse response = httpRequest.execute();
                                    logger.debug(String.format(LOGGER_RESPONSE_FORMAT,response.getStatusCode(),response.getStatusMessage(),response.getHeaders().toString()));

                                    // refresh Cookies keys
                                    if (response.isSuccessStatusCode()) {
                                        HttpHeaders hdr1 = response.getHeaders();
                                        if (updateLWSSOCookieValue(hdr1)) {
                                            String newCookie = LWSSO_COOKIE_KEY + "=" + lwssoValue;
                                            request.getHeaders().setCookie(newCookie);
                                            logger.debug("Retry with updated token.");
                                            logger.debug(String.format(LOGGER_REQUEST_FORMAT, request.getRequestMethod(), request.getUrl().toString(), request.getHeaders().toString()));

                                            // return true for retrying the origin request
                                            return true;
                                        }
                                    }
                                }
                                catch (Exception e){
                                    ErrorModel errorModel =  new ErrorModel(e.getMessage());
                                    logger.error("Error in contacting server: ", e);
                                    throw new NgaException(errorModel);
                                }

                                return false;
                            }
                        });

                        // BasicAuthentication needed only in first initialization
                        if (lwssoValue != null && lwssoValue.isEmpty())
                        {
                            authorisation.executeAuthorisation(request);
                            // username and password should be transient.
                        }
                        else
                        {
                            // retrieve new LWSSO in response if any
                            HttpHeaders responseHeaders = request.getResponseHeaders();
                            String lastResponseCookie;
                            if (updateLWSSOCookieValue(responseHeaders)) {
                                // renew LWSSO cookie
                                lastResponseCookie = LWSSO_COOKIE_KEY + "=" + lwssoValue;
                            } else {
                                // use current request cookie or set from current lwssoValue
                                lastResponseCookie = request.getHeaders().getCookie() != null ? request.getHeaders().getCookie() : LWSSO_COOKIE_KEY + "=" + lwssoValue;
                            }

                            request.getHeaders().setCookie(lastResponseCookie);
                            request.getHeaders().set(HPE_CLIENT_TYPE,HPE_MQM_UI);
                        }
                    }
                });
			
			GenericUrl genericUrl = new GenericUrl(urlDomain + OAUTH_AUTH_URL);
			try{
				HttpRequest httpRequest = requestFactory.buildPostRequest(genericUrl, null);
				logger.debug(String.format(LOGGER_REQUEST_FORMAT,httpRequest.getRequestMethod(),urlDomain + OAUTH_AUTH_URL,httpRequest.getHeaders().toString()));
				HttpResponse response = httpRequest.execute();
				logger.debug(String.format(LOGGER_RESPONSE_FORMAT,response.getStatusCode(),response.getStatusMessage(),response.getHeaders().toString()));
			
			
				// Initialize Cookies keys
				if (response.isSuccessStatusCode()) {
                    HttpHeaders hdr1 = response.getHeaders();
                    updateLWSSOCookieValue(hdr1);
					
					// TBD - Remove after debugging
					/* for (HttpCookie ck : Cookies) {
						 
						 if (ck.getName().equals(HPSSO_COOKIE_CSRF))
							 hppsValue = ck.getValue();
						 
						 if (ck.getName().equals(LWSSO_COOKIE_KEY))
							 lwssoValue = ck.getValue();
							 
					 }*/
					
		           if(lwssoValue!=null && !lwssoValue.isEmpty())
		           {
		        	   objNga = new NGA(requestFactory,urlDomain,idsharedSpaceId,workSpaceId);
		           }
				}
				 
	        } 
			catch (Exception e){

				ErrorModel errorModel =  new ErrorModel(e.getMessage());
				logger.error("Error in contacting server: ", e);
				throw new NgaException(errorModel);
			}
   	       	        
	        return objNga;
		}

        /**
         * retrieve new cookie from set-cookie header
         * @param headers
         * @return true if LWSSO cookie is renewed
         */
		private boolean updateLWSSOCookieValue(HttpHeaders headers) {
            boolean renewed = false;
			List<String> strHPSSOCookieCsrf1 = headers.getHeaderStringValues(SET_COOKIE);
            if (strHPSSOCookieCsrf1.isEmpty()) {
                return false;
            }

            /* Following code failed to parse set-cookie to get LWSSO cookie due to cookie version, check RFC 2965
            String strCookies = strHPSSOCookieCsrf1.toString();
            List<HttpCookie> Cookies = java.net.HttpCookie.parse(strCookies.substring(1, strCookies.length()-1));
            lwssoValue = Cookies.stream().filter(a -> a.getName().equals(LWSSO_COOKIE_KEY)).findFirst().get().getValue();*/
            for (String strCookie :
                    strHPSSOCookieCsrf1) {
                List<HttpCookie> cookies = HttpCookie.parse(strCookie);
                Optional<HttpCookie> lwssoCookie = cookies.stream().filter(a -> a.getName().equals(LWSSO_COOKIE_KEY)).findFirst();
                if (lwssoCookie.isPresent()) {
                    lwssoValue = lwssoCookie.get().getValue();
                    renewed = true;
                    break;
                }
            }

            return renewed;
		}
	}

}
