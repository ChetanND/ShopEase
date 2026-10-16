<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page not found | ShopEase" scope="request"/>
<jsp:include page="../customer/header.jsp"/>
<div class="text-center py-5">
    <h1 class="h3">404 — Page not found</h1>
    <p>The page you requested does not exist.</p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Go home</a>
</div>
<jsp:include page="../customer/footer.jsp"/>
