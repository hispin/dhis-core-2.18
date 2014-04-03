package org.hisp.dhis.dataapproval;

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

import java.util.List;

/**
 * @author Jim Grace
 */
public interface DataApprovalLevelService
{
    String ID = DataApprovalLevelService.class.getName();

    /**
     * Gets a list of all data approval levels.
     *
     * @return List of all data approval levels, ordered from 1 to n.
     */
    List<DataApprovalLevel> getAllDataApprovalLevels();

    /**
     * Gets a list of the data approval levels for which the user has
     * permission to approve, un.
     *
     * @return List of all data approval levels, ordered from 1 to n.
     */
    List<DataApprovalLevel> getUserDataApprovalLevels();

    /**
     * Gets data approval levels by org unit level.
     * 
     * @param orgUnitLevel the org unit level.
     * @return a list of data approval levels.
     */
    List<DataApprovalLevel> getDataApprovalLevelsByOrgUnitLevel( int orgUnitLevel );
    
    /**
     * Tells whether a level can move down in the list (can switch places with
     * the level below.)
     *
     * @param level the level to test.
     * @return true if the level can move down, otherwise false.
     */
    boolean canDataApprovalLevelMoveDown( int level );

    /**
     * Tells whether a level can move up in the list (can switch places with
     * the level above.)
     *
     * @param level the level to test.
     * @return true if the level can move up, otherwise false.
     */
    boolean canDataApprovalLevelMoveUp( int level );

    /**
     * Moves a data approval level down in the list (switches places with the
     * level below).
     *
     * @param level the level to move down.
     */
    void moveDataApprovalLevelDown( int level );

    /**
     * Moves a data approval level up in the list (switches places with the
     * level above).
     *
     * @param level the level to move up.
     */
    void moveDataApprovalLevelUp( int level );

    /**
     * Determines whether level already exists with the same organisation
     * unit level and category option group set (but not necessarily the
     * same level number.)
     *
     * @param level Data approval level to test for existence.
     * @return true if it exists, otherwise false.
     */
    public boolean dataApprovalLevelExists ( DataApprovalLevel level );

    /**
     * Adds a new data approval level. Adds the new level at the highest
     * position possible (to facilitate the use case where users add the
     * approval levels from low to high.)
     *
     * @param newLevel the new level to add.
     * @return true if level was added, false if not well formed or duplicate.
     */
    boolean addDataApprovalLevel( DataApprovalLevel newLevel );

    /**
     * Removes a data approval level.
     *
     * @param index index of the level to move up.
     */
    void deleteDataApprovalLevel( int index );

    /**
     * Gets the lowest data approval level that the current user may view.
     * (Note that the "lowest" approval level means the "highest" approval
     * level number.)
     * <p>
     * Look at all the levels, starting from the lowest level (highest level
     * number.) If the level has no category option group, or if it has a
     * category option group where the user can see at least one category
     * option within the group, then the user may see the level and all
     * higher levels.
     *
     * @return level number of the lowest level the user can view.
     */
    int getLowestUserDataApprovalLevel();
}
