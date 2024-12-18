/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.trans.steps.annotation;

import org.apache.commons.lang.StringUtils;
import org.pentaho.agilebi.modeler.models.annotations.ModelAnnotationGroup;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class SharedDimensionStep extends ModelAnnotationStep implements StepInterface {

  public SharedDimensionStep( StepMeta stepMeta,
      StepDataInterface stepDataInterface, int copyNr,
      TransMeta transMeta, Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  @Override public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {

    SharedDimensionMeta meta = (SharedDimensionMeta) smi;
    meta.setSharedDimension( true );
    if ( StringUtils.isNotEmpty( meta.sharedDimensionName ) ) {
      meta.setModelAnnotationCategory( meta.sharedDimensionName );
    }
    if ( StringUtils.isNotEmpty( meta.dataProviderStep ) ) {
      meta.setTargetOutputStep( meta.dataProviderStep );
    }

    ModelAnnotationGroup modelAnnotations = meta.getModelAnnotations();
    if ( modelAnnotations == null ) {
      modelAnnotations = new ModelAnnotationGroup();
      meta.setModelAnnotations( modelAnnotations );
    }

    meta.getModelAnnotations().setName( environmentSubstitute( meta.getModelAnnotationCategory() ) );
    modelAnnotations.addInjectedAnnotations( meta.createDimensionKeyAnnotations );
    modelAnnotations.addInjectedAnnotations( meta.createAttributeAnnotations );

    try {
      if ( !meta.createAttributeAnnotations.isEmpty() || !meta.createDimensionKeyAnnotations.isEmpty() ) {
        meta.saveToMetaStore( getMetaStore() );
      }
    } catch ( Exception e ) {
      logError( e.getMessage(), e );
    }

    return super.init( smi, sdi );
  }
}
