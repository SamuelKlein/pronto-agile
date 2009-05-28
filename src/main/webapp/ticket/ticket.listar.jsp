<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title>${backlog.descricao}${sprint.nome}</title>
	</head>
	<body>
		<h1>${backlog.descricao}${sprint.nome}</h1>
		<table>
			<tr>
				<th>#</th>
				<th>T�tulo</th>
			</tr>
			<c:forEach items="${tickets}" var="t">
				<tr>
					<td>${t.ticketKey}</td>
					<td>${t.titulo}</td>
					<td><a href="editar.action?ticketKey=${t.ticketKey}">Editar</a></td>
				</tr>
			</c:forEach>
		</table>	
		
		<div align="center">
			|&nbsp;&nbsp;
			<a href="editar.action?tipoDeTicketKey=1">Nova Est�ria</a>&nbsp;&nbsp;|&nbsp;&nbsp;
			<a href="editar.action?tipoDeTicketKey=2">Novo Defeito</a>&nbsp;&nbsp;|
		</div>
	</body>
</html>