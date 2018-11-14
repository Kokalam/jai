<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="article" class="ili.jai.lenscritique.data.Article" scope="page">
	<jsp:setProperty name="article" property="*"/>
</jsp:useBean>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>Création d'un article</h1>
	<form action="createarticle" method="post">
		<input type="text" name="title" size="80" value="${article.title}"/>
		<textarea rows="20" cols="80" name="content">${article.content}</textarea>
		<select name="tags" multiple="multiple">
			<c:forEach var="entry" items="${tags}">
				<option type="checkbox" name="tags" value="${entry.key}">${entry.value.label}</option>
			</c:forEach>
		</select>
		<input type="submit"/>
	</form>
</body>
</html>