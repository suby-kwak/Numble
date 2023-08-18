<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: KWAK
  Date: 2023-08-18
  Time: 오후 3:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<div>${user.userName}</div>
<a href="#">${user.root.folderName}</a>
<br>
<c:forEach var="storage" items="${user.root.child}">
    <a href="#">${storage.folderName}</a>
    <c:forEach items="${storage}" var="file">
        <a href="#">${file.fileOriginName}</a>
        <br>
    </c:forEach>
</c:forEach>
<c:forEach items="${user.root.files}" var="file">
    <a href="#">${file.fileOriginName}</a>
    <br>
</c:forEach>


<form action="/mybox/filesave" method="post" enctype="multipart/form-data">
    <input type="file" name="file">
    <br>
    <input type="submit" value="제출">
</form>
</body>
</html>
