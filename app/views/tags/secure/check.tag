#{if controllers.security.Secure.check(_arg) && !controllers.security.Secure.check(_not) }
#{doBody /}
#{/if}