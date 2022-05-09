/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2020 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.di.core.xml;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleXMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class XMLHandlerTest {

  private static final String DUMMY = "dummy";

  @Test
  public void getTagValueWithNullNode() {
    assertNull( XMLHandler.getTagValue( null, "text"  ) );
  }

  /**
   * Default behavior, an empty XML tag in the "Filter rows" step meta will be considered {@code null}.
   * This will prevent filtering rows with empty values.
   */
  @Test
  public void getTagValueEmptyTagYieldsNullValue() {
    System.setProperty( Const.KETTLE_XML_EMPTY_TAG_YIELDS_EMPTY_VALUE, "N" );
    assertNull( XMLHandler.getTagValue( getNode(), "text"  ) );
  }

  /**
   * An empty XML tag in the "Filter rows" step meta will be considered an empty string.
   * This will allow filtering rows with empty values.
   */
  @Test
  public void getTagValueEmptyTagYieldsEmptyValue() {
    System.setProperty( Const.KETTLE_XML_EMPTY_TAG_YIELDS_EMPTY_VALUE, "Y" );
    assertEquals( "", XMLHandler.getTagValue( getNode(), "text"  ) );
  }

  private Node getNode() {
    Element first = mock( Element.class );
    doReturn( null ).when( first ).getNodeValue();

    Node child = mock( Node.class );
    doReturn( "text" ).when( child ).getNodeName();
    doReturn( first ).when( child ).getFirstChild();
    doReturn( "" ).when( child ).getTextContent();

    NodeList children = mock( NodeList.class );
    doReturn( 1 ).when( children ).getLength();
    doReturn( child ).when( children ).item( 0 );

    Node node = mock( Node.class );
    doReturn( children ).when( node ).getChildNodes();

    return node;
  }
  @Test
  public void checkFile_FileDoesNotExist() throws Exception {
    FileObject fileObjectMock = mock( FileObject.class );
    doReturn( false ).when( fileObjectMock ).exists();
    doReturn( false ).when( fileObjectMock ).isFile();

    assertFalse( XMLHandler.checkFile( fileObjectMock ) );
  }

  @Test
  public void checkFile_IsFile() throws Exception {
    FileObject fileObjectMock = mock( FileObject.class );
    doReturn( true ).when( fileObjectMock ).exists();
    doReturn( true ).when( fileObjectMock ).isFile();

    assertTrue( XMLHandler.checkFile( fileObjectMock ) );
  }

  @Test
  public void checkFile_IsNotFile() throws Exception {
    FileObject fileObjectMock = mock( FileObject.class );
    doReturn( true ).when( fileObjectMock ).exists();
    doReturn( false ).when( fileObjectMock ).isFile();

    assertFalse( XMLHandler.checkFile( fileObjectMock ) );
  }


  @Test( expected = KettleXMLException.class )
  public void checkFile_Exception() throws Exception {
    FileObject fileObjectMock = mock( FileObject.class );
    doReturn( true ).when( fileObjectMock ).exists();
    doThrow( new FileSystemException( DUMMY ) ).when( fileObjectMock ).isFile();

    XMLHandler.checkFile( fileObjectMock );
  }

  @Test
  public void loadFile_NoFile() throws Exception {
    FileObject fileObjectMock = mock( FileObject.class );
    doReturn( true ).when( fileObjectMock ).exists();
    doReturn( false ).when( fileObjectMock ).isFile();

    try {
      XMLHandler.loadXMLFile( fileObjectMock );
    } catch ( KettleXMLException e ) {
      System.out.println( e.getMessage() );
      assertTrue( e.getMessage().contains( "does not exists" ) );
    }
  }

  @Test
  public void loadFile_ExceptionCheckingFile() throws Exception {
    FileObject fileObjectMock = mock( FileObject.class );
    doReturn( true ).when( fileObjectMock ).exists();
    doThrow( new FileSystemException( DUMMY ) ).when( fileObjectMock ).isFile();

    try {
      XMLHandler.loadXMLFile( fileObjectMock );
    } catch ( KettleXMLException e ) {
      System.out.println( e.getMessage() );
      assertTrue( e.getMessage().contains( "Unable to check if file" ) );
    }
  }
}
