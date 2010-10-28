package org.hisp.dhis.ll.action.employee;

import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeService;

import com.opensymphony.xwork2.Action;

public class GetEmployeeAction
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private EmployeeService employeeService;

	public void setEmployeeService(EmployeeService employeeService)
	{
		this.employeeService = employeeService;
	}
	
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

	private String pdsCode;

	public void setPdsCode(String pdsCode) 
	{
		this.pdsCode = pdsCode;
	}
	
	private Employee employee;

	public Employee getEmployee()
	{
		return employee;
	}
	
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
	{
		employee = employeeService.getEmployeeByPDSCode( pdsCode );
		
		return SUCCESS;
	}

}
