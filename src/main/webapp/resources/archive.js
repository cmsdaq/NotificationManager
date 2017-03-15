/*
 * queryParameters -> handles the query string parameters
 * queryString -> the query string without the fist '?' character
 * re -> the regular expression
 * m -> holds the string matching the regular expression
 */
var queryParameters = {}, queryString = location.search.substring(1);
queryParameters = $.deparam(queryString);

$(document).ready(function() {
	// debug//$('#curr-params').html("onLoad: " + $.param(queryParameters));
	initTypeSelector();
	initEntriesPerPageSelector();
	initDatePicker();
	initPagination();
	initTour();
});

function initTypeSelector() {

	$('#event-type-multiselect').multiselect({
		buttonWidth : '200px',
		nonSelectedText : 'showing all',
		onDropdownHidden : function(event) {
			var types = $('select#event-type-multiselect').val();
			// console.log("Types: " + types);
			queryParameters['type'] = types;
			queryParameters['page'] = 1; // reset the page

			location.search = $.param(queryParameters); // reload
		}
	});
	selected = [];
	if (queryParameters['type']) {
		// console.log("Exists type query: " + queryParameters['type']);
		selected = queryParameters['type'];
	}

	// console.log("Selected " + selected.length);
	if (selected.length != 0) {
		$('select#event-type-multiselect').multiselect('select', selected);
	}
}

/* initialize entries per page select */
function initEntriesPerPageSelector() {
	var selected = 20;
	$('#entries-per-page').val('')

	$('#entries-per-page').multiselect({
		buttonWidth : '100px',
		dropUp : true,
		maxHeight : 500,
		onChange : function(event) {
			queryParameters['entries'] = $('select#entries-per-page').val();
			queryParameters['page'] = 1;// go to first
			location.search = $.param(queryParameters); // reload
		}
	});

	if (queryParameters['entries']) {
		selected = queryParameters['entries']
	}

	$('select#entries-per-page').multiselect('select', [ selected ]);
}

// refresh main form with choosen date range
function cb(start, end) {
	$('#reportrange span').html(
			start.format('YYYY-MM-DD HH:mm') + ' - '
					+ end.format('YYYY-MM-DD HH:mm'));

	queryParameters['page'] = 1; // reset the page
	queryParameters['start'] = start.format();
	queryParameters['end'] = end.format();
}

function initDatePicker() {
	// init date range
	$('#reportrange').daterangepicker(
			{
				timePicker : true,
				timePickerIncrement : 10, // for timepicker minutes
				timePicker24Hour : true,
				locale : {
					format : 'YYYY-MM-DD HH:mm' // iso 8601
				},
				opens : 'left',
				ranges : {
					'Yesterday' : [ moment().startOf('day'),
							moment().endOf('day') ],
					'Last Hour' : [ moment().subtract(1, 'hours'), moment() ],
					'Last 24h' : [ moment().subtract(1, 'days'), moment() ],
					'Yesterday' : [
							moment().subtract(1, 'days').startOf('day'),
							moment().subtract(1, 'days').endOf('day') ],
					'Last 7 Days' : [
							moment().subtract(6, 'days').startOf('day'),
							moment().endOf('day') ],
					'Last 30 Days' : [
							moment().subtract(29, 'days').startOf('day'),
							moment().endOf('day') ],
					'This Month' : [ moment().startOf('month'),
							moment().endOf('month') ],
					'Last Month' : [
							moment().subtract(1, 'month').startOf('month'),
							moment().subtract(1, 'month').endOf('month') ],
					'Last 12 Months' : [
							moment().subtract(11, 'month').startOf('month'),
							moment().endOf('month') ]
				}
			}, cb);

	if (queryParameters['start']) {
		$('#reportrange span').html(
				moment(queryParameters['start']).format('YYYY-MM-DD HH:mm')
						+ ' - '
						+ moment(queryParameters['end']).format(
								'YYYY-MM-DD HH:mm'));

	}

	$('#reportrange').on('apply.daterangepicker', function(ev, picker) {

		location.search = $.param(queryParameters);
	});

}

function initPagination() {
	var all_entries = $('#count').attr("data-value");
	console.log(all_entries);
	var curr_entries = 20;
	if (queryParameters['entries']) {
		curr_entries = queryParameters['entries']
	}
	var pages = Math.ceil(all_entries / curr_entries);

	$('#pages').text(pages);

	var curr_page = 1;
	if (queryParameters['page']) {
		curr_page = queryParameters['page']
	}

	$('#page-selection').bootpag({
		total : Math.ceil(all_entries / curr_entries),
		maxVisible : 5,
		first : 'first',
		last : 'last',
		firstLastUse : true,
	}).on("page", function(event, num) {
		queryParameters['page'] = num;
		location.search = $.param(queryParameters);
	});

	$('#page-selection').bootpag({
		total : Math.ceil(all_entries / curr_entries),
		page : curr_page,
	});
}

function initTour() {
	// Instance the tour
	var tour = new Tour(
			{
				container : "body",
				name : "events-tour",
				smartPlacement : true,
				placement : "left",
				keyboard : true,
				storage : window.localStorage,
				debug : false,
				backdrop : true,
				backdropContainer : 'body',
				backdropPadding : 0,
				redirect : true,
				orphan : false,
				duration : false,
				delay : false,
				steps : [

						{
							title : "Archive introduction",
							orphan : true,
							content : function() {
								return "<p>This is <span class='text-muted'>Archive view</span>. You can browse all generated events here.</p>";
							}

						},
						{
							element : "#type-picker-group",
							title : "Filter by type",
							placement : 'bottom',
							content : "Filter events by type here"
						},
						{
							element : "#date-range-picker-group",
							title : "Data range picker",
							placement : 'bottom',
							content : "Select data range to find events by date of occurrence."
						},
						{
							element : "#tour",
							title : "Tour",
							placement : 'left',
							content : "You can always start this tour again here."
						} ]

			});
	$('#tour').click(function(e) {
		// console.log("Start tour");

		tour.restart();

		// it's also good practice to preventDefault on the click event
		// to avoid the click triggering whatever is within href:
		e.preventDefault();
	});

	$(document).ready(function() {
		// Initialize the tour
		tour.init();

		// Start the tour
		tour.start();

	});

}
