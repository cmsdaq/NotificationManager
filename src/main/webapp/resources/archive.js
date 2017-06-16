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
	initLogicModuleSelector();
	initEntriesPerPageSelector();
	initDatePicker();
	initPagination();
	initTour();
	initExpertLinks();
});

function initTypeSelector() {

	
	$('#event-type-multiselect').multiselect({
		buttonWidth : '200px',
		nonSelectedText : 'showing all',
        enableCollapsibleOptGroups: true,
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

function initLogicModuleSelector() {
	
	var optgroups = [
	                  {
	                     label: 'Identified downtime', children: [
	                         {label: 'Out of sequence data received', value: 'FlowchartCase1'},
	                         {label: 'Corrupted data received', value: 'FlowchartCase2'},
	                         {label: 'Partition problem', value: 'FlowchartCase3'},
	                         {label: 'Fed stuck', value: 'FlowchartCase5'},
	                         {label: 'Backpressure detected', value: 'FlowchartCase6'},
	                         {label: 'PI disconnected', value: 'PiDisconnected'},
	                         {label: 'PI problem', value: 'PiProblem'},
	                         {label: 'FED disconnected', value: 'FEDDisconnected'},
	                         {label: 'FMM problem', value: 'FMMProblem'},
	                         {label: 'Ferol FIFO stuck ', value: 'FEROLFifoStuck'},
	                     ]
	                 },
	                 {
	                     label: 'Missed downtime', children: [
	                         {label: 'Unidentified failure', value: 'UnidentifiedFailure'}]
	                 },
	                 {
	                     label: 'All Transitions', children: [
	                         {label: 'Session transitions', value: 'SessionComparator'},
	                         {label: 'LHC beam mode transitions', value: 'LHCBeamModeComparator'},
	                         {label: 'LHC machine mode transitions', value: 'LHCMachineModeComparator'},
	                         {label: 'Run transitions', value: 'RunComparator'},
	                         {label: 'L0 state transitions', value: 'LevelZeroStateComparator'},
	                         {label: 'TCDS state transitions ', value: 'TCDSStateComparator'},
	                         {label: 'DAQ state transitions', value: 'DAQStateComparator'}
	                     ]
	                 },
	                 {
	                     label: 'Other Conditions', children: [
	                         //{label: 'Deadtime', value: 'Deadtime'},
	                         {label: 'Deadtime during run', value: 'CriticalDeadtime'},
	                         //{label: 'Downtime', value: 'Downtime'},
	                         
	                         //{label: 'Rate out of range', value: 'RateOutOfRange'},
	                         {label: 'No rate when expected', value: 'NoRateWhenExpected'},
	                         //{label: 'No rate', value: 'NoRate'},
	                         
	                         {label: 'Warning in sub system', value: 'WarningInSubsystem'},
	                         {label: 'Subsyss running degraded', value: 'SubsystemRunningDegraded'},
	                         {label: 'Subsystem error', value: 'SubsystemError'},
	                         {label: 'Subsystem soft error', value: 'SubsystemSoftError'},
	                         {label: 'Fed deadtime', value: 'FEDDeadtime'},
	                         {label: 'Partition deadtime', value: 'PartitionDeadtime'},
	                         
	                         //{label: 'Stable beams', value: 'StableBeams'},
	                         //{label: 'Beam active', value: 'BeamActive'},
	                         {label: 'Run ongoing', value: 'RunOngoing'},
	                         //{label: 'Expected rate', value: 'ExpectedRate'},
	                         //{label: 'Transition ', value: 'Transition'},
	                         //{label: 'Long transition', value: 'LongTransition'},
	                     ]
	                 }
	             ];

	$('#event-source-multiselect').multiselect({
		buttonWidth : '200px',
		nonSelectedText : 'showing all',
		enableCaseInsensitiveFiltering: true,
        filterPlaceholder: 'Search for LM...',
		maxHeight : 500,
		onDropdownHidden : function(event) {
			var sources = $('select#event-source-multiselect').val();
			console.log("sources: " + JSON.stringify(sources));
			queryParameters['source'] = sources;
			queryParameters['page'] = 1; // reset the page

			console.log($.param(queryParameters));
			location.search = $.param(queryParameters); // reload
		},
		 enableClickableOptGroups: true
	});
	
	$('#event-source-multiselect').multiselect('dataprovider', optgroups);
	
	selected = [];
	if (queryParameters['source']) {
		// console.log("Exists type query: " + queryParameters['type']);
		selected = queryParameters['source'];
	}

	// console.log("Selected " + selected.length);
	if (selected.length != 0) {
		$('select#event-source-multiselect').multiselect('select', selected);
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
	//console.log(all_entries);
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

function initExpertLinks() {
	$('.expert-link').click(function(e) {
		var date = $(this).data("date");
		var base = $.trim($('#expertLink').attr("href"));
		var isoFormat = "YYYY-MM-DDTHH:mm:ssZ";
		console.log("'" + base + "'");
		
		var start = moment(date).add(-120,"second");
		var fakeEnd = moment(date).add(120,"second");

		var link =  base + "?start=" +  start.format(isoFormat) + "&end=" + fakeEnd.format(isoFormat);
		console.log("Expert link clicked: " + link);
		
		window.open(link);

	});
}
