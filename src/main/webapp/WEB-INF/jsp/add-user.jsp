<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<div class="span-24 last main">

<h2>Add User</h2>

<form:form modelAttribute="user" method="post">
<p>Username: <form:input path="userName" size="30" maxlength="30" />
<form:errors path="userName" cssClass="error"/></p>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "userName",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "username cannot be empty",
                                               	promptMessage : "username cannot be empty"
                                                   	
                                        }
                                }));
                        </script>

<p>Password: <form:password path="password" size="30" maxlength="50" />
<form:errors path="password" cssClass="error"/></p>
						<script type="text/javascript">
                                Spring.addDecoration(new Spring.ElementDecoration({
                                        elementId : "password",
                                        widgetType : "dijit.form.ValidationTextBox",
                                        widgetAttrs : {
                                                required : true,
                                               	invalidMessage : "password cannot be empty",
                                               	promptMessage : "password cannot be empty"
                                                   	
                                        }
                                }));
                        </script>
<p>Role: <form:select path="role">
    		<form:options items="${roleList}" />
		</form:select></p>
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
<p><input id="proceed" type="submit" value="Add User"/></p>
						<script type="text/javascript">
                            Spring.addDecoration(new Spring.ValidateAllDecoration({
                                    elementId: "proceed",
                                    event: "onclick" }));
                        </script>
</form:form>
	
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
