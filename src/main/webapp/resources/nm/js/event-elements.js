
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
