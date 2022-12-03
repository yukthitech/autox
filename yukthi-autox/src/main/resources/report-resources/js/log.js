$.application.controller('testLogAppController', function($scope){
	
	$scope.filterLevel = "LEVEL";
	$scope.levels = ["ALL", "DEBUG", "SUMMARY", "WARN", "ERROR", "INFO", "TRACE"];
	
	/**
	 * Gets invoked on init.
	 */
	$scope.fetchJsonObjects = function(){
		
		var urlStr = (window.location).search;
		
		urlStr = "./logs/" + urlStr.slice(1, urlStr.length) + ".js";
		$scope.importScript(urlStr, $scope.displayLogs);
	};
	
	/**
	 * Import required js file.
	 */
	$scope.importScript = function (sSrc, fOnload){
		
		  var oScript = document.createElement("script");
		  oScript.type = "text\/javascript";
		  
		  if(fOnload) 
		  { 
			 oScript.onload = fOnload; 
		  }
		  
		 var logScript = document.getElementById('logScript');
			
		 logScript.parentNode.insertBefore(oScript, logScript);
		 oScript.src = sSrc;
	};
	
	/**
	 * Display all the messages
	 */
	$scope.displayLogs = function(){
		
		//logData global variable present in included js file.
		$scope.testLogs = {};
		$scope.messages = [];
		var lineNo = 1;
		
		for(var i = 0 ; i < logs.length ; i++)
		{
			if(logs[i].type == 'Header')
			{
				$scope.testLogs.name = logs[i].executorName;
				$scope.testLogs.description = logs[i].executorDescription;
				$scope.testLogs.startTimeStr = logs[i].startTimeStr;
				continue;
			}
			
			if(logs[i].type == 'Footer')
			{
				$scope.testLogs.status = logs[i].status;
				$scope.testLogs.endTimeStr = logs[i].endTimeStr;
				$scope.testLogs.timeTaken = logs[i].timeTaken;
				continue;
			}

			$scope.messages.push(logs[i]);
			logs[i].display = true;
			logs[i].lineNo = lineNo;
			
			lineNo++;
		}
		
		try
		{
			$scope.$apply();
		}catch(ex)
		{}
		
		$scope.highlightCode();
	};

	/**
	 * Wraps the <code> elements with <pre> tag and highlights them.
	 */
	$scope.highlightCode = function() {
		$("pre code").unwrap();
		$("code").wrap('<pre style="padding: 4px; margin: 0.5em; display: inline-block;"></pre>');
		
		$('pre code').each(function(i, block) {
			hljs.highlightBlock(block);
		});	
	};
	
	/**
	 * Hihhlights all <code> elements and this function is made for use with filters.
	 */
	$scope.highlightFilter = function() {
		
		$scope.highlightCode();
		
		return function() {
			return true;
		};
	};
	
	/**
	 * Gets invoked on change of level.
	 */
	$scope.onChangeLevel = function(data){
		$scope.filterLevel = data;
	};
	
	/**
	 * Filter function to filter the logs. 
	 */
	$scope.filterLogs = function() {
		var filterFunc = function(item) {
			
			if($scope.filterLevel != "ALL" && $scope.filterLevel != "LEVEL")
			{
				if($scope.filterLevel != item.logLevel)
				{
					$scope.highlightCode();
					return false;
				}
			}
			
			var res = true;
			
			if($scope.searchByMessage && $scope.searchByMessage.length > 0)
			{
				var searchByMessage = $scope.searchByMessage.toLowerCase().trim();
				var filters = searchByMessage.split(/\s*\|\s*/);
				res = false;

				for(var i = 0; i < filters.length; i++)
				{
					if(filters[i].length <= 0)
					{
						continue;
					}
					
					if(item.message && item.message.toLowerCase().indexOf(filters[i]) >= 0)
					{
						res = true;
						break;
					}
				}
			}
			
			//$scope.highlightCode();
			return res;
		};
		
		return filterFunc;
	};
	
	/**
	 * Replace the message.
	 */
	$scope.replaceMessage = function(message){
		
		if(!message)
		{
			return "";
		}
		
		var result = message.replace(/\n/g, "<BR/>");
		result = result.replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
		
		return result;
		
	};
	
	$scope.getLogClass = function(level) {
		if(level == "ERROR")
		{
			return "errorVal";
		}

		if(level == "WARN")
		{
			return "warnVal";
		}
		
		if(level == "INFO")
		{
			return "infoVal";
		}

		return "defaultVal";
	};
	
	/**
	 * Goto
	 */
	goToLine = $.proxy(function(event){
		var line = $(event.target).attr("data-line-no"); 
		
		this.$scope.searchByMessage = "";
		this.$scope.filterLevel = "ALL";
		
		var location = window.location.href;
		
		if(location.indexOf("#") > 0)
		{
			location = location.substr(0, location.indexOf("#"));
		}

		try
		{
			this.$scope.$apply();
		}catch(ex)
		{}
		
		window.location.href = location + "#line_" + line;		
	}, {"$scope": $scope});
});