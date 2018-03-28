<%-- 
    Document   : admin
    Created on : 17 mars 2018, 16:55:30
    Author     : DGX
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="resources/css/custom.css">
        <link rel="stylesheet" type="text/css" href="resources/css/materialize.css">
        <link rel="stylesheet" type="text/css" href="resources/css/materialize.min.css">
               

    </head>
    <body>
        <h1>Hello Admin!</h1>
        
        <form action="LoginController" method="POST"> 
            <input type='submit' name='action' value='logout'>
        </form>
        <form action="" method="POST"> 
            <input type='submit' name='action' value='nom'>
        </form>
        
         <input type="date" class="choixdate" name="date_debut">
         <input type="date" class="choixdate" name="date_fin">
          <script type="text/javascript" src="resources/js/materialize.min.js"></script>  
   

    </body>
</html>
