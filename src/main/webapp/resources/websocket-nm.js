$(document)
		.ready(
				function() {

					var socket = new WebSocket(
							"ws://localhost:18081/nm/actions");
					socket.onmessage = onMessage;

					function onMessage(event) {
						var event = JSON.parse(event.data);
						if (event.action === "add") {
							printeventElement(event);
						}
						if (event.action === "remove") {
							document.getElementById(event.id).remove();
						}
					}

					function printeventElement(event) {
						var content = $("#content");

						var eventDiv = document.createElement("div");
						eventDiv.setAttribute("id", event.id);
						eventDiv
								.setAttribute("class",
										"list-group-item list-group-item-action flex-column align-items-start active");

						content.prepend(eventDiv);

						var eventHeader = document.createElement("div");
						eventHeader.setAttribute("class",
								"d-flex w-100 justify-content-between");

						
						var eventName = document.createElement("h5");
						eventName.setAttribute("class", "mb-1");
						eventName.innerHTML = event.name;

						var eventDate = document.createElement("small");
						eventDate.innerHTML = event.type;

						eventHeader.appendChild(eventName);
						eventHeader.appendChild(eventDate);

						eventDiv.appendChild(eventHeader);

						var eventContent = document.createElement("p");
						eventContent.setAttribute("class", "mb-1");
						eventContent.innerHTML = event.status;

						var eventExtra = document.createElement("small");
						eventExtra.innerHTML = event.description;

						eventDiv.appendChild(eventContent);
						eventDiv.appendChild(eventExtra);

						setTimeout(function() {
							$(eventDiv).removeClass('active');
						}, 1000);

					}

				});