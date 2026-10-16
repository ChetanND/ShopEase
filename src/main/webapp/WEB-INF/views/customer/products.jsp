<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Products | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<nav aria-label="breadcrumb"><ol class="breadcrumb"><li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Home</a></li><li class="breadcrumb-item active">Products</li></ol></nav>
<div class="row">
    <aside class="col-lg-3 mb-4">
        <form class="card card-body" method="get" action="${pageContext.request.contextPath}/products">
            <h2 class="h6">Filters</h2>
            <label class="form-label">Search</label>
            <input class="form-control mb-2" name="q" value="${q}">
            <label class="form-label">Category</label>
            <select class="form-select mb-2" name="category">
                <option value="">All</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.id}" ${selectedCategory == cat.id ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
            </select>
            <label class="form-label">Min price</label>
            <input class="form-control mb-2" name="minPrice" value="${minPrice}">
            <label class="form-label">Max price</label>
            <input class="form-control mb-2" name="maxPrice" value="${maxPrice}">
            <label class="form-label">Sort</label>
            <select class="form-select mb-3" name="sort">
                <option value="popularity" ${sort == 'popularity' or empty sort ? 'selected' : ''}>Popularity</option>
                <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Newest</option>
                <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
            </select>
            <button class="btn btn-primary w-100">Apply</button>
        </form>
    </aside>
    <div class="col-lg-9">
        <p class="text-muted">${pageResult.totalItems} products</p>
        <div class="row g-3">
            <c:forEach var="p" items="${pageResult.items}">
                <div class="col-md-6 col-xl-4">
                    <div class="card product-card h-100">
                        <img src="${p.imageUrl}" alt="${p.name}">
                        <div class="card-body d-flex flex-column">
                            <span class="badge bg-secondary mb-2">${p.categoryName}</span>
                            <h3 class="h6">${p.name}</h3>
                            <p class="small mb-1">★ ${p.rating}</p>
                            <p class="small mb-2"><span class="badge badge-stock ${p.inStock ? 'bg-success' : 'bg-danger'}">${p.inStock ? 'In stock' : 'Out of stock'}</span></p>
                            <p>
                                <strong>₹<fmt:formatNumber value="${p.salePrice}" minFractionDigits="2"/></strong>
                                <c:if test="${p.hasDiscount}"><span class="price-old ms-1">₹<fmt:formatNumber value="${p.price}" minFractionDigits="2"/></span>
                                <span class="badge bg-warning text-dark">${p.discount}% off</span></c:if>
                            </p>
                            <div class="mt-auto d-flex gap-2">
                                <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/product?id=${p.id}">View details</a>
                                <form method="post" action="${pageContext.request.contextPath}/cart">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="${p.id}">
                                    <button class="btn btn-sm btn-primary" ${p.inStock ? '' : 'disabled'}>Add to cart</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
        <nav class="mt-4">
            <ul class="pagination">
                <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
                    <li class="page-item ${pageResult.page == i ? 'active' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/products?q=${q}&category=${selectedCategory}&minPrice=${minPrice}&maxPrice=${maxPrice}&sort=${sort}&page=${i}">${i}</a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
    </div>
</div>
<jsp:include page="footer.jsp"/>
