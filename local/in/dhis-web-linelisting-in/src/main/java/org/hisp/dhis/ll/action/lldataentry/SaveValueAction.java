package org.hisp.dhis.ll.action.lldataentry;

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
import org.apache.struts2.ServletActionContext;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.period.PeriodService;

public class SaveValueAction
        implements Action {

    private static final Log LOG = LogFactory.getLog(SaveValueAction.class);
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private CurrentUserService currentUserService;

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }
    private LineListService lineListService;

    public void setLineListService(LineListService lineListService) {
        this.lineListService = lineListService;
    }
    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager(SelectedStateManager selectedStateManager) {
        this.selectedStateManager = selectedStateManager;
    }
    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface(DataBaseManagerInterface dataBaseManagerInterface) {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }
    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    private String value;

    public void setValue(String value) {
        this.value = value;
    }
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }
    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }
    private String storedBy;

    public String getStoredBy() {
        return storedBy;
    }
    private HttpSession session;

    public HttpSession getSession() {
        return session;
    }
    private LineListDataValue lineListDataValue;

    public LineListDataValue getLineListDataValue() {
        return lineListDataValue;
    }

    public void setLineListDataValue(LineListDataValue lineListDataValue) {
        this.lineListDataValue = lineListDataValue;
    }
    private Integer delRecordNo;

    public void setDelRecordNo(Integer delRecordNo) {
        this.delRecordNo = delRecordNo;
    }
    private LineListGroup lineListGroup;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() {

        //Map<String, String> llElementValuesMap = new HashMap<String, String>();
        List<LineListElement> lineListElements;

        List<LineListDataValue> llDataValuesList = new ArrayList<LineListDataValue>();
        List<LineListDataValue> llDataValuesUpdatedList = new ArrayList<LineListDataValue>();

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        Period period = selectedStateManager.getSelectedPeriod();

        period = periodService.getPeriod(period.getStartDate(), period.getEndDate(), period.getPeriodType());
        lineListGroup = selectedStateManager.getSelectedLineListGroup();

        //String parts[] =  dataElementId.split( ":" );

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        session = req.getSession();


        int totalRecords = Integer.parseInt(req.getParameter("totalRecords"));
        int recordsFromDb = Integer.parseInt(req.getParameter("recordsFromDb"));


        lineListElements = new ArrayList<LineListElement>(lineListGroup.getLineListElements());

        String recordNumbersList = req.getParameter("recordNumbersList");
        System.out.println(recordNumbersList);
        String[] recordNos = recordNumbersList.split(":");
        for (int j = 0; j < recordNos.length; j++) {
            String recordNo = recordNos[j];
            System.out.println("recordNo = " + recordNo);
            if (!(recordNo.equals(""))) {
                String valueChangedName = "changedValue:" + recordNo;
                String valueChanged = req.getParameter(valueChangedName);
                if (!(valueChanged.equalsIgnoreCase(""))) {
                    System.out.println("valueChanged = "+ valueChanged);
                    String[] elementNames = valueChanged.split(" ");
                    LineListDataValue lineListDataValue = new LineListDataValue();
                    Map<String, String> llElementValuesMap = new HashMap<String, String>();
                    for (int e = 0; e < elementNames.length; e++) {
                        String name = elementNames[e] + ":" + recordNo;
                        String value = req.getParameter(name);
                        //System.out.println("name = " + name + " value  = " + value);
                        if (value != null && value.trim().equals("")) {
                            value = "";
                        }
                        if(value != "")
                            llElementValuesMap.put(elementNames[e], value);
                    }


                    //add map in linelist data value
                    lineListDataValue.setLineListValues(llElementValuesMap);

                    //add recordNumber to pass to the update query
                    lineListDataValue.setRecordNumber(Integer.parseInt(recordNo));

                    //add stored by, timestamp in linelist data value

                    storedBy = currentUserService.getCurrentUsername();


                    if (storedBy == null) {
                        storedBy = "[unknown]";
                    }

                    lineListDataValue.setStoredBy(storedBy);
                    lineListDataValue.setTimestamp(new Date());



                    llDataValuesUpdatedList.add(lineListDataValue);

                }

            }
        }


        while (recordsFromDb < totalRecords) {
            System.out.println("recordsFromDb = " + recordsFromDb);
            System.out.println("totalRecords = " + totalRecords);
            recordsFromDb++;
            LineListDataValue lineListDataValue = new LineListDataValue();
            Map<String, String> llElementValuesMap = new HashMap<String, String>();
            for (LineListElement element : lineListElements) {

                String name = element.getShortName() + ":" + recordsFromDb;
                String value = req.getParameter(name);
                System.out.println("name = " + name + " value  = " + req.getParameter(name));

                if (value != null && value.trim().equals("")) {
                    value = "";
                }
                if(!(value.equals("")))
                    llElementValuesMap.put(element.getShortName(), value);
            }

            //add map in linelist data value
            lineListDataValue.setLineListValues(llElementValuesMap);

            //add period source, stored by, timestamp in linelist data value
            lineListDataValue.setPeriod(period);
            lineListDataValue.setSource(organisationUnit);

            storedBy = currentUserService.getCurrentUsername();


            if (storedBy == null) {
                storedBy = "[unknown]";
            }

            lineListDataValue.setStoredBy(storedBy);
            lineListDataValue.setTimestamp(new Date());



            llDataValuesList.add(lineListDataValue);
        }



        if (llDataValuesList.isEmpty() || llDataValuesList == null) {

            //deleteLLValue();

        } else {

            dataBaseManagerInterface.insertLLValueIntoDb(llDataValuesList, lineListGroup.getShortName());

        }

        if (llDataValuesUpdatedList.isEmpty() || llDataValuesUpdatedList == null) {


        } else {
            dataBaseManagerInterface.updateLLValue(llDataValuesUpdatedList, lineListGroup.getShortName());

        }

        deleteLLValue();

        return SUCCESS;

    }

    public void deleteLLValue() {
        if (delRecordNo != null) {
            //System.out.println("delRecordNo = " + delRecordNo);
            dataBaseManagerInterface.removeLLRecord(delRecordNo, lineListGroup.getShortName());
        } else {
            System.out.println("delRecordNo is null");
        }

    }
}


