<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Checkout | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<h1 class="h3 mb-4">Checkout</h1>
<div class="row g-4">
    <div class="col-lg-7">
        <div class="card mb-3"><div class="card-body">
            <h2 class="h6">Customer information</h2>
            <p class="mb-1">${user.fullName}</p>
            <p class="mb-1">${user.email}</p>
            <p class="mb-0">${user.phone}</p>
        </div></div>
        <div class="card mb-3"><div class="card-body">
            <h2 class="h6">Shipping address</h2>
            <c:choose>
                <c:when test="${not empty user.address}">
                    <p class="mb-0">${user.address.addressLine}<br>${user.address.city}, ${user.address.state} ${user.address.pincode}</p>
                </c:when>
                <c:otherwise>
                    <p>No address on file. <a href="${pageContext.request.contextPath}/profile">Add one in your profile</a>.</p>
                </c:otherwise>
            </c:choose>
        </div></div>
        <form method="post" action="${pageContext.request.contextPath}/checkout" class="card card-body">
            <h2 class="h6">Payment method</h2>
            <div class="form-check"><input class="form-check-input" type="radio" name="paymentMethod" value="COD" checked><label class="form-check-label">Cash on Delivery</label></div>
            <div class="form-check mb-3"><input class="form-check-input" type="radio" name="paymentMethod" value="ONLINE"><label class="form-check-label">Demo Online Payment</label></div>
            <button class="btn btn-primary" ${empty user.address ? 'disabled' : ''}>Place order</button>
        </form>
    </div>
    <div class="col-lg-5">
        <div class="card"><div class="card-body">
            <h2 class="h6">Order summary</h2>
            <c:forEach var="item" items="${cart.items}">
                <div class="d-flex justify-content-between small mb-2">
                    <span>${item.name} × ${item.quantity}</span>
                    <span>₹<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2"/></span>
                </div>
            </c:forEach>
            <hr>
            <p class="d-flex justify-content-between mb-1">Subtotal <span>₹<fmt:formatNumber value="${cart.subtotal}" minFractionDigits="2"/></span></p>
            <p class="d-flex justify-content-between mb-1">Discount <span>₹<fmt:formatNumber value="${cart.discount}" minFractionDigits="2"/></span></p>
            <p class="d-flex justify-content-between mb-1">Shipping <span>₹<fmt:formatNumber value="${cart.shipping}" minFractionDigits="2"/></span></p>
            <p class="d-flex justify-content-between fw-bold">Grand total <span>₹<fmt:formatNumber value="${cart.grandTotal}" minFractionDigits="2"/></span></p>
        </div></div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
