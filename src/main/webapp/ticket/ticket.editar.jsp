<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<script>
		$(function() {
		      $("#descricao").markItUp(mySettings);
		 });
		</script>
	</head>
	<body>
		<form action="salvar.action" method="post">
			<c:choose>
				<c:when test="${ticket.ticketKey gt 0}">
					<form:hidden path="ticket.ticketKey"/>
					<ul class="info"><h1>Ticket #${ticket.ticketKey}</h1></ul>
				</c:when>
				<c:otherwise>
					<ul class="info"><h1>Novo Ticket</h1></ul>
				</c:otherwise>
			</c:choose>
			<div class="group">
				<div>
					<form:input path="ticket.titulo" size="120"/>
					<p>T�tulo</p>
				</div>
				<div>
					<form:input path="ticket.cliente"/><br/>
					<p>Cliente</p>
				</div>
				<div>
					<form:input path="ticket.solicitador"/><br/>
					<p>Solicitador</p>
				</div>
				<div>
					<b>${ticket.reporter}</b>
					<p>Reporter</p> 
				</div>
				<div>
					<form:input path="ticket.valorDeNegocio"/><br/>
					<p>Valor de Neg�cio</p>
				</div>
				<div>
					<form:input path="ticket.esforco"/><br/>
					<p>Esfor�o</p>
				</div>
				<div>
					<form:textarea path="ticket.descricao" id="descricao"/><br/>
				</div>
			</div>
			<div align="center">
				<button type="button" onclick="window.location.href='listar.action'">Cancelar</button>
				<button type="submit">Salvar</button>
			</div>
			
		</form>		
	
	
	</body>
	
	
</html>