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
		setMessage(i18n_scheduling_is + " " + status);
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
	$.post( 'executeSendMessage.action',{
		execute:true,
		schedule: false,
		gateWayId: getFieldValue("gatewayId"),
		timeSendingMessage: getFieldValue("timeSendingMessage")
	}, function( json ){
		setMessage(i18n_execute_success);
	});
}
