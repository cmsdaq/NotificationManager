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
        if(currentConditionId && currentConditionId != 0){
            return "condition"; // if no recovery but exists non 0 condition than condition mode
        } else{
            if(lastRecovery){
                return "recovery";
            } else {
                if(lastDominatingConditionId && lastDominatingConditionId != 0){
                    return "condition";
                }
                else{
                    return "empty";
                }
            }
        }
    }
}

function Dashboard(props) {

    var versionMessageElement = null;
    if (currentVersion !== websocketDeclaredVersion) {
        showingVersion = true;
        const exclamation = React.createElement('span', {className: 'glyphicon glyphicon-exclamation-sign'});
        const versionText = React.createElement('span', {}, "New version available, please hard reload the browser to update the cached scripts. Version available: " + websocketDeclaredVersion + ", currently loaded version: " + currentVersion);
        const versionMessage = React.createElement('p', {}, exclamation, " ", versionText);
        versionMessageElement = React.createElement('div', {className: 'alert alert-warning', id: 'message'}, versionMessage);
    } else{
        showingVersion = false;
    }

    const autoUpdate = React.createElement('a',{id:'btn-auto-update', className:"btn btn-top btn-lg btn-primary btn-block"}, "Back to auto update mode");

    const icon = React.createElement('span', {className: 'glyphicon glyphicon-chevron-down'});

    const showMoreConditions = React.createElement('a',{id:'btn-show-more-condition', className:"row btn btn-default btn-bottom btn-xs"},icon);
    const currentPanel = React.createElement('div', {className: ""}, React.createElement(CurrentPanel, props));
    const conditionScrollable = React.createElement('div', {className: "pre-scrollable", id:'condition-scrollable'}, showMoreConditions,currentPanel, React.createElement(ConditionPanel, props));
    const leftPanel = React.createElement('div', {className: "col-md-8"}, conditionScrollable);




    const showMoreEvents = React.createElement('a',{id:'btn-show-more-event', className:"row btn btn-default btn-bottom btn-xs"}, icon);
    const testSoundSystemPanel = React.createElement(TestSoundSystemPanel, {});
    const eventsScrollable = React.createElement('div',{className:"pre-scrollable", id:'event-scrollable'},testSoundSystemPanel,React.createElement(EventPanel, props));
    const rightPanel = React.createElement('div', {className: "col-md-4"}, showMoreEvents, eventsScrollable);


    const pageContent = React.createElement('div', {className: 'row auto-scroll-holder'},autoUpdate, leftPanel, rightPanel);

    return React.createElement('div', {}, versionMessageElement, pageContent);


}

function TestSoundSystemPanel(props){
    var highlight = highlightCheck();
    var testSoundSystemPanel;
    const button = React.createElement('button',
         {
             className:"pull-right btn btn-sm " + (highlight?"btn-danger":"btn-default"),
             type:"button",
             onClick: function () {
                 console.log("Firing audio alarm");
                 issueTestAlarm();
             },
         },  React.createElement('span', {className:"glyphicon glyphicon-volume-up"}), "Issue test audio alarm");


    if(highlight){
        const icon = React.createElement('span', {className:"glyphicon glyphicon-exclamation-sign"});
        testSoundSystemPanel = React.createElement('div', {className: "alert alert-warning clearfix"}, icon, "Please check the sound system at the start of your shift", button ) ;
    } else{


        testSoundSystemPanel =  React.createElement('div', {className: "clearfix"}, button );
    }
    return testSoundSystemPanel;
}


function CurrentPanel(props) {

    //console.log("Updating current");
    var title, description, dateElement, statusElement, rightCornerInfo, stateIndicator, automatedRecovery, action;
    var key = 'empty';
    var highlight = '';
    var background = '';

    const problemBackground = "bg-active";
    const finishedBackground = "bg-finished";
    const recoveringBackground = "bg-recovery";



    if (props.mode == "condition" || props.mode == "recovery") {

        highlight = 'highlight';

        const finishedSymbol = React.createElement('span', {className: 'glyphicon glyphicon-ok'});
        const ongoingSymbol = React.createElement('span', {className: 'glyphicon glyphicon-exclamation-sign'});
        const progressIcon = React.createElement('span', {className:"glyphicon glyphicon-refresh glyphicon-refresh-animate"});



        switch(props.mode){
            case "recovery":

                if(props.recovery.disabled){
                    background = finishedBackground ;
                    stateIndicator = React.createElement('span', {className: ('label label-success')}, "RECOVERY FINISHED" );
                } else{
                    background = recoveringBackground ;
                    stateIndicator = React.createElement('span', {className: ('label label-danger')}, "RECOVERING" );
                }

                // show differend state depending on recovery

                statusElement = React.createElement(Duration, props.recovery);

                break;
            case "condition":
                background = (active(props.current) ? problemBackground : finishedBackground);
                stateIndicator = React.createElement('span', {className: ('label ' + (active(props.current) ? " label-danger " : " label-success"))}, ((active(props.current) ? ongoingSymbol : finishedSymbol )), " ", (active(props.current) ? "CURRENT PROBLEM" : "FINISHED" ));
                statusElement = React.createElement(Duration, props.current);

                break;

        }

        props.current.announced = true;
        key = props.current.id;



        title = React.createElement('h1', {className: 'display-5'}, props.current.title);
        description = React.createElement(UpdatedMessage, {element: props.current});


        dateElement = React.createElement(FormattedDate, {date: props.current.timestamp});
        rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement, " ", statusElement);

        //console.log("has action: " + JSON.stringify(props.current));


        if(props.recovery){
            //console.log("Automated recovery on");
            automatedRecovery = React.createElement(AutomatedRecovery, props );
        }

        action = React.createElement(ListPanel,
            {
                elements: generateConditionActionIds(props.current),
                childType: ActionElement,
                header: "Steps to recover",
                emptyMessage: "No recovery suggestion",
                recovery: props.recovery,
                recoveryStatus: props.recoveryStatus,
            });


    } else {
        title = React.createElement('h1', {className: 'display-5'}, "All ok");
        description = React.createElement('p', {className: 'lead'}, "DAQExpert has no suggestion at the moment");
        key = 'empty';

    }


    const headElement = React.createElement('div', {className: "row"}, React.createElement('div', {className: "col-xs-12"}, stateIndicator, rightCornerInfo));
    const bottomElement = React.createElement('div', {className: "row"}, React.createElement('div', {className: "col-xs-12"}, title, description, automatedRecovery,action));

    return React.createElement('div', {
            className: ("jumbotron " + highlight + " " + background), key: key,
        },
        headElement, bottomElement
    );

}

/**
 * Panel with recent-condition list
 */
function ConditionPanel(props) {

    return React.createElement(ListPanel,
        {
            elements: props.conditions,
            childType: ConditionElement,
            header: "Recent problems",
            emptyMessage: "No recent problems at the moment",
            reverse: true
        });

}

/**
 * Panel with recent event list
 */
function EventPanel(props) {

    return React.createElement(ListPanel, {
        elements: props.events,
        childType: EventElement,
        header: "Recent events",
        emptyMessage: "No recent events at the moment",
        reverse: true
    });

}

/**
 * Element of the recent-conditions list
 */
function ConditionElement(condition) {

    const updatedMessage = React.createElement(UpdatedMessage, {element: condition});
    const titleElement = React.createElement('span', {className: ""}, condition.title);


    const statusElement = React.createElement(Duration, condition);

    const descriptionElement = React.createElement('small', {className: ""}, updatedMessage);


    action = React.createElement(ListPanel,
        {
            elements: generateConditionActionIds(condition),
            childType: ActionElement,
            emptyMessage: "No recovery suggestion",
        });

    const buttonRequestShowAction = React.createElement('small', {}, React.createElement('a', {
        className: (condition.requestedShow ? "collapse": ""),
        onClick: function () {
            newUpdateDataArrived({id:condition.id, requestedShow: true});
        }
    }, " Show steps"));
    const buttonRequestHideAction = React.createElement('small', {}, React.createElement('a', {
        className: (condition.requestedShow ? "": "collapse"),
        onClick: function () {
            newUpdateDataArrived({id:condition.id, requestedShow: false});
        }
    }, " Hide steps"));


    const actionElement = React.createElement('small', {className: (condition.requestedShow ? "" : "collapse")}, action);


    const dateElement = React.createElement(FormattedDate, {date: condition.timestamp, link: true});


    const rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement, " ", statusElement);

    const headElement = React.createElement('div', {className: ""}, titleElement, rightCornerInfo);
    const bottomElement = React.createElement('div', {className: ""}, descriptionElement, buttonRequestShowAction, buttonRequestHideAction, actionElement);

    var highlight = '';
    if (condition.announced === false) {
        highlight = 'highlight';
    }

    return React.createElement('li', {
            className: 'list-group-item  ' + highlight
        },
        headElement, bottomElement
    );
}


/**
 * Element used in current panel for suggestin the actions
 */
function ActionElement(action) {

    var manualStep = true;
    var stepIcon = null;
    var approveRequestReceived = false;

    var actionId = action.id.split('-')[1];

    var executionDetails = null;
    var stepControlers = null;

    var recoveryStepStatus = null;

    var currentStep;

    /* Go through recovery data and check if this step may be automated*/
    if(action.recovery){
        //console.log("Automated recovery available: " + JSON.stringify(action.recovery.automatedSteps));
        for(var i = 0; i <  action.recovery.automatedSteps.length; i++){

            var current = action.recovery.automatedSteps[i];
            if(current.stepIndex == actionId){

                manualStep = false;
                recoveryStepStatus = current.status;
                currentStep = current;
            }
        }
        if(action.recovery.approvalRequested && action.recovery.approvalRequested == true){
            approveRequestReceived = true;
        }
    }


    if(action.recoveryStatus){
        if(action.recoveryStatus.stepStatuses[actionId]){
            currentStep = action.recoveryStatus.stepStatuses[actionId];
        }
    }



    if(manualStep){
        stepIcon = React.createElement('span', {className: 'glyphicon glyphicon-hand-up'});
    } else{
        stepIcon = React.createElement('span', {className: 'glyphicon glyphicon-cog'});
        var stepControlerMode;

        if(action.recovery.disabled){
            stepControlerMode = "disabled";
        } else if(recoveryStepStatus == "recovering"){
            executionDetails = React.createElement(RecoveryExecutionDetails,{currentStep:currentStep });
            stepControlerMode = "progress";
        } else{
            if(approveRequestReceived){
                stepControlerMode = "enabled";

            }else{
                stepControlerMode = "disabled";
            }
        }

        stepControlers = React.createElement(ActionElementControl,{mode: stepControlerMode, stepIndex: actionId, recoveryId: action.recovery.id, timesExecuted:currentStep.timesExecuted });

    }


    const step = React.createElement(UpdatedMessage, {element:{description:action.text}});
    const element = React.createElement('div', {className: ""}, stepIcon, " ", step, stepControlers);
    const elementWrapper = React.createElement('div', {className: ""}, element, executionDetails);


    return React.createElement('li', {
            className: 'list-group-item'
        },
        elementWrapper
    );
}

function RecoveryExecutionDetails(props){

    //console.log("Generating " + JSON.stringify(props));

    const time = React.createElement('span', {className:"pull-right"}, React.createElement(Duration,{duration: props.currentStep.duration ,status:"ongoing", customColor:"label-warning"})) ;
    const headline = React.createElement('h5',{}, "Recovery details", time);
    const t1key = React.createElement('dt',{},"Suggested");
    const t1val = React.createElement('dd',{},props.currentStep.suggested);

    const t2key = React.createElement('dt',{},"Started");
    const t2date = React.createElement(FormattedDate, {date:props.currentStep.started});
    const t2val = React.createElement('dd',{},t2date);

    const t3key = React.createElement('dt',{},"Finished");
    const t3date = React.createElement(FormattedDate, {date:props.currentStep.finished});
    const t3val = React.createElement('dd',{},t3date);

    const t4key = React.createElement('dt',{},"Automator status");
    const t4val = React.createElement('dd',{},props.currentStep.rcmsStatus);

    const params = React.createElement('dl',{'className':'dl-horizontal'},t1key, t1val,t2key, t2val, t3key, t3val, t4key, t4val);
    const body = React.createElement('span',{},params);
    return  React.createElement('div', {className:"bs-callout bs-callout-warning"}, headline, body);
}

/**
 * shows appropriate buttons next to action
 *  - enabled button ready for approval
 *  - disabled button
 *  - disabled button indicating progress
 *
 */
function ActionElementControl(props){

    const progressIcon = React.createElement('span', {className:"glyphicon glyphicon-refresh glyphicon-refresh-animate"});
    const playIcon = React.createElement('span', {className:"glyphicon glyphicon-play-circle"});
    const buttonText = " Execute step";
    var executionCounter;

    if(props.timesExecuted && props.timesExecuted > 0){
        executionCounter = React.createElement('span',{className: "text-muted"}, "Executed ", React.createElement('span', {className:"badge badge-light"}, props.timesExecuted));
    }
    var button;

    //console.log("Preparing controll button from properties " + JSON.stringify(props));
    const stepIndex = props.stepIndex;
    const recoveryId = props.recoveryId;

    if(props.mode == "enabled"){
        button = React.createElement('button',
            {
                id:'accept-step',
                className:"btn btn-success btn-xs",
                type:"button",
                onClick: function () {
                    console.log("Confirming step: " + recoveryId + ':' + stepIndex);
                    confirmStep(recoveryId, stepIndex);
                },
            }, playIcon, buttonText);
    } else if(props.mode == "progress"){
        button = React.createElement('button',{className:"btn btn-warning btn-xs disabled", type:"button"}, progressIcon, " Executing...");

    } else if(props.mode == "disabled"){
        button = React.createElement('button',{className:"btn btn-warning btn-xs disabled", type:"button"}, playIcon, buttonText);
    }

    return React.createElement('span', {className:"pull-right"}, executionCounter, " ", button);
}




function EventElement(event) {
    const updatedMessage = React.createElement(UpdatedMessage, {element: event});
    const dateElement = React.createElement(FormattedDate, {date: event.timestamp, link: false});


    const titleElement = React.createElement('span', {className: ""}, event.title);
    const descriptionElement = React.createElement('small', {className: ""}, updatedMessage);

    var soundInformationElement = null;
    if (event.sound) {
        const icon = React.createElement('span', {className: 'glyphicon glyphicon-volume-up'});
        soundInformationElement = React.createElement('span', {className: "label label-info"}, icon, " ", event.sound);
    }

    const rightCornerInfo = React.createElement('span', {className: "pull-right"}, soundInformationElement, " ", dateElement);

    var extraRow = null;
    if (event.tts) {
        const textToSpeechInfo = React.createElement('span', {className: "text-muted"}, event.tts);
        const icon = React.createElement('span', {className: 'glyphicon glyphicon-volume-up text-info'});
        extraRow = React.createElement('div', {className: ""}, icon, " ", textToSpeechInfo);
    }

    const headElement = React.createElement('div', {className: ""}, titleElement, rightCornerInfo);
    const bottomElement = React.createElement('div', {className: ""}, descriptionElement);

    return React.createElement('li', {
            className: 'list-group-item highlight-info'
        },
        headElement, extraRow, bottomElement
    );
}



function ListPanel(props) {


    var elementsList = [];

    if (props.elements && props.elements.length > 0) {
        props.elements.forEach(function (event) {

            const element = React.createElement(props.childType, Object.assign({}, event, {key: event.id, conditionId: props.conditionId, recovery: props.recovery, recoveryStatus: props.recoveryStatus}));
            if (props.reverse)
                elementsList.unshift(element);
            else {
                elementsList.push(element);
            }
        });
    } else {
        elementsList = React.createElement('div', {className: "alert alert-info"}, props.emptyMessage);
    }


    const eventsHeader = React.createElement('small', {className: "text-muted"}, props.header);

    return React.createElement('ul', {
            className: "list-group"
        },
        eventsHeader, elementsList
    );
}




/**
 * General information about recovery that is displayed inside CurrentPanel
 */
function AutomatedRecovery(props){
    var content;


    const message = React.createElement('span', {}, "Automatic recovery available!");
    const buttonGroup = React.createElement('div', {});
    const acceptButton = React.createElement('button',{onClick: function () {
        confirm(props.recovery.id);
    } ,id:'accept-recovery', className:"btn btn-success btn-xs", type:"button"}, "Approve");
    const rejectButton = React.createElement('button',{onClick: function () {
        reject(props.recovery.id);

    }, id:'reject-recovery', className:"btn btn-danger btn-xs"}, "Reject");
    content = React.createElement('span', {}, message);


    const icon = React.createElement('span', {className:"glyphicon glyphicon-exclamation-sign"});
    return React.createElement('div', {className:"alert alert-info", role:"alert"}, icon, content);
}



function active(current) {
    if (current && current.status && current.status === 'finished') {
        return false;
    }
    return true;
}

function Duration(props) {

    var duration = "";
    var statusLabel = "label-danger";

    if(props.customColor){
        statusLabel = props.customColor;
    }

    if (!active(props)) {
        statusLabel = "label-success";
    }

    if (props.duration && props.duration >= 0) {
        var durationVal = props.duration;

        var md = moment.duration(durationVal);
        var hours = md.get('hours');
        var minutes = md.get('minutes');
        var seconds = md.get('seconds');
        var millisecods = md.get('milliseconds');

        if(hours > 0){
            duration = hours + " h " + minutes + " min " + seconds + " s";
        } else if (minutes > 0){
            duration = minutes + " min " + seconds + " s";
        } else if (seconds >= 10){
            duration = seconds + " s";
        } else if (seconds > 0){
            duration = seconds + "." + Math.floor(millisecods/100) +  " s";
        } else {
            duration = millisecods + " ms";
        }

        //
        // if (durationVal < 1000) {
        //     duration = durationVal + " ms";
        // } else if (durationVal >= 1000 && durationVal < 60000) {
        //     duration = (durationVal / 1000).toPrecision(2) + " s";
        // } else if (durationVal >= 60000 && durationVal < 3600000){
        //     var minutes = Math.floor(durationVal / 1000 / 60);
        //     var seconds = Math.floor((durationVal - minutes*1000*60)/1000);
        //     duration =  minutes + " min " + seconds + " s";
        // } else if (durationVal >= 60000 && durationVal < 3600000){
        //     var minutes = Math.floor(durationVal / 1000 / 60);
        //     var seconds = Math.floor((durationVal - minutes*1000*60)/1000);
        //     duration =  minutes + " min " + seconds + " s";
        // }
    }

    const icon = React.createElement('span', {className: 'glyphicon glyphicon-time'});
    return React.createElement('span', {className: "label " + statusLabel}, icon, " ", duration);
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





function UpdatedMessage(props) {

    var highlighted = props.element.description;
    var preventHighlight = props.element.preventHighlight;

    if(!preventHighlight) {
        highlighted = highlighted.replace(/<strong>/g,'<strong class="highlight">');
    }

    return React.createElement('span', {dangerouslySetInnerHTML: {__html: highlighted}});
}

function FormattedDate(props) {
    var dateString = '-';
    var dateLink = null;
    var linkAvailable = false;
    if (props && props.date) {
        dateString = moment(props.date).format('YYYY-MM-DD HH:mm:ss');

        if(daqViewUrl){
            dateLink = React.createElement('a',{
                href:(daqViewUrl+'?setup='+daqSetup+'&time='+moment(props.date).format('YYYY-MM-DD\'T\'HH:mm:ss')),
                target:"_blank"
            },dateString);
            linkAvailable = true;
        }
    }

    return React.createElement('small', {className: "text-muted"}, (props.link && linkAvailable)?dateLink:dateString);
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
            "mode": mode
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


function newRecoveryDataArrived(newRecovery){
    console.log("Current recovery: " +  JSON.stringify(newRecovery));
    currentRecovery = newRecovery;

    if(currentRecovery.status && currentRecovery.status == "finished"){
        lastRecovery = currentRecovery;
        currentRecovery = null;
    } else{
        durationSinceLastRecovery = 0;
    }
    updateDuration(); // called to avoid glitches when new data arrives and duration is not calculated
    renderApp();
}

function newApprovalRequest(request){

    console.log("Approval requested for recovery: " + request.recoveryId);
    if(currentRecovery && currentRecovery.id == request.recoveryId){
        currentRecovery.approvalRequested = true;
        console.log("Found");
        return true;
    } else{
        console.log("Not found");
        return false;
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
    if (recovery.startDate && !recovery.endDate) {
        const duration = moment.duration(now.diff(recovery.startDate)).valueOf();
        recovery.duration = duration;
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
                renderApp();
            }
        }
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

