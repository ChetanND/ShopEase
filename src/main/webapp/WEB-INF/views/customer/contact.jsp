<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Contact | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<div class="row">
    <div class="col-lg-7">
        <h1 class="h3">Contact us</h1>
        <form method="post" action="${pageContext.request.contextPath}/contact" class="card card-body">
            <label class="form-label">Name</label>
            <input class="form-control mb-2" name="name" required>
            <label class="form-label">Email</label>
            <input class="form-control mb-2" type="email" name="email" required>
            <label class="form-label">Subject</label>
            <input class="form-control mb-2" name="subject" required>
            <label class="form-label">Message</label>
            <textarea class="form-control mb-3" name="message" rows="5" required></textarea>
            <button class="btn btn-primary">Send message</button>
        </form>
    </div>
</div>
<jsp:include page="footer.jsp"/>
