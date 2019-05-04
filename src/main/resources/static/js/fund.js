var fundingApp = angular.module('fundingApp', ["ngTable"]);

fundingApp.controller('fundController', function PhoneListController($scope, $http, NgTableParams) {

    $scope.displayFundsTable = true;
    $scope.isLoading = true;
    $scope.displayResult = false;
    $scope.result = -1;
    $scope.learningOptions = {
        currentFundCode: null,
        money: 10000,
        fee: 0.1,
        trainTimes: 10000,
        learningRate: 0.1
    };

    $http.get('/fund/getAllFunds').then(function (res) {
        $scope.fundsTable = new NgTableParams({}, {dataset: res.data});
        $scope.isLoading = false;
    });

    $scope.clickFund = function (fundcode) {
        $scope.isLoading = true;
        $scope.learningOptions.currentFundCode = fundcode;
        $http.get('/fund/pullHistory/' + fundcode).then(function (res) {
            $scope.displayFundsTable = false;
            $scope.isLoading = false;
            $scope.historyTable = new NgTableParams({}, {dataset: res.data});
        });
    }

    $scope.process= function() {
        $http.post('/fund/processFundLearning', $scope.learningOptions).then(function(res) {
            $scope.displayResult = true;
            $scope.result= res.data;
        });
    }
});