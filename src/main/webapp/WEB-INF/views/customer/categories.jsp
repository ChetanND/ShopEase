<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Categories | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<nav aria-label="breadcrumb"><ol class="breadcrumb"><li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Home</a></li><li class="breadcrumb-item active">Categories</li></ol></nav>
<h1 class="h3 mb-4">Shop by category</h1>
<div class="row g-3">
    <c:forEach var="cat" items="${categories}">
        <div class="col-md-4">
            <div class="card category-card h-100">
                <img src="${cat.imageUrl}" alt="${cat.name}">
                <div class="card-body">
                    <h2 class="h5">${cat.name}</h2>
                    <p>${cat.description}</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/products?category=${cat.id}">Browse ${cat.productCount} products</a>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<jsp:include page="footer.jsp"/>
