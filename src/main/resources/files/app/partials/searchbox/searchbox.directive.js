'use strict';

angular.module('onsTemplates')
	.directive('searchBox', ['$location', '$route', 'PageUtil',
		function($location, $route, PageUtil) {
			return {
				restrict: 'E',
				replace: true,
				templateUrl: 'app/partials/searchbox/searchbox.html',
				controller: ['$scope', '$rootScope', SearchBox]
			}

			function SearchBox($scope, $rootScope ) {
				
				$scope.autoCompleteLimit = $rootScope.onsAlphaConfiguration.autoCompleteLimit

				clearOnPageChange()

				function clearOnPageChange() {
				  $rootScope.$on('$locationChangeSuccess', function(event) {
				  	//Clear if not going to search results page
				  	if($location.$$path != "/search") {

				    	$scope.searchTerm = ''
				  	}
				  })
				}

				$scope.goToSearch = function(searchTerm) {
					if (!searchTerm) {
						return
					}
					$location.path('/search')
					$location.search('q', searchTerm)

					//Clear page parameter if any
					$location.search('page', null)
					$location.search('type', null)
						//Re-initializes controllers. Fixes searching on search results page when searching the same term
					$route.reload()
				}

				$scope.getStaticFormAction = function() {
					var staticSearchForm = '/static/search'
					return PageUtil.isPrerender() ? staticSearchForm : ''
				}
			}
		}
	])