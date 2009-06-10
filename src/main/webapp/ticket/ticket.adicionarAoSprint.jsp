<%@ include file="/commons/taglibs.jsp"%>
<c:url var="adicionarTarefasUrl" value="/ticket/adicionarAoSprint.action"/>
<c:url var="cancelarUrl" value="/ticket/listarPorSprint.action"/>
<html>
	<head>
		<title>${sprint.nome}</title>
		<script>
			function recalcular() {
	
				var valorDeNegocio = 0;
				var esforco = 0;
				$(':checked').each(function(i, el){
					valorDeNegocio += parseFloat($(el).parents('tr').children('.valorDeNegocio').text());
					esforco += parseFloat($(el).parents('tr').children('.esforco').text());
				});
				$('#somaEsforco').text(esforco);
				$('#somaValorDeNegocio').text(valorDeNegocio);
			}

			function enviar() {
				if($(":checkbox:checked").length == 0) {
					alert("Selecione ao menos uma est�ria ou defeito");
				} else {
					submit();
				}
			}
		</script>	
	</head>
	<body>
		<h1>Sprint ${sprint.nome}</h1>
		<h2>Adicionar Est�rias ou Defeitos</h2>	
		<form action="${adicionarTarefasUrl}" method="post" >
			<input type="hidden" name="sprintKey" value="${sprint.sprintKey}"/>
			<table style="width: 100%">
				<tr>
					<th></th>
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
						<td><input type="checkbox" value="${t.ticketKey}" name="ticketKey" onchange="recalcular()"/></td>
						<td>${t.ticketKey}</td>
						<td>${t.titulo}</td>
						<td>${t.tipoDeTicket.descricao}</td>
						<td>${t.cliente}</td>
						<td class="valorDeNegocio">${t.valorDeNegocio}</td>
						<td class="esforco">${t.esforco}</td>
						<td>${t.kanbanStatus.descricao}</td>
					</tr>
				</c:forEach>
				<tr>
					<th colspan="5"></th>
					<th id="somaValorDeNegocio"></th>
					<th id="somaEsforco"></th>
					<th></th>
				</tr>
			</table>	
			<div align="center">
				<button type="button" onclick="goTo('${cancelarUrl}?sprintKey=${sprint.sprintKey}')">Cancelar</button>
				<button type="button" onclick="enviar()">Adicionar</button>
			</div>
		</form>
	</body>
</html>