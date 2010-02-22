package org.hisp.dhis.dataentryform;



import java.util.Collection;
import java.util.List;


/**
 * 
 * @author Viet
 *
 */

public interface DataEntryFormAssociationService
{
    String ID = DataEntryFormAssociationService.class.getName();

    // -------------------------------------------------------------------------
    // DataEntryFormAssociation
    // -------------------------------------------------------------------------

    /**
     * Adds a DataEntryFormAssociation.
     * 
     * @param dataEntryForm The DataEntryForm to add.
     * @return The generated unique identifier for this DataEntryForm.
     */
    void addDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation  );

    /**
     * Updates a DataEntryFormAssociation.
     * 
     * @param dataEntryForm The DataEntryForm to update.
     */
    void updateDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation );

    /**
     * Deletes a DataEntryFormAssociation.
     * 
     * @param dataEntryForm The DataEntryForm to delete.
     */
    void deleteDataEntryFormAssociation(  DataEntryFormAssociation dataEntryFormAssociation  );

    /**
     *  Get a DataEntryFormAssociation
     * @param associationTableName : table name of the association ( dataset, programstage..)
     * @param associationId : the id of the element in the association table
     * @return The DataEntryFormAssociation with the given associationId and associationTablename or null if it does not exist
     */
    DataEntryFormAssociation getDataEntryFormAssociation( String associationTableName, int associationId   );

    
    /**
     * Get DataEntryFormAssociation by datasetId
     * @param dataSetId
     * @return The DataEntryFormAssociation with the given dataset id or null if it does not exist
     */
    DataEntryFormAssociation getDataEntryFormAssociationByDataSet( int dataSetId );
   
   /**
    * Get DataEntryFormAssociation by dataEntryFormId 
    * @param dataEntryFormId
    * @return The DataEntryFormAssociation with the given dataEntryFormId or null if it does not exist
    */
    DataEntryFormAssociation getDataEntryFormAssociationByDataEntryForm( DataEntryForm dataEntryForm );
    
    /**
     * Get DataEntryFormAssociation by programStageId
     * @param programStageId
     * @return The DataEntryFormAssociation with the given programStageId or null if it does not exist
     */
    DataEntryFormAssociation getDataEntryFormAssociationByProgramStage( int programStageId );

    /**
     * Get all DataEntryFormAssociations.
     * 
     * @return A collection containing all DataEntryFormAssociations.
     */
    Collection<DataEntryFormAssociation> getAllDataEntryFormAssociations();
    
    
    /**
     * List distinct DataEntryForms by list associationIds .
     * 
     * @return A collection containing all DataEntryForms corresponds to the given list associationIds.
     */
    public Collection<DataEntryForm> listDisctinctDataEntryFormByAssociationIds(String associationName, List<Integer> associationIds );
}
