<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="About | ShopEase" scope="request"/>
<jsp:include page="header.jsp"/>
<h1 class="h3">About ShopEase</h1>
<p>ShopEase is a full-stack e-commerce application built with Java Servlets, JSP, JDBC, and MySQL. It demonstrates MVC architecture, session-based authentication, transactional checkout, and an admin panel suitable for a Java developer portfolio.</p>
<p>Customers can search and filter products, manage a cart, place orders, and review purchased items. Administrators can manage catalog data, users, orders, and reviews.</p>
<jsp:include page="footer.jsp"/>
