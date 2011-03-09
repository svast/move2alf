<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Change <c:out value="${user.userName}" />'s role</h2>

<div class="frame-job">

<form:form modelAttribute="userClass" method="post" name="changeRole" >
<table class="indent">
<tr>
<td>Please enter your password:</td>
<td><form:password path="oldPassword" size="15" maxlength="15" /></td>
<td><form:errors path="oldPassword" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "oldPassword",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "password cannot be empty",
                                               	promptMessage : "password cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>

<tr>
<td>Role: </td>
<td><form:select path="role">
    		<form:options items="${roleList}" />
		</form:select></td>
								<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "role",
                                        widgetType : "dijit.form.FilteringSelect",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "this value is not allowed in this field",
                                               	promptMessage : "role cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
</table>

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/users" htmlEscape="true" />';">Cancel</button>
<input id="proceed" type="submit" value="Update role" class="right" />
						<script type="text/javascript">
                            Spring.addDecoration(new Spring.ValidateAllDecoration({
                                    elementId: "proceed",
                                    event: "onclick" }));
                        </script>
</form:form>
</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
