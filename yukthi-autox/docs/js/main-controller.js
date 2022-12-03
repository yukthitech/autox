$.application.controller('mainController', function($scope){
	$scope.headerLinks = [
	    {"id": "home", "label": "Home", file: "home.html"},
	    {"id": "apiIndex", "label": "Documentation", file: "api/doc-index.html"}
	];
	
	$scope.activeHeaderLink = null;
	
	/**
	 * Contains subpage id of the current page, if any.
	 */
	$scope.subpageId = null;
	
	/**
	 * Changes the page url with sublink or subpage location.
	 */
	$scope.changeLink = function(linkId) {
		console.log("Changing link to: " + linkId);
		
		var newLink = null;
		
		for(var i = 0; i < $scope.headerLinks.length; i++)
		{
			if($scope.headerLinks[i].id == linkId)
			{
				newLink = $scope.headerLinks[i];
				break;
			}
		}
		
		if(!newLink)
		{
			console.log("No page found with specified name, going to home page. Name: " + linkId);
			newLink = $scope.headerLinks[0];
		}
		
		$scope.activeHeaderLink = newLink;
		
		var location = window.location.href;
		
		if(location.indexOf("#") > 0)
		{
			location = location.substr(0, location.indexOf("#"));
		}
		
		window.location.href = location + "#" + newLink.id;
			
		$scope.subpageId = null;
		
		setTimeout(function(){ 
			$('pre code').each(function(i, block) {
				hljs.highlightBlock(block);
			});
		}, 500);
	};
	
	$scope.subpageChanged = function() {
		var location = window.location.href;
		
		if(location.indexOf("#") > 0)
		{
			var page = location.substr(location.indexOf("#") + 1, location.length);
			
			if(page.indexOf("/") == 0)
			{
				page = page.substr(1, page.length);
			}
			
			var subpageId = null;
			
			if(page.indexOf("$") > 0)
			{
				subpageId = page.substr(page.indexOf("$") + 1, page.length);
				page = page.substr(0, page.indexOf("$"));
			}
			
			if(!$scope.activeHeaderLink || $scope.activeHeaderLink.id != page)
			{
				console.log("Going for page: " + page);
				$scope.changeLink(page);
			}
			
			if(subpageId && $scope.subpageId != subpageId)
			{
				console.log("Setting subpage id as: " + subpageId);
				$scope.setSubpageId(subpageId);
				$scope.$broadcast("subpageIdChanged");
			}
			
			return true;
		}
		
		return false;
	};
	
	/**
	 * Controller init method, which fetches the page and subpage details from the url
	 * and loads the approp content.
	 */
	$scope.init = function() {
		var location = window.location.href;
		
		if( !$scope.subpageChanged() )
		{
			$scope.changeLink("home");			
		}
		
		window.onhashchange = $scope.subpageChanged;
	};
	
	/**
	 * Sets the specified sub page id into the page url.
	 */
	$scope.setSubpageId = function(subpageId) {
		var location = window.location.href;
		
		if(location.indexOf("$") > 0)
		{
			location = location.substr(0, location.indexOf("$"));
		}

		$scope.subpageId = subpageId;
		window.location.href = location + "$" + subpageId;
	};
	
	/**
	 * Gets the sub page location if any.
	 */
	$scope.getSubpageId = function() {
		return $scope.subpageId;
	};
});