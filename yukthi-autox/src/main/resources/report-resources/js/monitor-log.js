$.application.controller("monitorController", function($scope){

	/**
	 * Create dummy records
	 */
	$scope.initContent = function(){
		
		var content = $("#logContentContainer").text().trim();
		content = content.replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
		content = content.replace(/\ /g, "&nbsp;");

		var contentArr = content.split("\n")
		
		var messages = [];
		
		for(var i = 0 ; i < contentArr.length ; i++)
		{
			var line = contentArr[i];
			
			// replace
			line = line.trim();
			
			if(line.length > 0)
			{
				var obj = {"lineNo" : (i + 1), "line" : line};
				messages.push(obj);
			}
		}
		
		$scope.messages = messages;
	};
	
	/**
	 * Goto
	 */
	goToLine = $.proxy(function(event){
		var line = $(event.target).attr("data-line-no"); 
		
		this.$scope.searchByMessage = "";
		
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
					
					if(item.line.toLowerCase().indexOf(filters[i]) >= 0)
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
});