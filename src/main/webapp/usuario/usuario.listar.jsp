<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title>Pronto Agile</title>
		<%@ include file="/commons/scripts/scripts.jsp" %>
	</head>
	<body>
		<table>
			<tr>
				<th>username</th>
				<th>Nome</th>
				<th>e-mail</th>
			</tr>
			<c:forEach items="${usuarios}" var="u">
				<tr>
					<td>${u.username}</td>
					<td>${u.nome}</td>
					<td>${u.email}</td>
					<td><a href="editar.action?usuarioKey=${u.usuarioKey}">Editar</a></td>
				</tr>
			</c:forEach>
		</table>	
		
		<div align="center">
			<a href="editar.action">Novo</a>
		</div>
	</body>
</html>