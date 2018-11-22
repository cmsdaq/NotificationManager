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
            childType: PastStep,
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
 * Recovery step that is used for past conditions/recoveries
 */
function PastStep(action) {
    const step = React.createElement(UpdatedMessage, {element:{description:action.text}});
    const element = React.createElement('div', {className: ""}, step);

    return React.createElement(
        'li',
        {
            className: 'list-group-item'
        },
        element
    );
}