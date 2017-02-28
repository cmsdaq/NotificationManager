$(document)
		.ready(
				function() {

					var expertSocket = new WebSocket(
							"ws://localhost:18081/DAQExpert/actions");
					expertSocket.onmessage = onConditionMessage;

					function onConditionMessage(condition) {
						var condition = JSON.parse(condition.data);
						if (condition.action === "add") {
							printConditionElement(condition);
						}
						if (condition.action === "remove") {
							document.getElementById(condition.id).remove();
						}
						if (condition.action === "addSuggestion") {
							console.log("action update current suggestion");
							printCurrentSuggestion(condition);
						}
						if (condition.action === "removeCurrent") {
							$("#current-title").text("All ok");
							$("#current-description")
									.text(
											"DAQExpert has no suggestion at the moment");
							$("#current-action").empty();
						}
					}

					function printConditionElement(condition) {
						var content = $("#conditions");

						var conditionDiv = document.createElement("div");
						conditionDiv.setAttribute("id", condition.id);
						conditionDiv
								.setAttribute("class",
										"list-group-item list-group-item-action flex-column align-items-start active");

						content.prepend(conditionDiv);

						var conditionHeader = document.createElement("div");
						conditionHeader.setAttribute("class",
								"d-flex w-100 justify-content-between");

						var conditionName = document.createElement("h5");
						conditionName.setAttribute("class", "mb-1");
						conditionName.innerHTML = condition.name;

						var conditionStatus = document.createElement("p");
						var conditionDate = document.createElement("small");
						var conditionDuration = document.createElement("span");
						conditionDuration.setAttribute("class", "strong");
						var separator = document.createElement("span");

						conditionDate.innerHTML = condition.type;
						conditionDuration.innerHTML = condition.duration;
						separator.innerHTML = " ";

						conditionStatus.appendChild(conditionDate);
						conditionStatus.appendChild(separator);
						conditionStatus.appendChild(conditionDuration);

						conditionHeader.appendChild(conditionName);
						conditionHeader.appendChild(conditionStatus);

						conditionDiv.appendChild(conditionHeader);

						var conditionContent = document.createElement("p");
						conditionContent.setAttribute("class", "mb-1");
						conditionContent.innerHTML = condition.status;

						var conditionExtra = document.createElement("small");
						conditionExtra.innerHTML = condition.description;

						conditionDiv.appendChild(conditionContent);
						conditionDiv.appendChild(conditionExtra);

						setTimeout(function() {
							$(conditionDiv).removeClass('active');
						}, 1000);

					}

					function printCurrentSuggestion(condition) {

						$("#current-title").text(condition.name);
						$("#current-description").text(condition.description);
						$("#current-action").empty();

						$.each(condition.steps, function(index, value) {
							console.log("Step: " + step);
							var step = document.createElement("div");
							step.setAttribute("class", "list-group-item");
							step.innerHTML = value;
							$("#current-action").append(step);
						});
					}

				});