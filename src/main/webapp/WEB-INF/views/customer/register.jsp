<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Register | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm"><div class="card-body p-4">
            <h1 class="h4 mb-3">Create your ShopEase account</h1>
            <form method="post" action="${pageContext.request.contextPath}/register">
                <div class="row g-3">
                    <div class="col-md-6"><label class="form-label">Full name</label><input class="form-control" name="fullName" value="${formUser.fullName}" required></div>
                    <div class="col-md-6"><label class="form-label">Email</label><input class="form-control" type="email" name="email" value="${formUser.email}" required></div>
                    <div class="col-md-6"><label class="form-label">Phone</label><input class="form-control" name="phone" value="${formUser.phone}" required></div>
                    <div class="col-md-6"><label class="form-label">Password</label><input class="form-control" type="password" name="password" required minlength="6"></div>
                    <div class="col-md-6"><label class="form-label">Confirm password</label><input class="form-control" type="password" name="confirmPassword" required></div>
                    <div class="col-12"><label class="form-label">Address</label><input class="form-control" name="address" value="${formAddress.addressLine}" required></div>
                    <div class="col-md-4"><label class="form-label">City</label><input class="form-control" name="city" value="${formAddress.city}" required></div>
                    <div class="col-md-4"><label class="form-label">State</label><input class="form-control" name="state" value="${formAddress.state}" required></div>
                    <div class="col-md-4"><label class="form-label">Pincode</label><input class="form-control" name="pincode" value="${formAddress.pincode}" required></div>
                </div>
                <button class="btn btn-primary mt-3">Register</button>
            </form>
        </div></div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
