<!DOCTYPE html>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
	<jsp:attribute name="header">
	  <div class="row" style="margin-bottom: 15px;">
		 <div class="col-md-12">
		 
		 <span id="curr-params"></span>
		  
			<form class="form-inline pull-right " method="POST">
					
				<div class="input-group" id="source-picker-group">
				<span class="input-group-addon">source</span>
				  <select name="select" id="event-source-multiselect"
							multiple="multiple" class="multiselect form-control">
				  
				   		
				  </select>
			   </div>
			   <div class="input-group" id="type-picker-group">
				  <span class="input-group-addon">event type</span>
				  <select name="select" id="event-type-multiselect"
							multiple="multiple" class="multiselect form-control">
				  
						<c:forEach items="${eventTypes}" var="eventType">
					 		<option name="type" value="<c:out value="${eventType.name }"/>"><c:out
										value="${eventType.name }" />
										
		 					</option>
				   		</c:forEach>
				   		
				  </select>
			   </div>
			   <div class="input-group" id="date-range-picker-group">
				  <span class="input-group-addon">date range</span>
			   <div id="reportrange" class=" btn btn-default">
				  <i class="glyphicon glyphicon-calendar fa fa-calendar"></i>
				  <span>showing all</span> <b class="caret"></b>
			   </div>
			   </div>
			   	<div class="btn-group "> 
					<button class="btn btn-warning" id="tour" href="#">
							<i class="glyphicon glyphicon-question-sign"></i> Help</button>
					</div>
			   
			</form>
		 </div>
	  </div>
   </jsp:attribute>


	<jsp:attribute name="footer">


<div>
	<div class="navigation-bar row ">
		<div class="col-md-3">
			<div class="input-group pull-left" id="entries-group">
				<span class="input-group-addon">Entries per page</span>
				<select id="entries-per-page">
				<option value="10">10</option>
					<option value="20">20</option>
					<option value="50">50</option>
					<option value="100">100</option>
					<option value="200">200</option>
				</select> 
			</div>
		</div>
		<div class=" col-md-6 text-center" id="page-selection"></div>
		<div class="col-md-3 ">
			<div class="pull-right text-muted input-group" id="status-info">
								<span id="count" data-value="${count}">${count}</span> entries (<span
							id="pages">#</span> pages)</div>
		</div>
		
	 		
	</div>
</div>
	</jsp:attribute>

	<jsp:body>
	  <table id="event_occurrences_table" class="table well">
	  	<thead>
		 <tr>
			<th>Date</th>
			<th>Event Type</th>
			<th>Sender</th>
			<th>Title</th>
			<th>Displayed message</th>
			<th>Source LM</th>
			<th>Audible</th>
			<th>Link</th>
		 </tr>
		 </thead>
		 <tbody>
				<c:forEach items="${events}" var="event">
					 		<tr>
					 		<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"  value="${event.date}" /></td>
					 		<td><c:out value="${event.eventType }" /></td>
					 		<td><c:out value="${event.eventSenderType }" /> <i><c:out value="${event.sender }" /></i></td>
					 		<td><c:out value="${event.title }" /></td>
					 		<td><c:out value="${event.message }" /></td>
							<td>
								<c:choose>
									<c:when test="${event.logicModule != null}">${event.logicModule}</c:when> 
									<c:otherwise>n/a</c:otherwise>  
								</c:choose>
							</td>
					 		<td>
					 		<c:if test="${event.audible == false}">false</c:if> 
					 		<c:if test="${event.sound != null}">
								<span class="label label-info" data-toggle="tooltip"
									title="${event.sound.filename}"><span
									class="glyphicon glyphicon-music"></span> ${event.sound.displayName}</span>
							</c:if> 
					 		<c:if test="${event.textToSpeech != null}">${event.textToSpeech}</c:if> 
					 		<td><a href="#" class="expert-link" data-date="<fmt:formatDate pattern="yyyy-MM-dd'T'HH:mm:ss Z"  value="${event.date}" />"><span class="glyphicon glyphicon-link"></span></a></td>
				
					</tr>   		
				
				</c:forEach>
		 </tbody>
	  </table>
	  
   </jsp:body>

</t:genericpage>

