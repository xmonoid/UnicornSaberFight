var stompClient = null;

function connect() {
    var socket = new SockJS('http://localhost:8080/fencing-fight-app-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/fight-information/fencing-fight-app/board/change-score', function (score) {
            setScore(JSON.parse(score.body));
        });
        stompClient.subscribe('/fight-information/fencing-fight-app/board/change-time', function (time) {
            setTime(JSON.parse(time.body));
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

    scoreElement.innerHTML = score.newValue;
}

function setTime(time) {
    var timer = document.getElementById('time')
    timer.innerHTML = time.time
}

$(document).ready(function() {
    connect()
});