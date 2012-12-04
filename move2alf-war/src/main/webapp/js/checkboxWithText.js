function checkboxChanged(name){
	var item = $("#"+name);
	var textBox = $("#"+name+"Text");
	if(item.attr("checked")){
		textBox.removeAttr("disabled");
		textBox.select();
	}else{
		textBox.attr("disabled", "disabled");
	}
}