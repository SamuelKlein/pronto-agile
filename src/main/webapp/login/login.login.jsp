<%@ include file="/commons/taglibs.jsp"%>
<html>
	<head>
		<title>Login</title>
	</head>
	<body>
		<c:url var="loginUrl" value="/login.action"/>
		<form action="${loginUrl}" method="post">
			<ul class="info"><h1>Login</h1></ul>
			<div class="group">
				<div>
					<input type="text" name="username" id="username" value="${username}">
					<p>Username</p>
				</div>
				<div>
					<input type="password" name="password" id="password">
					<p>Password</p>
				</div>
			</div>
			<div align="center">
				<button type="submit">Login</button>
			</div>
		</form>	
		
		
	</body>
</html>