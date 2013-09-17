package org.hisp.dhis.api.controller.event;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.events.enrollment.Enrollment;
import org.hisp.dhis.dxf2.events.enrollment.EnrollmentService;
import org.hisp.dhis.dxf2.events.enrollment.Enrollments;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping(value = EnrollmentController.RESOURCE_PATH)
public class EnrollmentController
{
    public static final String RESOURCE_PATH = "/enrollments";

    @Autowired
    private EnrollmentService enrollmentService;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getEnrollments(
        @RequestParam(value = "orgUnit", required = false) String orgUnitUid,
        @RequestParam(value = "program", required = false) String programUid,
        @RequestParam(value = "person", required = false) String personUid,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request )
    {
        WebOptions options = new WebOptions( parameters );
        Enrollments enrollments;

        enrollments = enrollmentService.getEnrollments();

        model.addAttribute( "model", enrollments );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return "enrollments";
    }

    @RequestMapping( value = "/{id}", method = RequestMethod.GET )
    public String getEnrollment( @PathVariable String id, @RequestParam Map<String, String> parameters, Model model ) throws NotFoundException
    {
        WebOptions options = new WebOptions( parameters );
        Enrollment enrollment = getEnrollment( id );

        model.addAttribute( "model", enrollment );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return "person";
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private Enrollment getEnrollment( String id ) throws NotFoundException
    {
        Enrollment enrollment = enrollmentService.getEnrollment( id );

        if ( enrollment == null )
        {
            throw new NotFoundException( "Enrollment", id );
        }

        return enrollment;
    }

    private String getResourcePath( HttpServletRequest request, ImportSummary importSummary )
    {
        return ContextUtils.getContextPath( request ) + "/api/" + "enrollments" + "/" + importSummary.getReference();
    }
}
