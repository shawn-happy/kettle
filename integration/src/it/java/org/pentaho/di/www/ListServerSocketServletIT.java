/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.di.www;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class ListServerSocketServletIT {
  private TransformationMap mockTransformationMap;

  private ListServerSocketServlet listServerSocketServlet;

  @Before
  public void setup() {
    mockTransformationMap = mock( TransformationMap.class );
    listServerSocketServlet = new ListServerSocketServlet( mockTransformationMap );
  }

  @Test
  public void testListServerSocketServletEncodesParametersForHmtlResponse() throws ServletException, IOException {
    HttpServletRequest mockRequest = mock( HttpServletRequest.class );
    HttpServletResponse mockResponse = mock( HttpServletResponse.class );
    SocketPortAllocation mockSocketPortAllocation = mock( SocketPortAllocation.class );
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = new ServletOutputStream() {

      @Override
      public void write( int b ) throws IOException {
        byteArrayOutputStream.write( b );
      }
    };

    when( mockRequest.getContextPath() ).thenReturn( ListServerSocketServlet.CONTEXT_PATH );
    when( mockRequest.getParameter( anyString() ) ).thenReturn( ServletTestUtils.BAD_STRING );
    when( mockResponse.getOutputStream() ).thenReturn( servletOutputStream );
    when(
      mockTransformationMap.allocateServerSocketPort(
        anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
        anyString(), anyString() ) ).thenReturn( mockSocketPortAllocation );

    listServerSocketServlet.doGet( mockRequest, mockResponse );

    String response = byteArrayOutputStream.toString();
    assertFalse( ServletTestUtils.hasBadText( ServletTestUtils.getInsideOfTag( "H1", response ) ) );
  }
}
