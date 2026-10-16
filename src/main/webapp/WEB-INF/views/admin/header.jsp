<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty pageTitle ? 'Admin | ShopEase' : pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet">
</head>
<body>
<div class="d-flex min-vh-100">
    <aside class="admin-sidebar p-3" style="width:240px;background:#0f2744;">
        <a class="navbar-brand text-white d-block mb-4" href="${pageContext.request.contextPath}/admin/dashboard">ShopEase Admin</a>
        <a class="${pageContext.request.servletPath == '/admin/dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/admin/products">Products</a>
        <a href="${pageContext.request.contextPath}/admin/categories">Categories</a>
        <a href="${pageContext.request.contextPath}/admin/users">Users</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/admin/reviews">Reviews</a>
        <a href="${pageContext.request.contextPath}/home">Storefront</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </aside>
    <div class="flex-grow-1 bg-light">
        <div class="p-4">
            <c:if test="${not empty successMessage}"><div class="alert alert-success">${successMessage}</div></c:if>
            <c:if test="${not empty errorMessage}"><div class="alert alert-danger">${errorMessage}</div></c:if>
