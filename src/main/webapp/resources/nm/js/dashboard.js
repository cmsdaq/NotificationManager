var eventsToKeep = 100;
var conditionsToKeep = 50;

/** Holds all data about events */
var eventsData = [];


var currentVersion = null;
var websocketDeclaredVersion = null;



/** Holds all conditions data */
var conditionsData = [];

/** Id of condition which exists in conditionData that currently dominating */
var currentConditionId = null;

/** Id of condition which exists in conditionData that was dominating recently (in order to keep for some time after) */
var lastDominatingConditionId = null;

/** Duration since last ongoing condition (in order to keep for some time after)*/
var durationSinceLastOngoingCondition = 0;

/** in the form of [procedure-id]-[step-id]*/
var recommendedRecoveryStep = null;

var timeToKeepTheLastSuggestion = 120000;

var daqViewUrl;
var daqSetup;
var showingVersion = false;


/**
 * Represents recovery that is currently ongoing
 */
var currentRecovery = null;

/** Last recovery, finished already */
var lastRecovery = null;

/**
 * Controller status
 */
var controllerStatus = null;


var durationSinceLastRecovery = 0;


$(document).ready(function () {
    daqViewUrl = $('#daq-view-url').data('url');
    daqSetup = $('#daq-view-url').data('setup');
    renderApp();
});

/**
 * Mode selection: depending on what data is available the Dashboard will look differently and it will behave differently
 */
function modeSelect(){
    if(currentRecovery){
        return "recovery"; //if exist any recovery request than we operate in recovery mode, hands down
    } else{

        // and last recovery is not related

        if(
            currentConditionId && currentConditionId != 0 &&
            (
                lastRecovery == null ||
                (
                    lastRecovery != null &&
                    !lastRecovery.conditionIds.includes(currentConditionId)
                )

            )
        ){
            //console.log("condition mode, current id: " + currentConditionId);
            return "condition"; // if no recovery but exists non 0 condition than condition mode
        } else{
            if(lastRecovery){
                return "recovery";
            } else {
                if(lastDominatingConditionId && lastDominatingConditionId != 0){
                    //console.log("condition mode, last id: " + lastDominatingConditionId);
                    return "condition";
                }
                else{
                    return "empty";
                }
            }
        }
    }
}





function active(current) {
    if (current && current.status && current.status === 'finished') {
        return false;
    }
    return true;
}


function generateConditionActionIds(condition) {
    var actionWithIds = [];
    if (condition.action) {

        for (var i = 0; i < condition.action.length; i++) {
            var element = {};
            element.text = condition.action[i];
            element.id = condition.id + "-" + i;
            actionWithIds.push(element);
        }

    }
    return actionWithIds;
}




var previousShovingVersion = false;

function renderApp() {

    var conditionToGenerate = null;
    var recoveryToGenerate = null;
    var otherConditionsToGenerate = [];

    var idToUse;

    var mode = modeSelect();

    //console.log("Current mode is: " + mode );
    switch (mode) {
        case "empty":
            break;
        case "recovery":

            if(currentRecovery) {
                recoveryToGenerate = currentRecovery;
            } else {
                recoveryToGenerate = lastRecovery;
                recoveryToGenerate.disabled = true;
            }

            //take the latest conditionId from current recovery
            idToUse = recoveryToGenerate.conditionIds[recoveryToGenerate.conditionIds.length-1];
            break;
        case "condition":
            idToUse = currentConditionId;
            if (idToUse == null || idToUse == 0) {
                idToUse = lastDominatingConditionId;
            }
            break;
    }
    //console.log("Current mode is: " + mode + " conditionId to use: " + idToUse);


    conditionsData.forEach(function (item) {
        if (idToUse && idToUse > 0 && item.id === idToUse) {
            item.focused = true;
            conditionToGenerate = item;
            //console.log("Condition to generate: " + conditionToGenerate);
        } else {
            otherConditionsToGenerate.push(item);
        }
    });


    //console.log("Generating with: " + JSON.stringify(conditionToGenerate));
    //console.log("Generating with: " + JSON.stringify(recoveryToGenerate));

    ReactDOM.render(React.createElement(Dashboard, {
            "events": eventsData,
            "conditions": otherConditionsToGenerate,
            "current": conditionToGenerate,
            "recovery": recoveryToGenerate,
            "mode": mode,
            "controllerStatus": controllerStatus
        }),
        document.getElementById('react-list-container')
    );

    if(showingVersion != previousShovingVersion){
        updateScrollable();
    }
    previousShovingVersion = showingVersion;

}

function newEventsDataArrived(event) {
    eventsData.push.apply(eventsData, event);
    eventsData = eventsData.splice(-eventsToKeep, eventsToKeep);
    renderApp();
}

function newConditionsDataArrived(condition) {

    //console.log("New Condition data arrived to REACT: " + JSON.stringify(condition));
    conditionsData.push.apply(conditionsData, condition);
    conditionsData = conditionsData.splice(-conditionsToKeep, conditionsToKeep);
    renderApp();

}

function newControllerStatusArrived(status){
    controllerStatus = status;
    renderApp();
}

function newRecoveryDataArrived(newRecovery){
    //console.log("Current recovery: " +  JSON.stringify(newRecovery));
    currentRecovery = newRecovery;

    if(
        currentRecovery.endDate != null
    ){
        lastRecovery = currentRecovery;
        currentRecovery = null;
    } else{
        durationSinceLastRecovery = 0;
    }
    updateDuration(); // called to avoid glitches when new data arrives and duration is not calculated
    renderApp();
}

function newApprovalRequest(request){

    console.log("Approval requested for recovery: " + JSON.stringify(request));
    if(currentRecovery){
        console.log("Existing recovery: " + JSON.stringify(currentRecovery.id));
        if(currentRecovery.id == request.recoveryProcedureId){
            currentRecovery.approvalRequested = true;
            recommendedRecoveryStep = request.recoveryProcedureId +"-"+ request.defaultStepIndex;
            console.log("Found");
            renderApp();
            return true;
        } else{
            console.log("Not found");
            return false;
        }
    } else{
        console.log("No recovery exists, ignoring approval request");
    }

}

function recoveryDecision(id, approved){
    if(currentRecovery.id == id){
        if(approved){
            currentRecovery.status = "approved";
        }else{
            currentRecovery.status = "rejected";
        }
        renderApp();
    }
}

function recoveryTimeout(id){
    if(currentRecovery !== null && currentRecovery.id == id){
        currentRecovery.status = "timeout";
        renderApp();
    }
}

/**
 * Update of Condition
 */
function newUpdateDataArrived(update) {
    var found = false;
    var foundItem;
    conditionsData.forEach(function (item) {
        if (item.id == update.id) {
            $.extend(item, {preventHighlight:true});
            found = true;
        }
    });
    if (found) {
        renderApp();
    }

    setTimeout(function () {

        conditionsData.forEach(function (item) {
            if (item.id == update.id) {
                //item = Object.assign({}, item, update);
                $.extend(item, update);
                $.extend(item, {preventHighlight:false});
                //item.description = update.description;
                found = true;
                foundItem = item;
            }
        });
        eventsData.forEach(function (item) {
            if (item.id == update.id) {
                $.extend(item, update);
                found = true;
                foundItem = item;
            }
        });
        if (found) {
            renderApp();
        }

    }, 10);
}

function newVersionDataArrived(version) {
    //console.log("New version available: " + version);
    websocketDeclaredVersion = version;
    if (currentVersion == null) {
        //console.log("First connect to websocket, establishing current version as " + version);
        currentVersion = websocketDeclaredVersion;
    }
    renderApp();
}


setInterval(function () {
    updateDuration();
}, 100);



/** Updates durations of conditions and recoveries */
function updateDuration() {

    const now = moment();

    updateConditionsDurations(now);

    if(currentRecovery) {
        updateRecoveryDuration(now, currentRecovery);
    }

    if(lastRecovery){
        updateRecoveryDuration(now, lastRecovery);
    }
}

function handleControllerDisconnected() {
    currentRecovery = null;
}

/**
 * Update durations of all conditions
 */
function updateConditionsDurations(now){

    conditionsData.forEach(function (item) {

        if (item.status !== "finished") {
            //console.log("There is a current suggestion");
            var currentStart = moment(item.timestamp);

            //console.log("Start date is " + currentStart);
            var duration = moment.duration(now.diff(currentStart)).valueOf();
            //console.log("Durations in seconds: " + duration);
            item.duration = duration;

            renderApp();
        }
    });
}

/**
 * Update duration of recovery
 * @param recovery
 */
function updateRecoveryDuration(now, recovery){
    var recalculated = false;
    if (recovery.startDate && !recovery.endDate) {
        const duration = moment.duration(now.diff(recovery.startDate)).valueOf();
        recovery.duration = duration;
        recalculated = true;
    } else if (recovery.startDate && recovery.endDate){
        const duration = moment.duration(moment(recovery.endDate).diff(recovery.startDate)).valueOf();
        //console.log("Calculating duration " + duration + " based on " + recovery.startDate + " and " + recovery.endDate);
        recovery.duration = duration;
    }

    if (recovery.automatedSteps) {
        //console.log("There is recovery");
        for (var i = 0; i < recovery.automatedSteps.length; i++) {

            var calculate = false;
            var start, end;
            var o = recovery.automatedSteps[i];
            //console.log("Checking steps");
            if (o.started && !o.finished) {
                start = moment(o.started);
                end = moment();
                calculate = true;
            } else if (o.finished && !o.duration) {
                start = moment(o.started);
                end = moment(o.finished);
                calculate = true;
            }

            if (calculate) {
                var duration = moment.duration(end.diff(start)).valueOf();
                o.duration = duration;
                recalculated = true;
            }
        }
    }

    if(recalculated){
        renderApp();
    }
}


function updateSelected(id) {
    //console.log("Updating selected condition, id= " + id);
    lastDominatingConditionId = currentConditionId;
    currentConditionId = id;

    if (id != 0) {
        durationSinceLastOngoingCondition = 0;
    }

    renderApp();
}

/** Removes last condition/recovery after specific period of time */
setInterval(function () {
    if (currentConditionId == null || currentConditionId == 0) {
        durationSinceLastOngoingCondition += 1000;
        //console.log("Nothing happening for " + durationSinceLastOngoingCondition + " ms");

        if (lastDominatingConditionId != 0 && durationSinceLastOngoingCondition > timeToKeepTheLastSuggestion) {
            console.log(durationSinceLastOngoingCondition + " ms passed since last condition, removing it from main panel");
            lastDominatingConditionId = 0;
            renderApp();
        }
    }

    if(!currentRecovery){

        durationSinceLastRecovery += 1000;

        if(lastRecovery && durationSinceLastRecovery > timeToKeepTheLastSuggestion){
            console.log(durationSinceLastRecovery + " ms passed since last recovery, removing it from main panel");
            lastRecovery = null;
            renderApp();
        }

    }

}, 1000);

