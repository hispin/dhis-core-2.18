package org.hisp.dhis.ll.action.employee;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;

import com.opensymphony.xwork2.Action;

public class GetEmployeeListAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private List<Employee> employeeList;
    
    public List<Employee> getEmployeeList()
    {
        return employeeList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
    	employeeList = new ArrayList<Employee>( employeeService.getAllEmployee() );
    	
        return SUCCESS;
    }
}
