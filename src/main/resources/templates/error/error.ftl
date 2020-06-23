<!DOCTYPE html>
<html>
	<head>
		<title>出现了一点儿小问题</title>
	</head>
	<body>
		<h3>${message.message!"未知异常"}</h3>
		<hr/>
		<pre>${stackTrace!}</pre>
	</body>
</html>