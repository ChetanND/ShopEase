<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="My Orders | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<h1 class="h3 mb-4">My orders</h1>
<div class="table-responsive">
    <table class="table">
        <thead><tr><th>Order ID</th><th>Date</th><th>Total</th><th>Payment</th><th>Status</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="o" items="${pageResult.items}">
            <tr>
                <td>${o.orderNumber}</td>
                <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                <td>₹<fmt:formatNumber value="${o.grandTotal}" minFractionDigits="2"/></td>
                <td>${o.paymentMethod}</td>
                <td><span class="badge bg-info text-dark">${o.status}</span></td>
                <td><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/order?id=${o.orderNumber}">View details</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<ul class="pagination">
    <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
        <li class="page-item ${pageResult.page == i ? 'active' : ''}"><a class="page-link" href="${pageContext.request.contextPath}/orders?page=${i}">${i}</a></li>
    </c:forEach>
</ul>
<jsp:include page="footer.jsp"/>
