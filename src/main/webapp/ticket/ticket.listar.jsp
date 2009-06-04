<%@ include file="/commons/taglibs.jsp"%>
<c:url var="estimarPorSprintUrl" value="/ticket/estimarSprint.action"/>
<c:url var="estimarPorBacklogUrl" value="/ticket/estimarBacklog.action"/>
<c:url var="adicionarTarefasUrl" value="/ticket/listarTarefasParaAdicionarAoSprint.action"/>

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

			function toImpedimentos(ticketKey){
				var url = 'moverParaImpedimentos.action?ticketKey=' + ticketKey; 
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
		<c:choose>
			<c:when test="${sprint.nome ne null}">
				<h1>
					Sprint ${sprint.nome} 
					<pronto:icons name="estimar.png" title="Estimar Sprint" onclick="goTo('${estimarPorSprintUrl}?sprintKey=${sprint.sprintKey}')"/>
					<pronto:icons name="adicionar.png" title="Adicionar Est�rias ou Defeitos do Product Backlog ao Sprint" onclick="goTo('${adicionarTarefasUrl}?sprintKey=${sprint.sprintKey}')"/>
				</h1>	
			</c:when>
			<c:otherwise>
				<h1>${backlog.descricao} <pronto:icons name="estimar.png" title="Estimar Backlog" onclick="goTo('${estimarPorBacklogUrl}?backlogKey=${backlog.backlogKey}')"/>  </h1>
			</c:otherwise>
		</c:choose>
		
		<table style="width: 100%">
			<tr>
				<th>#</th>
				<th>T�tulo</th>
				<th>Tipo</th>
				<th>Cliente</th>
				<th>Valor de Neg�cio</th>
				<th>Esfor�o</th>
				<th>Status</th>
			</tr>
			<c:forEach items="${tickets}" var="t">
				<tr id="${t.ticketKey}">
					<td>${t.ticketKey}</td>
					<td>${t.titulo}</td>
					<td>${t.tipoDeTicket.descricao}</td>
					<td>${t.cliente}</td>
					<td>${t.valorDeNegocio}</td>
					<td>${t.esforco}</td>
					<td>${t.kanbanStatus.descricao}</td>
					<td>
						<pronto:icons name="editar.png" title="Editar" onclick="goTo('editar.action?ticketKey=${t.ticketKey}')"></pronto:icons>
					</td>
					<c:if test="${t.backlog.backlogKey eq 1 or t.backlog.backlogKey eq 3}">
						<td>
							<pronto:icons name="mover_para_pb.png" title="Mover para o Product Backlog" onclick="toProductBacklog(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
					<c:if test="${t.backlog.backlogKey eq 2}">
						<td>
							<pronto:icons name="mover_para_ideias.png" title="Mover para o Backlog de Id�ias" onclick="toIdeias(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
					
					<c:if test="${backlog.backlogKey eq 1 or backlog.backlogKey eq 2 or backlog.backlogKey eq 3}">
						<td>
							<pronto:icons name="mover_para_impedimentos.png" title="Mover para o Backlog de Impedimentos" onclick="toImpedimentos(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
					
					<c:if test="${t.backlog.backlogKey eq 1 or t.backlog.backlogKey eq 2}">
						<td>
							<pronto:icons name="lixeira.png" title="Mover para a Lixeira" onclick="toTrash(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
					<c:if test="${t.backlog.backlogKey eq 4 or t.backlog.backlogKey eq 5}">
						<td>
							<pronto:icons name="restaurar.png" title="Restaurar" onclick="restaurar(${t.ticketKey})"></pronto:icons>
						</td>
					</c:if>
				</tr>
			</c:forEach>
			<tr>
				<th colspan="4"></th>
				<th>${sprint.valorDeNegocioTotal}${backlog.valorDeNegocioTotal}</th>
				<th>${sprint.esforcoTotal}${backlog.esforcoTotal}</th>
				<th></th>
			</tr>
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
			</c:choose>
			<c:if test="${backlog.backlogKey ne 5 and backlog.backlogKey ne 4 and backlog.backlogKey ne 3 and backlog.backlogKey ne null}">
				|
			</c:if>			
		</div>
	</body>
</html>