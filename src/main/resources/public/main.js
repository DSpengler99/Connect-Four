// Hinweis für ausführen von mehreren requests hintereinander: https://pis2020.slack.com/archives/C012JM9QXTK/p1595075383432200?thread_ts=1594990115.426900&cid=C012JM9QXTK
// Senden von mehreren Requests (Beispiel): https://www.w3schools.com/js/js_ajax_http_response.asp

function sendRequest(url, cFunction) {
    var http = new XMLHttpRequest();
    http.onreadystatechange = function() {
        if(this.status == 200 && this.readyState == 4) {
            cFunction(http);
        }
    };
    http.open('GET', url);
    http.send();
}

// Callback nur zum ausgeben der response
function refreshGame(http) {
    document.getElementById('game').innerHTML = http.responseText;
}

// Wird einmal vor Beginn des Spiels aufgerufen um das Spielbrett und den Beginner zu generieren
function startGameWithPlayer() {
    sendRequest('startGame?beginner=-1', refreshGame);
}

function startGameWithAI() {
    sendRequest('startGame?beginner=1', requestAIMove);
}

// Wird jedes Mal aufgerufen, wenn der Spieler einen Zug macht
function sendColumn(col) {
    sendRequest('sendMove?move='+col, requestAIMove);
}

// Per Request den Computer einen Zug ausführen lassen
function requestAIMove(http) {
    refreshGame(http);
    sendRequest('action?perform=aiMove', refreshGame);
}

// Besten Zug für den Spieler machen lassen
function requestBestMove() {
    sendRequest('action?perform=bestMove', requestAIMove);
}

// Zurücksetzen von Zügen
function requestUndo() {
    sendRequest('action?perform=undo', refreshGame);
}

function prepareTests() {
    sendRequest('test?task=prepare', runTests)
}

function runTests(http) {
    document.getElementById('game').innerHTML = http.responseText;
    sendRequest('test?task=run', refreshGame)
}