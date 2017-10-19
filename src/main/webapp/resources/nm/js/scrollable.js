$(window).resize(function() {
    updateScrollable();
});

function updateScrollable(){
    var htmlHeight = $( window ).height();
    var navHeight = $('.navbar').outerHeight(true);
    var messageHeight = 0;
    if($('#message').length > 0) {
        messageHeight = $('#message').outerHeight(true);
    }
    var height = htmlHeight - navHeight -messageHeight;
    $('.pre-scrollable').css('max-height', height);
    $('.pre-scrollable').css('height', height);
}

$( document ).ready(function() {
    $('.pre-scrollable').scroll(  function() {scrollFunction()});
    $('#btn-auto-update').click(function(){topFunction()});
    $('#btn-show-more-condition').click(function(){enableScrolling($('#condition-scrollable'))});
    $('#btn-show-more-event').click(function(){enableScrolling($('#event-scrollable'))});

    updateScrollable();
});


function scrollFunction() {
    var scrollable1 = $('#condition-scrollable');
    var scrollable2 = $('#event-scrollable');
    if ( scrollable1.scrollTop() > 0 || scrollable2.scrollTop() > 0) {
        $('#btn-auto-update').css('display', "block");
    } else {
        $('#btn-auto-update').css('display', "none");
        topFunction();
    }
}

// When the user clicks on the button, scroll to the top of the document
function topFunction() {
    $('.pre-scrollable').animate({ scrollTop: 0 }, "slow");
    $('#condition-scrollable').css('overflow-y', 'hidden');
    $('#event-scrollable').css('overflow-y', 'hidden');
}

function enableScrolling(element){
    element.css('overflow-y', 'scroll');
    element.animate({ scrollTop: element.scrollTop() + 100 }, "slow");


}