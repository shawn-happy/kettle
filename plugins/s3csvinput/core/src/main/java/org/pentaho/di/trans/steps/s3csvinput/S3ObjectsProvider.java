/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.di.trans.steps.s3csvinput;

import java.util.List;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

public class S3ObjectsProvider {
  private AmazonS3 s3Client;

  public S3ObjectsProvider( AmazonS3 s3Client ) {
    super();
    this.s3Client = s3Client;
  }

  /**
   * Returns the buckets belonging to the service user
   *
   * @return the list of buckets owned by the service user.
   * @throws SdkClientException
   */
  private List<Bucket> getBuckets() throws SdkClientException {
    ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

    List result;
    try {
      Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
      result = s3Client.listBuckets();
    } finally {
      Thread.currentThread().setContextClassLoader( currentClassLoader );
    }

    return result;
  }

  /**
   * Returns the names of buckets belonging to the service user
   *
   * @return the list of buckets names owned by the service user.
   * @throws SdkClientException
   */
  public String[] getBucketsNames() throws SdkClientException {
    return getBuckets().stream().map( b -> b.getName() ).toArray( String[]::new );
  }

  /**
   * Returns the named bucket.
   *
   * @param bucketName
   *          the name of the bucket to find.
   * @return the bucket, or null if no the named bucket has found.
   * @throws SdkClientException
   */
  public Bucket getBucket( String bucketName ) throws SdkClientException {
    return s3Client.doesBucketExistV2( bucketName ) ? new Bucket( bucketName ) : null;
  }

  /**
   * Returns the objects in a bucket. The objects returned by this method contain only minimal information.
   *
   * @param bucket
   *          the bucket whose contents will be listed.
   * @return the set of objects contained in a bucket.
   * @throws SdkClientException
   */
  private ObjectListing getS3Objects( Bucket bucket ) throws SdkClientException {
    ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );
      return s3Client.listObjects( bucket.getName() );
    } finally {
      Thread.currentThread().setContextClassLoader( currentClassLoader );
    }
  }

  /**
   * Returns the objects names in a bucket.
   *
   * @param bucketName
   *          the bucket whose contents will be listed.
   * @return the set of names of objects contained in a bucket.
   * @throws Exception
   */
  public String[] getS3ObjectsNames( String bucketName ) throws Exception {
    Bucket bucket = getBucket( bucketName );
    if ( bucket == null ) {
      throw new Exception( Messages.getString( "S3DefaultService.Exception.UnableToFindBucket.Message", bucketName ) );
    }
    return getS3Objects( bucket ).getObjectSummaries().stream().map( b -> b.getKey() ).toArray( String[]::new );
  }

  /**
   * Returns an object representing the details and data of an item in S3.
   *
   * @param bucket
   *          the bucket containing the object.
   * @param objectKey
   *          the key identifying the object.
   * @param byteRangeStart
   *          include only a portion of the object's data - starting at this point
   * @param byteRangeEnd
   *          include only a portion of the object's data - ending at this point
   * @return the object with the given key in S3, including details and data
   * @throws SdkClientException
   */
  public S3Object getS3Object( Bucket bucket, String objectKey, Long byteRangeStart, Long byteRangeEnd ) throws SdkClientException {
    if ( byteRangeStart != null && byteRangeEnd != null ) {
      GetObjectRequest rangeObjectRequest =
        new GetObjectRequest( bucket.getName(), objectKey ).withRange( byteRangeStart, byteRangeEnd );
      return s3Client.getObject( rangeObjectRequest );
    } else {
      return s3Client.getObject( bucket.getName(), objectKey );
    }
  }

  /**
   * Returns an object representing the details and data of an item in S3.
   *
   * @param bucket
   *          the bucket containing the object.
   * @param objectKey
   *          the key identifying the object.
   * @return the object with the given key in S3, including details and data
   * @throws SdkClientException
   */
  public S3Object getS3Object( Bucket bucket, String objectKey ) throws SdkClientException {
    return getS3Object( bucket, objectKey, null, null );
  }

  /**
   * Returns an object representing the details of an item in S3. The object is returned without the object's data.
   *
   * @param bucket
   *          the bucket containing the object.
   * @param objectKey
   *          the key identifying the object.
   * @return the object with the given key in S3, including only general details and metadata (not the data input
   *         stream)
   * @throws SdkClientException
   */
  private ObjectMetadata getS3ObjectDetails( Bucket bucket, String objectKey ) throws SdkClientException {
    return s3Client.getObjectMetadata( bucket.getName(), objectKey );
  }

  /**
   * Returns the content length, or size, of this object's data, or 0 if it is unknown.
   *
   * @param bucket
   *          the bucket containing the object.
   * @param objectKey
   *          the key identifying the object.
   * @return the content length, or size, of this object's data, or 0 if it is unknown
   * @throws SdkClientException
   */
  public long getS3ObjectContentLenght( Bucket bucket, String objectKey ) throws SdkClientException {
    return getS3ObjectDetails( bucket, objectKey ).getContentLength();
  }
}
