<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Profile | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<h1 class="h3 mb-4">My profile</h1>
<div class="row g-4">
    <div class="col-lg-7">
        <form class="card card-body" method="post" action="${pageContext.request.contextPath}/profile">
            <h2 class="h6">Account &amp; address</h2>
            <label class="form-label">Full name</label>
            <input class="form-control mb-2" name="fullName" value="${user.fullName}" required>
            <label class="form-label">Email</label>
            <input class="form-control mb-2" value="${user.email}" disabled>
            <label class="form-label">Phone</label>
            <input class="form-control mb-2" name="phone" value="${user.phone}" required>
            <label class="form-label">Address</label>
            <input class="form-control mb-2" name="address" value="${user.address.addressLine}" required>
            <div class="row">
                <div class="col-md-4"><label class="form-label">City</label><input class="form-control mb-2" name="city" value="${user.address.city}" required></div>
                <div class="col-md-4"><label class="form-label">State</label><input class="form-control mb-2" name="state" value="${user.address.state}" required></div>
                <div class="col-md-4"><label class="form-label">Pincode</label><input class="form-control mb-2" name="pincode" value="${user.address.pincode}" required></div>
            </div>
            <button class="btn btn-primary">Save profile</button>
        </form>
    </div>
    <div class="col-lg-5">
        <form class="card card-body" method="post" action="${pageContext.request.contextPath}/profile">
            <input type="hidden" name="action" value="password">
            <h2 class="h6">Change password</h2>
            <label class="form-label">Current password</label>
            <input class="form-control mb-2" type="password" name="currentPassword" required>
            <label class="form-label">New password</label>
            <input class="form-control mb-2" type="password" name="newPassword" required minlength="6">
            <label class="form-label">Confirm new password</label>
            <input class="form-control mb-3" type="password" name="confirmPassword" required>
            <button class="btn btn-outline-primary">Update password</button>
        </form>
        <div class="card card-body mt-3">
            <h2 class="h6">Saved address</h2>
            <p class="mb-0">${user.address.addressLine}<br>${user.address.city}, ${user.address.state} ${user.address.pincode}</p>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
