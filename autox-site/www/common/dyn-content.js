var currentFile = "";

$( document ).ready(function() {
	var href = location.href;
	var htmlIdx = href.toLowerCase().indexOf(".html");
	var slashIdx = href.lastIndexOf("/", htmlIdx);
	
	currentFile = href.substr(slashIdx + 1, htmlIdx);
	console.log("Got current file as: " + currentFile);
	
    var includes = $("div[include]");
	
	for(var i = 0; i < includes.length; i++)
	{
		var res = $(includes[i]).attr("include");
		
		$.get(res, $.proxy(function(data){
			$(this.elem).html(data);
			
			replaceDynAttr(this.elem);
		}, {"elem": includes[i]}));
	}
});

function replaceDynAttr(parentElem)
{
	var dynClsElems = $(parentElem).find("[dyn-class]");
	
	for(var i = 0; i < dynClsElems.length; i++)
	{
		var res = $(dynClsElems[i]).attr("dyn-class");
		$(dynClsElems[i]).attr("class", eval(res));
	}	
}

