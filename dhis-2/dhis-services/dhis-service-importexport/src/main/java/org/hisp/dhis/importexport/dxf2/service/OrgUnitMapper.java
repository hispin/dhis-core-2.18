package org.hisp.dhis.importexport.dxf2.service;

import java.util.Set;

import org.hisp.dhis.importexport.dxf2.model.OrgUnit;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class OrgUnitMapper
{
    private LinkBuilder linkBuilder = new LinkBuilderImpl();

    public OrgUnit get( OrganisationUnit unit )
    {
        OrgUnit dxfUnit = new OrgUnit();

        dxfUnit.setName( unit.getName() );
        dxfUnit.setId( unit.getUid() );

        OrganisationUnit parent = unit.getParent();
        if ( parent != null )
            dxfUnit.setParent( linkBuilder.get( parent ) );

        Set<OrganisationUnit> children = unit.getChildren();
        if ( children != null && !children.isEmpty() )
            dxfUnit.setChildren( linkBuilder.getLinks( children ) );

        dxfUnit.setDataSets( linkBuilder.getLinks( unit.getDataSets() ) );

        return dxfUnit;
    }
}
