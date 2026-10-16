<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Order ${order.orderNumber} | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<nav aria-label="breadcrumb"><ol class="breadcrumb"><li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/orders">My Orders</a></li><li class="breadcrumb-item active">${order.orderNumber}</li></ol></nav>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h1 class="h4 mb-0">Order ${order.orderNumber}</h1>
    <span class="badge bg-primary">${order.status}</span>
</div>
<div class="row g-4">
    <div class="col-lg-8">
        <table class="table">
            <thead><tr><th>Product</th><th>Qty</th><th>Price</th><th>Line total</th></tr></thead>
            <tbody>
            <c:forEach var="item" items="${order.items}">
                <tr>
                    <td>${item.productName}</td>
                    <td>${item.quantity}</td>
                    <td>₹<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2"/></td>
                    <td>₹<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="col-lg-4">
        <div class="card card-body">
            <h2 class="h6">Shipping address</h2>
            <p>${order.fullName}<br>${order.addressLine}<br>${order.city}, ${order.state} ${order.pincode}</p>
            <p>Payment: ${order.paymentMethod} (${order.paymentStatus})</p>
            <p class="fw-bold">Total: ₹<fmt:formatNumber value="${order.grandTotal}" minFractionDigits="2"/></p>
            <c:if test="${order.cancellable}">
                <form method="post" action="${pageContext.request.contextPath}/order">
                    <input type="hidden" name="id" value="${order.orderNumber}">
                    <button class="btn btn-outline-danger" onclick="return confirm('Cancel this order?');">Cancel order</button>
                </form>
            </c:if>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
