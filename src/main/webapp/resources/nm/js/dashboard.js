const eventsToKeep = 10;
const conditionsToKeep = 5;

var eventsData = [];
var conditionsData = [];
var currentConditionId = null;
var currentConditionObject = null;

var lastDominatingConditionId = null;

var currentVersion = null;
var websocketDeclaredVersion = null;


$(document).ready(function () {
    renderApp();
});

function UpdatedMessage(props){
    var split = props.element.description.split(/[\b>>\b|\b<<\b]+/);
    var key = props.element.id;

    var partsOfMessage = [];
    var highlight = false;

    $.each(split, function( index, item ) {
        props = {key: ('m'+key + '-'+ index + '-' + item)};
        if(highlight){
            props = Object.assign({}, props, { className: 'highlight' });
        }
        partsOfMessage.push(
            React.createElement('span',
                props, item
            )
        );
        highlight = !highlight;
    });
    return React.createElement('span', {className: ""}, partsOfMessage);
}

function FormattedDate(props){
    var dateString = '-';
    if(props && props.date){
    	//console.log("Trying to parse " + props.date);
        dateString = moment(props.date).format('YYYY-MM-DD HH:mm:ss');
    }
    return React.createElement('small', {className: "text-muted"}, dateString );
}



function EventElement(event){
    const updatedMessage = React.createElement(UpdatedMessage, {element:event});
    const dateElement = React.createElement(FormattedDate, {date: event.timestamp});


    const idElement = React.createElement('span', {}, event.id + ': ');
    const titleElement = React.createElement('span', {className: ""}, event.title);
    const descriptionElement = React.createElement('small', {className: ""}, updatedMessage);
    const rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement);


    const headElement = React.createElement('div', {className: "row"},idElement, titleElement, rightCornerInfo);
    const bottomElement = React.createElement('div', {className: "row"}, descriptionElement);

    return  React.createElement('li', {
            className: 'list-group-item highlight-info'
        },
        headElement, bottomElement
    );
}

function ConditionElement(condition){


    const idElement = React.createElement('span', {}, condition.id + ': ');
    const updatedMessage = React.createElement(UpdatedMessage, {element:condition});
    const titleElement = React.createElement('span', {className: ""}, condition.title);


    const statusElement = React.createElement(Duration, condition);

    const descriptionElement = React.createElement('small', {className: ""}, updatedMessage);
    const dateElement = React.createElement(FormattedDate, {date: condition.timestamp})


    const rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement, " ",statusElement);



    const headElement = React.createElement('div', {className: "row"},idElement, titleElement, rightCornerInfo);
    const bottomElement = React.createElement('div', {className: "row"}, descriptionElement);

    var highlight = '';
    if(condition.announced === false){
        highlight = 'highlight';
    }

    return  React.createElement('li', {
            className: 'list-group-item  ' + highlight
        },
        headElement, bottomElement
    );
}


function ConditionPanel(props){
    var conditionsList = [];
    if (props.conditions && props.conditions.length > 0) {
        props.conditions.forEach(function (condition) {

            var listElement = React.createElement(ConditionElement, Object.assign({}, condition, { key: condition.id }));


            //$(listElement).animate({backgroundColor: '#FF0000'}, 'slow');
            conditionsList.unshift(
                listElement
            );
        });
    } else {
        conditionsList = React.createElement('div', {className: "col-md-12"}, "Conditions list is empty");
    }

    return React.createElement('ul', {
            className: "list-group"
        },
        conditionsList
    );
}

function EventPanel(props){
    var eventsList = [];
    if (props.events && props.events.length > 0) {
        props.events.forEach(function (event) {

            eventsList.unshift(
               React.createElement(EventElement, Object.assign({}, event, { key: event.id }))
            );
        });
    } else {
        eventsList = React.createElement('div', {className: "col-md-12"}, "Events list is empty");
    }

    return React.createElement('ul', {
            className: "list-group"
        },
        eventsList
    );
}

function active(current){
    if(current && current.status && current.status === 'finished'){
        return false;
    }
    return true;
}

function Duration(props){

    var duration = "";
    var statusLabel = "label-danger";

    if(!active(props)){
        statusLabel = "label-success";
    }

    if(props.duration && props.duration >=0){
        var durationVal = props.duration;
        if(durationVal < 1000){
            duration = durationVal + " ms";
        } else if (durationVal >= 1000 && durationVal < 60000) {
            duration = (durationVal/1000).toPrecision(2) + " s";
        } else {
            duration = Math.floor(durationVal/1000/60) + " min";
        }
    }

    return React.createElement('span', {className: "label " + statusLabel}, duration);
}


function CurrentPanel(props){

    //console.log("Updating current");
    var title, description, dateElement, statusElement, rightCornerInfo, stateIndicator;
    var key = 'empty';
    var highlight = '';
    var background = '';

    if(props.current){

        highlight = 'highlight';
        background = (active(props.current)?" bg-active ":" bg-finished ");
        props.current.announced = true;


        stateIndicator = React.createElement('span',{className:('label ' + (active(props.current)?" label-danger ":" label-success")) }, (active(props.current)?"CURRENT PROBLEM":"FINISHED" ));


        title = React.createElement('h1', {className:'display-5'}, props.current.title);
        description = React.createElement(UpdatedMessage, {element:props.current});

        statusElement = React.createElement(Duration, props.current);

        dateElement = React.createElement(FormattedDate, {date: props.current.timestamp});
        rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement, " ",statusElement);
        key = props.current.id;



    } else{
        title = React.createElement('h1', {className:'display-5'}, "All ok");
        description = React.createElement('p', {className:'lead'}, "DAQExpert has no suggestion at the moment");
        key = 'empty';

    }



    const headElement = React.createElement('div', {className: "row"},  React.createElement('div',{className: "col-xs-12"}, stateIndicator,rightCornerInfo));
    const bottomElement = React.createElement('div', {className: "row"},React.createElement('div',{className: "col-xs-12"},title, description));

    return React.createElement('div', {
            className: ("jumbotron " + highlight + " " +  background), key:key,
        },
        headElement, bottomElement
    );

}


function Dashboard(props) {

    var versionMessageElement = null;
    if (currentVersion !== websocketDeclaredVersion) {
        const exclamation = React.createElement('span', {className: 'glyphicon glyphicon-exclamation-sign'});
        const versionText = React.createElement('span', {}, "New version available, please hard reload the browser to update the cached scripts. Version available: " + websocketDeclaredVersion + ", currently loaded version: " + currentVersion);
        const versionMessage = React.createElement('p', {}, exclamation, " ", versionText);
        versionMessageElement = React.createElement('div', {className: 'alert alert-warning'}, versionMessage);
    }


    const currentPanel = React.createElement('div', {className: ""}, React.createElement(CurrentPanel, props));
    const leftPanel = React.createElement('div', {className: "col-md-8"}, currentPanel, React.createElement(ConditionPanel, props));
    const rightPanel = React.createElement('div', {className: "col-md-4"}, React.createElement(EventPanel, props));


    const pageHead = React.createElement('div', {className: 'row'}, versionMessageElement);
    const pageContent = React.createElement('div', {className: 'row'}, leftPanel, rightPanel);

    return React.createElement('div', {}, pageHead, pageContent);


}

function renderApp() {

    var current = null;
    var dataToShow = [];
    
    var idToUse = currentConditionId;
    if(idToUse == null || idToUse == 0){
    	idToUse = lastDominatingConditionId;
    }

        conditionsData.forEach(function (item) {
            if (idToUse && idToUse > 0 && item.id === idToUse) {
                item.focused = true;
                current = item;
                currentConditionObject = current;
            } else{
                dataToShow.push(item);
            }
        });


    ReactDOM.render(React.createElement(Dashboard, {
            "events": eventsData,
            "conditions": dataToShow,
            "current" : current
        }),
        document.getElementById('react-list-container')
    );
    
}


function newEventsDataArrived(events) {
	//console.log("New Condition data arrived to REACT: " + JSON.stringify(event));
    eventsData.push.apply(eventsData, events);
    eventsData = eventsData.splice(-eventsToKeep, eventsToKeep);
    renderApp();
}

function newConditionsDataArrived(conditions) {
	//console.log("New Condition data arrived to REACT: " + JSON.stringify(condition));
    conditionsData.push.apply(conditionsData, conditions);
    conditionsData = conditionsData.splice(-conditionsToKeep, conditionsToKeep);
    renderApp();
    
    		
}


/**
 * Update of Condition
 */
function newUpdateDataArrived(update) {
    var found = false;
    var foundItem;
    conditionsData.forEach(function (item) {
        if (item.id == update.id) {
            //item = Object.assign({}, item, update);
            $.extend(item, update);
            //item.description = update.description;
            found = true;
            foundItem = item;
        }
    });
    eventsData.forEach(function (item) {
        if (item.id == update.id) {
            $.extend( item, update );
            found = true;
            foundItem = item;
        }
    });
    if (found) {
        renderApp();
    }
}


var idCounter = 0;

function updateSelected(id){
	lastDominatingConditionId = currentConditionId;
	currentConditionId = id;
	
	if(id != 0){
		durationSinceLastOngoingCondition = 0;
	}
	
	renderApp();
}

function newVersionDataArrived(version) {
    console.log("New version available: " + version);
    websocketDeclaredVersion = version;
    if (currentVersion == null) {
        console.log("First connect to websocket, establishing current version as " + version);
        currentVersion = websocketDeclaredVersion;
    }
}

var durationSinceLastOngoingCondition = 0;


setInterval(function () {
	if(currentConditionId == null || currentConditionId == 0){
		durationSinceLastOngoingCondition += 5000;
		console.log("Nothing happening for " + durationSinceLastOngoingCondition + " ms");
		
		if(lastDominatingConditionId != 0 && durationSinceLastOngoingCondition > 10000){
			console.log("Last condition is no longer needed");
			lastDominatingConditionId = 0;
			renderApp();
		}
	}
	
}, 5000);


/**
 * Add new Event
 */
//setInterval(function () {
//    var newData = [{
//        id: idCounter++,
//        title: sampleEvents[idCounter % 10].title,
//        description: sampleEvents[idCounter % 10].description,
//        timestamp: moment()
//    }];
//    newEventsDataArrived(newData);
//}, 1800);


/**
 * ADD new Critical Condition
 */
//setInterval(function () {
//
//    var id = idCounter++;
//
//    currentConditionId = id;
//
//    var newData = [{
//        id: id,
//        title: sampleConditions[id%7].title,
//        description: sampleConditions[id%7].description,
//        status: "ongoing",
//        duration: 0,
//        timestamp: moment(),
//        announced: false
//    }];
//    newConditionsDataArrived(newData);
//    randomizeUpdate(id);
//
//
//}, 20000);

/**
 * ADD new Condition
 */
//setInterval(function () {
//
//    var id = idCounter++;
//
//    var newData = [{
//        id: id,
//        title: sampleConditions[id%7].title,
//        description: sampleConditions[id%7].description,
//        status: "ongoing",
//        duration: 0,
//        timestamp: moment(),
//        announced: false
//    }];
//    newConditionsDataArrived(newData);
//    randomizeUpdate(id);
//
//
//}, 12000);

/**
 *
 * Update Condition after random time
 */
function randomizeUpdate(id){
    var random = Math.random() * 15000;

    setTimeout(function () {
        var updateData = {
            id: id,
            status: "finished",
        };
        newUpdateDataArrived(updateData);
    }, 2*random)

    setTimeout(function () {
        var updateData = {
            id: id,
            description: sampleUpdatedConditions[id%7].description,
        };
        newUpdateDataArrived(updateData);
    }, random)
}



setInterval(function () {
    updateDuration();
}, 100);


function updateDuration(){

    conditionsData.forEach(function (item) {


        if (item.status !== "finished") {
            //console.log("There is a current suggestion");
            var currentStart = moment(item.timestamp);
            var now = moment();

            //console.log("Start date is " + currentStart);
            var duration = moment.duration(now.diff(currentStart)).valueOf();
            //console.log("Durations in seconds: " + duration);
            item.duration = duration;

            renderApp();
        }
    });


}


const sampleEvents = [{title: "Started: Run ongoing", description: "Run is ongoing according to TCDS state"},
    {title: "TCDS State: Running", description: "New TCDS state identified"},
    {title: "Level Zero State: Running", description: "New Level zero state identified"},
    {title: "DAQ state: Running", description: "New DAQ state identified"},
    {title: "DAQ state: Starting", description: "New DAQ state identified"},
    {title: "Level Zero State: Starting", description: "New Level zero state identified"},
    {title: "Level Zero State: Recovering", description: "New Level zero state identified"},
    {title: "Level Zero State: Undefined", description: "New Level zero state identified"},
    {title: "Run: 302492", description: "New run has been identified"},
    {title: "TCDS State: Configured", description: "New TCDS state identified"},];

const sampleConditions = [{title: "Deadtime during run", description:"There is deadtime during running"},
    {title: "FED deadtime", description:"Deadtime of fed(s) 853 in subsystem(s) CSC is greater than 5.0%"},
    {title: "Partition deadtime", description:"Deadtime of partition(s) CSC- in subsystem(s) CSC is greater than 5.0%"},
    {title: "Warning in subsystem", description:"TTCP CSC+ of CSC subsystem is in warning 50.01877, it may affect rate."},
    {title: "Corrupted data received", description:"Run blocked by corrupted data from FED 622 received by RU ru-c2e14-29-01.cms which is now in failed state. Problem FED belongs to partition EB- in ECAL subsystem This causes backpressure at FED 1386 in partition MUTFUP of TRG"},
    {title: "Fed stuck", description:"TTCP EB+ of ECAL subsystem is blocking trigger, it's in BUSY TTS state, The problem is caused by FED 632 in BUSY"},
    {title: "Rate too high", description:"The readout rate is 106552.0 Hz which is above the expected maximum 100000.0 Hz. This may be a problem with the L1 trigger."},
    ]

const sampleUpdatedConditions = [{title: "Deadtime during run", description:"There is <<critical>> deadtime during running"},
    {title: "FED deadtime", description:"Deadtime of fed(s) <<853>> in subsystem(s) <<CSC>> is greater than <<5.0%>>"},
    {title: "Partition deadtime", description:"Deadtime of partition(s) <<CSC+>> in subsystem(s) <<CSC>> is greater than <<5.0%>>"},
    {title: "Warning in subsystem", description:"TTCP <<CSC->> of CSC subsystem is in warning <<80>>, it may affect rate."},
    {title: "Corrupted data received", description:"Run blocked by corrupted data from FED <<632>> received by RU ru-c2e14-29-01.cms which is now in failed state. Problem FED belongs to partition <<EB+>> in ECAL subsystem This causes backpressure at FED 1386 in partition MUTFUP of TRG"},
    {title: "Fed stuck", description:"TTCP EB+ of ECAL subsystem is blocking trigger, it's in BUSY TTS state, The problem is caused by FED <<622>> in <<WARNING>>"},
    {title: "Rate too high", description:"The readout rate is <<119382.0 Hz>> which is above the expected maximum 100000.0 Hz. This may be a problem with the L1 trigger."},
]


