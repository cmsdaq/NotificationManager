
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<%@  page import="cern.cms.daq.nm.servlet.Application"%>

<t:genericpage>
	<jsp:attribute name="header">
	 <div class="row " style="margin-bottom: 15px;">
         <div class="col-md-12">
		<div class="btn-group pull-right"> 
	    	<button class="btn btn-warning" id="tour" href="#">
						<i class="glyphicon glyphicon-question-sign"></i> Help</button>
    	</div>
    	</div>
    </div>
    </jsp:attribute>
	<jsp:body>
	
	
	
	<div id="empty-alert" class="alert alert-info">
	  <strong>No events</strong> There is no events to show right now.
	</div> 
	
	
		
	<div id="elements" style="min-height:200px;"></div>
	
	<div id="indicator">
	
	<i id="refresh-symbol" class="fa fa-refresh text-primary"></i>
	Suggestions are color coded: 
	<span class='label label-primary label-as-badge'>active suggestions</span>
	<span class='label label-info label-as-badge'>recent suggestions (not older than 2m)</span>
	<span class='label label-default label-as-badge'>past suggestions (older than 2m)</span>
	</div>
	
	<script type="text/javascript">
		$(document)
				.ready(
						function() {

							/* model */
							var currentEvents = [];

							/* last entry id */
							var last_entry = null;

							var maxEvents = 5;

							/* run update periodically */
							(function worker() {
								getNewEvents();
								//console.log("Current: " + currentEvents.length);
								refreshSpans();
								refreshHighlight();
								AnimateRotate(360);
								setTimeout(worker, 1000);
							})();

							/* reqest from API new event occurrences */
							function getNewEvents() {
								parameters = {};
								parameters['entries'] = maxEvents + "";
								parameters['page'] = 1 + "";
								parameters['dashboard'] = true;
								if (last_entry != null)
									parameters['last'] = last_entry;
								$
										.get("event_occurrences_api",
												parameters)
										.done(
												function(responseJson) {
													newElements = responseJson["results"];
													//console.log("received: " + newElements);
													removed = updateModel(newElements);
													renderNewElements(
															newElements,
															removed);
													getEventsUpdates();
												});
							}

							/* request from API events updates */
							function getEventsUpdates() {
								var parameters = {};
								parameters['ids'] = [];
								for (var i = 0; i < currentEvents.length; i++) {
									//console.log(currentEvents[i].duration);
									if (currentEvents[i].duration == 0) {
										var id = currentEvents[i].id;
										var duration = currentEvents[i].duration;
										//console.log("Event with id " + id + " not finished " + duration);
										parameters['ids'].push(id);
									}
								}

								//console.log("Request parameteers " + JSON.stringify(parameters));
								if (parameters['ids'].length > 0) {
									$.get("event_occurrences_update_api",
											parameters).done(
											function(responseJson) {
												//console.log("Response: " + JSON.stringify(responseJson));
												renderUpdates(responseJson);
											});
								}

							}

							/* Render updates in elements */
							function renderUpdates(updates) {
								//console.log("Render updates");

								// for each 
								$
										.each(
												updates,
												function(id, duration) {
													//console.log("Current response element " + id + ", " + duration);
													for (var j = 0; j < currentEvents.length; j++) {
														if (currentEvents[j].id == id) {
															//console.log("Update " + currentEvents[j] + " with duration " + duration);
															currentEvents[j].duration = duration;

															if (duration == 0) {
																$("#dur_" + id)
																		.html(
																				"active");
															} else {
																$("#dur_" + id)
																		.html(
																				"duration "
																						+ duration
																						/ 1000
																						+ " s");
															}
															refreshHighlight();

														}
													}

												});
							}

							/* update presentation after new elements*/
							function renderNewElements(newElements, removed) {
								//console.log("Rendering events: " + removed);
								if (newElements.length > 0) {
									$("#empty-alert").addClass("hidden");

									////
									var $container = $("#elements");

									////
									$
											.each(
													newElements,
													function(index, eventOcc) {
														//currentEvents.push(eventOcc);
														$container
																.prepend($(
																		"<div id='panel_"+eventOcc.id+"' class='panel panel-default'>")
																		.append(
																				$(
																						"<div class='panel-heading clearfix'>")
																						.append(
																								$(
																										"<h4 class='panel-title pull-left' style='padding-top: 7.5px;'>")
																										.text(
																												"Suggested ")
																										.append(
																												$(
																														"<span class='badge'>")
																														.append(
																																$(
																																		"<span class='glyphicon glyphicon-time' aria-hidden='true'>")
																																		.add(
																																				$(
																																						"<span id='diff_"+eventOcc.id+"'>")
																																						.text(
																																								"X"))

																														)))
																						.append(
																								$(
																										"<h4 class='panel-title pull-right' style='padding-top: 7.5px;'>")
																										.append(
																												$(
																														"<span id='"+eventOcc.id+"' class='event-date'>")
																														.text(
																																eventOcc.date+ " "))
																										.append(
																												$(
																														"<span class='badge' id='dur_"+eventOcc.id+"'>")
																														.text(
																																"duration "
																																		+ eventOcc.duration
																																		/ 1000
																																		+ " s"))))
																		.append(
																				$(
																						"<div>")
																						.append(
																								$(
																										"<div class='panel-body'>")
																										.append(
																												$(
																														"<div class='row'>")
																														.append(
																																$(
																																		"<div class='col-sm-12 col-md-4'>")
																																		.text(
																																				eventOcc.message))
																														.append(
																																$(
																																		"<div class='col-sm-12 col-md-8'>")
																																		.append(
																																				$("<ol id='actions_"+eventOcc.id+"'>"))))
																										.append(
																												$(
																														"<div class='row'>")
																														.append(
																																$("<div class='col-sm-12' id='link_"+eventOcc.id+"'>"))))

																		));

														$
																.each(
																		eventOcc.actionSteps,
																		function(
																				key,
																				value) {
																			$(
																					"#actions_"
																							+ eventOcc.id)
																					.append(
																							$(
																									"<li>")
																									.text(
																											value))
																		});

														var base = $(
																"#expertLink")
																.attr("href");
														var duration = eventOcc.duration;

														if (duration == 0) {
															duration = 15000;
														}

														var start = moment(
																eventOcc.date)
																.add(-duration,
																		"MILLISECONDS")
																.format();
														var end = moment(
																eventOcc.date)
																.add(
																		2 * duration,
																		"MILLISECONDS")
																.format();
														var link = base
																+ "/?start="
																+ start
																+ "&end=" + end;
														console.log("base: "
																+ base);
														$(
																"#link_"
																		+ eventOcc.id)
																.append(
																		$(
																				"<a href='" +link + "'>")
																				.text(
																						"inspect"))
														refreshSpans();
													});
									for (var i = 0; i < removed; i++) {
										$('#elements > div:last').remove();
									}
								}
							}

							function updateModel(newElements) {

								if (newElements.length > 0) {

									//console.log("Updating model with new data: " + newElements.length);
									var lastEntry = newElements[0];

									newElements.reverse();
									if (lastEntry != null) {
										//console.log("newest entry id: " + lastEntry.id);
										last_entry = lastEntry.id;
									}
									var rowCount = currentEvents.length;

									//console.log("Currently in model: " + rowCount + ", new events: " + newElements.length + ", max: " + maxEvents);
									var toRemove = rowCount
											+ newElements.length - maxEvents;

									// in case there was so many new events
									if (toRemove < 0) {
										toRemove = 0;
									}

									console.log("To remove: " + toRemove);
									//console.log(results.length + " new events from autoupdate, need to remove " + toRemove);

									currentEvents = currentEvents
											.concat(newElements);

									for (var i = 0; i < toRemove; i++) {
										currentEvents.shift()
									}
									return toRemove;
								}
								return 0;
							}

							function refreshHighlight() {
								for (var i = 0; i < currentEvents.length; i++) {
									var currentEvent = currentEvents[i];
									var now = moment();
									var then = moment(currentEvent.date);
									var diff_ms = now.diff(then, 'seconds');
									var duration = currentEvent.duration;
									// highlighting
									var panel = $("#panel_" + currentEvent.id);
									$(panel).removeClass("panel-info")
									$(panel).removeClass("panel-primary")
									$(panel).removeClass("panel-default")
									if (duration == 0) {
										$(panel).addClass("panel-primary")
									} else if (diff_ms < 60 * 2) {
										$(panel).addClass("panel-info")
									} else {
										$(panel).addClass("panel-default")
									}
								}
							}

							function refreshSpans() {
								//console.log("Refreshing spans" );

								for (var i = 0; i < currentEvents.length; i++) {
									var currentEvent = currentEvents[i];

									//console.log("Refreshing current event: " + currentEvent);

									var now = moment();
									var then = moment(currentEvent.date);
									var diff = now.diff(then, 'seconds');
									var diff_ms = diff;
									var diff_text = "--";

									if (diff < 60) {
										diff_text = diff + "s"
									} else if (diff < 60 * 60) {
										diff = now.diff(then, 'minutes');
										diff_text = diff + "m"
									} else if (diff < 60 * 60 * 48) {
										diff = now.diff(then, 'hours');
										diff_text = diff + "h"
									} else {
										diff = now.diff(then, 'days');
										diff_text = diff + "d"
									}

									$("#diff_" + currentEvent.id).html(
											" " + diff_text + " ago");

								}
							}

							function AnimateRotate(d) {
								var elem = $("#refresh-symbol");

								$({
									deg : 0
								}).animate(
										{
											deg : d
										},
										{
											duration : 200,
											step : function(now) {
												elem.css({
													transform : "rotate(" + now
															+ "deg)"
												});
											}
										});
							}

						});
	</script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			console.log("ready!");
			$(".nav").find(".active").removeClass("active");
			$(".nav").find("#dashboard").addClass("active");
		});

		// Instance the tour
		var tour = new Tour(
				{
					container : "body",
					name : "dashboard-tour",
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
						title : "Dashboard introduction",
						orphan : true,
						smartPlacement: true,
						//placement: 'auto',
						content : function() {
							return "<p>This is <span class='text-muted'>Dashboard</span>. Notifications appear here in real time in the form <span class='badge'>what's the problem</span> + <span class='badge'>what's the best action to take</span>. The view was designed to be used in control room to alarm shifters.</p><p>Note that not all cases are covered and sometimes no suggestions will be delivered - only 'no rate when expected' message.</p>";
						}

					},{
						title : "Color coding",
						element : "#indicator",
						placement : 'top',
						content : "You can find color coding explanation and refresh indicator here."

					},{
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


