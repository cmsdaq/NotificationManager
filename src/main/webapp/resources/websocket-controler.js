var socket = null;
var stompClient;


$( document ).ready(function() {
    stompConnect();
    $("#approve" ).click(confirm);
    $("#reject" ).click(reject);
});

var stompFailureCallback = function (error) {
    console.log('STOMP: ' + error);
    setTimeout(stompConnect, 10000);
    console.log('STOMP: Reconecting in 10 seconds');
};

var stompSuccessCallback = function (frame) {
    console.log('STOMP: Connection successful');
    stompClient.subscribe('/topic/greetings', function (message) {
        onNewRecovery(JSON.parse(message.body));
    });

    stompClient.subscribe('/topic/timeout', function (message) {
        onRecoveryTimeout(JSON.parse(message.body));
    });
};

function stompConnect() {
    console.log('STOMP: Attempting connection');
    // recreate the stompClient to use a new WebSocket
    socket = new SockJS('http://localhost:8082/recovery');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, stompSuccessCallback, stompFailureCallback);
}


function confirm(idToConfirm){
    console.log("Confirmed: " + idToConfirm);
    stompClient.send("/app/approve", {}, JSON.stringify({"id":idToConfirm, "approved":true}));
    recoveryDecision(idToConfirm, true);
}

function reject(idToConfirm){
    console.log("Rejected: " + idToConfirm);
    stompClient.send("/app/approve", {}, JSON.stringify({"id":idToConfirm, "approved":false}));
    recoveryDecision(idToConfirm, false);
}

function onNewRecovery(message) {

    var recid = message.id;
    console.log("Recovery id: " + recid + " for problem id: " + message.problemId);

    var recoveryData = message;
    recoveryData.status = "new";
    newRecoveryDataArrived(recoveryData);

}

function onRecoveryTimeout(id){
  console.log("Recovery timeout, id: " + id);
  recoveryTimeout(id);

}
