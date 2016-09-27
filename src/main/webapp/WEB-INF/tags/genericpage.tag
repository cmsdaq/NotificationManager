<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<html>
<head>

<title>Notification Manager</title>

<meta name="viewport" content="width=device-width, initial-scale=1">

<!-- jQuery -->
<script type="text/javascript"
	src="resources/external/jquery/jquery.min.js"></script>

<!-- bootstrap css -->
<link rel="stylesheet"
	href="resources/external/bootstrap/css/bootstrap.min.css"
	type="text/css" />

<!-- clockpicker -->
<link rel="stylesheet" type="text/css"
	href="resources/external/bootstrap/css/bootstrap-clockpicker.min.css" />

<!-- touchspin -->
<link rel="stylesheet" type="text/css"
	href="resources/external/bootstrap/css/jquery.bootstrap-touchspin.min.css" />

<!-- bootstrap js -->
<script type="text/javascript"
	src="resources/external/bootstrap/js/bootstrap.min.js"></script>

<!-- bootpag -->
<script src="resources/external/bootpag/jquery.bootpag.min.js"></script>

<!-- datetime range picker -->
<script type="text/javascript"
	src="resources/external/momentjs/moment.min.js"></script>
<script type="text/javascript"
	src="resources/external/daterangepicker-2/daterangepicker.js"></script>
<link rel="stylesheet" type="text/css"
	href="resources/external/daterangepicker-2/daterangepicker.css" />

<!-- multiselect-->
<link rel="stylesheet"
	href="resources/external/bootstrap/css/bootstrap-multiselect.css"
	type="text/css" />
<script type="text/javascript"
	src="resources/external/bootstrap/js/bootstrap-multiselect.js"></script>


<link rel="stylesheet"
	href="resources/external/font-awesome-4.5.0/css/font-awesome.min.css">
<link rel="stylesheet" href="resources/nm/css/nm.css">

<link
	href="resources/external/bootstrap-tour-0.10.3/bootstrap-tour.min.css"
	rel="stylesheet">
<script
	src="resources/external/bootstrap-tour-0.10.3/bootstrap-tour.min.js"></script>


</head>
<body>

	<%@  tag import="cern.cms.daq.nm.servlet.Application"%>

	<nav class="navbar navbar-default">
		<div class="container-fluid">



			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
					aria-expanded="false">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>

				<a class="navbar-brand"
					href="<%out.println(Application.get().getProp().getProperty(Application.LANDING));%>"><b>DAQ</b>
					Expert</a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-1">

				<ul class="nav navbar-nav">

					<!-- EXPERT BROWSER -->
					<li><a id="expertLink"
						href="<%out.println(Application.get().getProp().getProperty(Application.EXPERT_BROWSER));%>"><i
							class="glyphicon glyphicon-tasks"></i> Browser</a></li>

					<!-- NM DASHBOARD -->
					<li id="dashboard"><a href="dashboard"><i
							class="glyphicon glyphicon-bell"></i> Dashboard</a></li>

					<!-- NM NOTIFICATIONS -->
					<li id="event_occurrences"><a href="event_occurrences"><i
							class="glyphicon glyphicon-calendar"></i> Notifications</a></li>



					<!-- Turned off for P5: DELIVERY REPORT -->
					<!-- <li class=""><a href="reports">Delivery report</a></li> -->

					<!-- Turned off for P5: CONFIGURATION -->
					<!-- <li><a href="confs">Configuration <span class="sr-only">(current)</span></a></li>
					
					<!-- Turned off for P5: EVENT TYPES -->
					<!-- <li><a href="events">Event types</a></li>-->
				</ul>



				<!-- Turned off for P5: LOGOUT & SETTINGS-->
				<%-- <ul class="nav navbar-nav navbar-right">
					<!-- <li><a href="#">Link</a></li> -->
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-haspopup="true"
						aria-expanded="false">Hello <c:out
								value="${pageContext.request.userPrincipal.name}" /> <span
							class="caret"></span></a>
						<ul class="dropdown-menu">
							<!-- 							<li><a href="#">Action</a></li>
							<li><a href="#">Another action</a></li>-->
							<li><a href="settings">Settings</a></li>
							<li role="separator" class="divider"></li>
							<li><a href="https://login.cern.ch/adfs/ls/?wa=wsignout1.0">Logout</a></li>
						</ul></li>
				</ul> --%>

			</div>
			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container-fluid -->
	</nav>


	<div class="container">

		<div id="pageheader">
			<jsp:invoke fragment="header" />
		</div>
		<div id="body">
			<jsp:doBody />
		</div>
		<div id="pagefooter">
			<jsp:invoke fragment="footer" />
		</div>
	</div>


</body>
</html>