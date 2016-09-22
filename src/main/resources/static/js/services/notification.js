angular.module('app').factory('Notification', ['$http', function ($http) {
    this.error = function (text) {
        var noti = {
            id: -1,
            title: 'Ошибка',
            time: 5000,
            text: text,
            class_name: 'gritter-error'
        }
        noti.class_name = 'gritter-error'
        $.gritter.add(noti);
    }
    this.warning = function (text) {
        var noti = {
            id: -1,
            title: 'Внимание',
            time: 10000,
            text: text,
            class_name: 'gritter-orange'
        }
        noti.class_name = 'gritter-orange'
        $.gritter.add(noti);
    }
    this.success = function (text) {
        var noti = {
            id: -1,
            title: 'Успех',
            time: 3000,
            text: text,
            class_name: 'gritter-success'
        }
        $.gritter.add(noti)
    }
    this.successBg = function (text) {
        var noti = {
            id: -1,
            title: 'Успех',
            time: 3000,
            text: text,
            class_name: 'gritter-success'
        }
        $.gritter.add(noti)
    }
    return this;
}]);