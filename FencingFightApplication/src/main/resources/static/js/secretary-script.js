var timerId = null;
var stompClient = null;
var canEditScore = false;
var disableKeypressHandling = false;
var pageId = null;

function connect() {
    const urlParams = new URLSearchParams(window.location.search);
    pageId = urlParams.get('id');

    if (pageId == null) {
        alert('There is no page ID. Please, add a value ?id=<id> manually and reload the page!');
        return
    }

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

function add_point(fighter, addition, evt) {
    if (evt && evt.screenX === 0 && evt.screenY === 0) {
        return;
    }

    var number_field
    if (fighter == 'red') {
        number_field = document.getElementById('red-score')
    } else {
        number_field = document.getElementById('blue-score')
    }
    var score = new Number(number_field.innerHTML)
    score += addition

    score = score >= 0 ? score : 0;

    number_field.innerHTML = score.toString()
    stompClient.send("/fencing-fight-app/secretary/change-score/" + pageId, {}, JSON.stringify({
        fighter: fighter,
        newValue: score
    }));
}

function send_time(time) {
    stompClient.send("/fencing-fight-app/secretary/change-time/" + pageId, {}, JSON.stringify({
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
    document.getElementById('red_name').value = ''
    document.getElementById('blue_name').value = ''
    document.getElementById('red-score').innerHTML = '0'
    document.getElementById('blue-score').innerHTML = '0'

    set_disabled_all_elements_by_class('add_button');
    set_disabled_all_elements_by_class('start_stop_time');
    delete_disabled_all_elements_by_class('name')

    canEditScore = false;
}

$(document).ready(function() {
    default_values();
    connect();
    $('body').keyup(function(evt) {
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
            case 32: //space bar
                pressed_timer(document.getElementById('start_stop_time_button'));
                break;
            case 81: // q
                add_point('blue', 1);
                break;
            case 65: // a
                add_point('blue', -1);
                break;
            case 69: // e
                add_point('red', 1);
                break;
            case 68: // d
                add_point('red', -1);
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
});

function pressed_timer(button, evt) {
    if (evt && evt.screenX === 0 && evt.screenY === 0) {
        return;
    }

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

function add_time(value, evt) {
    if (evt && evt.screenX === 0 && evt.screenY === 0) {
        return;
    }

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

function start_stop_fight(evt) {
    if (evt && evt.screenX === 0 && evt.screenY === 0) {
        return;
    }

    var button = document.getElementById('start_stop_fight_button')
    if (button.value == 'Закончить бой') {
        var timer = document.getElementById('time');
        if (timer.innerHTML !== '00:00') {
            var dialog = document.getElementById('stop_fight_dialog');
            canEditScore = false;
            dialog.showModal();
        } else {
            stop_fight(button);
        }
    } else {

        delete_disabled_all_elements_by_class('add_button');
        delete_disabled_all_elements_by_class('start_stop_time');
        set_disabled_all_elements_by_class('name');
        canEditScore = true;


        stompClient.send("/fencing-fight-app/secretary/set-names/" + pageId, {}, JSON.stringify({
            redName: document.getElementById('red_name').value,
            blueName: document.getElementById('blue_name').value
        }));
        send_time(document.getElementById('time').innerHTML)

        stompClient.send("/fencing-fight-app/secretary/change-score/" + pageId, {}, JSON.stringify({
            fighter: 'red',
            newValue: document.getElementById('red-score').innerHTML
        }));
        stompClient.send("/fencing-fight-app/secretary/change-score/" + pageId, {}, JSON.stringify({
            fighter: 'blue',
            newValue: document.getElementById('red-score').innerHTML
        }));

        button.value = 'Закончить бой'
    }
}

function set_stop_fight(stop_fight_value) {
    var dialog = document.getElementById('stop_fight_dialog');
    canEditScore = true;
    dialog.close();
    if (stop_fight_value) {
        stop_fight(document.getElementById('start_stop_fight_button'));
    }
}