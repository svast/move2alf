<@labeledSingleLineTextInput label="Username" name="userName" binding="user.userName" />
<@labeledSingleLinePasswordInput label="Password" name="password" binding="user.password" />
<@labeledSelectList label="Role" name="role" options=roleList; item>
		<option value="${item}" <#if (role?? && role==item)>selected="selected"</#if>> ${item}</option>
</@labeledSelectList>
<input type="submit" value="submit" />
