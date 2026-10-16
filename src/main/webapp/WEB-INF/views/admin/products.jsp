<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Products | ShopEase Admin" scope="request"/>
<jsp:include page="header.jsp"/>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h1 class="h4 mb-0">Products</h1>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/products?action=new">Add product</a>
</div>
<form class="row g-2 mb-3" method="get">
    <div class="col-md-4"><input class="form-control" name="q" value="${q}" placeholder="Search"></div>
    <div class="col-md-3">
        <select class="form-select" name="category">
            <option value="">All categories</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.id}">${cat.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-2"><button class="btn btn-outline-primary">Search</button></div>
</form>
<table class="table table-striped">
    <thead><tr><th>ID</th><th>Name</th><th>Category</th><th>Price</th><th>Discount</th><th>Stock</th><th>Status</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="p" items="${pageResult.items}">
        <tr>
            <td>${p.id}</td>
            <td>${p.name}</td>
            <td>${p.categoryName}</td>
            <td>₹<fmt:formatNumber value="${p.price}" minFractionDigits="2"/></td>
            <td>${p.discount}%</td>
            <td>${p.stock}</td>
            <td>${p.status}</td>
            <td class="d-flex gap-1">
                <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.id}">Edit</a>
                <form method="post">
                    <input type="hidden" name="id" value="${p.id}">
                    <c:choose>
                        <c:when test="${p.status == 'ACTIVE'}">
                            <button class="btn btn-sm btn-outline-danger" name="action" value="deactivate">Deactivate</button>
                        </c:when>
                        <c:otherwise>
                            <button class="btn btn-sm btn-outline-success" name="action" value="activate">Activate</button>
                        </c:otherwise>
                    </c:choose>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<ul class="pagination">
    <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
        <li class="page-item ${pageResult.page == i ? 'active' : ''}"><a class="page-link" href="?q=${q}&page=${i}">${i}</a></li>
    </c:forEach>
</ul>
<jsp:include page="footer.jsp"/>
