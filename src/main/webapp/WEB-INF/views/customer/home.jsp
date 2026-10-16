<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="ShopEase | Home" scope="request"/>
<jsp:include page="header.jsp"/>

<section class="hero mb-5">
    <div class="col-lg-8">
        <p class="text-uppercase small mb-2">Everyday essentials, thoughtfully priced</p>
        <h1 class="display-5 fw-bold">Shop smarter with ShopEase</h1>
        <p class="lead">Browse electronics, fashion, home, and more. Filter by category, compare prices, and check out with cash on delivery or a demo online payment.</p>
        <a class="btn btn-warning btn-lg me-2" href="${pageContext.request.contextPath}/products">Shop products</a>
        <a class="btn btn-outline-light btn-lg" href="${pageContext.request.contextPath}/categories">Browse categories</a>
    </div>
</section>

<section class="mb-5">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="h4 mb-0">Popular categories</h2>
        <a href="${pageContext.request.contextPath}/categories">View all</a>
    </div>
    <div class="row g-3">
        <c:forEach var="cat" items="${categories}">
            <div class="col-6 col-md-4 col-lg-3">
                <a class="text-decoration-none text-dark" href="${pageContext.request.contextPath}/products?category=${cat.id}">
                    <div class="card category-card h-100">
                        <img src="${cat.imageUrl}" alt="${cat.name}">
                        <div class="card-body">
                            <h3 class="h6 mb-1">${cat.name}</h3>
                            <p class="small text-muted mb-0">${cat.productCount} products</p>
                        </div>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>
</section>

<section class="mb-5">
    <h2 class="h4 mb-3">Featured products</h2>
    <div class="row g-3">
        <c:forEach var="p" items="${featured}">
            <div class="col-sm-6 col-lg-3">
                <div class="card product-card h-100">
                    <img src="${p.imageUrl}" alt="${p.name}">
                    <div class="card-body d-flex flex-column">
                        <span class="badge bg-light text-dark mb-2">${p.categoryName}</span>
                        <h3 class="h6">${p.name}</h3>
                        <p class="small text-muted">Rating ${p.rating} · <c:choose><c:when test="${p.inStock}">In stock</c:when><c:otherwise>Out of stock</c:otherwise></c:choose></p>
                        <p class="mb-2">
                            <strong>₹<fmt:formatNumber value="${p.salePrice}" minFractionDigits="2"/></strong>
                            <c:if test="${p.hasDiscount}"><span class="price-old ms-1">₹<fmt:formatNumber value="${p.price}" minFractionDigits="2"/></span></c:if>
                        </p>
                        <div class="mt-auto d-flex gap-2">
                            <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/product?id=${p.id}">Details</a>
                            <form method="post" action="${pageContext.request.contextPath}/cart">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productId" value="${p.id}">
                                <input type="hidden" name="quantity" value="1">
                                <button class="btn btn-sm btn-primary" ${p.inStock ? '' : 'disabled'}>Add to cart</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</section>

<section class="mb-5">
    <h2 class="h4 mb-3">New arrivals</h2>
    <div class="row g-3">
        <c:forEach var="p" items="${newest}">
            <div class="col-sm-6 col-lg-3">
                <div class="card product-card h-100">
                    <img src="${p.imageUrl}" alt="${p.name}">
                    <div class="card-body">
                        <h3 class="h6">${p.name}</h3>
                        <p class="mb-2"><strong>₹<fmt:formatNumber value="${p.salePrice}" minFractionDigits="2"/></strong></p>
                        <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/product?id=${p.id}">View</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</section>

<section class="row g-3 mb-5">
    <div class="col-md-4">
        <div class="card h-100 p-3"><h3 class="h6">Special offers</h3><p class="mb-0">Free shipping on orders of ₹999 and above. Product discounts are already shown on each card.</p></div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 p-3"><h3 class="h6">Secure checkout</h3><p class="mb-0">Session-based cart, hashed passwords, and transactional order placement.</p></div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 p-3"><h3 class="h6">Easy returns mindset</h3><p class="mb-0">Cancel early-stage orders from My Orders. Stock is restored automatically.</p></div>
    </div>
</section>

<jsp:include page="footer.jsp"/>
