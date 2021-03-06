angular.module("app", [
    'ngRoute',
    'ngSanitize',
    'ngAnimate',
    'ui.router',
    'ui.bootstrap',
    'textAngular',
    'jkuri.gallery',
    'angular-loading-bar',
    'infinite-scroll',
    'ngFileUpload'
])
angular.module("app").constant('APP_CONSTANTS', {
    version: "0.0.1"
})
// configure our routes
angular.module("app").config(['$routeProvider', '$stateProvider', '$urlRouterProvider', '$httpProvider', '$locationProvider',
    function ($routeProvider, $stateProvider, $urlRouterProvider, $httpProvider, $locationProvider) {
        $locationProvider.html5Mode({
            enabled: true,
            requireBase: false
        });

        $urlRouterProvider.otherwise("/")
        $stateProvider
            .state('welcome', {
                url: "/",
                views: {
                    "single": {
                        templateUrl: 'view/file/list.html',
                        controller: 'fileListController',
                        resolve: {
                            model: ['$q', '$http', '$stateParams', 'Notification',
                                function ($q, $http, $stateParams, Notification) {
                                    var deferred = $q.defer();
                                    var urlCalls = [];
                                    urlCalls.push($http({
                                        method: 'GET',
                                        url: 'list',
                                        headers: {'Content-Type': 'application/json;charset=UTF-8'}
                                    }))
                                    $q.all(urlCalls)
                                        .then(
                                        function (results) {
                                            var data = []
                                            data.fileList = results[0].data
                                            deferred.resolve(data)
                                        },
                                        function (errors) {
                                            Notification.error(errors.data)
                                        },
                                        function (updates) {
                                            deferred.update(updates);
                                        });
                                    return deferred.promise;
                                }]
                        }
                    }
                }
            })

        //cfpLoadingBarProvider.includeSpinner = false;
    }])
angular.module("app").run(['$rootScope', '$http', function ($rootScope, $http) {

}])

angular.module("app").controller('mainController', ['$scope', '$http', '$window', 'APP_CONSTANTS', '$timeout', '$location', '$sce',
    function ($scope, $http, $window, APP_CONSTANTS, $timeout, $location, $sce) {
        $scope.APP_CONSTANTS = APP_CONSTANTS
        $scope.APPLICATION_LOADING_DELAY = 1000
        $scope.$location = $location;
    }])