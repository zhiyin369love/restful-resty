<html>
<head>
    <title>使用说明</title>
</head>
<body>
<#list help_center_list as help_center >
<li>${help_center_index+1} <a href="/api/v2.0.1/buyer/help_center/${help_center.id}.ftl"> ${help_center.title}</a>
</li>
</#list>
</body>
</html>