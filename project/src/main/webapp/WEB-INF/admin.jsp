<%-- 
    Document   : admin
    Created on : 17 mars 2018, 16:55:30
    Author     : DGX
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page Test</title>
    </head>
    <body>
        <h1>Hello Admin!</h1>
        
        <form action="LoginController" method="POST"> 
            <input type='submit' name='action' value='logout'>
            
        </form>
        <form action="" method="POST"> 
            <input type='submit' name='action' value='nom'>
        </form>
    </body>
</html>
