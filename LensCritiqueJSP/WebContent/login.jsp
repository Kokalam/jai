<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>
</head>
<body>
	<form action="<c:url value="/logas"/>">
		<select name="author">
			<c:forEach var="entry" items="${authors}">
				<option value="${entry.key}">${entry.value.pseudo}</option>
			</c:forEach>
		</select>
		<input type="submit"/>
	</form>
</body>
</html>