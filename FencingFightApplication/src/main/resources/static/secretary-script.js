var timerId = null;
var stompClient = null;
var canEditScore = false;
var disableKeypressHandling = false;

function connect() {
    var socket = new SockJS(window.location.origin + '/fencing-fight-app-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {});
}

function disconnect() {
    //TODO add usage
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

    score = score >= 0 ? score : 0;

    if (kind === 'warning') {
        score = score < 7 ? score : 6;
    }

    number_field.innerHTML = score.toString()
    stompClient.send("/fencing-fight-app/secretary/change-score", {}, JSON.stringify({
        fighter: fighter,
        kind: kind,
        newValue: score
    }));
}

function add_mutual_hit(addition) {
    var mutual_hit_count_field = document.getElementById('mutual_hit');
    var mutual_hit_count = new Number(mutual_hit_count_field.innerHTML);
    mutual_hit_count += addition;

    mutual_hit_count = mutual_hit_count >= 0 ? mutual_hit_count : 0;
    mutual_hit_count = mutual_hit_count <= 4 ? mutual_hit_count : 4;

    mutual_hit_count_field.innerHTML = mutual_hit_count.toString();
    stompClient.send("/fencing-fight-app/secretary/change-mutual-hit-count", {}, JSON.stringify({
        newMutualHitCount: mutual_hit_count
    }));
}

function send_time(time) {
    stompClient.send("/fencing-fight-app/secretary/change-time", {}, JSON.stringify({
        time: time
    }));
}

function set_disabled_all_elements_by_class(class_name) {
    var buttons = document.getElementsByClassName(class_name);
    $.each(buttons, function (index, button) {
        button.setAttribute('disabled', 'disabled');
    });
}

function delete_disabled_all_elements_by_class(class_name) {
    var buttons = document.getElementsByClassName(class_name);
    $.each(buttons, function (index, button) {
        button.removeAttribute("disabled");
    });
}

function default_values() {
    document.getElementById('time').innerHTML = '02:00'
    document.getElementById('start_stop_time_button').value = 'Старт время'
    document.getElementById('start_stop_fight_button').value = 'Начать бой'
    document.getElementById('mutual_hit').innerHTML = '0'
    document.getElementById('red_name').value = ''
    document.getElementById('blue_name').value = ''
    document.getElementById('red-score').innerHTML = '0'
    document.getElementById('red-warning').innerHTML = '0'
    document.getElementById('blue-score').innerHTML = '0'
    document.getElementById('blue-warning').innerHTML = '0'

    set_disabled_all_elements_by_class('add_button');
    set_disabled_all_elements_by_class('start_stop_time');
    delete_disabled_all_elements_by_class('name')

    canEditScore = false;
}

$(document).ready(function() {
    $("body").keyup(function(evt) {
        if (evt.which === 192 && disableKeypressHandling) {
            disableKeypressHandling = false;
        } else if (evt.which === 192 && !disableKeypressHandling) {
            disableKeypressHandling = true;
        } else if (disableKeypressHandling) {
            return;
        }

        if (!canEditScore) {
            return
        }

        switch (evt.which) {
            // case 32: //space bar
            //     pressed_timer(document.getElementById('start_stop_time_button'));
            //     break;
            case 81: // q
                add_point('blue', 'score', 1);
                break;
            case 65: // a
                add_point('blue', 'score', -1);
                break;
            case 69: // e
                add_point('red', 'score', 1);
                break;
            case 68: // d
                add_point('red', 'score', -1);
                break;
            case 84: // t
                add_mutual_hit(1);
                break;
            case 71: // g
                add_mutual_hit(-1);
                break;

            case 73: // i
                add_time(1);
                break;
            case 85: // u
                add_time(-1);
                break;
            case 75: // k
                add_time(10);
                break;
            case 74: // j
                add_time(-10);
                break;
            case 77: // m
                add_time(60);
                break;
            case 78: // n
                add_time(-60);
                break;
        }
    });
    default_values()
    connect()
});

function pressed_timer(button) {
    if (button.value == 'Старт время') {
        start_countdown()
    } else {
        button.value = 'Старт время'
        stop_countdown()
    }
}

function countdown_step() {
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
}

function start_countdown() {
    document.getElementById('start_stop_time_button').value = 'Стоп время';
    set_disabled_all_elements_by_class('start_stop_fight');
    //countdown_step(); TODO find better solution
    timerId = setInterval(countdown_step, 1000);
}

function stop_countdown() {
    document.getElementById('start_stop_time_button').value = 'Старт время';
    delete_disabled_all_elements_by_class('start_stop_fight');
    clearInterval(timerId)
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

function stop_fight(button) {
    default_values();
    button.value = 'Начать бой';
    send_time('00:00');
}

function start_stop_fight() {
    var button = document.getElementById('start_stop_fight_button')
    if (button.value == 'Закончить бой') {
        var timer = document.getElementById('time');
        if (timer.innerHTML !== '00:00') {
            var dialog = document.getElementById('stop_fight_dialog');
            dialog.showModal();
        } else {
            stop_fight(button);
        }
    } else {

        delete_disabled_all_elements_by_class('add_button');
        delete_disabled_all_elements_by_class('start_stop_time');
        set_disabled_all_elements_by_class('name');
        canEditScore = true;


        stompClient.send("/fencing-fight-app/secretary/set-names", {}, JSON.stringify({
            redName: document.getElementById('red_name').value,
            blueName: document.getElementById('blue_name').value
        }));
        send_time(document.getElementById('time').innerHTML)

        stompClient.send("/fencing-fight-app/secretary/change-score", {}, JSON.stringify({
            fighter: 'red',
            kind: 'score',
            newValue: document.getElementById('red-score').innerHTML
        }));
        stompClient.send("/fencing-fight-app/secretary/change-score", {}, JSON.stringify({
            fighter: 'red',
            kind: 'warning',
            newValue: document.getElementById('red-score').innerHTML
        }));
        stompClient.send("/fencing-fight-app/secretary/change-score", {}, JSON.stringify({
            fighter: 'blue',
            kind: 'score',
            newValue: document.getElementById('red-score').innerHTML
        }));
        stompClient.send("/fencing-fight-app/secretary/change-score", {}, JSON.stringify({
            fighter: 'blue',
            kind: 'warning',
            newValue: document.getElementById('red-score').innerHTML
        }));

        button.value = 'Закончить бой'
    }
}

function set_stop_fight(stop_fight_value) {
    var dialog = document.getElementById('stop_fight_dialog');
    dialog.close();
    if (stop_fight_value) {
        stop_fight(document.getElementById('start_stop_fight_button'));
    }
}