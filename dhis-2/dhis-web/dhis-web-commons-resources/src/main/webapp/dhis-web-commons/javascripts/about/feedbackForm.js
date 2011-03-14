function validateForm()
{
	var userMessage = document.getElementById("userMessage");

	if (userMessage.value.length == 0) {
		setMessage(i18n_enter_message);
	} else {
		document.getElementById("messageForm").submit();
	}
}
