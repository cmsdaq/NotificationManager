
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="header">
      <h1>Notification reports</h1>
      
    </jsp:attribute>
	<jsp:body>
			<table class="table">
				<tr>
					<th>Event id</th>
					<th>Event type</th>
					<th>User</th>
					<th>Channel</th>
					<th>Status</th>
					<th>Date</th>
				</tr>
				<c:forEach items="${reports}" var="report">
					<tr>
					
						<td><c:out value="${report.eventOccurrence.id}" /></td>
						<td><c:out value="${report.eventOccurrence.eventType.name}" /></td>
						<td><c:out value="${report.user.username}" /></td>
						<td><c:out value="${report.channel.name}" /></td>
						<td>
							<c:out value="${report.status.name}" /> 
				
				
				<c:if test="${report.status == 'Failure' }">
											
				<i type="button" class="fa fa-info-circle pointable text-danger"
							aria-hidden="true" data-toggle="popover" title="Error occurred"
							data-content="<c:out value="${report.message}" /> "
							data-placement="bottom">
							</i>	


							</c:if> 
							
							
						</td>
						<td><fmt:formatDate value='${report.date}' pattern="yyyy-MM-dd HH:mm:ss" /></td>
					</tr>
				</c:forEach>
				<script type="text/javascript">
					$(document).ready(function() {
						  
						$(function() {
							$('[data-toggle="popover"]').popover()
						})
					});
				</script>
			</table>
    </jsp:body>
</t:genericpage>


