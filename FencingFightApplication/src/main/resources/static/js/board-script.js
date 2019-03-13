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
        scoreElement = document.getElementById('red-score');
    } else if (score.fighter === 'blue') {
        scoreElement = document.getElementById('blue-score');
    } else {
        scoreElement = null;
    }

    scoreElement.innerHTML = score.newValue;
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