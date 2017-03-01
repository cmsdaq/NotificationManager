/**
 * 
 */

var curr_start_range = null;
var curr_end_range = null;
var curr_entries = 10;
var curr_page = 1;
var all_entries;
var json_entries = null;

$(document).ready(
		function() {

			/* initialize event type select */
			$(".multiselect").multiselect({
				numberDisplayed : 1,
				onDropdownHide : function(event) {
				}
			});

			/* initialzie entries per page select */
			$('#entries-per-page').multiselect({
				buttonWidth : '100px',
				onChange : function(event) {
					// get selected value
					curr_entries = $('select#entries-per-page').val();
					// go to first page on change
					curr_page = 1;
				}
			});

			/* initialize pagination buttons */
			$('#page-selection').bootpag({
				total : Math.ceil(all_entries / curr_entries),
				maxVisible : 5,
				first : 'first',
				last : 'last',
				firstLastUse : true,
			}).on("page", function(event, num) {
				curr_page = num;
				getNewDataAndRefreshView();
			});

			// init date range
			$('#reportrange')
					.daterangepicker(
							{

								timePicker : true,
								timePickerIncrement : 10, // for timepicker
															// minutes
								timePicker24Hour : true,
								locale : {
									format : 'YYYY-MM-DD HH:mm' // iso 8601
								},
								opens : 'left',
								ranges : {
									'Yesterday' : [ moment().startOf('day'),
											moment().endOf('day') ],
									'Last Hour' : [
											moment().subtract(1, 'hours'),
											moment() ],
									'Last 24h' : [
											moment().subtract(1, 'days'),
											moment() ],
									'Yesterday' : [
											moment().subtract(1, 'days')
													.startOf('day'),
											moment().subtract(1, 'days').endOf(
													'day') ],
									'Last 7 Days' : [
											moment().subtract(6, 'days')
													.startOf('day'),
											moment().endOf('day') ],
									'Last 30 Days' : [
											moment().subtract(29, 'days')
													.startOf('day'),
											moment().endOf('day') ],
									'This Month' : [ moment().startOf('month'),
											moment().endOf('month') ],
									'Last Month' : [
											moment().subtract(1, 'month')
													.startOf('month'),
											moment().subtract(1, 'month')
													.endOf('month') ],
									'Last 12 Months' : [
											moment().subtract(11, 'month')
													.startOf('month'),
											moment().endOf('month') ]
								}
							}, cb);

			/*
			 * Callback on apply new date range
			 */
			$('#reportrange').on('apply.daterangepicker', function(ev, picker) {
			});

			cb(moment().subtract(29, 'days'), moment());

		});

// refresh main form with choosen date range
function cb(start, end) {
	$('#reportrange span').html(
			start.format('YYYY-MM-DD HH:mm') + ' - '
					+ end.format('YYYY-MM-DD HH:mm'));
	curr_start_range = start;
	curr_end_range = end;
}

$(document).ready(function() {
	console.log("ready!");
	$(".nav").find(".active").removeClass("active");
	$(".nav").find("#event_occurrences").addClass("active");
});
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
						title : "Notifications introduction",
						orphan : true,
						content : function() {
							return "<p>This is <span class='text-muted'>Notifications view</span>. You can browse all generated notifications here.</p>";
						}

					},
					{
						element : "#type-picker-group",
						title : "Filter by type",
						placement : 'bottom',
						content : "Filter notifications by type here"
					},
					{
						element : "#date-range-picker-group",
						title : "Data range picker",
						placement : 'bottom',
						content : "Select data range to find notifications by date of occurrence."
					}, {
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
	console.log("initializing tour");
	// Initialize the tour
	tour.init();

	// Start the tour
	tour.start();

});