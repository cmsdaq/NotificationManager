

<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
	<jsp:attribute name="header">
      <div class="row" style="margin-bottom: 15px;">
         <div class="col-md-12">
            <form class="form-inline pull-right " method="POST">
                	

               <div class="input-group" id="type-picker-group">
                  <span class="input-group-addon">event type</span>
                  <select name="select" id="event-type-multiselect"
							multiple="multiple" class="multiselect form-control">
                  
						<c:forEach items="${eventTypes}" var="eventType">
                     		<option selected name="type"
									value="<c:out value="${eventType.name }"/>"><c:out
										value="${eventType.name }" /></option>
                   		</c:forEach>
                  </select>
               </div>
               <div class="input-group" id="date-range-picker-group">
                  <span class="input-group-addon">date range</span>
               <div id="reportrange" class=" btn btn-default">
                  <i class="glyphicon glyphicon-calendar fa fa-calendar"></i>
                  <span></span> <b class="caret"></b>
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
			<!-- daata-toggle="modal" data-target="#myModal"> -->
<div class="navigation-bar container row ">
	<div class=" pull-right">
        <div class="input-group" id="entries-group">
 		
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
		<div class=" pull-left">
    <div class="text-muted input-group" id="status-info"></div>
    </div>
    <div class="text-center" id="page-selection"></div>
 		
</div>
</div>
    </jsp:attribute>

	<jsp:body>
      <table id="event_occurrences_table" class="table well">
      	<thead>
         <tr>
            <th>Date</th>
            <th>Event Type</th>
            <th>Sender Type</th>
            <th>Title</th>
            <th>Sender</th>
            <th>Message</th>
            <th>Message</th>
            <th>Status</th>
            <th>Display</th>
            <th>Play</th>
            <th>Link</th>
         </tr>
         </thead>
         <tbody>
				<c:forEach items="${events}" var="event">
                     		<tr>
                     		<td><c:out value="${event.date }" /></td>
                     		<td><c:out value="${event.eventType }" /></td>
                     		<td><c:out value="${event.eventSenderType }" /></td>
                     		<td><c:out value="${event.title }" /></td>
                     		<td><c:out value="${event.sender }" /></td>
                     		<td><c:out value="${event.message }" /></td>
                     		<td><c:out value="${event.textToSpeech }" /></td>
                     		<td>-</td>
                     		<td>-</td>
                     		<td>-</td>
                     		<td>-</td>
                </tr>   		
				
				</c:forEach>
         </tbody>
      </table>
      
      <!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body">
        ...
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default"
							data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>
     
   </jsp:body>

</t:genericpage>

