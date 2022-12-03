$.application = angular.module("application", ["ngSanitize"]);

$.application.filter('unsafe', ["$sce", function($sce) { 
	return $sce.trustAsHtml; 
}]);
