<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title>${backlog.descricao}${sprint.nome}</title>
		<script>
			function toTrash(ticketKey){
				var url = 'jogarNoLixo.action?ticketKey=' + ticketKey; 
				$.post(url, {
					success: function() {
						$('#'+ticketKey).remove();		
					}
				});
			}

			function toProductBacklog(ticketKey){
				var url = 'moverParaProductBacklog.action?ticketKey=' + ticketKey; 
				$.post(url, {
					success: function() {
						$('#'+ticketKey).remove();		
					}
				});
			}

			function toIdeias(ticketKey){
				var url = 'moverParaIdeias.action?ticketKey=' + ticketKey; 
				$.post(url, {
					success: function() {
						$('#'+ticketKey).remove();		
					}
				});
			}
			
			function restaurar(ticketKey){
				var url = 'restaurar.action?ticketKey=' + ticketKey; 
				$.post(url, {
					success: function() {
						$('#'+ticketKey).remove();		
					}
				});
			}
		</script>
	</head>
	<body>
		<h1>${backlog.descricao}${sprint.nome}</h1>
		<table style="width: 100%">
			<tr>
				<th>#</th>
				<th>T�tulo</th>
				<th>Tipo</th>
				<th>Cliente</th>
				<th>Valor de Neg�cio</th>
				<th>Esfor�o</th>
			</tr>
			<c:forEach items="${tickets}" var="t">
				<tr id="${t.ticketKey}">
					<td>${t.ticketKey}</td>
					<td>${t.titulo}</td>
					<td>${t.tipoDeTicket.descricao}</td>
					<td>${t.cliente}</td>
					<td>${t.valorDeNegocio}</td>
					<td>${t.esforco}</td>
					
					<td>
						<pronto:icons name="editar.png" title="Editar" onclick="goTo('editar.action?ticketKey=${t.ticketKey}')"></pronto:icons>
					</td>
					<c:if test="${backlog.backlogKey eq 1}">
						<td>
							<pronto:icons name="mover_para_pb.png" title="Mover para o Product Backlog" onclick="toProductBacklog(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
					<c:if test="${backlog.backlogKey eq 2}">
						<td>
							<pronto:icons name="mover_para_ideias.png" title="Mover para o Backlog de Id�ias" onclick="toIdeias(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
					<c:if test="${backlog.backlogKey eq 1 or backlog.backlogKey eq 2}">
						<td>
							<pronto:icons name="lixeira.png" title="Mover para a Lixeira" onclick="toTrash(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
					<c:if test="${backlog.backlogKey eq 4}">
						<td>
							<pronto:icons name="restaurar.png" title="Restaurar" onclick="restaurar(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</table>	
		
		<div align="center">
			<c:choose>
				<c:when test="${backlog.backlogKey eq 1}">
					|&nbsp;&nbsp;<a href="editar.action?backlogKey=${backlog.backlogKey}&tipoDeTicketKey=1">Nova Id�ia</a>&nbsp;&nbsp;
				</c:when>
				<c:when test="${backlog.backlogKey eq 2}">
					|&nbsp;&nbsp;<a href="editar.action?backlogKey=${backlog.backlogKey}&tipoDeTicketKey=2">Nova Est�ria</a>&nbsp;&nbsp;
					|&nbsp;&nbsp;<a href="editar.action?backlogKey=${backlog.backlogKey}&tipoDeTicketKey=3">Novo Defeito</a>&nbsp;&nbsp;
				</c:when>
				<c:when test="${backlog.backlogKey eq 5}">
					|&nbsp;&nbsp;<a href="editar.action?backlogKey=${backlog.backlogKey}&tipoDeTicketKey=5">Novo Impedimento</a>&nbsp;&nbsp;
				</c:when>
			</c:choose>
			<c:if test="${backlog.backlogKey ne 4}">
				|
			</c:if>			
		</div>
	</body>
</html>