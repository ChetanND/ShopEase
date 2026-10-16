<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Order confirmed | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<div class="text-center py-4">
    <h1 class="h3">Thank you. Your order is placed.</h1>
    <p class="lead">Order ID: <strong>${order.orderNumber}</strong></p>
    <p>Status: ${order.status} · Payment: ${order.paymentMethod} (${order.paymentStatus})</p>
    <p>Grand total: ₹<fmt:formatNumber value="${order.grandTotal}" minFractionDigits="2"/></p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/order?id=${order.orderNumber}">View order details</a>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/products">Continue shopping</a>
</div>
<jsp:include page="footer.jsp"/>
