$(document).ready(
		function() {

			var nmSocketAddress = document.getElementById("nm-socket-address")
					.getAttribute("url");
			var socket = new ReconnectingWebSocket(nmSocketAddress);
			socket.onmessage = onMessage;
			socket.onopen = onOpenHandle;
			socket.onclose = onCloseHandle;

			function onMessage(message) {
				var data = JSON.parse(message.data);
				if (data.action === "add") {
					//console.log("New events: " + JSON.stringify(data));
					newEventsDataArrived(data.objects);
				} else if (data.action === "version"){
					console.log("version command received: " + JSON.stringify(data));
					newVersionDataArrived(data.version);
				}

			}

			function onOpenHandle(event) {
				console.log("NM websocket (re)connected");
				$("#nm-status").text("Connected");

			}

			function onCloseHandle(event) {
				$("#nm-status").text("Disconnected");
			}

		});