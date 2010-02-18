<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title>Configura��es</title>
	</head>
	<body>
		
		<h1>Configura��es</h1>
		
		<form action="${raiz}configuracoes/salvar" id="formConfiguracoes" method="POST">
			
			<div class="group">
				<div>
					<select name="tipo_de_estimativa">
						<c:forEach items="${tiposDeEstimativa}" var="tipo">
							<c:choose>
								<c:when test="${tipo.string eq mapa['tipo_de_estimativa']}">
									<option selected="selected" value="${tipo}">${tipo.descricao}</option>
								</c:when>
								<c:otherwise>
									<option value="${tipo}">${tipo.descricao}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					<p>Tipo de Estimativa</p>
				</div>
			</div>
			<button type="button" onclick="salvar()">Salvar</button>
			
		</form>
		

	<script>
		function salvar() {
			$("#formConfiguracoes").submit();
		}
	</script>	
	</body>
</html>
	