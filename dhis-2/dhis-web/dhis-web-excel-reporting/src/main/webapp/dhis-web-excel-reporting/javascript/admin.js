
function saveReportUserRoles()
{
	selectAllById( "userRoleId" );
	document.getElementById( "adminForm" ).submit();
}

function selectReportUserRoles()
{
	window.location.href = "getAdminOptions.action?reportId=" + getListValue( "reportId" );
}