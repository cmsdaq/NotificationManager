

<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
   <jsp:attribute name="header">
      <div class="row" style="margin-bottom:15px;">
         <div class="col-md-12">
            <form class="form-inline pull-right " method="POST">
                	

               <div class="input-group" id="type-picker-group">
                  <span class="input-group-addon">event type</span>
                  <select  name="select" id="event-type-multiselect" multiple="multiple" class="multiselect form-control">
                  
						<c:forEach items="${eventTypes}" var="eventType">
                     		<option selected name="type" value="<c:out value="${eventType.id }"/>"><c:out value="${eventType.name }"/></option>
                   		</c:forEach>
                  </select>
               </div>
               <div class="input-group" id="date-range-picker-group">
                  <span class="input-group-addon">date range</span>
               <div id="reportrange" class=" btn btn-default" >
                  <i class="glyphicon glyphicon-calendar fa fa-calendar"></i>
                  <span></span> <b class="caret"></b>
               </div>
               </div>
               	<div class="btn-group "> 
			    	<button class="btn btn-warning" id="tour" href="#"><i
									class="glyphicon glyphicon-question-sign"></i> Help</button>
			    	</div>
               
            </form>
         </div>
      </div>
   </jsp:attribute>
   
   
   <jsp:attribute name="footer">


<div><!-- daata-toggle="modal" data-target="#myModal"> -->
<div class="navigation-bar container row " >
	<div class=" pull-right">
        <div class="input-group" id="entries-group">
 		
        	<span class="input-group-addon">Entries per page</span>
			  <select id="entries-per-page" >
			    <option value="10">10</option>
			    <option value="20">20</option>
			    <option value="50">50</option>
			    <option value="100">100</option>
			    <option value="200">200</option>
			</select> 
			</div>
		</div>
		<div class=" pull-left">
    <div class="text-muted input-group" id="status-info"></div>
    </div>
    <div class="text-center" id="page-selection"    ></div>
 		
</div>
</div>
    </jsp:attribute>
   
   <jsp:body>
      <table id="event_occurrences_table" class="table well">
      	<thead>
         <tr>
            <th>Date</th>
            <th>Type</th>
            <th>Message</th>
            <th>Status</th>
            <th>Display</th>
            <th>Play</th>
            <th>Duration</th>
            <th>Link</th>
         </tr>
         </thead>
         <tbody>

         </tbody>
      </table>
      
      <!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body">
        ...
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>
     
      <script type="text/javascript">
      
      var curr_start_range = null;
      var curr_end_range = null;
      var curr_entries = 10;
      var curr_page = 1;
      var all_entries;
      var json_entries = null;
      

      /*
       * initialize all elements
       */
      $(document).ready(function() {
    	  


      	/* get initial data */
      	getNewDataAndRefreshView();

      	/* initialize event type select */
      	$(".multiselect").multiselect({
      		numberDisplayed: 1,
      		onDropdownHide: function(event) {
      			getNewDataAndRefreshView();
      		}
      	});

      	/* initialzie entries per page select*/
      	$('#entries-per-page').multiselect({
      		buttonWidth: '100px',
      		onChange: function(event) {
      			// get selected value
      			curr_entries = $('select#entries-per-page').val();
      			// go to first page on change
      			curr_page = 1;
      			getNewDataAndRefreshView2();
      		}
      	});

      	/* initialize  pagination buttons  */
      	$('#page-selection').bootpag({
      		total: Math.ceil(all_entries / curr_entries),
      		maxVisible: 5,
      		first: 'first',
      		last: 'last',
      		firstLastUse: true,
      	}).on("page", function(event, num) {
      		curr_page = num;
      		getNewDataAndRefreshView();
      	});
      	
		// init date range
      	$('#reportrange')
      		.daterangepicker({

      			timePicker: true,
      			timePickerIncrement: 10, // for timepicker minutes
      			timePicker24Hour: true,
      			locale: {
      				format: 'YYYY-MM-DD HH:mm' // iso 8601
      			},
      			opens: 'left',
      			ranges: {
      				'Yesterday': [moment().startOf('day'), moment().endOf('day')],
      				'Last Hour': [moment().subtract(1, 'hours'), moment()],
      				'Last 24h': [moment().subtract(1, 'days'), moment()],
      				'Yesterday': [moment().subtract(1, 'days').startOf('day'), moment().subtract(1, 'days').endOf('day')],
      				'Last 7 Days': [moment().subtract(6, 'days').startOf('day'), moment().endOf('day')],
      				'Last 30 Days': [moment().subtract(29, 'days').startOf('day'), moment().endOf('day')],
      				'This Month': [moment().startOf('month'), moment().endOf('month')],
      				'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
      				'Last 12 Months': [moment().subtract(11, 'month').startOf('month'), moment().endOf('month')]
      			}
      		}, cb);

      	/*
      	 * Callback on apply new date range
      	 */
      	$('#reportrange').on('apply.daterangepicker', function(ev, picker) {
      		getNewDataAndRefreshView();
      	});
      	
      	cb(moment().subtract(29, 'days'), moment());
      	
      	/* add worker to refresh page */
      	
      });


      function getNewDataAndRefreshView2() {
    		  getNewDataAndRefreshView();
      }

      function getNewDataAndRefreshView() {
        
        // Get data from server
        
		// get event types
      	var types = $('select#event-type-multiselect').val()
      	if (types == null)
      		types = [];

		// build query parameters
      	parameters = {};
      	parameters['eventTypes'] = types;
      	parameters['start'] = curr_start_range + "";
      	parameters['end'] = curr_end_range + "";
      	parameters['entries'] = curr_entries + "";
      	parameters['page'] = curr_page + "";
      	
        // remove current data from table
    	$("#event_occurrences_table tbody tr").remove();

 
      	$.get("event_occurrences_api", parameters)
      		.done(function(responseJson) {
      			all_entries = responseJson['total'];
   	      		console.log("executing API in normal mode");
   				updateElements(responseJson['results']);
      		});

      	}
      
		function updateElements(results){
			var $table = $("#event_occurrences_table >tbody");
	      	var base = $("#expertLink").attr("href");
  			$.each(results, function(index, eventOcc) {
  				
  				var duration = eventOcc.duration;
  				var printableDuration = "n/a";
  				
  				
  				if (duration == 0) {
  					duration = 15000;
  				} else{
  					printableDuration = duration/1000;
  					printableDuration = printableDuration + " s"
  				}
  				
  				var start = moment(eventOcc.date).add(-duration,"MILLISECONDS").format();
  				var end = moment(eventOcc.date).add(2 * duration,"MILLISECONDS").format();
  				var link = base	+ "/?start="+ start	+ "&end=" + end;
  				console.log("base: "+ base);
  				
  				$("<tr>").appendTo($table)
					
  					.append($("<td>").text(eventOcc.date))
  					.append($("<td>").text(eventOcc.eventType.name))
  					.append($("<td>").text(eventOcc.message))
  					.append($("<td>").text(eventOcc.status))
  					.append($("<td>").text(eventOcc.display))
  					.append($("<td>").text(eventOcc.play))
  					.append($("<td class='text-right'>").text(printableDuration))
  					.append($("<td>").append($("<a href='" +link + "'>").text("inspect")));
  				
  				
  			});
  			
  			// update status info
  			$("#status-info").html(all_entries + " entries ("+ Math.ceil(all_entries / curr_entries) + " pages) &nbsp;")

			
			// update pagination
  			$('#page-selection').bootpag({
  				total: Math.ceil(all_entries / curr_entries),
  				page: curr_page,
  			});
		}


		// refresh main form with choosen date range
      	function cb(start, end) {
   			$('#reportrange span')
   				.html(
   					start
   					.format('YYYY-MM-DD HH:mm') + ' - ' + end
   					.format('YYYY-MM-DD HH:mm'));
      		curr_start_range = start;
      		curr_end_range = end;
      	}
		

      </script>
      
      	<script type="text/javascript">
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
					},
					{
						element : "#tour",
						title : "Tour",
						placement : 'left',
						content : "You can always start this tour again here."
					}  ]
				
				
				});
		$('#tour').click(function(e) {
			//console.log("Start tour");

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
	</script>
   </jsp:body>
   
</t:genericpage>

