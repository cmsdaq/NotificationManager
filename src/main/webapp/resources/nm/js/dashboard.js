var eventsToKeep = 10;
var conditionsToKeep = 5;

var eventsData = [];
var conditionsData = [];
var currentConditionId = null;
var currentConditionObject = null;
var lastDominatingConditionId = null;

var currentVersion = null;
var websocketDeclaredVersion = null;
var durationSinceLastOngoingCondition = 0;

var timeToKeepTheLastSuggestion = 20000;


$(document).ready(function () {
    renderApp();
});


function UpdatedMessage(props) {
    var split = props.element.description.split(/[\b>>\b|\b<<\b]+/);
    var key = props.element.id;

    var partsOfMessage = [];
    var highlight = false;

    $.each(split, function (index, item) {
        props = {key: ('m' + key + '-' + index)};
        if (highlight) {
            props = Object.assign({}, props, {className: 'highlight'});
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

function FormattedDate(props) {
    var dateString = '-';
    if (props && props.date) {
        dateString = moment(props.date).format('YYYY-MM-DD HH:mm:ss');
    }
    return React.createElement('small', {className: "text-muted"}, dateString);
}


function EventElement(event) {
    const updatedMessage = React.createElement(UpdatedMessage, {element: event});
    const dateElement = React.createElement(FormattedDate, {date: event.timestamp});


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
        extraRow = React.createElement('div', {className: "row"}, icon, " ", textToSpeechInfo);
    }

    const headElement = React.createElement('div', {className: "row"}, titleElement, rightCornerInfo);
    const bottomElement = React.createElement('div', {className: "row"}, descriptionElement);

    return React.createElement('li', {
            className: 'list-group-item highlight-info'
        },
        headElement, extraRow, bottomElement
    );
}

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


    const dateElement = React.createElement(FormattedDate, {date: condition.timestamp})


    const rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement, " ", statusElement);

    const headElement = React.createElement('div', {className: "row"}, titleElement, rightCornerInfo);
    const bottomElement = React.createElement('div', {className: "row"}, descriptionElement, buttonRequestShowAction, buttonRequestHideAction, actionElement);

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


const decreaseEvents = function () {
    setAndRerender(conditionsToKeep, eventsToKeep - 1)
};
const increaseEvents = function () {
    setAndRerender(conditionsToKeep, eventsToKeep + 1)
};
const decreaseConditions = function () {
    setAndRerender(conditionsToKeep - 1, eventsToKeep)
};
const increaseConditions = function () {
    setAndRerender(conditionsToKeep + 1, eventsToKeep)
};

function setAndRerender(newConditionsToKeep, newEventsToKeep) {
    conditionsToKeep = newConditionsToKeep;
    eventsToKeep = newEventsToKeep;
    console.log("number of elements to keep: " + conditionsToKeep + " " + eventsToKeep);
    renderApp();
}

function ListSizeSelectorPanel(props) {

    const increaseButton = React.createElement('a', {
        onClick: props.increaseFunction,
    }, "+");
    const decreaseButton = React.createElement('a', {
        onClick: props.decreaseFunction,
    }, "-");


    const currentValue = React.createElement('span', {className: "text-muted"}, " ", props.max, " ");
    return React.createElement('span', {}, decreaseButton, currentValue, increaseButton);
}

function ListPanel(props) {


    var elementsList = [];

    if (props.elements && props.elements.length > 0) {
        props.elements.forEach(function (event) {

            const element = React.createElement(props.childType, Object.assign({}, event, {key: event.id}));
            if (props.reverse)
                elementsList.unshift(element);
            else {
                elementsList.push(element);
            }
        });
    } else {
        elementsList = React.createElement('div', {className: "alert alert-info"}, props.emptyMessage);
    }

    const sizeSelection = React.createElement('div', {className: "pull-right"}, props.sizeSelector);

    const eventsHeader = React.createElement('small', {className: "text-muted"}, props.header, sizeSelection);

    return React.createElement('ul', {
            className: "list-group"
        },
        eventsHeader, elementsList
    );
}

function ConditionPanel(props) {

    const listSizeSelectorPanel = React.createElement(ListSizeSelectorPanel, {
        increaseFunction: increaseConditions,
        decreaseFunction: decreaseConditions,
        max: conditionsToKeep
    });

    return React.createElement(ListPanel,
        {
            elements: props.conditions,
            childType: ConditionElement,
            header: "Conditions",
            emptyMessage: "No conditions at the moment",
            sizeSelector: listSizeSelectorPanel,
            reverse: true
        });

}


function EventPanel(props) {

    const listSizeSelectorPanel = React.createElement(ListSizeSelectorPanel, {
        increaseFunction: increaseEvents,
        decreaseFunction: decreaseEvents,
        max: eventsToKeep
    });

    return React.createElement(ListPanel, {
        elements: props.events,
        childType: EventElement,
        header: "Events",
        emptyMessage: "No events at the moment",
        sizeSelector: listSizeSelectorPanel,
        reverse: true
    });

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

    if (!active(props)) {
        statusLabel = "label-success";
    }

    if (props.duration && props.duration >= 0) {
        var durationVal = props.duration;
        if (durationVal < 1000) {
            duration = durationVal + " ms";
        } else if (durationVal >= 1000 && durationVal < 60000) {
            duration = (durationVal / 1000).toPrecision(2) + " s";
        } else {
            duration = Math.floor(durationVal / 1000 / 60) + " min";
        }
    }

    const icon = React.createElement('span', {className: 'glyphicon glyphicon-time'});
    return React.createElement('span', {className: "label " + statusLabel}, icon, " ", duration);
}

function ActionElement(action) {

    const icon = React.createElement('span', {className: 'glyphicon glyphicon-hand-right'});

    const actionStep = React.createElement('span', {}, icon, " ", action.text);

    const element = React.createElement('div', {className: "row"}, actionStep);


    return React.createElement('li', {
            className: 'list-group-item'
        },
        element
    );
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

function CurrentPanel(props) {

    //console.log("Updating current");
    var title, description, dateElement, statusElement, rightCornerInfo, stateIndicator, action;
    var key = 'empty';
    var highlight = '';
    var background = '';

    if (props.current) {

        highlight = 'highlight';
        background = (active(props.current) ? " bg-active " : " bg-finished ");
        props.current.announced = true;
        key = props.current.id;

        const finishedSymbol = React.createElement('span', {className: 'glyphicon glyphicon-ok'});
        const ongoingSymbol = React.createElement('span', {className: 'glyphicon glyphicon-exclamation-sign'});

        stateIndicator = React.createElement('span', {className: ('label ' + (active(props.current) ? " label-danger " : " label-success"))}, ((active(props.current) ? ongoingSymbol : finishedSymbol )), " ", (active(props.current) ? "CURRENT PROBLEM" : "FINISHED" ));


        title = React.createElement('h1', {className: 'display-5'}, props.current.title);
        description = React.createElement(UpdatedMessage, {element: props.current});

        statusElement = React.createElement(Duration, props.current);

        dateElement = React.createElement(FormattedDate, {date: props.current.timestamp});
        rightCornerInfo = React.createElement('span', {className: "pull-right"}, dateElement, " ", statusElement);

        //console.log("has action: " + JSON.stringify(props.current));


        action = React.createElement(ListPanel,
            {
                elements: generateConditionActionIds(props.current),
                childType: ActionElement,
                header: "Steps to recover",
                emptyMessage: "No recovery suggestion",
            });


    } else {
        title = React.createElement('h1', {className: 'display-5'}, "All ok");
        description = React.createElement('p', {className: 'lead'}, "DAQExpert has no suggestion at the moment");
        key = 'empty';

    }


    const headElement = React.createElement('div', {className: "row"}, React.createElement('div', {className: "col-xs-12"}, stateIndicator, rightCornerInfo));
    const bottomElement = React.createElement('div', {className: "row"}, React.createElement('div', {className: "col-xs-12"}, title, description, action));

    return React.createElement('div', {
            className: ("jumbotron " + highlight + " " + background), key: key,
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
    if (idToUse == null || idToUse == 0) {
        idToUse = lastDominatingConditionId;
    }

    conditionsData.forEach(function (item) {
        if (idToUse && idToUse > 0 && item.id === idToUse) {
            item.focused = true;
            current = item;
            currentConditionObject = current;
        } else {
            dataToShow.push(item);
        }
    });


    ReactDOM.render(React.createElement(Dashboard, {
            "events": eventsData,
            "conditions": dataToShow,
            "current": current
        }),
        document.getElementById('react-list-container')
    );
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
            $.extend(item, update);
            found = true;
            foundItem = item;
        }
    });
    if (found) {
        renderApp();
    }
}

function newVersionDataArrived(version) {
    //console.log("New version available: " + version);
    websocketDeclaredVersion = version;
    if (currentVersion == null) {
        //console.log("First connect to websocket, establishing current version as " + version);
        currentVersion = websocketDeclaredVersion;
    }
}


setInterval(function () {
    updateDuration();
}, 100);


function updateDuration() {

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


function updateSelected(id) {
    //console.log("Updating selected condition, id= " + id);
    lastDominatingConditionId = currentConditionId;
    currentConditionId = id;

    if (id != 0) {
        durationSinceLastOngoingCondition = 0;
    }

    renderApp();
}

setInterval(function () {
    if (currentConditionId == null || currentConditionId == 0) {
        durationSinceLastOngoingCondition += 5000;
        //console.log("Nothing happening for " + durationSinceLastOngoingCondition + " ms");

        if (lastDominatingConditionId != 0 && durationSinceLastOngoingCondition > timeToKeepTheLastSuggestion) {
            //console.log("Last condition is no longer needed");
            lastDominatingConditionId = 0;
            renderApp();
        }
    }

}, 5000);

