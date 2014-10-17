package org.hisp.dhis.dataelement;

import java.util.List;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;

public interface CategoryOptionGroupStore
    extends GenericIdentifiableObjectStore<CategoryOptionGroup>
{
    List<CategoryOptionGroup> getCategoryOptionGroups( CategoryOptionGroupSet groupSet );
}
