<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Demo Payment | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card shadow-sm"><div class="card-body p-4 text-center">
            <h1 class="h4">Demo online payment</h1>
            <p>This is a simulated payment page. No real card is charged.</p>
            <p class="fs-5">Amount payable: ₹<fmt:formatNumber value="${cart.grandTotal}" minFractionDigits="2"/></p>
            <form method="post" action="${pageContext.request.contextPath}/payment">
                <button class="btn btn-success">Simulate successful payment</button>
            </form>
            <a class="btn btn-link" href="${pageContext.request.contextPath}/checkout">Back to checkout</a>
        </div></div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
