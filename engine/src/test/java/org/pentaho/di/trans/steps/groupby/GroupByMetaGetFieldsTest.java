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

package org.pentaho.di.trans.steps.groupby;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.pentaho.di.trans.steps.groupby.GroupByMeta.TYPE_GROUP_COUNT_ANY;
import static org.pentaho.di.trans.steps.groupby.GroupByMeta.TYPE_GROUP_MAX;
import static org.pentaho.di.trans.steps.groupby.GroupByMeta.TYPE_GROUP_MIN;

import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;

/**
 * @author Luis Martins
 */
@RunWith( PowerMockRunner.class )
@PowerMockIgnore( "jdk.internal.reflect.*" )
@PrepareForTest( { ValueMetaFactory.class } )
public class GroupByMetaGetFieldsTest {

  private GroupByMeta groupByMeta;
  private RowMetaInterface rowMeta;

  private RowMetaInterface[] mockInfo;
  private StepMeta mockNextStep;
  private VariableSpace mockSpace;
  private IMetaStore mockIMetaStore;

  @Before
  public void setup() throws KettlePluginException {
    rowMeta = spy( new RowMeta() );
    groupByMeta = spy( new GroupByMeta() );

    mockStatic( ValueMetaFactory.class );
    when( ValueMetaFactory.createValueMeta( anyInt() ) ).thenCallRealMethod();
    when( ValueMetaFactory.createValueMeta( anyString(), anyInt() ) ).thenCallRealMethod();
    when( ValueMetaFactory.createValueMeta( "maxDate", 3, -1, -1 ) ).thenReturn( new ValueMetaDate( "maxDate" ) );
    when( ValueMetaFactory.createValueMeta( "minDate", 3, -1, -1 ) ).thenReturn( new ValueMetaDate( "minDate" ) );
    when( ValueMetaFactory.createValueMeta( "countDate", 5, -1, -1 ) ).thenReturn( new ValueMetaInteger( "countDate" ) );
    when( ValueMetaFactory.getValueMetaName( 3 ) ).thenReturn( "Date" );
    when( ValueMetaFactory.getValueMetaName( 5 ) ).thenReturn( "Integer" );
  }

  @After
  public void cleanup() {
  }

  @Test
  public void getFieldsWithSubject_WithFormat() {
    ValueMetaDate valueMeta = new ValueMetaDate();
    valueMeta.setConversionMask( "yyyy-MM-dd" );
    valueMeta.setName( "date" );

    doReturn( valueMeta ).when( rowMeta ).searchValueMeta( "date" );

    groupByMeta.setSubjectField( new String[] { "date" } );
    groupByMeta.setGroupField( new String[] {} );
    groupByMeta.setAggregateField( new String[] { "maxDate" } );
    groupByMeta.setAggregateType( new int[] { TYPE_GROUP_MAX } );

    groupByMeta.getFields( rowMeta, "Group by", mockInfo, mockNextStep, mockSpace, null, mockIMetaStore );

    verify( rowMeta, times( 1 ) ).clear();
    verify( rowMeta, times( 1 ) ).addRowMeta( any() );
    assertEquals( "yyyy-MM-dd", rowMeta.searchValueMeta( "maxDate" ).getConversionMask() );
  }

  @Test
  public void getFieldsWithSubject_NoFormat() {
    ValueMetaDate valueMeta = new ValueMetaDate();
    valueMeta.setName( "date" );

    doReturn( valueMeta ).when( rowMeta ).searchValueMeta( "date" );

    groupByMeta.setSubjectField( new String[] { "date" } );
    groupByMeta.setGroupField( new String[] {} );
    groupByMeta.setAggregateField( new String[] { "minDate" } );
    groupByMeta.setAggregateType( new int[] { TYPE_GROUP_MIN } );

    groupByMeta.getFields( rowMeta, "Group by", mockInfo, mockNextStep, mockSpace, null, mockIMetaStore );

    verify( rowMeta, times( 1 ) ).clear();
    verify( rowMeta, times( 1 ) ).addRowMeta( any() );
    assertEquals( null, rowMeta.searchValueMeta( "minDate" ).getConversionMask() );
  }

  @Test
  public void getFieldsWithoutSubject() {
    ValueMetaDate valueMeta = new ValueMetaDate();
    valueMeta.setName( "date" );

    doReturn( valueMeta ).when( rowMeta ).searchValueMeta( "date" );

    groupByMeta.setSubjectField( new String[] { null } );
    groupByMeta.setGroupField( new String[] { "date" } );
    groupByMeta.setAggregateField( new String[] { "countDate" } );
    groupByMeta.setAggregateType( new int[] { TYPE_GROUP_COUNT_ANY } );

    groupByMeta.getFields( rowMeta, "Group by", mockInfo, mockNextStep, mockSpace, null, mockIMetaStore );

    verify( rowMeta, times( 1 ) ).clear();
    verify( rowMeta, times( 1 ) ).addRowMeta( any() );
    assertNotNull( rowMeta.searchValueMeta( "countDate" ) );
  }
}
