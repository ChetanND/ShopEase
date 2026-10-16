<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Server error | ShopEase" scope="request"/>
<jsp:include page="../customer/header.jsp"/>
<div class="text-center py-5">
    <h1 class="h3">500 — Something went wrong</h1>
    <p>We could not complete this request. If this continues, check the database connection and Tomcat logs.</p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Go home</a>
</div>
<jsp:include page="../customer/footer.jsp"/>
