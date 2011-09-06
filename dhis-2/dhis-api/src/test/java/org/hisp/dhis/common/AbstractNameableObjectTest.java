package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import org.hisp.dhis.dataelement.DataElement;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author bobj
 * @version created 06-Sep-2011
 */
public class AbstractNameableObjectTest {

    @Test
    public void testCodeMap()
    {
        List<DataElement> dataElements = new LinkedList<DataElement>();
        for (int i=0; i<5; ++i)
        {
            DataElement de = new DataElement();
            de.setId( i);
            de.setCode("code"+i);
            dataElements.add( de );
        }

        Map<String,Integer> codeMap = DataElement.getCodeMap( dataElements );
        int id = codeMap.get( "code2");
        assertEquals(2, id);

        DataElement dup = new DataElement();
        dup.setId(6);
        dup.setCode("code2");
        dataElements.add( dup );

        codeMap = DataElement.getCodeMap( dataElements );
        Integer i = codeMap.get( "code2");
        assertNull(i);

    }

}
