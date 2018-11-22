

function ControllerStatusPanel(props){

    const title =  React.createElement('h5', {className:""}, "Controller state");
    var state = null;
    var button = null;
    if(props.controllerStatus == null){
        state =  React.createElement('span', {className:""}, "Not available");
    } else{
        state =  React.createElement('span', {className:"badge badge-primary"}, props.controllerStatus);
    }

    if(props.controllerStatus && props.controllerStatus.toLowerCase() != "Idle".toLowerCase()){
        button = React.createElement(
            'button',
            {
                className:"btn",
                onClick: function(){
                    console.log("interrupt requested");
                    interrupt();
                }
            },
            "Interrupt"
        );
    }
    const card = React.createElement('div', {className:""}, title, button, state);
    return card;
}


/**
 * General information about recovery that is displayed inside CurrentPanel
 */
function AutomatedRecovery(props){
    var content;


    const message = React.createElement('span', {}, "Automatic recovery available! #" + props.recovery.id + ". Execute all steps or each step separately. ");
    const buttonGroup = React.createElement('div', {});
    const acceptButton = React.createElement('button',{onClick: function () {
        confirm(props.recovery.id);
    } ,id:'accept-recovery', className:"btn btn-success btn-xs pull-right", type:"button"}, "Execute all steps");
    const rejectButton = React.createElement('button',{onClick: function () {
        reject(props.recovery.id);

    }, id:'reject-recovery', className:"btn btn-danger btn-xs"}, "Reject");
    content = React.createElement('span', {}, message, acceptButton);


    const icon = React.createElement('span', {className:"glyphicon glyphicon-exclamation-sign"});
    return React.createElement('div', {className:"alert alert-info", role:"alert"}, icon, content);
}

function RecoverySummary(props){
    var content;

    var durationValue =  moment.duration(moment(props.endDate).diff(moment(props.startDate))).valueOf();
    const message = React.createElement(
        'span',
        {},
        "Automatic recovery #"+props.id+" has finished in "+
        getDurationPrintable(durationValue) + ", " +
        "final state is " + props.status
    );
    const summaryWrapper = React.createElement('div',{});


    var actionSummary = [];
    // generate ids if not exists
    for(var i=0; i<props.actionSummary.length; i++){
        var element = props.actionSummary[i];
        element.id = 'rs-' + i;
        //console.log("Action summary: " + JSON.stringify(element));
        actionSummary.push(element);
    }


    const summary = React.createElement(
                ListPanel,
                {
                    elements: actionSummary,
                    childType: RecoverySummaryEvent,
                    header: "Summary of recovery procedure",
                    emptyMessage: "No recovery procedure events"
                }
            );

    content = React.createElement('span', {}, message);


    const icon = React.createElement('span', {className:"glyphicon glyphicon-exclamation-sign"});
    const alertPanel = React.createElement('div', {className:"alert alert-info", role:"alert"}, icon, content);

    return React.createElement('div', {}, alertPanel, summary)

}

function RecoverySummaryEvent(props){
    var element = React.createElement('small', {}, props.content);
    var time = React.createElement('small', {className:"pull-right"}, props.date);

    return React.createElement('li', {
            className: 'list-group-item list-group-item-compact'
        },
        element, time
    );
}


/**
 * Element used in current panel for suggestin the actions
 *
 * id - id to match with current recovery
 * recovery: current recovery information
 * controllerStatus: status of the controller in order to display steps in appropriate mode
 *
 * check this:
 * conditionId: props.conditionId,
 */
function RecoveryStepElement(props) {

    //console.log("RecoveryStepElement called with: " + JSON.stringify(props));
    //console.log("Recomended: " + JSON.stringify(recommendedRecoveryStep));
    //console.log("Current: " + JSON.stringify(props.id));

    /* flag indicating if step is manual or automatic */
    var manualStep = true;

    /* Icon of the step */
    var stepIcon = null;

    /* Information about step status */
    var stepStatusSummary = null;

    var stepIndex = props.id.split('-')[1];

    /* Details about step execution */
    var executionDetails = null;

    /* Controller buttons for step */
    var stepControlers = null;

    var recommendedIndicator = null;

    /* draw attention to given step */
    var drawAttention = false;

    /* Last step index retrieved from action summary, in order to mark which step we're observing */
    var lastStepIndexFromActionSummary = null;

    /*
    This will contain:
     stepIndex
     started
     finished
     status
     rcmsStatus
     timesExecuted
    */
    var jobStatus;

    /* 1 Find step with matching index (automatedSteps=jobStatuses) */
    if(props.recovery){

        for(var i = 0; i <  props.recovery.automatedSteps.length; i++){

            var current = props.recovery.automatedSteps[i];
            if(current.stepIndex == stepIndex){

                //console.log("Automated step: " + JSON.stringify(current))
                manualStep = false;
                jobStatus = current;

            }
        }
    }


    /* 2. add step description and icon */
    const step = React.createElement(UpdatedMessage, {element:{description:props.text}});
    if(manualStep){
        stepIcon = React.createElement('span', {className: 'glyphicon glyphicon-hand-up'});
    } else{
        stepIcon = React.createElement('span', {className: 'glyphicon glyphicon-cog'});
    }

    /* 3. add step status (for all) */
    if(jobStatus){
        var executionCounter;
        if(jobStatus.timesExecuted && jobStatus.timesExecuted > 0){
            executionCounter = React.createElement(
                'span',
                {className: "text-muted pull-right"},
                "Executed ",
                React.createElement('span', {className:"badge badge-light"},
                jobStatus.timesExecuted)
            );
        }
        var lastStatus;
        if(jobStatus.status){
            lastStatus = React.createElement(
                'span',
                {className: "text-muted pull-right"},
                "Status ", React.createElement('span', {className:"badge badge-light"},
                jobStatus.status)
            );
        }
        stepStatusSummary = React.createElement('span', {}, executionCounter, lastStatus);
    }

    /* 4. add step controller if appropriate (only if awaiting-approval)*/
    if(
        !manualStep &&
        props.controllerStatus &&
        props.controllerStatus.toLowerCase() == "AwaitingApproval".toLowerCase()
    ){
        stepControlers = React.createElement(
            ActionElementControl,
            {
                stepIndex:stepIndex,
                recoveryId:props.recovery.id
            }
            );
    }

    /* 5. add step execution details if appropriate (only if recovering) */
    if(
        !manualStep &&
        props.controllerStatus &&
        props.controllerStatus.toLowerCase() == "Recovering".toLowerCase() &&

        jobStatus.status &&
        jobStatus.status.toLowerCase() == "Recovering".toLowerCase()
    ){
        executionDetails = React.createElement(
            RecoveryExecutionDetails,
            {
                duration:jobStatus.duration,
                suggested:jobStatus.suggested,
                started:jobStatus.started,
                finished:jobStatus.finished,
                rcmsStatus:jobStatus.rcmsStatus
            }
        );
    }




    /* 6. indicate that this step is recommended if appropriate (only if awaiting-approval) */
    if(
        props.controllerStatus &&
        props.controllerStatus.toLowerCase() == "AwaitingApproval".toLowerCase() &&
        recommendedRecoveryStep &&
        props.recovery &&
        recommendedRecoveryStep.split('-')[0] == props.recovery.id &&
        recommendedRecoveryStep.split('-')[1] == stepIndex

    ){
        recommendedIndicator = React.createElement('span',{className:"badge badge-success"}, "Recommended now");
        drawAttention = true;
    }

    /* 7. indicate that the system is being observed if appropriate (only if observing) */
    if(props.recovery && props.recovery.actionSummary){
        for(var i=0; i<props.recovery.actionSummary.length; i++){
            var current = props.recovery.actionSummary[i];
            if(current.stepIndex){
                lastStepIndexFromActionSummary = current.stepIndex;
            }
        }
    }
    if(
        !manualStep &&
        props.controllerStatus &&
        props.controllerStatus.toLowerCase() == "Observe".toLowerCase() &&
        jobStatus.status &&
        jobStatus.status.toLowerCase() == "Completed".toLowerCase() &&
        lastStepIndexFromActionSummary == stepIndex

    ){
        stepControlers = React.createElement('span',{className:"badge badge-warning"}, "Observing..");
    }

    /* 8. draw attention to given step if in observing or recovering */
    if(
        !manualStep &&
        props.controllerStatus &&
        jobStatus.status &&
        (
            (
                props.controllerStatus.toLowerCase() == "Observe".toLowerCase() &&
                jobStatus.status.toLowerCase() == "Completed".toLowerCase()
            ) ||
            (
                props.controllerStatus.toLowerCase() == "Recovering".toLowerCase() &&
                jobStatus.status.toLowerCase() == "Recovering".toLowerCase()

            )
        )
    ) {
        drawAttention = true;
    }

    const element = React.createElement('div', {className: ""}, stepIcon, " ", step,recommendedIndicator, stepStatusSummary, stepControlers, executionDetails);


    return React.createElement('li', {
            className: 'list-group-item ' + (drawAttention == true?'pulse':'')
        },
        element
    );
}


/**
 * Execution details displayed when step is being executed
 * - props.currentStep.duration
 * - props.currentStep.suggested
 * - props.currentStep.started
 * - props.currentStep.finished
 * - props.currentStep.rcmsStatus
 */
function RecoveryExecutionDetails(props){

    //console.log("Generating " + JSON.stringify(props));

    const time = React.createElement(
        'span',
        {className:"pull-right"},
        React.createElement(
            Duration,
            {duration: props.duration ,status:"ongoing", customColor:"label-warning"}
        )
    );
    const headline = React.createElement('h5',{}, "Recovery details", time);
//    const t1key = React.createElement('dt',{},"Suggested");
//    const t1val = React.createElement('dd',{},props.suggested);

    const t2key = React.createElement('dt',{},"Started");
    const t2date = React.createElement(FormattedDate, {date:props.started});
    const t2val = React.createElement('dd',{},t2date);

    const t3key = React.createElement('dt',{},"Finished");
    const t3date = React.createElement(FormattedDate, {date:props.finished});
    const t3val = React.createElement('dd',{},t3date);

    const t4key = React.createElement('dt',{},"Automator status");
    const t4val = React.createElement('dd',{className:"highlight"},props.rcmsStatus);

    const params = React.createElement('dl',{'className':'dl-horizontal'},/*t1key, t1val, */t2key, t2val, t3key, t3val, t4key, t4val);
    const body = React.createElement('span',{},params);
    return  React.createElement('div', {className:"bs-callout bs-callout-warning"}, headline, body);
}

function Timer(props){
    const progressIcon = React.createElement('span', {className:"glyphicon glyphicon-refresh glyphicon-refresh-animate"});
    executeButton = React.createElement('button',{className:"btn btn-warning btn-xs disabled", type:"button"}, progressIcon, " Executing...");
    return React.createElement('span', {className:"pull-right"}, executeButton);
}

/**
 * shows appropriate buttons next to action
 *  - enabled button ready for approval
 *
 */
function ActionElementControl(props){

    //console.log("Preparing control button from properties " + JSON.stringify(props));
    const stepIndex = props.stepIndex;
    const recoveryId = props.recoveryId;

    const simpleExecution = React.createElement(
        'button',
        {
            id:'accept-step',
            className:"btn btn-success btn-xs",
            type:"button",
            onClick: function () {
                console.log("Confirming step: " + recoveryId + ':' + stepIndex);
                confirmStep(recoveryId, stepIndex);
            },
        },
        React.createElement('span', {className:"glyphicon glyphicon-play-circle"}),
        'Execute step'
    );

    const playExecution = React.createElement(
        'button',
        {
            id:'execute-from-step',
            className:"btn btn-primary btn-xs",
            type:"button",
            onClick: function () {
                console.log("Playing from step: " + recoveryId + ':' + stepIndex);
                playFromStep(recoveryId, stepIndex);
            },
        },
        React.createElement('span', {className:"glyphicon glyphicon-play-circle"}),
        'Execute from'
    );

    const executeButton = React.createElement(
        'div',
        {className:"btn-group", role:"group"},
        simpleExecution/*,
        playExecution*/
    );

    return React.createElement(
        'span',
        {className:"pull-right"},
        executeButton
    );
}