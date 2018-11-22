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

    const controllerStatus = React.createElement(ControllerStatusPanel,props);

    const eventsScrollable = React.createElement('div',{className:"pre-scrollable", id:'event-scrollable'},
        controllerStatus,
        testSoundSystemPanel,
        React.createElement(EventPanel, props));

    const rightPanel = React.createElement('div', {className: "col-md-4"}, showMoreEvents, eventsScrollable);


    const pageContent = React.createElement('div', {className: 'row auto-scroll-holder'},autoUpdate, leftPanel, rightPanel);

    return React.createElement('div', {}, versionMessageElement, pageContent);


}


function CurrentPanel(props) {


    if(!props.current)
        return null;

    var title, description, dateElement, statusElement, rightCornerInfo, stateIndicator, automatedRecovery, action, recoverySummary;
    var key = 'empty';
    var highlight = '';
    var background = '';

    const problemBackground = "bg-active";
    const finishedBackground = "bg-finished";
    const recoveringBackground = "bg-recovery";



    if (props.mode == "condition" || props.mode == "recovery") {

        var dashboardStatus;

        highlight = 'highlight';

        const finishedSymbol = React.createElement('span', {className: 'glyphicon glyphicon-ok'});
        const ongoingSymbol = React.createElement('span', {className: 'glyphicon glyphicon-exclamation-sign'});
        const progressIcon = React.createElement('span', {className:"glyphicon glyphicon-refresh glyphicon-spin"});



        switch(props.mode){
            case "recovery":

                //console.log("Recovery: " + JSON.stringify(props));

                if(
                    props.controllerStatus &&
                    props.controllerStatus.toLowerCase() == "Idle".toLowerCase() &&
                    props.recovery.endDate
                ){
                    background = finishedBackground ;
                    stateIndicator = React.createElement('span', {className: ('label label-success')}, props.recovery.status );
                    dashboardStatus = 'finished';
                } else{
                    background = recoveringBackground ;
                    stateIndicator = React.createElement('span', {className: ('label label-danger')}, props.recovery.status );
                    dashboardStatus = 'ongoing';
                }

                // show differend state depending on recovery

                statusElement = React.createElement(Duration, props.recovery);

                break;
            case "condition":
                background = (active(props.current) ? problemBackground : finishedBackground);
                dashboardStatus = (active(props.current) ? 'ongoing' : 'finished');
                stateIndicator = React.createElement('span', {className: ('label ' + (active(props.current) ? " label-danger " : " label-success"))}, ((active(props.current) ? ongoingSymbol : finishedSymbol )), " ", (active(props.current) ? "CURRENT PROBLEM" : "FINISHED" ));
                statusElement = React.createElement(Duration, props.current);

                break;

        }

        props.current.announced = true;
        key = props.current.id;


        var progressIndicator = null;


        if (
            props.mode == "recovery" &&
            props.controllerStatus &&
            (
            props.controllerStatus.toLowerCase() == "Observe".toLowerCase() ||
            props.controllerStatus.toLowerCase() == "Recovering".toLowerCase()
            )
        ){
            //progressIndicator = React.createElement('span', {className:'text-muted glyphicon glyphicon-refresh glyphicon-spin'});
            progressIndicator = React.createElement(ProgressIndicator, {});
        }

        title = React.createElement('h1', {className: 'display-5 ' + dashboardStatus == 'finished' ? 'text-muted' : ''}, props.current.title);
        description = React.createElement(UpdatedMessage, {element: props.current});


        dateElement = React.createElement(FormattedDate, {date: props.current.timestamp});
        rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement, " ", statusElement);

        //console.log("has action: " + JSON.stringify(props.current));


        if(props.recovery){
            if(
                props.controllerStatus &&
                props.controllerStatus.toLowerCase() == "Idle".toLowerCase()
            ){

                automatedRecovery = React.createElement(RecoverySummary, props.recovery);
            }
            else if(
                props.controllerStatus &&
                props.controllerStatus.toLowerCase() == "AwaitingApproval".toLowerCase()
            ){
                automatedRecovery = React.createElement(AutomatedRecovery, props);
            }
        }



        action = React.createElement(
            ListPanel,
            {
                elements: generateConditionActionIds(props.current),
                childType: RecoveryStepElement,
                header: "Steps to recover",
                emptyMessage: "No recovery suggestion",
                recovery: props.recovery,
                controllerStatus: props.controllerStatus,
            }
        );


    } else {
        title = React.createElement('h1', {className: 'display-5'}, "All ok");
        description = React.createElement('p', {className: 'lead'}, "DAQExpert has no suggestion at the moment");
        key = 'empty';

    }

    var interruptButton = null;
    if(props.controllerStatus && props.controllerStatus.toLowerCase() != "Idle".toLowerCase()){

        interruptButton = React.createElement(
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

    const titleWrapper = React.createElement(
        'div',
        {className:'row'},
        React.createElement(
            'div',
            {className:'col-xs-10 vcenter'},
            title
        ),
        React.createElement(
            'div',
            {className:'col-xs-2 vcenter'},
            React.createElement(
                'div',
                {className:'pull-right'},
                interruptButton,
                progressIndicator


            )
        )
    );

    const headElement = React.createElement('div', {className: "row"}, React.createElement('div', {className: "col-xs-12"}, stateIndicator, rightCornerInfo));
    const bottomElement = React.createElement('div', {className: "row"}, React.createElement('div', {className: "col-xs-12"}, titleWrapper, description, automatedRecovery, action));

    return React.createElement('div', {
            className: ("jumbotron " + highlight + " " + background), key: key,
        },
        headElement, bottomElement
    );

}





function ListPanel(props) {


    var elementsList = [];

    if (props.elements && props.elements.length > 0) {
        props.elements.forEach(function (event) {

            const element = React.createElement(props.childType, Object.assign({}, event, {key: event.id, conditionId: props.conditionId, recovery: props.recovery,controllerStatus: props.controllerStatus}));
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


function Duration(props) {

    var duration = "";
    var statusLabel = "label-danger";

    if(props.customColor){
        statusLabel = props.customColor;
    }

    if (!active(props)) {
        statusLabel = "label-success";
    }

    duration = getDurationPrintable(props.duration);


    const icon = React.createElement('span', {className: 'glyphicon glyphicon-time'});
    return React.createElement('span', {className: "label " + statusLabel}, icon, " ", duration);
}

function getDurationPrintable(durationVal){
    var duration = "?";
    if (durationVal && durationVal >= 0) {

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

    }
    return duration;
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

function ProgressIndicator(){
    const child1 = React.createElement('div', {className:'double-bounce1'});
    const child2 = React.createElement('div', {className:'double-bounce2'});
    const parent = React.createElement('div', {className:'spinner'}, child1, child2);
    return parent;
}