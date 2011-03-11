<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Change password</h2>

<div class="frame-job">

<form:form modelAttribute="userClass" method="post" name="changePassword" onSubmit='return editPasswordValidation(this);' >
<table class="indent">
<tr>
<td>old password:</td>
<td><form:password path="oldPassword" size="15" maxlength="15" /></td>
<td id="oldPasswordError" class="hide error">password may not be empty.</td>
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
<td>new password:</td>
<td><form:password path="newPassword" size="15" maxlength="15" /></td>
<td id="newPasswordError" class="hide error">password may not be empty.</td>
<td><form:errors path="newPassword" cssClass="error"/></td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "newPassword",
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
<td>Please retype the new password:</td>
<td><form:password path="newPasswordRetype" size="15" maxlength="15" /></td>
<td id="newPasswordRetypeError" class="hide error">password may not be empty.</td>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "newPasswordRetype",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "password cannot be empty",
                                               	promptMessage : "password cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
</tr>
</table>

<button type="button" class="left" onclick="javascript:location.href ='<spring:url value="/user/profile" htmlEscape="true" />';">Cancel</button>

<input id="proceed" type="submit" value="Update password" class="right" />
	<!-- 					<script type="text/javascript">
                            Spring.addDecoration(new Spring.ValidateAllDecoration({
                                    elementId: "proceed",
                                    event: "onclick" }));
                        </script>
                        -->
</form:form>
</div>

</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
