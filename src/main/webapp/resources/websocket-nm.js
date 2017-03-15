$(document)
		.ready(
				function() {

					var nmSocketAddress = document.getElementById(
							"nm-socket-address").getAttribute("url");
					var socket = new ReconnectingWebSocket(nmSocketAddress);
					socket.onmessage = onMessage;
					socket.onopen = onOpenHandle;
					socket.onclose = onCloseHandle;

					function onMessage(event) {
						var event = JSON.parse(event.data);
						if (event.action === "add") {
							printeventElement(event);
						}
						if (event.action === "remove") {
							document.getElementById(event.id).remove();
						}
					}

					function onOpenHandle(event) {

						$("#content").empty();
						$("#event-list-empty-msg").show();

						console.log("NM websocket (re)connected");
						$("#nm-status").text("Connected");

					}

					function onCloseHandle(event) {
						$("#nm-status").text("Disconnected");
					}

					function printeventElement(event) {
						var content = $("#content");

						$("#event-list-empty-msg").hide();

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

						var headerRight = document.createElement("span");
						var eventDate = document.createElement("small");
						eventDate.innerHTML = event.type;
						headerRight.setAttribute("class", "pull-right");

						var soundStatus = document.createElement("span");
						soundStatus.setAttribute("class", "label label-info");
						var soundIcon = document.createElement("span");
						soundIcon.setAttribute("class",
								"glyphicon glyphicon-music ");
						var soundFile = document.createElement("span");
						soundFile.innerHTML = event.sound;
						var separator0 = document.createElement("span");
						separator0.innerHTML = " ";

						soundStatus.appendChild(soundIcon);
						soundStatus.appendChild(separator0);
						soundStatus.appendChild(soundFile);

						var separator1 = document.createElement("span");
						separator1.innerHTML = " ";

						if (event.sound !== '') {
							headerRight.appendChild(soundStatus);
							headerRight.appendChild(separator1);
						}

						headerRight.appendChild(eventDate);

						eventHeader.appendChild(headerRight);
						eventHeader.appendChild(eventName);

						eventDiv.appendChild(eventHeader);

						// Text to speech
						var textToSpeechStatus = document.createElement("p");
						textToSpeechStatus.setAttribute("class", "mb-1");
						var textToSpeechMessage = document
								.createElement("span");
						var textToSpeechIcon = document.createElement("span");
						textToSpeechIcon.setAttribute("class",
								"glyphicon glyphicon-bullhorn text-info");
						textToSpeechMessage.innerHTML = event.tts;
						var separator = document.createElement("span");
						separator.innerHTML = " ";

						textToSpeechStatus.appendChild(textToSpeechIcon);
						textToSpeechStatus.appendChild(separator);
						textToSpeechStatus.appendChild(textToSpeechMessage);

						var eventExtra = document.createElement("small");
						eventExtra.innerHTML = event.description;

						if (event.tts !== '') {
							eventDiv.appendChild(textToSpeechStatus);
						}
						eventDiv.appendChild(eventExtra);

						setTimeout(function() {
							$(eventDiv).removeClass('active');
						}, 2000);

					}

				});