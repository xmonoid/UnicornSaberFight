var timerId = null
var stompClient = null;

function connect() {
    var socket = new SockJS('http://localhost:8080/fencing-fight-app-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {});
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
}

function add_point(fighter, kind, addition) {
    var number_field
    if (fighter == 'red') {
        if (kind == 'score') {
            number_field = document.getElementById('red-score')
        } else {
            number_field = document.getElementById('red-warning')
        }
    } else {
        if (kind == 'score') {
            number_field = document.getElementById('blue-score')
        } else {
            number_field = document.getElementById('blue-warning')
        }
    }
    var score = new Number(number_field.innerHTML)
    score += addition
    number_field.innerHTML = score.toString()
    stompClient.send("/fencing-fight-app/secretary/change-score", {}, JSON.stringify({
        fighter: fighter,
        kind: kind,
        newValue: score
    }));
}

function send_time(time) {
    stompClient.send("/fencing-fight-app/secretary/change-time", {}, JSON.stringify({
        time: time
    }));
}


function default_values() {
    document.getElementById('time').innerHTML = '02:00'
    document.getElementById('start_stop_time_button').value = 'Старт время'
    document.getElementById('start_stop_fight_button').value = 'Начать бой'
    document.getElementById('red_name').value = ''
    document.getElementById('blue_name').value = ''
    document.getElementById('red-score').innerHTML = '0'
    document.getElementById('red-warning').innerHTML = '0'
    document.getElementById('blue-score').innerHTML = '0'
    document.getElementById('blue-warning').innerHTML = '0'
}

$(document).ready(function() {
    default_values()
    connect()
});

function pressed_timer(button) {
    if (button.value == 'Старт время') {
        button.value = 'Стоп время'
        start_countdown()
    } else {
        button.value = 'Старт время'
        stop_countdown()
    }
}

function start_countdown() {

    var timer = document.getElementById('time')
    var time = timer.innerHTML
    var arr = time.split(':')
    var min = new Number(arr[0])
    var sec = new Number(arr[1])
    if (sec == 0) {
        if (min == 0) {
            stop_countdown()
            return
        } else {
            min--
            sec = 59
        }
    } else {
        sec--
    }

    if (min < 10) {
        min = '0' + min
    }
    if (sec < 10) {
        sec = '0' + sec
    }

    var result = min + ':' + sec
    send_time(result)
    timer.innerHTML = result

    timerId = setTimeout(start_countdown, 1000)
}

function stop_countdown() {
    clearTimeout(timerId)
}

function add_time(value) {
    var timer = document.getElementById('time')
    var time = timer.innerHTML
    var arr = time.split(':')
    var min = new Number(arr[0])
    var sec = new Number(arr[1])

    switch (value) {
    case 60: {
        min++
        break
    }
    case -60: {
        min--
        if (min < 0) {
            min = 0
            sec = 0
        }
        break
    }
    case 10: {
        sec += 10
        if (sec >= 60) {
            sec -= 60
            min++
        }
        break
    }
    case -10: {
        sec -= 10
        if (sec < 0) {
            if (min == 0) {
                min = 0
                sec = 0
            } else {
                sec += 60
                min--
            }
        }
        break
    }
    case 1: {
        sec++
        if (sec >= 60) {
            sec -= 60
            min++
        }
        break
    }
    case -1: {
        sec--
        if (sec < 0) {
            if (min == 0) {
                min = 0
                sec = 0
            } else {
                sec += 60
                min--
            }
        }
        break
    }
    default:
        console.log('Wrong value in add_time function: ' + value)
        return
    }

    if (min < 10) {
        min = '0' + min
    }
    if (sec < 10) {
        sec = '0' + sec
    }

    var result = min + ':' + sec
    send_time(result)
    timer.innerHTML = result
}

function start_stop_fight() {
    var button = document.getElementById('start_stop_fight_button')
    if (button.value == 'Закончить бой') {
        stop_countdown()
        default_values()
        button.value = 'Начать бой'
    } else {

        button.value = 'Закончить бой'
    }
}