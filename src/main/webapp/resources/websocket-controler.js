var socket = null;
var stompClient;
var controllerSocketAddress;

$( document ).ready(function() {
    controllerSocketAddress = document.getElementById(
        "controller-socket-address").getAttribute("url");

    stompConnect();
    $("#approve" ).click(confirm);
    $("#reject" ).click(reject);
});

var stompFailureCallback = function (error) {
    handleControllerDisconnected();
    console.log('STOMP: ' + error);
    setTimeout(stompConnect, 10000);
    console.log('STOMP: Reconecting in 10 seconds');
};

var stompSuccessCallback = function (frame) {
    console.log('STOMP: Connection successful');
    stompClient.subscribe('/topic/approveRequests', function (message) {
        console.log("Approve request: " + JSON.stringify(message.body));
        const found = newApprovalRequest(JSON.parse(message.body));
        if(!found){
            console.log("Approve request was not processed successfully, will retry in 1 second");
            setTimeout(function(){
                newApprovalRequest(JSON.parse(message.body));
            }, 1000);
        }
    });

    stompClient.subscribe('/topic/recovery-status', function (message) {
        console.log("Recoveyr data: " + JSON.stringify(message.body));
        newRecoveryDataArrived(JSON.parse(message.body));
    });
};

function stompConnect() {
    console.log('STOMP: Attempting connection');
    // recreate the stompClient to use a new WebSocket
    socket = new SockJS(controllerSocketAddress);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, stompSuccessCallback, stompFailureCallback);
}


/**
 * Confirm whole recovery
 */
function confirm(idToConfirm){
    console.log("Confirmed: " + idToConfirm);
    stompClient.send("/app/approve", {}, JSON.stringify({"id":idToConfirm, "approved":true}));
    recoveryDecision(idToConfirm, true);
}

/**
 * Confirm single step of the recovery
 */
function confirmStep(procedureId, stepId){
    console.log("Confired step " + stepId + " of recovery "+ procedureId);
    stompClient.send("/app/approve", {}, JSON.stringify({"recoveryId":procedureId, "step": stepId, "approved":true}));

}

function reject(idToConfirm){
    console.log("Rejected: " + idToConfirm);
    stompClient.send("/app/approve", {}, JSON.stringify({"recoveryId":idToConfirm, "approved":false}));
    recoveryDecision(idToConfirm, false);
}
