<%@ include file="/commons/taglibs.jsp"%>
<c:url var="listarExecucoesUrl" value="/execucao/listar.action"/>
<c:url var="listarBancosUrl" value="/bancoDeDados/listar.action"/>
<c:url var="editarTicketUrl" value="/ticket/editar.action"/>
<html>
	<head>
		<title>Scripts</title>
		<script type="text/javascript">

			function incluir(){
				goTo('incluir.action');
			}

			function excluir(scriptKey) {
				if (confirm('Tem certeza que deseja excluir este script? Todos as execu��es associadas a ele ser�o exclu�das.')) {
					goTo('excluir.action?scriptKey=' + scriptKey);
				}
			}

			function reload() {
				$('#formListar').submit();
			}
		
		</script>
	</head>
	<body>
		<h1>
			Scripts
			<pronto:icons name="banco_de_dados.png" title="Ver Bancos de Dados" onclick="goTo('${listarBancosUrl}')"/>
			<pronto:icons name="execucao.png" title="Ver Execu��es" onclick="goTo('${listarExecucoesUrl}')"/>
		</h1>
		
		<div align="right">
			<form action="listar.action" id="formListar">
				Exibir:
				<select name="situacao" onchange="reload()">
					<option value="1"  ${situacao eq 1 or situacao eq null ? 'selected="selected"' : ''}>Pendentes</option>
					<option value="2" ${situacao eq 2 ? 'selected="selected"' : ''}>Executados</option>
					<option value="0" ${situacao eq 0 ? 'selected="selected"' : ''}>Todos</option>
				</select>
			</form>
		</div>
		
		<c:set var="cor" value="${true}"/>
		<table style="width: 100%">
			<tr>
				<th style="width: 40px;"></th>
				<th>Descri��o</th>
				<th>Situa��o</th>
				<th>#</th>
				<th style="width: 16px;"></th>
				<th style="width: 16px;"></th>
			</tr>
			<c:forEach items="${scripts}" var="s">
				<c:set var="cor" value="${!cor}"/>
				
				<tr id="${s.scriptKey}" class="${cor ? 'odd' : 'even'}">
					<td>${s.scriptKey}</td>
					<td class="descricao">${s.descricao}</td>
					<td>${s.situacao}</td>
					<td>
						<c:if test="${s.ticket ne null}">
							<c:choose>
								<c:when test="${s.ticket.estoria}">
									<pronto:icons name="estoria.png" title="Ir para Est�ria - ${s.ticket}" onclick="goTo('${editarTicketUrl}?ticketKey=${s.ticket.ticketKey}')"/>								
								</c:when>
								<c:when test="${s.ticket.tarefa}">
									<pronto:icons name="tarefa.png" title="Ir para Tarefa - ${s.ticket}" onclick="goTo('${editarTicketUrl}?ticketKey=${s.ticket.ticketKey}')"/>
								</c:when>
								<c:otherwise>
									<pronto:icons name="defeito.png" title="Ir para Defeito - ${s.ticket}" onclick="goTo('${editarTicketUrl}?ticketKey=${s.ticket.ticketKey}')"/>
								</c:otherwise>
							</c:choose>
							#${s.ticket.ticketKey}
							(${s.ticket.kanbanStatus.descricao})
						</c:if>
					</td>
					
					<td>
						<a href="editar.action?scriptKey=${s.scriptKey}"><pronto:icons name="editar_script.png" title="Editar" /></a>
					</td>
					<td>
						<a href="#"><pronto:icons name="excluir_script.png" title="Excluir" onclick="excluir(${s.scriptKey});"/></a>
					</td>
				</tr>
			</c:forEach>
		</table>	
		
		<div align="center">
			<button onclick="incluir()">Incluir Script</button>
		</div>
		
	</body>
</html>