$(document)
		.ready(
				function() {
					var expertSocketAddress = document.getElementById(
							"expert-socket-address").getAttribute("url");
					var expertSocket = new ReconnectingWebSocket(expertSocketAddress);
					expertSocket.onmessage = onConditionMessage;

					expertSocket.onerror = onErrorHandle;
					expertSocket.onopen = onOpenHandle;
					expertSocket.onclose = onCloseHandle;

					function onConditionMessage(condition) {
						var condition = JSON.parse(condition.data);
						if (condition.action === "add") {
							printConditionElement(condition);
						}
						if (condition.action === "remove") {
							document.getElementById("c-"+condition.id).remove();
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

					function onErrorHandle(event) {
						console.log("new error: " + JSON.stringify(event));
					}

					function onOpenHandle(event) {

						$("#conditions").empty();
						$("#condition-list-empty-msg").show();
						$("#current-title").text("Connected");
						$("#current-description")
								.text(
										"DAQExpert is now connected, you will now see suggestions");
						$("#current-action").empty();
						console.log("Expert websocket (re)connected");
						$("#expert-status").text("Connected");
						
					}

					function onCloseHandle(event) {
						$("#current-title").text("Disconnected");
						$("#current-description")
								.text(
										"DAQExpert is now disconnected, dashboard will try to reconnect..");
						$("#current-action").empty();
						$("#expert-status").text("Disconnected");
					}

					function printConditionElement(condition) {
						var content = $("#conditions");

						$("#condition-list-empty-msg").hide();

						var conditionDiv = document.createElement("div");
						conditionDiv.setAttribute("id", "c-"+ condition.id);
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