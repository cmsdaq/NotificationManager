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
    newControllerStatusArrived(null);
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
        //console.log("Recovery data: " + JSON.stringify(message.body));

        var jsonBody = JSON.parse(message.body);
        var recoveryProcedure = jsonBody.lastProcedureStatus;
        var executorState = jsonBody.executorState;

        //console.log("Recovery procedure data: " + JSON.stringify(recoveryProcedure));
        //console.log("Service status: " + JSON.stringify(executorState));
        if(executorState)
            newControllerStatusArrived(executorState);
        if(recoveryProcedure)
            newRecoveryDataArrived(recoveryProcedure);
    });

    stompClient.send("/app/status", {});
};

function stompConnect() {
    console.log('STOMP: Attempting connection');
    // recreate the stompClient to use a new WebSocket
    socket = new SockJS(controllerSocketAddress);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, stompSuccessCallback, stompFailureCallback);
}

function interrupt(){
    console.log("Sending interrupt request");
    stompClient.send("/app/interrupt", {});
}


/**
 * Confirm whole recovery
 */
function confirm(idToConfirm){
    console.log("Confirm recovery procedure: " + idToConfirm);
    stompClient.send("/app/approve", {}, JSON.stringify(
        {"recoveryProcedureId":idToConfirm, "procedureContext":true, "approved":true}));
}

/**
 * Confirm single step of the recovery
 */
function confirmStep(procedureId, stepId){
    console.log("Confirm recovery job, step " + stepId + " of procedure "+ procedureId);
    stompClient.send("/app/approve", {}, JSON.stringify(
        {"recoveryProcedureId":procedureId, "step": stepId, "approved":true}));

}

function playFromStep(procedureId, stepId){
    console.log("Recovery will continue automatically from step " + stepId + " of procedure "+ procedureId);
    //stompClient.send("/app/approve", {}, JSON.stringify({"recoveryId":procedureId, "step": stepId, "approved":true}));

}

function reject(idToConfirm){
    console.log("Rejected: " + idToConfirm);
    stompClient.send("/app/approve", {}, JSON.stringify({"recoveryId":idToConfirm, "approved":false}));
    recoveryDecision(idToConfirm, false);
}
