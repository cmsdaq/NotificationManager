
var nextCheck;

function highlightCheck(){

    var now = moment();

    if(nextCheck == null || now > nextCheck){
        return true;
    } else{
        return false;
    }

}

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

    nextCheck = getNextShiftStartTime(moment());
    console.log("Next sound system check scheduled for " + nextCheck.toISOString(true));
}

function getNextShiftStartTime(time){

	if(0 <= time.hours() && time.hours() < 7){
    	//night shift
        return time.hours(7).minutes(0).seconds(0).milliseconds(0);
    }
    else if(7 <= time.hours() && time.hours() < 15){
        // morning shift
        return time.hours(15).minutes(0).seconds(0);
    } else if (15 <= time.hours() && time.hours() < 23){
        // evening shift
        return time.hours(23).minutes(0).seconds(0).milliseconds(0);
    } else{
        // night shift
        return time.add(1, 'day').hours(7).minutes(0).seconds(0).milliseconds(0);
    }
}
