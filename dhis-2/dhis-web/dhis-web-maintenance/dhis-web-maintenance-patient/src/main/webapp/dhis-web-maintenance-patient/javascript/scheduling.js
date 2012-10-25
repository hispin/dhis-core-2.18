// -----------------------------------------------------------------------
// Schedule Messages
// -----------------------------------------------------------------------

function scheduleTasks()
{
	$.post( 'scheduleTasks.action',{
		execute:false,
		schedule: true,
		gateWayId: getFieldValue("gatewayId"),
		timeSendingMessage: getFieldValue("timeSendingMessage")
	}, function( json ){
		var status = json.scheduleTasks.status;
		if( status=='not_started' ){
			status = i18n_not_started;
		}
		setInnerHTML('info', i18n_scheduling_is + " " + status);
		if( json.scheduleTasks.running=="true" ){
			setFieldValue('scheduledBtn', i18n_stop);
		}
		else{
			setFieldValue('scheduledBtn', i18n_start);
		}
	});
}

function executeTasks()
{
	var ok = confirm( i18n_execute_tasks_confirmation );
	setWaitMessage( i18n_executing );	
	if ( ok )
	{		
		$.post( 'executeSendMessage.action',{}
		, function( json ){
			setMessage(i18n_execute_success);
		});
	}
}

// -----------------------------------------------------------------------
// Schedule Aggregate Query Builder
// -----------------------------------------------------------------------

function schedulingAggCondTasks()
{
	$.post( 'scheduleCaseAggTasks.action',{
		execute:false,
		orgUnitGroupSetAggLevel:getFieldValue("orgUnitGroupSetAggLevel"),
		aggQueryBuilderStrategy:getFieldValue("aggQueryBuilderStrategy")
	}, function( json ){
		var status = json.scheduleTasks.status;
		if( status=='not_started' ){
			status = i18n_not_started;
		}
		setInnerHTML('info', i18n_scheduling_is + " " + status);
		if( json.scheduleTasks.running=="true" ){
			setFieldValue('scheduledBtn', i18n_stop);
		}
		else{
			setFieldValue('scheduledBtn', i18n_start);
		}
	});
}

function executeAggCondTasks()
{
	var ok = confirm( i18n_execute_tasks_confirmation );
	setWaitMessage( i18n_executing );	
	if ( ok )
	{
		$.post( 'scheduleCaseAggTasks.action',{
			execute:true,
			orgUnitGroupSetAggLevel:getFieldValue("orgUnitGroupSetAggLevel"),
			aggQueryBuilderStrategy:getFieldValue("aggQueryBuilderStrategy")
		},function( json ){
			setMessage(i18n_execute_success);
		});
	}
}