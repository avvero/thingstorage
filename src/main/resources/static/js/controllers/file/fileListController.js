angular.module("app").controller('fileListController', ['$scope', '$stateParams', 'model', function ($scope, $stateParams, model) {
    $scope.fileList = model.fileList
}])