$(document).ready(
		function() {

			var expertSocketAddress = document.getElementById(
					"expert-socket-address").getAttribute("url");
			var expertSocket = new ReconnectingWebSocket(expertSocketAddress);
			expertSocket.onmessage = onConditionMessage;

			expertSocket.onerror = onErrorHandle;
			expertSocket.onopen = onOpenHandle;
			expertSocket.onclose = onCloseHandle;

			function onConditionMessage(condition) {
				var message = JSON.parse(condition.data);
				if (message.action === "add") {
					var conditions = message.objects;
					newConditionsDataArrived(conditions);
				}
				if (message.action === "update") {
					var condition = message.object;
					console.log("Updating with: " + JSON.stringify(condition));

					newUpdateDataArrived(condition);

				}
				if (message.action === "select") {
					console.log("Selecting dominating: " + JSON.stringify(message.id));
					updateSelected(message.id);
				}

			}

			function onErrorHandle(event) {
				console.log("new error: " + JSON.stringify(event));
			}

			function onOpenHandle(event) {
				console.log("Expert websocket (re)connected");
				$("#expert-status").text("Connected");

			}

			function onCloseHandle(event) {
				$("#expert-status").text("Disconnected");
			}

		});