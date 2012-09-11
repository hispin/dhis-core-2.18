function scheduleTasks()
{
	$.post( 'scheduleTasks.action',{
		execute:false,
		schedule: true,
		gateWayId: getFieldValue("gatewayId"),
		timeSendingMessage: getFieldValue("timeSendingMessage")
	}, function( json ){
		setMessage(i18n_scheduling_is + " " + json.scheduleTasks.status);
		if( json.scheduleTasks.running ){
			setFieldValue('scheduledBtn', i18n_stop );
		}
		else{
			setFieldValue('scheduledBtn', i18n_start );
		}
	});
}

function executeTasks()
{
	$.post( 'scheduleTasks.action',{
		execute:true,
		schedule: false,
		gateWayId: getFieldValue("gatewayId"),
		timeSendingMessage: getFieldValue("timeSendingMessage")
	});
}
