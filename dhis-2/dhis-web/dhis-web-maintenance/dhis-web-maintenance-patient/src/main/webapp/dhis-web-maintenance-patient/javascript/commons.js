function dobTypeOnChange(){

	var type = byId('dobType').value;
	
	if(type == 'V' || type == 'D'){
		showById('birthdaySpan');
		hideById('ageSpan');
	}else if(type == 'A'){
		hideById('birthdaySpan');
		showById('ageSpan');
	}else {
		hideById('birthdaySpan');
		hideById('ageSpan');
	}
}