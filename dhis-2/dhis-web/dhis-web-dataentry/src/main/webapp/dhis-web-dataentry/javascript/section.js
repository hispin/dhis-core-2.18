function openCloseSection( sectionId )
{
	var divSection = document.getElementById( sectionId );	
	
	if( divSection.style.display == 'none' )
	{		
		//window.location = "sectionform.action?selectedSectionId=" + sectionId;
		divSection.style.display = ('block');
	}
	else
	{		
		//window.location = "sectionform.action?selectedSectionId=" + '';
		divSection.style.display = ('none');
	}
}