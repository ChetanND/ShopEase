<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Dashboard | ShopEase Admin" scope="request"/>
<jsp:include page="header.jsp"/>
<h1 class="h4 mb-4">Dashboard</h1>
<div class="row g-3 mb-4">
    <div class="col-md-4 col-xl-2"><div class="card stat-card p-3"><div class="small text-muted">Products</div><div class="fs-4">${stats.totalProducts}</div></div></div>
    <div class="col-md-4 col-xl-2"><div class="card stat-card p-3"><div class="small text-muted">Customers</div><div class="fs-4">${stats.totalCustomers}</div></div></div>
    <div class="col-md-4 col-xl-2"><div class="card stat-card p-3"><div class="small text-muted">Orders</div><div class="fs-4">${stats.totalOrders}</div></div></div>
    <div class="col-md-4 col-xl-2"><div class="card stat-card p-3"><div class="small text-muted">Revenue</div><div class="fs-5">₹<fmt:formatNumber value="${stats.totalRevenue}" minFractionDigits="2"/></div></div></div>
    <div class="col-md-4 col-xl-2"><div class="card stat-card p-3"><div class="small text-muted">Pending</div><div class="fs-4">${stats.pendingOrders}</div></div></div>
    <div class="col-md-4 col-xl-2"><div class="card stat-card p-3"><div class="small text-muted">Low stock</div><div class="fs-4">${stats.lowStockProducts}</div></div></div>
</div>
<div class="row g-4">
    <div class="col-lg-6">
        <div class="card p-3"><canvas id="orderChart" height="180"></canvas></div>
    </div>
    <div class="col-lg-6">
        <div class="card p-3">
            <h2 class="h6">Low-stock products</h2>
            <table class="table table-sm">
                <c:forEach var="p" items="${stats.lowStockList}">
                    <tr><td>${p.name}</td><td>${p.stock}</td></tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>
<script>
document.addEventListener("DOMContentLoaded", function () {
  const labels = [<c:forEach var="l" items="${stats.chartLabels}" varStatus="s">"${l}"<c:if test="${!s.last}">,</c:if></c:forEach>];
  const values = [<c:forEach var="v" items="${stats.chartValues}" varStatus="s">${v}<c:if test="${!s.last}">,</c:if></c:forEach>];
  const ctx = document.getElementById("orderChart");
  if (ctx && window.Chart) {
    new Chart(ctx, { type: "bar", data: { labels: labels, datasets: [{ label: "Orders by status", data: values, backgroundColor: "#1a7a6d" }] } });
  }
});
</script>
<jsp:include page="footer.jsp"/>
