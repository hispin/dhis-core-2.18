package org.hisp.dhis.dataanalysis;

public interface DataAnalysisServiceProvider
{
    DataAnalysisService provide( String key );
}
