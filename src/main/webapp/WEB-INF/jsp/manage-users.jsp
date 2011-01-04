<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<h2>Manage users</h2>

<p>List of users, edit user, add user, delete user...</p>

<table>
  <thead>
    <th>Username</th>
    <th>Roles</th>
  </thead>
  <c:forEach var="user" items="${users}">
    <tr>
      <td><c:out value="${user.userName}" /></td>
      <td>
        <c:forEach var="role" items="${user.userRoleSet}">
          <c:out value="${role.role}" /><br />
        </c:forEach>
      </td>
    </tr>
  </c:forEach>
</table>


<%@ include file="/WEB-INF/jsp/footer.jsp" %>
