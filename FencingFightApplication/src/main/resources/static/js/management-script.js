function getRowFromTableById(id, table) {
    var rows = table.bootgrid("getCurrentRows");
    for (var ind = 0; ind < rows.length; ind++) {
        if (rows[ind].id === id) {
            return rows[ind];
        }
    }
    return null;
}

// nominations tab
$('a[href="#nominations-tab"]').on('shown.bs.tab', function (evt) {
    fillNominationsTable();
});

$('#nomination-model-dialog').on('hidden.bs.modal', function (evt) {
    var form = $("#nomination-form")[0];
    form.elements.id.value = "";
    form.elements.title.value = "";

});

var nominationsTable = $("#nominations-table").bootgrid({
    navigation: 0,
    padding: 0,
    columnSelection: false,
    rowCount: -1,
    sorting: true,
    keepSelection: false,
    ajax: false,
    formatters: {
        "commands": function (column, row) {
            return "<button type=\"button\" class=\"btn btn-default btn-success command-edit\" data-row-id=" + row.id + "><i class=\"glyphicon glyphicon-pencil\"></i></button> " +
                "<button type=\"button\" class=\"btn btn-default btn-danger command-delete\" data-row-id=" + row.id + "><i class=\"glyphicon glyphicon-trash\"></i></button> " +
                "<a type=\"button\" class=\"btn btn-default btn-info command-details\" data-row-id=" + row.id + "><i class=\"glyphicon glyphicon-cog\"></i></a>";
        }
    }
}).on("loaded.rs.jquery.bootgrid", function () {
    nominationsTable.find(".command-edit").on("click", function (evt) {
        var row = getRowFromTableById($(this).data("row-id"), nominationsTable);
        var form = $("#nomination-form")[0];
        form.elements.id.value = row.id;
        form.elements.title.value = row.name;
        $("#nomination-model-dialog").modal("show");
    }).end().find(".command-delete").on("click", function (evt) {
        deleteNomination($(this).data("row-id"), false, function () {
            fillNominationsTable();
        });
    }).end().find(".command-details").on("click", function (evt) {
        var app = "/management/" + $(this).data("row-id") + "/nomination"
        $(location).attr('href',app)
    });
});

function fillNominationsTable() {
    $.ajax({
        "async": true,
        "url": "/nomination/all",
        "method": "GET",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false
    }).done(function (response) {
        var rows = response.data;
        $.each(rows, function (index, elem) {
            elem.currentStageTitle = getTitleFromCompetitionStage(elem.currentStage);
        });
        nominationsTable.bootgrid("clear");
        nominationsTable.bootgrid("append", rows);
    }).fail(function (response) {
        responseErrorHandling(response);
    })
}

$("#save-nomination-btn").on("click", function () {
    addOrUpdateNomination();
});

function addOrUpdateNomination() {
    var form = $("#nomination-form")[0];
    var nomination = {
        id: form.elements.id.value,
        name: form.elements.title.value
    };

    if (!nomination.name) {
        return;
    }

    if (nomination.id) {
        updateNominationName(nomination, function () {
            $("#nomination-model-dialog").modal("hide");
            fillNominationsTable();
        })
    } else {
        addNewNomination(nomination, function () {
            $("#nomination-model-dialog").modal("hide");
            fillNominationsTable();
        })
    }

    return false;
}

function deleteNomination(id, cascadeDelete, callback) {
    $.ajax({
        "async": true,
        "url": "/nomination/delete",
        "method": "DELETE",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify({
            objectId: id,
            cascadeDelete: cascadeDelete
        })
    }).done(function (response) {
        if (response.exception && response.exception.possibleSolution) {
            responseExceptionHandling(response, function (result) {
                if (result) {
                    deleteNomination(id, result, callback);
                } else {
                    responseErrorHandling(response);
                }
            });
        } else if (response.exception) {
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

function addNewNomination(nomination, callback) {
    $.ajax({
        "async": true,
        "url": "/nomination/add",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify(nomination)
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

function updateNominationName(nomination, callback) {
    $.ajax({
        "async": true,
        "url": "/nomination/" + nomination.id + "/update-name",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": nomination.name
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

// fighters tab
$('a[href="#fighters-tab"]').on('shown.bs.tab', function (evt) {
    fillFightersTable();
});

$('#fighter-model-dialog').on('hidden.bs.modal', function (evt) {
    var form = $("#fighter-form")[0];
    form.elements.id.value = "";
    form.elements.firstName.value = "";
    form.elements.lastName.value = "";
    form.elements.parentName.value = "";
    form.elements.sex.value = "";
    form.elements.sex[0].checked = false;
    form.elements.sex[1].checked = false;
    form.elements.birthDate.value = "";
    form.elements.club.value = "";
    form.elements.city.value = "";
    form.elements.nominationsSelector.selectedOptions = [];
    $('option', form.elements.nominationsSelector).remove();
});

$('#fighter-model-dialog').on('show.bs.modal', function (evt) {
    var form = $("#fighter-form")[0];
    var divElement = $("#nominations-block")[0];
    if (form.elements.id.value) {
        $(divElement).addClass("hidden");
    } else {
        $(divElement).removeClass("hidden");
        $.ajax({
            "async": false,
            "url": "/nomination/all",
            "method": "GET",
            "headers": {
                "content-type": "application/json",
                "cache-control": "no-cache"
            },
            "processData": false
        }).done(function (response) {
            var selector = $("#nominations-selector")[0];
            $.each(response.data, function (index, elem) {
                $(selector).append('<option value="' + elem.id + '">' + elem.name + '</option>');
            });
        }).fail(function (response) {
            responseErrorHandling(response);
        })
    }
});

var fightersTable = $("#fighters-table").bootgrid({
    navigation: 0,
    padding: 0,
    rowCount: -1,
    columnSelection: false,
    sorting: true,
    multiSort: true,
    keepSelection: false,
    ajax: false,
    converters: {
        booleanToStr: {
            from: function(value) {
                return value === "Муж";
            },
            to: function(value) {
                return value ? "Муж" : "Жен";
            }
        },
        longToDateStr: {
            from: function(value) {
                return $.datepicker.parseDate("dd.mm.yy", value).getTime();
            },
            to: function(value) {
                return $.datepicker.formatDate("dd.mm.yy", new Date(value));
            }
        }
    },
    formatters: {
        "commands": function (column, row) {
            return "<div class=\"btn-group-vertical\" data-toggle=\"buttons\"><button type=\"button\" class=\"btn btn-default btn-success command-edit\" data-row-id=" + row.id + "><i class=\"glyphicon glyphicon-pencil\"></i></button> " +
                "<button type=\"button\" class=\"btn btn-default btn-info command-change-status\" data-row-id=" + row.id + "><i class=\"glyphicon glyphicon-transfer\"></i></button></div> " +
                "<div class=\"btn-group-vertical\" data-toggle=\"buttons\"><button type=\"button\" class=\"btn btn-default btn-success command-change-nominations\" data-row-id=" + row.id + "><i class=\"glyphicon glyphicon-list-alt\"></i></button> " +
                "<button type=\"button\" class=\"btn btn-default btn-danger command-delete\" data-row-id=" + row.id + "><i class=\"glyphicon glyphicon-trash\"></i></button></div>";
        },
        "nominations": function (column, row) {
            var result = "<ul class=\"list-group\">";
            for (var ind = 0; ind < row.nominations.length; ind++) {
                result += "<li lass=\"list-group-item\">" + row.nominations[ind].name + "</li>";
            }
            result += "</ul>";
            return result;
        },
        "activeStatus": function (column, row) {
            if (row.isActive) {
                return "<i class=\"glyphicon glyphicon-ok\" style=\"color:green\"></i>";
            } else {
                return "<i class=\"glyphicon glyphicon-remove\" style=\"color:red\"></i>"
            }
        }
    }
}).on("loaded.rs.jquery.bootgrid", function () {
    fightersTable.find(".command-edit").on("click", function (evt) {
        var row = getRowFromTableById($(this).data("row-id"), fightersTable);
        var form = $("#fighter-form")[0];

        form.elements.id.value = row.id;
        form.elements.firstName.value = row.firstName;
        form.elements.lastName.value = row.lastName;
        form.elements.parentName.value = row.parentName;
        if (row.sex) {
            form.elements.sex.value = "Муж";
            form.elements.sex[0].checked = true;
        } else {
            form.elements.sex.value = "Жен";
            form.elements.sex[1].checked = true;
        }
        form.elements.birthDate.value = $.datepicker.formatDate("yy-mm-dd", new Date(row.birthDate));
        form.elements.club.value = row.club;
        form.elements.city.value = row.city;

        $("#fighter-model-dialog").modal("show");
    }).end().find(".command-delete").on("click", function (evt) {
        deleteFighter($(this).data("row-id"), false, function () {
            fillFightersTable();
        });
    }).end().find(".command-change-status").on("click", function (evt) {
        var row = getRowFromTableById($(this).data("row-id"), fightersTable);
        changeActiveStatus($(this).data("row-id"), !row.isActive, function () {
            fillFightersTable();
        });
    }).end().find(".command-change-nominations").on("click", function (evt) {
        var row = getRowFromTableById($(this).data("row-id"), fightersTable);

        var form = $("#fighter-nominations-form")[0];
        form.elements.id.value = row.id;

        var label = $("#fighter-full-name")[0];
        $(label).text(row.lastName + " " + row.firstName + " " + row.parentName);

        $("#fighter-nominations-model-dialog").modal("show");
        $.each(row.nominations, function (index, elem) {
            $("#fighter-nominations-selector [value='" + elem.id + "']").attr("selected", "selected");
        });
    });
});

function fillFightersTable() {
    $.ajax({
        "async": true,
        "url": "/fighter/all",
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

function deleteFighter(id, cascadeDelete, callback) {
    $.ajax({
        "async": true,
        "url": "/fighter/delete",
        "method": "DELETE",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify({
            objectId: id,
            cascadeDelete: cascadeDelete
        })
    }).done(function (response) {
        if (response.exception && response.exception.possibleSolution) {
            responseExceptionHandling(response, function (result) {
                if (result) {
                    deleteNomination(id, result, callback);
                } else {
                    responseErrorHandling(response);
                }
            });
        } else if (response.exception) {
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

function changeActiveStatus(id, isActive, callback) {
    $.ajax({
        "async": true,
        "url": "/fighter/" + id + "/update-fighter-status",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify(isActive)
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

$("#save-fighter-btn").on("click", function () {
    addOrUpdateFighter();
});

function addOrUpdateFighter() {
    var form = $("#fighter-form")[0];
    var fighter = {
        id: form.elements.id.value,
        firstName: form.elements.firstName.value,
        lastName: form.elements.lastName.value,
        parentName: form.elements.parentName.value,
        sex: form.elements.sex.value ? form.elements.sex.value === "Муж" : null,
        birthDate: form.elements.birthDate.value ? $.datepicker.parseDate("yy-mm-dd", form.elements.birthDate.value).getTime() : null,
        club: form.elements.club.value,
        city: form.elements.city.value,
        nominations: []
    };

    var nominations = form.elements.nominationsSelector.selectedOptions;
    $.each(nominations, function (index, elem) {
        fighter.nominations.push({
            id: parseInt(elem.value)
        })
    });

    if (!fighter.firstName || !fighter.lastName || fighter.sex === null || !fighter.birthDate) {
        return;
    }

    if (fighter.id) {
        updateFighterPersonalData(fighter, function () {
            $("#fighter-model-dialog").modal("hide");
            fillFightersTable();
        })
    } else {
        addNewFighter(fighter, function () {
            $("#fighter-model-dialog").modal("hide");
            fillFightersTable();
        })
    }

    return false;
}

function addNewFighter(fighter, callback) {
    $.ajax({
        "async": true,
        "url": "/fighter/add",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify(fighter)
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

function updateFighterPersonalData(fighter, callback) {
    $.ajax({
        "async": true,
        "url": "/fighter/update-personal-data",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify(fighter)
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

function updateFighterNominations(fighterId, nominationIds, callback) {
    $.ajax({
        "async": true,
        "url": "/fighter/" + fighterId + "/update-nominations",
        "method": "POST",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false,
        "data": JSON.stringify(nominationIds)
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

$("#save-fighter-nominations-btn").on("click", function () {
    var form = $("#fighter-nominations-form")[0];

    var nominations = [];
    $.each(form.elements.nominationsSelector.selectedOptions, function (index, elem) {
        nominations.push(parseInt(elem.value))
    });

    updateFighterNominations(form.elements.id.value, nominations, function () {
        $("#fighter-nominations-model-dialog").modal("hide");
        fillFightersTable();
    })
});

$('#fighter-nominations-model-dialog').on('hidden.bs.modal', function (evt) {
    var form = $("#fighter-nominations-form")[0];
    form.elements.id.value = "";
    form.elements.nominationsSelector.selectedOptions = [];
    $('option', form.elements.nominationsSelector).remove();
    var label = $("#fighter-full-name")[0];
    $(label).text("");
});

$('#fighter-nominations-model-dialog').on('show.bs.modal', function (evt) {
    $.ajax({
        "async": false,
        "url": "/nomination/all",
        "method": "GET",
        "headers": {
            "content-type": "application/json",
            "cache-control": "no-cache"
        },
        "processData": false
    }).done(function (response) {
        var selector = $("#fighter-nominations-selector")[0];
        $.each(response.data, function (index, elem) {
            $(selector).append('<option value="' + elem.id + '">' + elem.name + '</option>');
        });
    }).fail(function (response) {
        responseErrorHandling(response);
    })
});

$("#load-fighters-btn").on("click", function () {
    var app = "https://script.google.com/macros/s/AKfycbwB4WSt9KYsBHDEJc2J0bBdaklQBJ8T8IUqa3-FZg/exec",
        request = new XMLHttpRequest();
    request.open('GET', app);
    request.onreadystatechange = function() {
        if (request.readyState !== 4) return;

        if (request.status == 200) {
            $.ajax({
                "async": true,
                "url": "/fighter/add-all",
                "method": "POST",
                "headers": {
                    "content-type": "application/json",
                    "cache-control": "no-cache"
                },
                "processData": false,
                "data": request.responseText
            }).done(function (response) {
                if (response.exception) {
                    responseErrorHandling(response);
                } else {
                    responseSuccessHandling(response);
                }

                fillFightersTable();
            }).fail(function (response) {
                responseErrorHandling(response);
            })
        }

    };
    request.send();
});