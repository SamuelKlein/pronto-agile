<%@ include file="/commons/taglibs.jsp"%>
<c:url var="raiz" value="/"/>
<ul id="jsddm">
	<li><a href="#">Cadastros</a>
	<ul>
		<li><a href="${raiz}usuario/listar.action">Usu�rios</a></li>
	</ul>
	</li>
	<li><a href="#">Kanban</a></li>
	<li><a href="${raiz}ticket/listar.action">Product Backlog</a></li>
	<li><a href="#">Backlog de Id�ias</a></li>
	<li><a href="#">Sprints</a>
	<ul>
		<li><a href="#">Portugal</a></li>
		<li><a href="#">Inglaterra</a></li>
	</ul>
	</li>
	<li><a href="#">Ajuda</a></li>
</ul>
<div class="clear"></div>