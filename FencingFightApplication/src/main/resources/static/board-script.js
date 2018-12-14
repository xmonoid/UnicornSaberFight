var stompClient = null;

function connect() {
    var socket = new SockJS(window.location.origin + '/fencing-fight-app-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/fight-information/fencing-fight-app/board/change-score', function (score) {
            setScore(JSON.parse(score.body));
        });
        stompClient.subscribe('/fight-information/fencing-fight-app/board/change-time', function (time) {
            setTime(JSON.parse(time.body));
        });
        stompClient.subscribe('/fight-information/fencing-fight-app/board/set-names', function (names) {
            setNames(JSON.parse(names.body));
        });
        stompClient.subscribe('/fight-information/fencing-fight-app/board/change-mutual-hit-count', function (score) {
            setMutualHitCount(JSON.parse(score.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
}

function setScore(score) {
    var scoreElement;
    if (score.fighter === 'red') {
        if (score.kind === 'score') {
            scoreElement = document.getElementById('red-score');
        } else {
            scoreElement = document.getElementById('red-warning');
        }
    } else if (score.fighter === 'blue') {
        if (score.kind === 'score') {
            scoreElement = document.getElementById('blue-score');
        } else {
            scoreElement = document.getElementById('blue-warning');
        }
    } else {
        scoreElement = null;
    }

    if (score.kind === 'score') {
        scoreElement.innerHTML = score.newValue;
    } else {
        scoreElement.innerHTML = '';
        for (var ind = 0; ind < score.newValue; ind++) {
            scoreElement.innerHTML += "<img class='warning_image' src='images/warning.png' alt='Предупреждение'>";
        }
    }
}

function setMutualHitCount(mutual_hit_count) {
    var mutual_hit_countElement = document.getElementById('mutual_hit');
    mutual_hit_countElement.innerHTML = '';
    for (var ind = 0; ind < mutual_hit_count.newMutualHitCount; ind++) {
        mutual_hit_countElement.innerHTML += "<img class='mutual_hit_image' src='images/mutual_hit.png' alt='Обоюдное поражение'>";
    }
}

function setTime(time) {
    var timer = document.getElementById('time')
    timer.innerHTML = time.time
}

function setNames(names) {
    var red = document.getElementById('red-name')
    red.innerHTML = names.redName
    var blue = document.getElementById('blue-name')
    blue.innerHTML = names.blueName
}

$(document).ready(function() {
    connect()
});