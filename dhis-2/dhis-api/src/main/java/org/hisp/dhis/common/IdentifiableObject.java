package org.hisp.dhis.common;

public interface IdentifiableObject
    extends ImportableObject
{

    public abstract int getId();

    public abstract String getUuid();

    public abstract String getName();

}