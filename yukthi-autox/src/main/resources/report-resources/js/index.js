$.application.controller('testAutomationAppController', function($scope){
	
	$scope.status = "Test Case Status";
	$scope.searchTestCaseName = "";
	
	$scope.statusToName = {"All" : "ALL", "Success" : "SUCCESSFUL", "Error" : "ERRORED", "Failed" : "FAILED", "Skipped" : "SKIPPED"};
	
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
		
		var dataFilter = $scope.statusToName[dataDisplay];
		
		var filterByNameAndStatus = false;
		if($scope.searchTestCaseName.length > 0)
		{
			filterByNameAndStatus = true;
		}
		
		$scope.commonFilterTestCaseNameStatus("STATUS", dataFilter, filterByNameAndStatus);
	};
	
	
	
	/**
	 * Gets invoked on type for filter test case.
	 */
	$scope.filterTestCase = function(event){
		
		var searchString = $scope.searchTestCaseName.toLowerCase();
		
		var filterByNameAndStatus = false;
		if($scope.status != "Test Case Status")
		{
			filterByNameAndStatus = true;
		}
		
		$scope.commonFilterTestCaseNameStatus("TEST_CASE_NAME", searchString, filterByNameAndStatus);
	};
	
	/**
	 * Common filter for status and name.
	 */
	$scope.commonFilterTestCaseNameStatus = function(filterType, filterValue, filterByNameAndStatus){
		
		for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		 {
			 var testSuiteObj = $scope.testSuiteResults[i];
			 var testCaseResults = $scope.testSuiteResults[i].testCaseResults;
			 
			 var allRowsAreFiltered = true;
			 
			 for(var j = 0 ; j < testCaseResults.length ; j++)
			 {
				var testCaseObj = testCaseResults[j];

				switch(filterType)
				{
					case "STATUS":
						{
							if(filterValue == "ALL")
							{
								testCaseObj.filterByStatus = true
							}else
							{
								testCaseObj.filterByStatus = testCaseObj.status.includes(filterValue);
							}
							break;
						}
						
					case "TEST_CASE_NAME":
						{
							testCaseObj.filterByTestCaseName = testCaseObj.testCaseName.toLowerCase().includes(filterValue);
						}
				}
				
				if(filterByNameAndStatus)
				{
					if(testCaseObj.filterByStatus && testCaseObj.filterByTestCaseName)
					{
						testCaseObj.display = true;
					}else
					{
						testCaseObj.display = false;
					}
				}else
				{
					if(testCaseObj.filterByStatus || testCaseObj.filterByTestCaseName)
					{
						testCaseObj.display = true;
					}else
					{
						testCaseObj.display = false;
					}
				}
				
				// check for if all rows are filtered
				if(testCaseObj.display)
				{
					allRowsAreFiltered = false;
				}
			 }
			 
			// set display value if all rows are hidden. 
			$scope.suiteNameToObj[testSuiteObj.suiteName].display = !allRowsAreFiltered; 
		 }
	};
	
	
	/**
	 * Gets  invoked on type for filter test suite.
	 */
	$scope.filterTestSuite = function(event){
		
		var searchString = $scope.searchTestSuiteName.toLowerCase();
		
		for(var i = 0 ; i < $scope.testSuiteResults.length ; i++)
		{
			var obj = $scope.testSuiteResults[i];
			obj.display = obj.suiteName.toLowerCase().includes(searchString);
			
			$scope.suiteNameToObj[obj.suiteName].display = obj.display;
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