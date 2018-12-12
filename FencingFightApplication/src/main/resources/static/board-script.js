var stompClient = null;

function connect() {
    var socket = new SockJS('http://localhost:8080/fencing-fight-app-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/fight-information/fencing-fight-app/board/change-score', function (score) {
            setScore(JSON.parse(score.body));
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

$(document).ready(function() {
    connect()
});