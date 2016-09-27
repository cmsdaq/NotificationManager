
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="header">
      <h1>Event types</h1>
    </jsp:attribute>
	<jsp:body>
			<table class="table">
				<tr>
					<th>Id</th>
					<th>Name</th>
					<th>Description</th>
				</tr>
				<c:forEach items="${events}" var="event">
					<tr>
						<td><c:out value="${event.id}" /></td>
						<td><c:out value="${event.name}" /></td>
						<td><c:out value="${event.description}" /></td>
					</tr>
				</c:forEach>
			</table>

    </jsp:body>
</t:genericpage>


