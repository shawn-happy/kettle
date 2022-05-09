/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.core;

import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.pentaho.di.core.util.HttpClientManager;
import org.pentaho.di.core.util.Utils;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * HTTP
 * <p>
 * This class contains HTTP protocol properties such as request headers. Response headers and other properties of the
 * HTTP protocol can be added to this class.
 *
 * @author sflatley
 */
public class HTTPProtocol {

  /*
   * Array of HTTP request headers- this list is incomplete and more headers can be added as needed.
   */

  private static final String[] requestHeaders = { "accept", "accept-charset", "cache-control", "content-type" };

  /**
   * @return array of HTTP request headers
   */
  public static String[] getRequestHeaders() {
    return requestHeaders;
  }

  /**
   * Performs a get on urlAsString using username and password as credentials.
   * <p>
   * If the status code returned not -1 and 401 then the contents are returned. If the status code is 401 an
   * AuthenticationException is thrown.
   * <p>
   * All other values of status code are not dealt with but logic can be added as needed.
   *
   * @param urlAsString The url to connect to
   * @param username Basic Authentication username
   * @param password Basic Authentication password
   * @return If the status code returned not -1 and 401 then the contents are returned. If the status code is 401 an
   * AuthenticationException is thrown.
   * @throws AuthenticationException
   * @throws IOException
   */
  public String get( String urlAsString, String username, String password )
    throws IOException, AuthenticationException {

    HttpGet getMethod = new HttpGet( urlAsString );
    CloseableHttpClient httpClient = null;
    CloseableHttpResponse httpResponse = null;
    InputStreamReader inputStreamReader = null;
    try {
      httpClient = openHttpClient( username, password );
      httpResponse = httpClient.execute( getMethod );
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      StringBuilder bodyBuffer = new StringBuilder();

      if ( statusCode != -1 ) {
        if ( statusCode != HttpStatus.SC_UNAUTHORIZED ) {
          // the response
          inputStreamReader = new InputStreamReader( httpResponse.getEntity().getContent() );

          int c;
          while ( ( c = inputStreamReader.read() ) != -1 ) {
            bodyBuffer.append( (char) c );
          }
          inputStreamReader.close();

        } else {
          throw new AuthenticationException();
        }
      }

      // Display response
      return bodyBuffer.toString();
    } finally {
      if ( httpResponse != null ) {
        httpResponse.close();
      }
      if ( inputStreamReader != null ) {
        inputStreamReader.close();
      }
      if ( httpClient != null ) {
        httpClient.close();
      }
    }
  }

  CloseableHttpClient openHttpClient( String username, String password ) {
    CloseableHttpClient httpClient;
    if ( !Utils.isEmpty( username ) ) {
      HttpClientManager.HttpClientBuilderFacade clientBuilder = HttpClientManager.getInstance().createBuilder();
      clientBuilder.setCredentials( username, password );
      httpClient = clientBuilder.build();
    } else {
      httpClient = HttpClientManager.getInstance().createDefaultClient();
    }
    return httpClient;
  }
}
