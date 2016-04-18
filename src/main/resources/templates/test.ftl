<html>
<head>
    <title>freemarker测试</title>
</head>
<body>
<h1>${message},${user}</h1>
<#list listsss as being>
<br>
<li>${being.name}  ${being.age} <br>
</li>
</#list>
</body>
</html>