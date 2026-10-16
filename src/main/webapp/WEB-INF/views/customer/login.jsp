<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Login | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<div class="row justify-content-center">
    <div class="col-md-5">
        <div class="card shadow-sm"><div class="card-body p-4">
            <h1 class="h4 mb-3">Login</h1>
            <form method="post" action="${pageContext.request.contextPath}/login">
                <label class="form-label">Email</label>
                <input class="form-control mb-2" type="email" name="email" required>
                <label class="form-label">Password</label>
                <input class="form-control mb-3" type="password" name="password" required>
                <button class="btn btn-primary w-100">Login</button>
            </form>
            <p class="mt-3 mb-0 small">New here? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
        </div></div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
