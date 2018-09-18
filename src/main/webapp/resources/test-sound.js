function issueTestAlarm(){

    console.log("Sending post request for test alarm");
    var data = {};
    data.message = "Manually issued audio alarm in order to verify the sound system in CR";
    data.title = "Test alarm";
    data.eventType = "Single";
    data.eventSenderType = "External";
    data.sender = "Dashboard client";
    data.textToSpeech = "Sound system check";
    data.sound = "U2Bell.wav";
    data.date = moment();

    $.ajax({
        'type': 'POST',
        'url': 'rest/events',
        'contentType': 'application/json',
        'data': JSON.stringify([data]),
        'dataType': 'json'
        });

}