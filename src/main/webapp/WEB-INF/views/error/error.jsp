<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Error | ShopEase" scope="request"/>
<jsp:include page="../customer/header.jsp"/>
<div class="py-5">
    <h1 class="h3">We could not complete that action</h1>
    <p>${empty errorMessage ? 'The request was invalid or a required record was not found.' : errorMessage}</p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Go home</a>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/products">Browse products</a>
</div>
<jsp:include page="../customer/footer.jsp"/>
