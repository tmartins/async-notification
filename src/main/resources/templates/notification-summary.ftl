${frequency} summary for your Subscriptions

<#if notifications?size &gt; 0 >
<ul>
<#list notifications as notif>
<li>${notif['date']}: ${notif['action']} on document "${notif['documentTitle']}" by ${notif['actor']}</li>
</#list>
</ul>
</#if>

Nuxeo robot !