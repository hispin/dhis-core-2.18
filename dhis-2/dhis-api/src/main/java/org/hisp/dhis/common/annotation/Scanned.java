package org.hisp.dhis.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A very simple annotation that is currently only used for marking identifiable collections
 * that should be part of the auto-populating reference scanning of the DefaultImporter.
 *
 * This is here to separate between collections that we want scanned (e.g. dataSet.indicators) and collections
 * that we don't want scanned (e.g. reportTable.allIndicators).
 *
 * TODO
 *  - refactor out scanner from DefaultImporter
 *  - add support for profiling (only scan for this profile, etc)
 *  - add support for annotation the setter or getter also
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Target( {ElementType.FIELD} )
@Retention( RetentionPolicy.RUNTIME )
public @interface Scanned
{
    String[] value() default {};
}
