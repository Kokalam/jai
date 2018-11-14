<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Lens Critique en JSP</title>
</head>
<body>
	<c:url var="connecturl" value="/login.jsp" scope="page"/>
	<c:choose>
		<c:when test="${empty sessionScope.author}">
			<a href="${connecturl}">s'identifier</a>
		</c:when>
		<c:otherwise>
			<a href="${connecturl}">${sessionScope.author.pseudo}</a>
			<a href="<c:url value="/logout"/>">déconnecter</a>
		</c:otherwise>
	</c:choose>
	<h1>Vérifions les tags</h1>
	<ul>
		<c:forEach var="entry" items="${tags}">
			<li>${entry.value.label}</li>
		</c:forEach>
	</ul>
	<h1>Vérifions les auteurs</h1>
	<ul>
		<c:forEach var="entry" items="${authors}">
			<li>${entry.value.pseudo}</li>
		</c:forEach>
	</ul>
	<h1>Vérifions articles</h1>
	<p><a href="<c:url value="/article.jsp"/>">Ajouter article</a></p>
	<ul>
		<c:forEach var="entry" items="${articles}">
			<li>${entry.value.title}</li>
		</c:forEach>
	</ul>
</body>
</html>