package org.hisp.dhis.reporting.tallysheet.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.tallysheet.TallySheet;
import org.hisp.dhis.tallysheet.TallySheetService;
import org.hisp.dhis.tallysheet.TallySheetTuple;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Haavard Tegelsrud, Oddmund Stroemme, Joergen Froeysadal, Ruben
 *         Wangberg
 * @version $Id$
 */
public class ViewTallySheetAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String tallySheetName;

    public void setTallySheetName( String tallySheetName )
    {
        this.tallySheetName = tallySheetName;
    }

    private boolean largeFormat;

    public void setLargeFormat( boolean largeFormat )
    {
        this.largeFormat = largeFormat;
    }

    private boolean displayFacilityName;

    public void setDisplayFacilityName( boolean displayFacilityName )
    {
        this.displayFacilityName = displayFacilityName;
    }

    private Integer selectedDataSetId;

    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }

    private double factor = 1;

    public double getFactor()
    {
        return factor;
    }

    public void setFactor( double factor )
    {
        this.factor = factor;
    }

    private boolean[] checked;

    public void setChecked( boolean[] checked )
    {
        this.checked = checked;
    }

    private boolean recalculate = false;

    public void setRecalculate( boolean recalculate )
    {
        this.recalculate = recalculate;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private TallySheet tallySheet;

    public TallySheet getTallySheet()
    {
        return tallySheet;
    }

    private List<TallySheetTuple> tallySheetTuples;

    public List<TallySheetTuple> getTallySheetTuples()
    {
        return tallySheetTuples;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TallySheetService tallySheetService;

    public void setTallySheetService( TallySheetService tallySheetService )
    {
        this.tallySheetService = tallySheetService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    private ArrayList<DataElement> dataElements;

    private OrganisationUnit organisationUnit;

    private DataSet selectedDataSet;

    public String execute()
        throws Exception
    {
        if ( recalculate )
        {
            tallySheet = (TallySheet) ActionContext.getContext().getSession().get( TallySheet.KEY_TALLY_SHEET );

            if ( tallySheet != null )
            {
                tallySheetTuples = tallySheet.getTallySheetTuples();

                for ( int i = 0; i < checked.length; i++ )
                {
                    TallySheetTuple tallySheetTuple = tallySheetTuples.get( i );
                    tallySheetTuple.setChecked( checked[i] );
                    tallySheetTuple.recalculateRows( factor );
                }
            }

            return SUCCESS;
        }

        organisationUnit = selectionTreeManager.getSelectedOrganisationUnit();
        selectedDataSet = dataSetService.getDataSet( selectedDataSetId );
        dataElements = new ArrayList<DataElement>( dataSetService.getDataElements( selectedDataSet ) );
        Collections.sort( dataElements, IdentifiableObjectNameComparator.INSTANCE );

        tallySheet = tallySheetService.createTallySheet( organisationUnit, dataElements, largeFormat,
            displayFacilityName, selectedDataSet, tallySheetName );

        tallySheetTuples = tallySheet.getTallySheetTuples();

        ActionContext.getContext().getSession().put( TallySheet.KEY_TALLY_SHEET, tallySheet );

        return SUCCESS;
    }
}
