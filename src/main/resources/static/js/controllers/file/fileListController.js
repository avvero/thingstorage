angular.module("app").controller('fileListController', ['$scope', '$stateParams', 'model', function ($scope, $stateParams, model) {
    $scope.fileList = model.fileList

    $scope.selectedFile = null
    $scope.selectInList = function(file) {
        var oldSelected = $scope.selectedFile
        if ($scope.selectedFile != null) {
            $scope.selectedFile.isSelected = null
            $scope.selectedFile = null
        }
        if (oldSelected != file) {
            $scope.selectedFile = file
            $scope.selectedFile.isSelected = true
        }
    }
}])