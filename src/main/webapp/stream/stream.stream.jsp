<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title>Stream</title>
		<style type="text/css">
		.stream-person {
			display: block;
			float: left;
			padding-right: 25px;
			padding-bottom: 15px;
		}
		
		.stream-item {
			min-height: 75px;
			width: 90% !important;
			margin: auto;
		}
		
		.stream-data {
			font-size: 10px;
			color: #5aa7d4;
			float: right;
		}

		.stream-ticket {
			font-weight: bold;
			margin-bottom: 3px;
		}
		
		.stream-html {
			font-size: 13px;
			line-height: normal;
			margin-left: 100px;
		}
		
		.stream-html p {
			clear: none;
			margin: auto;
			font-size: inherit;
		}
		
		</style>
	</head>
	<body>
		<h1>
			Stream
			<%@ include file="/commons/sprintLinks.jsp" %>
		</h1>

		<c:forEach items="${stream}" var="item">
			<div class="htmlbox stream-item" style="position: relative;">
				<div class="stream-data"><fmt:formatDate value="${item.data}" type="both"/></div>
				<div class="stream-person">
					<div class="person">
						<img alt="Gravatar" align="left" src="http://www.gravatar.com/avatar/${item.usuario.emailMd5}?s=50"/>
						<div class="person_name">${item.usuario.username}</div>
					</div>
				</div>
				<div class="stream-ticket">
					<a href="${raiz}tickets/${item.ticket.ticketKey}">#${item.ticket.ticketKey} - ${item.ticket.titulo}</a>
				</div>
				<div class="stream-html">
					${item.mensagem}
				</div>
			</div>
		</c:forEach>
		<br/>

	</body>
</html>