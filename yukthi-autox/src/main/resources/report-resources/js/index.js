$.application.controller('testAutomationAppController', function($scope){
	
	$scope.status = "Test Case Status";
	$scope.searchTestSuiteName = "";
	$scope.searchTestCaseName = "";
	
	$scope.statusToName = {"Test Case Status" : "ALL", "All" : "ALL", "Success" : "SUCCESSFUL", "Error" : "ERRORED", "Failed" : "FAILED", "Skipped" : "SKIPPED"};
	
	/**
	 * Gets invoked on init.
	 */
	$scope.fetchObjects = function(){
		
		//reportData global variable present in included js file.
		$scope.testResults = reportData;
		
		$scope.testSuiteResults = $scope.testResults.testSuiteResults;
		
		for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		{
			var obj = $scope.testSuiteResults[i];
			obj.display = true;
		}
		 
		 $scope.suiteNameToObj = {};
		 var testResultsArr = [];
		 for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		 {
			 var testCaseResults = $scope.testSuiteResults[i].testCaseResults;

			 for(var j = 0 ; j < testCaseResults.length ; j++)
			 {
				 var testCaseObj = testCaseResults[j];
				 testCaseObj.filterByStatus = false; 
				 testCaseObj.filterByTestCaseName = false;
				 testCaseObj.display = true;
			 }
			 
			 var obj = {"suiteName" : $scope.testSuiteResults[i].suiteName, "testCaseResults" : testCaseResults, "testSuiteResult": $scope.testSuiteResults[i]};
			 obj.display = true;
			 
			 $scope.suiteNameToObj[obj.suiteName] = obj;
			 testResultsArr.push(obj);
		 }
		 
		$scope.testResultsArr = testResultsArr;
	};
	
	/**
	 * On change of status.
	 */
	$scope.onChangeStatus = function(dataDisplay){
		$scope.status = dataDisplay;
		$scope.applyFilter();
	};
	
	$scope.applyFilter = function() {
		var testCaseFilter = $scope.searchTestCaseName;
		testCaseFilter = (testCaseFilter.trim().length > 0) ? testCaseFilter.trim().toLowerCase() : null;
		
		var testSuiteFilter = $scope.searchTestSuiteName;
		testSuiteFilter = (testSuiteFilter.trim().length > 0) ? testSuiteFilter.trim().toLowerCase() : null;
		
		var statusFilter = $scope.statusToName[$scope.status];
		statusFilter = (statusFilter != 'ALL') ? statusFilter : null;
		
		for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		{
			var testSuiteObj = $scope.testSuiteResults[i];
			var testCaseResults = $scope.testSuiteResults[i].testCaseResults;
			
			testSuiteObj.display = false;
			
			if(testSuiteFilter && testSuiteObj.report.name.toLowerCase().indexOf(testSuiteFilter) < 0)
			{
				continue;
			}
			
			var filteredTcCount = 0;
			 
			for(var j = 0 ; j < testCaseResults.length ; j++)
			{
				var testCaseObj = testCaseResults[j];
				testCaseObj.display = false;

				if(statusFilter && testCaseObj.mainExecutionDetails.statusStr != statusFilter)
				{
					continue;
				}
				
				if(testCaseFilter && testCaseObj.name.toLowerCase().indexOf(testCaseFilter) < 0)
				{
					continue;
				}
				
				testCaseObj.display = true;
				filteredTcCount++;
			 }
			 
			 if(filteredTcCount > 0)
			 {
				testSuiteObj.display = true;
			 }
		 }
	};
	
	/**
	 * Replace all.
	 */
	$scope.replaceAll = function(data, target, replacement){
		
		  return data.split(target).join(replacement);
	};
		
	/**
	 * Replace the message.
	 */
	$scope.replaceMessage = function(message){
		
		if(!message)
		{
			return message;
		}
		
		var result = $scope.replaceAll(message, "/n", "<BR/>");
		
		return result;
		
	};
	
});