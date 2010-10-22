package org.hisp.dhis.sqlview;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

/**
 * @author Dang Duy Hieu
 * @version $Id SqlCodeMapUtil.java Aug 16, 2010$
 */
public class SqlViewJoinLib
{
    /**
     * COCN_JOIN_DEGSS is presenting the relationship between two 
     * resourcetables _CategoryOptionComboname and _DataElementGroupSetStructure 
     * through the INNER JOIN between CategoryCombos_OptionCombos and _DataElementGroupSetStructure
     */
    public static final String COCN_JOIN_DEGSS = 
        "JOIN CategoryCombos_OptionCombos AS ccoc ON _cocn.categoryOptionComboId = ccoc.categoryoptioncomboid \n"
        + "JOIN CategoryCombo AS cc ON ccoc.categorycomboid = cc.categorycomboid \n"
        + "JOIN DataElement AS de ON cc.categorycomboid = de.categorycomboid \n"
        + "JOIN _DataElementGroupSetStructure AS _degss ON de.dataelementid = _degss.dataelementid \n";

    /**
     * DEGSS_JOIN_COCN is presenting to the relationship between two
     * resourcetables _DataElementGroupSetStructure and _CategoryOptionComboname 
     * through the INNER JOIN between DataElement and _CategoryOptionComboname
     */
    public static final String DEGSS_JOIN_COCN = 
        "JOIN DataElement AS de ON _degss.dataelementid = de.dataelementid \n"
        + "JOIN CategoryCombo AS cc ON de.categorycomboid = cc.categorycomboid \n"
        + "JOIN CategoryCombos_OptionCombos AS ccoc ON cc.categorycomboid = ccoc.categorycomboid\n"
        + "JOIN _CategoryOptionComboname AS _cocn ON ccoc.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

    /**
     * OUS_JOIN_DEGSS is presenting to the relationship between two
     * resourcetables _OrgUnitStructure and _DataElementGroupSetStructure 
     * through the INNER JOIN between DataSetSource and _DataElementGroupSetStructure
     */
    public static final String OUS_JOIN_DEGSS = 
        "JOIN DataSetSource AS dss ON _ous.organisationunitid = dss.sourceid \n"
        + "JOIN DataSetMembers AS dsm ON dss.datasetid = dsm.datasetid \n"
        + "JOIN _DataElementGroupSetStructure AS _degss ON dsm.dataelementid = _degss.dataelementid ";

    /**
     * DEGSS_JOIN_OUS is presenting to the relationship between two
     * resourcetables _DataElementGroupSetStructure and _OrgUnitStructure 
     * through the INNER JOIN between DataSetMembers and _OrgUnitStructure
     */
    public static final String DEGSS_JOIN_OUS = 
        "JOIN DataSetMembers AS dsm ON _degss.dataelementid = dsm.dataelementid \n"
        + "JOIN DataSetSource AS dss ON dsm.datasetid = dss.datasetid \n"
        + "JOIN _OrgUnitStructure AS _ous ON dss.sourceid = _ous.organisationunitid \n";

    /**
     * DEGSS_JOIN_OUGSS is presenting to the relationship between two
     * resourcetables _DataElementGroupSetStructure and _OrgUnitGroupSetStructure 
     * through the INNER JOIN between DataSetMembers and _OrgUnitGroupSetStructure
     */
    public static final String DEGSS_JOIN_OUGSS = 
        "JOIN DataSetMembers AS dsm ON _degss.dataelementid = dsm.dataelementid \n"
        + "JOIN DataSetSource AS dss ON dsm.datasetid = dss.datasetid \n"
        + "JOIN _OrgUnitGroupSetStructure AS _ougss ON dss.sourceid = _ougss.organisationunitid \n";

    /**
     * DEGSS_JOIN_OUSTGSS is presenting to the relationship between two
     * resourcetables _DataElementGroupSetStructure and _OrganisationUnitGroupSetStructure 
     * through the INNER JOIN between DataSetMembers and _OrganisationUnitGroupSetStructure
     */
    public static final String DEGSS_JOIN_OUSTGSS = 
        "JOIN DataSetMembers AS dsm ON _degss.dataelementid = dsm.dataelementid \n"
        + "JOIN DataSetSource AS dss ON dsm.datasetid = dss.datasetid \n"
        + "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON dss.sourceid = _oustgss.organisationunitid \n";

}
