<!DOCTYPE html>

<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>



<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Web Market - Aussel Dagneaux Peltier </title>
                 <script type="text/javascript" src="resources/js/EssaiJS.js"></script>
                <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
                <script type="text/javascript" src="resources/js/materialize.min.js"></script>
                <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
                <link href="https://fonts.googleapis.com/css?family=Pacifico" rel="stylesheet"/>
                <link rel="stylesheet" type="text/css" href="resources/css/custome.css">
                <link rel="stylesheet" type="text/css" href="resources/css/materialize.css">
                <link rel="stylesheet" type="text/css" href="resources/css/materialize.min.css">
               

	</head>
	<body>
		

		

		<section class="connexion container  z-depth-4">

                    <div class="row Bonjour">
                      <div class="col s12">
                        <div class="" id="j1">
                          <div class="texte" id="j2">Bonjour</div>
                        </div>
                      </div>
                    </div>
                    <form class="col s12 login-form" action="<c:url value="LoginController" />" method="POST">
                      <div class="row center-align">
                        
                          <img src="resources/assets/guarantee(2).png">
                      </div>



                      <div class="row">
                        <div class="input-field offset-s1 col s10 log-style">
                          <i class="material-icons prefix">account_circle</i>
                          <input name='loginParam' id="email" class="validate log-style" type="email">
                          <label for="email">Email</label>
                        </div>
                      </div>

                      <div class="row">
                        <div class="input-field offset-s1 col s10">
                            <i class="material-icons prefix">lock</i>
                            <input name='passwordParam' class="validate" id="password" type="password">
                          <label for="password">Password</label>
                        </div>
                      </div>

                      <div class="row">
                        <div class=" center-align">
                          <button class="btn btn-connexion waves-effect waves-light" type="submit" name="action" value="login">
                              Connexion
                          </button>
                        </div>
                      </div>

                    </form>
                      <div class="center-align error">${errorMessage}</div>
                      
                      <div class="row Bonjour">
                      <div class="col s12">
                        <div class="" >
                           &nbsp
                        </div>
                      </div>
                    </div>

                  </section>
	
                
                
               
               
	</body>
</html>
