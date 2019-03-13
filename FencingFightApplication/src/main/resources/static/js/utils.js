var competitionStageTitles = [
    "Отборочный этап",
    "Плей-офф",
    "Полуфинал",
    "Бой за третье место",
    "Финал"
];

function getTitleFromCompetitionStage(competitionStage) {
    switch (competitionStage) {
        case "QUALIFYING_STAGE":
            return "Отборочный этап";
        case "ADDITIONAL_DUELS":
            return "Дополнительные бои";
        case "PLAY_OFF_STAGE":
            return "Плей-офф";
        case "SEMI_FINALl":
            return "Полуфинал";
        case "THIRD_FINAL":
            return "Бой за третье место";
        case "FINAL":
            return "Финал";
        default:
            return "";
    }
}

function getTitleFromDuelStatus(duelStatus) {
    switch (duelStatus) {
        case "UNFINISHED":
            return "Не завершен";
        case "FINISHED":
            return "Завершен";
        case "CANCELED":
            return "Отменен";
        default:
            return "";
    }
}

function getDuelStatusByTitle(title) {
    switch (title) {
        case "Не завершен":
            return "UNFINISHED";
        case "Завершен":
            return "FINISHED";
        case "Отменен":
            return "CANCELED";
        default:
            return null;
    }
}

function responseErrorHandling(response) {
    var message;

    if (response.responseJSON) {
        message = response.responseJSON.message;
    } else if (response.message) {
        message = response.message;
    } else {
        message = "Error";
    }

    $.confirm({
        icon: "glyphicon glyphicon-warning-sign",
        title: 'Ошибка',
        content: message,
        type: 'red',
        typeAnimated: true,
        closeIcon: true,
        buttons: false
    });
}

function responseSuccessHandling(response) {
    if (!response.message) {
        return;
    }

    $.confirm({
        icon: "glyphicon glyphicon-info-sign",
        title: 'Информация',
        content: response.message,
        type: 'blue',
        typeAnimated: true,
        closeIcon: true,
        buttons: false
    });
}

function responseExceptionHandling(response, callback) {
    $.confirm({
        icon: "glyphicon glyphicon-question-sign",
        title: 'Предупреждение',
        content: response.exception.possibleSolution,
        type: 'orange',
        typeAnimated: true,
        closeIcon: function () {
            callback(false);
        },
        buttons: {
            yes: {
                text: 'Да',
                action: function(){
                    callback(true);
                }
            },
            no: {
                text: 'Нет',
                action: function(){
                    callback(false);
                }
            }
        }
    });
}