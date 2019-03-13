var nominationId;
$(document).ready(function () {
    nominationId = parseInt(window.location.pathname.split("/")[2]);
    loadNomination(nominationId);
});

function loadNomination(nominationId, callback) {
    $.ajax({
        "async": true,
        "url": "/nomination/" + nominationId,
        "method": "GET",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false
    }).done(function (response) {
        var nomination = response.data;
        $("#nomination-name").text(nomination.name);
        $("#current-state").text("Текущий эпат: " + getTitleFromCompetitionStage(nomination.currentStage));
        $("#current-round").text("Текущий круг: " + nomination.currentRoundIndex);
        if (callback) {
            callback();
        }
    }).fail(function (response) {
        responseErrorHandling(response);
    })
}

// fighters tab
$('a[href="#fighters-tab"]').on('shown.bs.tab', function (evt) {
    fillFightersTable();
});

var fightersTable = $("#fighters-table").bootgrid({
    navigation: 0,
    padding: 0,
    rowCount: -1,
    columnSelection: false,
    sorting: true,
    multiSort: true,
    keepSelection: false,
    ajax: false
});

function fillFightersTable() {
    $.ajax({
        "async": true,
        "url": "/management/" + nominationId + "/fighters",
        "method": "GET",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false
    }).done(function (response) {
        var rows = response.data;
        fightersTable.bootgrid("clear");
        fightersTable.bootgrid("append", rows);
    }).fail(function (response) {
        responseErrorHandling(response);
    })
}

// duels tab
$('a[href="#duels-tab"]').on('shown.bs.tab', function (evt) {
    fillDuelsTables(nominationId);
});

var duelsPlayground1Table = $("#duels-playground-1-table").bootgrid({
    navigation: 0,
    padding: 0,
    rowCount: -1,
    columnSelection: false,
    sorting: true,
    multiSort: true,
    keepSelection: false,
    ajax: false,
    converters: {
        duelStatus: {
            from: function(value) {
                return getDuelStatusByTitle(value);
            },
            to: function(value) {
                return getTitleFromDuelStatus(value);
            }
        }
    }
});

var duelsPlayground2Table = $("#duels-playground-2-table").bootgrid({
    navigation: 0,
    padding: 0,
    rowCount: -1,
    columnSelection: false,
    sorting: true,
    multiSort: true,
    keepSelection: false,
    ajax: false,
    converters: {
        duelStatus: {
            from: function(value) {
                return getDuelStatusByTitle(value);
            },
            to: function(value) {
                return getTitleFromDuelStatus(value);
            }
        }
    }
});

function fillDuelsTables(nominationId) {
    $.ajax({
        "async": true,
        "url": "/management/" + nominationId + "/duels",
        "method": "GET",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false
    }).done(function (response) {
        var rowForPlayground1 = response.data[1];
        var rowForPlayground2 = response.data[2];

        fillFightersName(rowForPlayground1);
        fillFightersName(rowForPlayground2);

        duelsPlayground1Table.bootgrid("clear");
        duelsPlayground1Table.bootgrid("append", rowForPlayground1);

        duelsPlayground2Table.bootgrid("clear");
        duelsPlayground2Table.bootgrid("append", rowForPlayground2);
    }).fail(function (response) {
        responseErrorHandling(response);
    })
}

function createDuelsForNewRound(nominationId, callback) {
    $.confirm({
        icon: "glyphicon glyphicon-question-sign",
        title: 'Новый раунд',
        content: "На какие площадки распределить новые пары?",
        type: 'blue',
        typeAnimated: true,
        closeIcon: true,
        buttons: {
            playground_1: {
                text: 'На первую',
                action: function(){
                    createDuels(nominationId, 1, callback);
                }
            },
            playground_2: {
                text: 'На вторую',
                action: function(){
                    createDuels(nominationId, 2, callback);
                }
            },
            playground_all: {
                text: 'На обе',
                action: function(){
                    createDuels(nominationId, 3, callback);
                }
            }
        }
    });
}

function createDuels(nominationI, playgroudNumber, callback) {
    $.ajax({
        "async": true,
        "url": "/management/" + nominationI + "/create-duels",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify(playgroudNumber)
    }).done(function (response) {
        if (response.exception) {
            responseErrorHandling(response);
        } else {
            responseSuccessHandling(response);
        }

        if (callback) {
            callback();
        }
    }).fail(function (response) {
        responseErrorHandling(response);
    })
}

function loadDuelsForCurrentRound(nominationId) {
    $.confirm({
        icon: "glyphicon glyphicon-question-sign",
        title: 'Загрузка боев',
        content: "На какие площадки распределить пары из базы данных?",
        type: 'blue',
        typeAnimated: true,
        closeIcon: true,
        buttons: {
            playground_1: {
                text: 'На первую',
                action: function(){
                    loadDuels(nominationId, 1);
                }
            },
            playground_2: {
                text: 'На вторую',
                action: function(){
                    loadDuels(nominationId, 2);
                }
            },
            playground_all: {
                text: 'На обе',
                action: function(){
                    loadDuels(nominationId, 3);
                }
            }
        }
    });
}

function loadDuels(nominationI, playgroudNumber) {
    $.ajax({
        "async": true,
        "url": "/management/" + nominationI + "/load-duels",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify(playgroudNumber)
    }).done(function (response) {
        var rowForPlayground1 = response.data[1];
        var rowForPlayground2 = response.data[2];

        fillFightersName(rowForPlayground1);
        fillFightersName(rowForPlayground2);

        duelsPlayground1Table.bootgrid("clear");
        duelsPlayground1Table.bootgrid("append", rowForPlayground1);

        duelsPlayground2Table.bootgrid("clear");
        duelsPlayground2Table.bootgrid("append", rowForPlayground2);
    }).fail(function (response) {
        responseErrorHandling(response);
    })
}

$("#create-duels-btn").on("click", function () {
    createDuelsForNewRound(nominationId, function () {
        loadNomination(nominationId, function () {
            fillDuelsTables(nominationId);
        })
    });
});

$("#load-duels-btn").on("click", function () {
    loadDuelsForCurrentRound(nominationId)
});

function fillFightersName(rows) {
    $.each(rows, function (index, elem) {
        elem.redFighterName = elem.redFighter.lastName + " " + elem.redFighter.firstName;
        if (elem.blueFighter) {
            elem.blueFighterName = elem.blueFighter.lastName + " " + elem.blueFighter.firstName;
        } else {
            elem.blueFighterName = "";
        }
    });
}