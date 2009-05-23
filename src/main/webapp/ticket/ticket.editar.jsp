<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<%@ include file="/commons/scripts/scripts.jsp" %>
	</head>
	<body>
		<form action="salvar.action">
			<c:if test="${ticket.ticketKey gt 0}">
				#${ticket.ticketKey}<br/>
				<form:hidden path="ticket.ticketKey"/>
			</c:if>
			T�tulo: <form:input path="ticket.titulo"/><br/>
			Descri��o: <form:textarea path="ticket.descricao"/><br/>
			<button type="submit">Salvar</button><br/>
		</form>		
	</body>
</html>