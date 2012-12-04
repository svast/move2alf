<script>
function scrollTo(name){
	$('html, body').animate({
                    scrollTop: $('[name="'+name+'"]').offset().top
                     }, 500);

}
</script>

<#list errors! as error>
    <div onclick="scrollTo('${error.field}')" class="clickable alert alert-error">
    	${error.defaultMessage}
    </div>
</#list>