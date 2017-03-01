
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<%@  page import="cern.cms.daq.nm.Application"%>

<t:genericpage2>
	<jsp:body>

	<div class="container-fluid">
		<div class="row">
			<div class="col-sm-8">
				<div class="row">
					<div class="col-sm-12">
						<div class="row">
							<div class="col-sm-12">
								<div class="jumbotron jumbotron-fluid">
									<div id="current" class="container">
										<h1 id="current-title" class="display-5">No connection</h1>
										<p id="current-description" class="lead">DAQExpert not
											connected to expert system</p>
										<div id="current-action" class="list-group"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-sm-12">
						<div class="panel panel-default">
							<!-- Default panel contents -->
							<div class="panel-heading">Recent Suggestions</div>
							<div class="panel-body">
								<div id="conditions" class="list-group"></div>
								<p id="condition-list-empty-msg" >no recent conditions</p>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-sm-4">
				<div class="panel panel-default">
					<!-- Default panel contents -->
					<div class="panel-heading">All Events</div>
					<div class="panel-body">
						<div id="content" class="list-group"></div>
						<p id="event-list-empty-msg">no recent events</p>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
		<p>
					<small>Expert websocket status: <span id="expert-status"
						class="badge">Not connected</span> NM websocket status: <span
						id="nm-status" class="badge">Not connected</span></small>
				</p>
		</div>
	</div>

    </jsp:body>
</t:genericpage2>



