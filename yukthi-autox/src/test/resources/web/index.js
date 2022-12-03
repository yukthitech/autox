
function onLoad()
{
	var windowId = localStorage.getItem("windowId");
	
	if(!windowId)
	{
		windowId = 100;
	}
	else
	{
		windowId = parseInt("" + windowId) + 1;
	}
	
	localStorage.setItem("windowId", windowId);
	
	var fld = document.getElementById("windowIdFld");
	fld.value = "" + windowId;
}

function changeStatusTo(status)
{
	console.log("Changing status to: ", status);
	
	var fld = document.getElementById("status");
	fld.value = status;
	
	var div = document.getElementById("testLayer");
	div.style.visibility = 'visible';
}

function setHiddenValue()
{
	console.log("Setting hidden field value...");
	
	var fld = document.getElementById("status");
	var hiddenFld = document.getElementById("hiddenFld");
	
	fld.value = hiddenFld.value;
}

var clickCount = 0;

function onClickOfClickButton1()
{
	console.log("Buttong is clicked. Current click count: ", clickCount);
	if(clickCount < 2)
	{
		clickCount ++;
		return;
	}
	
	setTimeout(function(){
		var div = document.getElementById("clickButton1Res");
		div.style.visibility = 'visible';
	}, 1600);
}

function showConfirm()
{
	var div = document.getElementById("alertRes");
	div.innerHTML = "Clicking...";
	
	var res = confirm("Choose your button...");
	
	if(res)
	{
		div.innerHTML = "Okay";
	}
	else
	{
		div.innerHTML = "Cancel";
	}
}

function showPrompt()
{
	var div = document.getElementById("alertRes");
	div.innerHTML = "Promting...";
	
	var res = prompt("Provide a value...");
	
	if(res)
	{
		div.innerHTML = res;
	}
	else
	{
		div.innerHTML = "NoVal";
	}
}
