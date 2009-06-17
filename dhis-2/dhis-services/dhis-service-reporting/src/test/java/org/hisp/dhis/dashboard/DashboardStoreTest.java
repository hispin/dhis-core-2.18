package org.hisp.dhis.dashboard;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.hibernate.NonUniqueObjectException;
import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.olap.OlapURL;
import org.hisp.dhis.olap.OlapURLService;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportStore;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;
import org.junit.Test;
import org.springframework.test.annotation.ExpectedException;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DashboardStoreTest
    extends DhisSpringTest
{
    private UserStore userStore;
    
    private ReportStore reportStore;
    
    private OlapURLService olapURLService;
    
    private DashboardStore dashboardStore;
    
    private User userA;
    
    private Report reportA;
    
    private OlapURL urlA;
    
    private DashboardContent contentA;
    private DashboardContent contentB;
    
    @Override
    public void setUpTest()
    {
        userStore = (UserStore) getBean( UserStore.ID );
        
        reportStore = (ReportStore) getBean( ReportStore.ID );

        olapURLService = (OlapURLService) getBean( OlapURLService.ID );
        
        dashboardStore = (DashboardStore) getBean( DashboardStore.ID );
        
        userA = createUser( 'A' );
        userStore.addUser( userA );
        
        reportA = new Report( "ReportA", "DesignA", null );
        reportStore.saveReport( reportA );
        
        urlA = createOlapURL( 'A' );
        olapURLService.saveOlapURL( urlA );
        
        contentA = new DashboardContent();
        contentB = new DashboardContent();
    }
    
    @Test
    public void saveGet()
    {
        contentA.setUser( userA );
        contentA.getReports().add( reportA );
        contentA.getOlapUrls().add( urlA );
        
        dashboardStore.saveDashboardContent( contentA );
        
        assertEquals( contentA, dashboardStore.getDashboardContent( userA ) );
        assertEquals( userA, dashboardStore.getDashboardContent( userA ).getUser() );
        assertEquals( reportA, dashboardStore.getDashboardContent( userA ).getReports().iterator().next() );
        assertEquals( urlA, dashboardStore.getDashboardContent( userA ).getOlapUrls().iterator().next() );
    }
    
    @Test
    @ExpectedException( NonUniqueObjectException.class )
    public void duplicate()
    {
        contentA.setUser( userA );
        contentB.setUser( userA );
        
        dashboardStore.saveDashboardContent( contentA );
        dashboardStore.saveDashboardContent( contentB );        
    }
    
    @Test
    public void saveOrUpdate()
    {
        contentA.setUser( userA );
        contentA.getReports().add( reportA );

        dashboardStore.saveDashboardContent( contentA );
        
        assertEquals( contentA, dashboardStore.getDashboardContent( userA ) );
        assertEquals( reportA, dashboardStore.getDashboardContent( userA ).getReports().iterator().next() );
        
        contentA.getOlapUrls().add( urlA );

        dashboardStore.saveDashboardContent( contentA );

        assertEquals( contentA, dashboardStore.getDashboardContent( userA ) );
        assertEquals( urlA, dashboardStore.getDashboardContent( userA ).getOlapUrls().iterator().next() );
    }
    
    @Test
    public void delete()
    {
        contentA.setUser( userA );
        contentA.getReports().add( reportA );
        
        dashboardStore.saveDashboardContent( contentA );
        
        assertNotNull( dashboardStore.getDashboardContent( userA ) );
        
        dashboardStore.deleteDashboardContent( contentA );
        
        assertNull( dashboardStore.getDashboardContent( userA ) );
    }
}
