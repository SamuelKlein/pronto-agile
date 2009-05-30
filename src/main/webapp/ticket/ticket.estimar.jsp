<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title>${backlog.descricao}${sprint.nome}</title>
	</head>
	<body>
		<c:choose>
			<c:when test="${sprint.nome ne null}">
				<h1>Sprint ${sprint.nome}</h1>	
			</c:when>
			<c:otherwise>
				<h1>${backlog.descricao}</h1>
			</c:otherwise>
		</c:choose>
		
		<c:url var="urlSalvarEstimativa" value="/ticket/salvarEstimativa.action"/>
		<form action="${urlSalvarEstimativa}">
			<table style="width: 100%">
				<tr>
					<th>#</th>
					<th>T�tulo</th>
					<th>Tipo</th>
					<th>Cliente</th>
					<th>Valor de Neg�cio</th>
					<th>Esfor�o</th>
					<th>Status</th>
					<td></td>
				</tr>
				<c:forEach items="${tickets}" var="t">
					<tr id="${t.ticketKey}">
						<td>
							${t.ticketKey}
							<input type="hidden" name="ticketKey" value="${t.ticketKey}"/>
						</td>
						<td>${t.titulo}</td>
						<td>${t.tipoDeTicket.descricao}</td>
						<td>${t.cliente}</td>
						<td><input type="text" size="5" name="valorDeNegocio" value="${t.valorDeNegocio}"/></td>
						<td><input type="text" size="5" name="esforco" value="${t.esforco}"/></td>
						<td>${t.kanbanStatus.descricao}</td>
						<td>
							<pronto:icons name="editar.png" title="Editar" onclick="goTo('editar.action?ticketKey=${t.ticketKey}')"></pronto:icons>
						</td>
					</tr>
				</c:forEach>
			</table>	
		
			<div align="center">
				
				<c:choose>
					<c:when test="${sprint ne null}">
						<c:url var="urlCancelar" value="/ticket/listarPorSprint.action?sprintKey=${sprint.sprintKey}"/>
					</c:when>
					<c:otherwise>
						<c:url var="urlCancelar" value="/ticket/listarPorBacklog.action?backlogKey=${backlog.backlogKey}"/>
					</c:otherwise>
				</c:choose>	
				
				<button type="button" onclick="goTo('${urlCancelar}')">Cancelar</button>
				<button type="submit">Salvar</button>	
			</div>
		</form>
	</body>
</html>