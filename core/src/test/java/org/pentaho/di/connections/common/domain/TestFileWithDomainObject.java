/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.di.connections.common.domain;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;

import java.io.InputStream;

public class TestFileWithDomainObject extends AbstractFileObject<TestFileWithDomainSystem> {

  public TestFileWithDomainObject( AbstractFileName name,
                                   TestFileWithDomainSystem fs ) {
    super( name, fs );
  }

  @Override public boolean exists() throws FileSystemException {
    return true;
  }

  @Override protected long doGetContentSize() throws Exception {
    return 0;
  }

  @Override protected InputStream doGetInputStream() throws Exception {
    return null;
  }

  @Override protected FileType doGetType() throws Exception {
    return FileType.FILE;
  }

  @Override protected String[] doListChildren() throws Exception {
    return new String[] { "file1", "file2", "file3" };
  }
}
