<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
    <c:if test="${empty sessionScope}">
        <section>
            <b style="font-size: 20px;">회원가입</b>
            <form action="/mybox/signup" method="post">
                <input type="text" name="userID" placeholder="id">
                <input type="text" name="password" placeholder="password">
                <input type="text" name="userName" placeholder="name">
                <br>
                <input type="submit" value="회원가입">
            </form>
        </section>
        <br>
        <section>
            <b style="font-size: 20px;">로그인</b>
            <form action="/mybox/login" method="post">
                <input type="text" name="userID" placeholder="id">
                <input type="text" name="password" placeholder="password">
                <br>
                <input type="submit" value="로그인">
            </form>
        </section>
    </c:if>

    <c:if test="${not empty sessionScope}">
        <div>${sessionScope.userName}</div>
        <fmt:formatNumber value="${user.storageSize/(1024*1024*1024)-user.storageSize/(1024*1024*1024)%0.01}" pattern="#.##"/>GB<br>
        <button type="button" onclick="location.href='/mybox/logout'">로그아웃</button>
        <br>
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
            <a href="/mybox/filedownload/${file.fileNo}">${file.fileOriginName}</a>
            <br>
        </c:forEach>

        <form action="/mybox/filesave" method="post" enctype="multipart/form-data">
            <input type="file" name="file">
            <br>
            <input type="submit" value="제출">
        </form>
    </c:if>
</body>
</html>
