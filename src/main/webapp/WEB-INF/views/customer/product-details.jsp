<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${product.name} | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Home</a></li>
        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/products">Products</a></li>
        <li class="breadcrumb-item active">${product.name}</li>
    </ol>
</nav>
<div class="row g-4">
    <div class="col-lg-6">
        <img class="detail-image shadow-sm" src="${product.imageUrl}" alt="${product.name}">
    </div>
    <div class="col-lg-6">
        <span class="badge bg-secondary">${product.categoryName}</span>
        <h1 class="h3 mt-2">${product.name}</h1>
        <p class="text-muted">★ ${product.rating} (${product.reviewCount} reviews) · Stock: ${product.stock}</p>
        <p class="fs-4">
            ₹<fmt:formatNumber value="${product.salePrice}" minFractionDigits="2"/>
            <c:if test="${product.hasDiscount}">
                <span class="price-old fs-6">₹<fmt:formatNumber value="${product.price}" minFractionDigits="2"/></span>
                <span class="badge bg-warning text-dark">${product.discount}% off</span>
            </c:if>
        </p>
        <p>${product.description}</p>
        <h2 class="h6">Specifications</h2>
        <ul>
            <li>Category: ${product.categoryName}</li>
            <li>Availability: ${product.inStock ? 'In stock' : 'Out of stock'}</li>
            <li>SKU: SE-${product.id}</li>
        </ul>
        <form class="d-flex align-items-center gap-2 mb-3" method="post" action="${pageContext.request.contextPath}/cart">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="productId" value="${product.id}">
            <div class="input-group" style="width:140px;">
                <button class="btn btn-outline-secondary" type="button" data-qty="-1">-</button>
                <input id="qtyInput" class="form-control text-center" name="quantity" value="1" min="1" max="${product.stock}">
                <button class="btn btn-outline-secondary" type="button" data-qty="1">+</button>
            </div>
            <button class="btn btn-primary" ${product.inStock ? '' : 'disabled'}>Add to cart</button>
            <button class="btn btn-warning" name="buyNow" value="1" ${product.inStock ? '' : 'disabled'}>Buy now</button>
        </form>
    </div>
</div>

<section class="mt-5">
    <h2 class="h4">Customer reviews</h2>
    <c:forEach var="r" items="${reviews}">
        <div class="card mb-2"><div class="card-body">
            <strong>${r.userName}</strong> · ★ ${r.rating}
            <p class="mb-0">${r.comment}</p>
        </div></div>
    </c:forEach>
    <c:if test="${empty reviews}"><p class="text-muted">No reviews yet.</p></c:if>
    <c:if test="${canReview}">
        <div class="card mt-3"><div class="card-body">
            <h3 class="h6">Write a review</h3>
            <form method="post" action="${pageContext.request.contextPath}/review">
                <input type="hidden" name="productId" value="${product.id}">
                <label class="form-label">Rating</label>
                <select class="form-select mb-2" name="rating" required>
                    <option value="5">5</option><option value="4">4</option><option value="3">3</option><option value="2">2</option><option value="1">1</option>
                </select>
                <textarea class="form-control mb-2" name="comment" rows="3" required minlength="10"></textarea>
                <button class="btn btn-primary">Submit review</button>
            </form>
        </div></div>
    </c:if>
</section>
<jsp:include page="footer.jsp"/>
