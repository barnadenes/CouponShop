<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="en">
<jsp:include page="../snippets/head.jsp">
    <jsp:param name="title" value="Shop"/>
</jsp:include>
<body>
<h1>Shop</h1>
<c:if test="${empty error}">
    <p>ID: ${shop.id}</p>
    <p>Name: ${shop.name}</p>
</c:if>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Percentage (%)</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="item" items="${Flist}">
        <tr>
            <td>${item.id}</td>
            <td>${item.name}</td>
            <td>${item.percentage}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<jsp:include page="../snippets/show-error.jsp"/>
<jsp:include page="../snippets/to-profile.jsp"/>
<jsp:include page="../snippets/logout.jsp"/>
</body>
</html>
