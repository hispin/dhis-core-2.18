var menuTimeout = 500;
var closeTimer = null;
var dropDownId = null;

function showDropDown( id ){
    cancelHideDropDownTimeout();
    var newDropDownId = "#" + id;
  
    if ( dropDownId != newDropDownId )
    {
        if ( dropDownId ){
            hideDropDown();
        }

        dropDownId = newDropDownId;
        $( dropDownId ).show();
    }
}

function hideDropDown(){
    $( dropDownId ).hide();
    dropDownId = null;
}

function hideDropDownTimeout(){
    closeTimer = window.setTimeout( hideDropDown, menuTimeout );
}

function cancelHideDropDownTimeout(){
    if ( closeTimer ){
        window.clearTimeout( closeTimer );
        closeTimer = null;
    }
}
