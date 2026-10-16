<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Cart | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<h1 class="h3 mb-4">Shopping cart</h1>
<c:choose>
    <c:when test="${empty cart.items}">
        <p>Your cart is empty.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/products">Continue shopping</a>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead><tr><th>Product</th><th>Price</th><th>Qty</th><th>Total</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="item" items="${cart.items}">
                    <tr>
                        <td>
                            <div class="d-flex gap-3 align-items-center">
                                <img src="${item.imageUrl}" alt="" width="64" height="64" style="object-fit:cover;border-radius:8px;">
                                <div>${item.name}<div class="small text-muted">Stock ${item.stock}</div></div>
                            </div>
                        </td>
                        <td>₹<fmt:formatNumber value="${item.salePrice}" minFractionDigits="2"/></td>
                        <td>
                            <form class="d-flex gap-1" method="post" action="${pageContext.request.contextPath}/cart">
                                <input type="hidden" name="productId" value="${item.productId}">
                                <button class="btn btn-sm btn-outline-secondary" name="action" value="decrease">-</button>
                                <input class="form-control form-control-sm" style="width:70px" name="quantity" value="${item.quantity}">
                                <button class="btn btn-sm btn-outline-secondary" name="action" value="increase">+</button>
                                <button class="btn btn-sm btn-light" name="action" value="update">Update</button>
                            </form>
                        </td>
                        <td>₹<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2"/></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/cart">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="productId" value="${item.productId}">
                                <button class="btn btn-sm btn-outline-danger">Remove</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="row justify-content-end">
            <div class="col-md-4">
                <ul class="list-group mb-3">
                    <li class="list-group-item d-flex justify-content-between">Items <span>${cart.itemCount}</span></li>
                    <li class="list-group-item d-flex justify-content-between">Subtotal <span>₹<fmt:formatNumber value="${cart.subtotal}" minFractionDigits="2"/></span></li>
                    <li class="list-group-item d-flex justify-content-between">Discount <span>₹<fmt:formatNumber value="${cart.discount}" minFractionDigits="2"/></span></li>
                    <li class="list-group-item d-flex justify-content-between">Shipping <span>₹<fmt:formatNumber value="${cart.shipping}" minFractionDigits="2"/></span></li>
                    <li class="list-group-item d-flex justify-content-between fw-bold">Total <span>₹<fmt:formatNumber value="${cart.grandTotal}" minFractionDigits="2"/></span></li>
                </ul>
                <a class="btn btn-outline-secondary me-2" href="${pageContext.request.contextPath}/products">Continue shopping</a>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/checkout">Proceed to checkout</a>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<jsp:include page="footer.jsp"/>
