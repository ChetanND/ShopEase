<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty pageTitle ? 'ShopEase' : pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark" style="background:#0f2744;">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/home">ShopEase</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/products">Products</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/categories">Categories</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/about">About</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/contact">Contact</a></li>
            </ul>
            <form class="d-flex me-3" action="${pageContext.request.contextPath}/products" method="get">
                <input class="form-control me-2" type="search" name="q" placeholder="Search products" value="${param.q}">
                <button class="btn btn-warning" type="submit">Search</button>
            </form>
            <a class="btn btn-outline-light me-2 position-relative" href="${pageContext.request.contextPath}/cart">
                <i class="bi bi-bag"></i>
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">${cartCount}</span>
            </a>
            <c:choose>
                <c:when test="${not empty sessionScope.authUser}">
                    <div class="dropdown">
                        <button class="btn btn-light dropdown-toggle" data-bs-toggle="dropdown">${sessionScope.authUser.fullName}</button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <c:if test="${sessionScope.authUser.admin}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">Admin Panel</a></li>
                            </c:if>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">My Profile</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders">My Orders</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-warning me-2" href="${pageContext.request.contextPath}/login">Login</a>
                    <a class="btn btn-warning" href="${pageContext.request.contextPath}/register">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>
<main class="container py-4">
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>
