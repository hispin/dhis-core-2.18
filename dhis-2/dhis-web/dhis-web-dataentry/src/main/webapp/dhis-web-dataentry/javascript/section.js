function openCloseSection( sectionId )
{
	var divSection = document.getElementById( sectionId );
	var sectionLabel = document.getElementById( sectionId + ":name" );	
	
	if( divSection.style.display == 'none' )
	{			
		divSection.style.display = ('block');
		sectionLabel.style.textAlign = 'center';
	}
	else
	{			
		divSection.style.display = ('none');
		sectionLabel.style.textAlign = 'left';
	}
}