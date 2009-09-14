$.fn.extend({  
    showAtCenter: function(){
		var div = document.getElementById(this.attr('id'));
		var width = div.style.width;
		var height = div.style.height;		
		var x = (document.documentElement.clientHeight / 2) - new Number(height.replace('px',''))/2;
		var y = (document.documentElement.clientWidth / 2) - new Number(width.replace('px',''))/2;	
		div.style.top = x +"px";
		div.style.left  = y +"px";	
		this.show();
	}  
});