
$( document ).ready( function() 
{
	if ( $( '#isRunning' ).val() == 'true' )
	{
		$( '.scheduling' ).attr( 'disabled', 'disabled' );
	}
	else
	{
		$( '.scheduling' ).removeAttr( 'disabled' );
	}
} );

function submitSchedulingForm()
{
	$( '.scheduling' ).removeAttr( 'disabled' );
	$( '#schedulingForm' ).submit();
}

function executeTasks()
{
	var ok = confirm( i18n_execute_tasks_confirmation );
	
	if ( ok )
	{
		$.get( 'scheduleTasks.action?execute=true' );
	}
}