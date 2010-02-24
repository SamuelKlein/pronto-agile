<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title><c:choose><c:when test="${ticket.ticketKey gt 0}">${ticket.tipoDeTicket.descricao} #${ticket.ticketKey}</c:when><c:otherwise>Incluir ${ticket.tipoDeTicket.descricao}</c:otherwise></c:choose></title>
		<link rel="stylesheet" type="text/css" media="all" href="${raiz}ticket/ticket.editar.css" />
		<script>
		
		function alterarDesenvolvedores(icone) {
			$('.desenvolvedor').css('display','inline');
			$(icone).hide();
		}

		function alterarTestadores(icone) {
			$('.testador').css('display','inline');
			$(icone).hide();
		}
		
		function toogleDesenvolvedor(username){
			var $imagem = $('#dev_img_'+username);
			var $campo = $('#dev_chk_'+username);
			
			if ($imagem.hasClass('ativo')) {
				$imagem.removeClass('ativo');
				$imagem.addClass('inativo');
				$campo.removeAttr('checked');
			} else {
				$imagem.removeClass('inativo');
				$imagem.addClass('ativo');
				$campo.attr('checked','checked');
			}
		}

		function toogleTestador(username){
			var $imagem = $('#tes_img_'+username);
			var $campo = $('#tes_chk_'+username);
			
			if ($imagem.hasClass('ativo')) {
				$imagem.removeClass('ativo');
				$imagem.addClass('inativo');
				$campo.removeAttr('checked');
			} else {
				$imagem.removeClass('inativo');
				$imagem.addClass('ativo');
				$campo.attr('checked','checked');
			}
		}

		 var alterarPrioridadeDaTarefa = function(ui, event) {
				var $tarefas = $('#listaTarefas li');
				var novaOrdem = new Array($tarefas.length);
				var indice = 0;
				$tarefas.each(function(i, el) {
					novaOrdem[indice++] = el.id;
				});
				$.post('${raiz}tickets/${ticket.ticketKey}/ordenar', { 'ticketKey': novaOrdem });
			};
		
		$(function() {
			  $('#formTicket').validate();
		      $("#descricao").markItUp(mySettings);
		      $("#comentario").markItUp(mySettings);
		      $("#titulo").focus();
    		  $("#dialog").dialog({ 
        		  autoOpen: false, 
        		  height: $(document).height() - 50, 
					width: $(document).width() - 50, 
        		  modal: true });
    		  $("#listaTarefas").sortable({
    				placeholder: 'ui-state-highlight',
    				stop: alterarPrioridadeDaTarefa 
    		  });
    		  $("#listaTarefas").disableSelection();
		 });

		function adicionarScript() {
			goTo('${raiz}scripts/novo?ticketKey=${ticket.ticketKey}');
		}

		function editarScript() {
			goTo('${raiz}scripts/${ticket.script.scriptKey}');
		}
		
		 function excluirAnexo(ticketKey, anexo) {
			if (confirm('Tem certeza que deseja excluir este anexo?')) {
				pronto.doDelete('${raiz}tickets/' +ticketKey + '/anexos/', [{name:'file', value:anexo}]);
			}
		 }

		 function verDescricao(ticketKey) {
				$.ajax({
					url: '${raiz}tickets/' + ticketKey + '/descricao',
					cache: false,
					success: function (data) {
						$("#dialog").dialog('option', 'title', '#' + ticketKey + ' - ' + $('#' + ticketKey + ' .titulo').text());
						$("#dialogDescricao").html(data);
						$("#dialog").dialog('open');
					}
				});
			}
		</script>
	</head>
	<body>
		
		<c:choose>
			<c:when test="${ticket.ticketKey gt 0}">
				<h1>${ticket.tipoDeTicket.descricao} #${ticket.ticketKey} - ${ticket.titulo} (${ticket.kanbanStatus.descricao})</h1>
			</c:when>
			<c:otherwise>
				<h1>Incluir ${ticket.tipoDeTicket.descricao}</h1>
			</c:otherwise>
		</c:choose>

		
		
		<div id="ticketTabs">
			<ul>
				<li><a href="#detalhes">Detalhes</a></li>
				<c:if test="${!empty ticket.filhos}">
					<li><a href="#tarefas">Tarefas (${fn:length(ticket.filhosProntos)} / ${fn:length(ticket.filhos)})</a></li>
				</c:if>
				<li><a href="#comentarios">Coment�rios (${fn:length(ticket.comentarios)})</a></li>
				<li><a href="#anexos">Anexos (${fn:length(anexos)})</a></li>
				<li><a href="#historico">Hist�rico (${fn:length(ticket.logs)})</a></li>
				<div align="right">
				<c:if test="${ticket.ticketKey gt 0}">
					<!-- Operacoes -->
					<c:if test="${ticket.pai ne null and ticket.ticketKey gt 0}">
						<a href="${raiz}tickets/${ticket.pai.ticketKey}"><pronto:icons name="estoria.png" title="Ir para Est�ria" /></a>
					</c:if>
					<c:if test="${ticket.tipoDeTicket.tipoDeTicketKey eq 2}">
						<pronto:icons name="transformar_em_bug.png" title="Transformar em Defeito" onclick="pronto.transformarEmDefeito('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${ticket.tipoDeTicket.tipoDeTicketKey eq 3}">
						<pronto:icons name="transformar_em_estoria.png" title="Transformar em Est�ria" onclick="pronto.transformarEmEstoria('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${(ticket.backlog.backlogKey eq 1 or ticket.backlog.backlogKey eq 3) and usuarioLogado.productOwner and !ticket.tarefa}">
							<pronto:icons name="mover_para_pb.png" title="Mover para o Product Backlog" onclick="pronto.moverParaProductBacklog('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${ticket.backlog.backlogKey eq 2 and !ticket.tarefa}">
							<pronto:icons name="mover_para_ideias.png" title="Mover para o Backlog de Ideias" onclick="pronto.moverParaIdeias('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${(ticket.backlog.backlogKey eq 1 or ticket.backlog.backlogKey eq 2) and usuarioLogado.productOwner}">
						<pronto:icons name="lixeira.png" title="Mover para a Lixeira" onclick="pronto.jogarNoLixo('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${ticket.backlog.backlogKey eq 1 or (ticket.backlog.backlogKey eq 2 and usuarioLogado.productOwner) or ticket.backlog.backlogKey eq 3}">
						<pronto:icons name="mover_para_impedimentos.png" title="Mover para Impedimentos" onclick="pronto.impedir('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${(ticket.backlog.backlogKey eq 2 and usuarioLogado.productOwner)}">
						<pronto:icons name="mover_para_o_sprint_atual.png" title="Mover para o Sprint Atual" onclick="pronto.moverParaSprintAtual('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${ticket.backlog.backlogKey eq 4 or ticket.backlog.backlogKey eq 5}">
						<c:if test="${!ticket.tarefa or (ticket.tarefa && ticket.pai.backlog.backlogKey ne 4 && ticket.pai.backlog.backlogKey ne 5)}">
							<pronto:icons name="restaurar.png" title="Restaurar" onclick="pronto.restaurar('${ticket.ticketKey}')"></pronto:icons>
						</c:if>
					</c:if>
					<c:if test="${ticket.tipoDeTicket.tipoDeTicketKey eq 1 or ticket.tipoDeTicket.tipoDeTicketKey eq 2}">
						<pronto:icons name="nova_tarefa.png" title="Incluir Tarefa" onclick="pronto.incluirTarefa('${ticket.ticketKey}')"></pronto:icons>
					</c:if>
					<c:if test="${ticket.tipoDeTicket.tipoDeTicketKey eq 6}">
						<pronto:icons name="nova_tarefa.png" title="Incluir Tarefa" onclick="pronto.incluirTarefa('${ticket.pai.ticketKey}')"></pronto:icons>
					</c:if>
					<c:choose>
						<c:when test="${ticket.script eq null}">
							<pronto:icons name="adicionar_script.png" title="Adicionar Script de Banco de Dados" onclick="adicionarScript()"/>
						</c:when>
						<c:otherwise>
							<pronto:icons name="editar_script.png" title="Editar Script de Banco de Dados" onclick="editarScript()"/>
						</c:otherwise>
					</c:choose>
					<!-- Fim das Operacoes -->
					<br/><br/>
				</c:if>
				
				</div>
			</ul>
			<div id="detalhes">
				<c:if test="${ticket.ticketKey gt 0}">
					<div class="htmlbox">
						${ticket.html}
					</div>
				</c:if>
				<form action="${raiz}tickets" method="post" id="formTicket">
					<form:hidden path="ticket.tipoDeTicket.tipoDeTicketKey" />
					<form:hidden path="ticket.prioridadeDoCliente"/>
					
					<c:if test="${ticket.ticketKey gt 0}">
						<form:hidden path="ticket.ticketKey"/>
						<c:if test="${ticket.script ne null}">
							<form:hidden path="ticket.script.scriptKey" />
						</c:if>	
					</c:if>
					
					<c:if test="${ticket.pai ne null}">
						<input type="hidden" name="paiKey" value="${ticket.pai.ticketKey}"/>
						<a href="${raiz}tickets/${ticket.pai.ticketKey}">
							<b>#${ticket.pai.ticketKey} - ${ticket.pai.titulo}</b>
						</a>
						<p>Est�ria</p>
					</c:if>

					<div id="divReporter">
						<div align="center" class="person">
							<img alt="Gravatar" align="bottom" title="ticket.reporter.nome" src="http://www.gravatar.com/avatar/${ticket.reporter.emailMd5}?s=80" />
							<div class="person_name">${ticket.reporter.username}</div>
							<div>Reporter</div>
						</div>
						<form:hidden path="ticket.reporter.username"/><br/>
					</div>
						
					<div class="group">
						
						<div>
							<form:input path="ticket.titulo" size="70" id="titulo" cssClass="required"/>
							<p>T�tulo</p>
						</div>							

						<div class="bloco">

							<div id="divBacklog">
								<form:hidden path="ticket.backlog.backlogKey"/>
								<b>${ticket.backlog.descricao}</b>					
								<p>Backlog</p>
							</div>
							
							<div id="divSprint">
								<c:if test="${ticket.backlog.backlogKey eq 3}">
									<c:choose>
										<c:when test="${!ticket.tarefa and !(ticket.sprint ne null and ticket.sprint.fechado)}">
											<form:select path="ticket.sprint.sprintKey">
												<form:options items="${sprints}" itemLabel="nome" itemValue="sprintKey"/>
											</form:select>
											<p>Sprint</p>
										</c:when>
										<c:otherwise>
											<form:hidden path="ticket.sprint.sprintKey"/>
											<b>${ticket.sprint.nome}</b>
											<p>Sprint</p>
										</c:otherwise>
									</c:choose>
								</c:if>
							</div>
						</div>
						
						<div class="bloco">
						
							<div id="divCliente">
								<c:choose>
									<c:when test="${!ticket.tarefa}">
										<select name="clienteKey" id="clienteKey">
											<c:forEach var="c" items="${clientes}">
												<c:choose>
													<c:when test="${c.clienteKey eq ticket.cliente.clienteKey}">
														<option value="${c.clienteKey}" selected="selected">${c.nome}</option>	
													</c:when>
													<c:otherwise>
														<option value="${c.clienteKey}">${c.nome}</option>
													</c:otherwise>
												</c:choose>
												
											</c:forEach>							
										</select>
										<br/>
									</c:when>
									<c:otherwise>
										<input type="hidden" name="clienteKey" value="${ticket.cliente.clienteKey}" />
										<b>${ticket.cliente eq null ? '-' : ticket.cliente.nome}</b>							
									</c:otherwise>
								</c:choose>
								<p>Cliente</p>
							</div>
							
							<div id="divSolicitador">
								<c:choose>
									<c:when test="${!ticket.tarefa}">
										<form:input path="ticket.solicitador"  cssClass="required"/><br/>
									</c:when>
									<c:otherwise>
										<form:hidden path="ticket.solicitador"/>
										<b>${ticket.solicitador eq null ? '-' : ticket.solicitador}</b>
									</c:otherwise>
								</c:choose>
								<p>Solicitador</p>
							</div>
						</div>

						<div class="bloco">
							
							<c:if test="${ticket.ticketKey gt 0}">
								<div id="divTempoDeVida">
									<b>${ticket.tempoDeVidaEmDias} dias</b>
									<p>Tempo de Vida</p> 
								</div>
								
								<div id="divDataDeCriacao">
									<fmt:formatDate value="${ticket.dataDeCriacao}" type="both" var="dataDeCriacao"/>
									<b>${dataDeCriacao}</b>
									<input type="hidden" name="dataDeCriacao" value="${dataDeCriacao}">
									<p>Data de Cria��o</p> 
								</div>
								
							
								<div id="divDataUltimaAlteracao">
									<fmt:formatDate var="dataDaUltimaAlteracao" value="${ticket.dataDaUltimaAlteracao}" pattern="dd/MM/yyyy HH:mm:ss"/>
									<input type="hidden" name="dataDaUltimaAlteracao" value="${dataDaUltimaAlteracao}"/>
									<b>${dataDaUltimaAlteracao}</b>
									<p>Data da �ltima Altera��o</p>
								</div>
							</c:if>
						
						</div>
						
						
						<div class="bloco">
							<c:if test="${!ticket.tarefa}">
								<div id="divValorDeNegocio">
									<c:choose>
										<c:when test="${usuarioLogado.productOwner}">
											<form:input path="ticket.valorDeNegocio" cssClass="required digits" size="4" maxlength="4"/><br/>
										</c:when>
										<c:otherwise>
											<form:hidden path="ticket.valorDeNegocio"/>
											${ticket.valorDeNegocio gt 0 ? ticket.valorDeNegocio : "-"}<br/>
										</c:otherwise>
									</c:choose>
									<p>Valor de Neg�cio</p>
								</div>
							</c:if>
							
							<div id="divEsforco">
								<c:choose>
									<c:when test="${usuarioLogado.equipe and empty ticket.filhos}">
										<c:choose>
											<c:when test="${configuracoes['tipoDeEstimativa'] eq 'PMG'}">
												<form:select path="ticket.esforco" >
													<form:option value="10">Pequeno</form:option>
													<form:option value="20">M�dio</form:option>
													<form:option value="30">Grande</form:option>
												</form:select>
											</c:when>
											<c:otherwise>
												<form:input path="ticket.esforco" cssClass="required number" size="5"/>	
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<form:hidden path="ticket.esforco"/>
										<b>${ticket.esforco gt 0 ? ticket.esforco : "-"}</b>
									</c:otherwise>
								</c:choose>
								<br/><p>Esfor�o</p>
							</div>
							
							<div>
								<form:select path="ticket.par">
									<form:option value="true">Sim</form:option>
									<form:option value="false">N�o</form:option>
								</form:select>
								<br/>
								<p>Em Par?</p>
							</div>
						</div>
						
						<div class="bloco">
							<div>
								<form:select path="ticket.planejado">
									<form:option value="true">Sim</form:option>
									<form:option value="false">N�o</form:option>
								</form:select>
								<br/>
								<p>Planejado?</p>
							</div>
							
							<div>
									<form:select path="ticket.categoria.categoriaKey" items="${categorias}" itemLabel="descricao" itemValue="categoriaKey"/>
									<br/>
									<p>Categoria</p>
							</div>
							
							
							<c:if test="${empty ticket.filhos}">
								<div>
									<form:input path="ticket.branch" size="30"/><br/>
									<p>Branch</p>
								</div>
							</c:if>
							
						</div>
						
						<div class="bloco">
							<div>
								<form:select path="ticket.kanbanStatus.kanbanStatusKey">
									<form:options items="${kanbanStatus}" itemLabel="descricao" itemValue="kanbanStatusKey"/>
								</form:select>
								<br/>
								<p>Kanban Status</p>
							</div>
							
							<div>
								<fmt:formatDate var="dataDePronto" value="${ticket.dataDePronto}" pattern="dd/MM/yyyy"/>
								<input type="text" value="${dataDePronto}" name="dataDePronto" class="datePicker"/>
								<p>Data de Pronto</p>
							</div>
						</div>
						
						
						<c:if test="${ticket.tarefa or empty ticket.filhos}">
							<div class="linha">
							<div>
								<c:forEach items="${desenvolvedores}" var="u" varStatus="s">
									<c:set var="checked" value="${false}"/>
									<c:forEach items="${ticket.desenvolvedores}" var="d">
										<c:if test="${d.username eq u.username}">
											<c:set var="checked" value="${true}"/>
										</c:if>
									</c:forEach>
									<div class="person desenvolvedor" style="display: ${checked ? 'inline' : 'none'}">
										<img alt="${u.username} - Clique para adicionar/remover" id="dev_img_${u.username}" class="${checked ? 'ativo' : 'inativo'}" align="bottom" title="${u.nome}" src="http://www.gravatar.com/avatar/${u.emailMd5}?s=45" onclick="toogleDesenvolvedor('${u.username}')" style="cursor:pointer"/>
										<input id="dev_chk_${u.username}"  type="checkbox" name="desenvolvedor" value="${u.username}" ${checked ? 'checked="checked"' : ''} style="display: none;">
										<div class="person_name">${u.username}</div>
									</div>
								</c:forEach>
								<p style="clear: both"><b>Desenvolvedores</b><pronto:icons name="editar.png" title="Alterar Desenvolvedores" onclick="alterarDesenvolvedores(this)"/></p>
							</div>
							</div>
							<div class="linha">
							<div>
								<c:forEach items="${testadores}" var="u" varStatus="s">
									<c:set var="checked" value="${false}"/>
									<c:forEach items="${ticket.testadores}" var="d">
										<c:if test="${d.username eq u.username}">
											<c:set var="checked" value="${true}"/>
										</c:if>
									</c:forEach>
									<div class="person testador" style="display: ${checked ? 'inline' : 'none'}">
										<img alt="${u.username} - Clique para adicionar/remover" id="tes_img_${u.username}" class="${checked ? 'ativo' : 'inativo'}" align="bottom" title="${u.nome}" src="http://www.gravatar.com/avatar/${u.emailMd5}?s=45" onclick="toogleTestador('${u.username}')" style="cursor:pointer"/>
										<input id="tes_chk_${u.username}"  type="checkbox" name="testador" value="${u.username}" ${checked ? 'checked="checked"' : ''} style="display: none;">
										<div class="person_name">${u.username}</div>
									</div>
								</c:forEach>
								<p style="clear: both"><b>Testadores</b><pronto:icons name="editar.png" title="Alterar Testadores" onclick="alterarTestadores(this)"/></p>
							</div>
							</div>
						</c:if>
						
						
						<c:if test="${ticket.tarefa}">
							<form:hidden path="ticket.prioridade"/><br/>
						</c:if>

						<div class="linha">						
							<h3>Descri��o</h3>
							<div>
								<form:textarea path="ticket.descricao" id="descricao"/>
							</div>
						</div>
						
					</div>
					<div align="center">
						<c:choose>
							<c:when test="${ticket.sprint ne null}">
								<button type="button" onclick="pronto.doGet('${raiz}backlogs/sprints/${ticket.sprint.sprintKey}')">Cancelar</button>
							</c:when>
							<c:otherwise>
								<button type="button" onclick="pronto.doGet('${raiz}backlogs/${ticket.backlog.backlogKey}')">Cancelar</button>
							</c:otherwise>
						</c:choose>
						<button type="submit">Salvar</button>
					</div>
				</form>	
			</div>
			<c:if test="${!empty ticket.filhos}">
				<div id="tarefas">
					<ul id="listaTarefas">
						<c:forEach items="${ticket.filhosOrdenadosKanbanStatus}" var="filho">
							<c:set var="cssClass" value="${filho.dataDePronto eq null ? 'ui-state-default': 'ui-state-disabled'}"/>
							<li class="${cssClass}" id="${filho.ticketKey}">
								<span style="float: left;">
									<b>${filho.ticketKey}</b>
									<span class="titulo">${filho.titulo}</span>
								</span>
								<span style="float: right;">
									${filho.kanbanStatus.descricao}
									<pronto:icons name="ver_descricao.png" title="Ver Descri��o" onclick="verDescricao(${filho.ticketKey});"/>
									<a href="${raiz}tickets/${filho.ticketKey}"><pronto:icons name="editar.png" title="Editar" /></a>
								</span>
							</li>
						</c:forEach>
					</ul>
					<br/>
				</div>
			</c:if>
			<div id="comentarios">
				<%@ include file="ticket.comentarios.jsp" %>
			</div>
			<div id="anexos">
				<c:if test="${ticket.ticketKey gt 0}">
				<ul style="list-style-type: none;">
					<c:forEach items="${anexos}" var="anexo">
						<li>
							<c:choose>
								<c:when test="${anexo.imagem}">
									<pronto:icons name="imagem.gif" title="Imagem ${anexo.extensao}"/>
								</c:when>
								<c:when test="${anexo.planilha}">
									<pronto:icons name="excel.png" title="Plan�lha ${anexo.extensao}"/>
								</c:when>
								<c:when test="${anexo.extensao eq 'pdf'}">
									<pronto:icons name="pdf.png" title="Arquivo PDF"/>
								</c:when>
								<c:otherwise>
									<pronto:icons name="anexo.png" title="${anexo.extensao}"/>
								</c:otherwise>
							</c:choose>
							
							${anexo.nomeParaExibicao}
							<pronto:icons name="download.gif" title="Baixar Anexo" onclick="goTo('${raiz}tickets/${ticket.ticketKey}/anexos?file=${anexo}')"/>
							<pronto:icons name="excluir.png" title="Excluir Anexo" onclick="excluirAnexo(${ticket.ticketKey},'${anexo}');"/>
						</li>
					</c:forEach>
				</ul>
				<h4>Incluir anexo</h4>						
				<form action="${raiz}tickets/${ticket.ticketKey}/upload" method="post" enctype="multipart/form-data">
					<input type="file" name="arquivo">
					<button type="submit">Upload</button>
				</form>
			</div>
			<div id="historico">
				<ul id="listaHistorico">
					<c:set var="dataGrupo" value="${null}"/>
					<c:forEach items="${ticket.logs}" var="log">
						<fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy" var="dataAtual"/>
						<c:if test="${dataGrupo eq null or dataGrupo ne dataAtual}">
							<h4><fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy"/></h4>
							<fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy" var="dataGrupo"/>
						</c:if>
						<c:choose>
							<c:when test="${log.campo eq 'descri��o' or log.campo eq 'descricao'}">
								<li><fmt:formatDate value="${log.data}" pattern="HH:mm"/> - ${log.usuario} - descri��o alterada <a href="${raiz}tickets/${ticket.ticketKey}/log/${log.ticketHistoryKey}">(ver)</a></li>
							</c:when>
							<c:otherwise>
								<li>${log.descricaoSemData}</li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>
			</div>
		</div>
			
		</c:if>
		
		<div title="Descri��o" id="dialog" style="display: none; width: 500px;">
			<div align="left" id="dialogDescricao">Aguarde...</div>
		</div>
		<script>
			$(function(){
				$('#ticketTabs').tabs();
				$('.datePicker').datepicker();
			});
		</script>
	</body>
</html>