<%-- 
    Document   : customer
    Created on : 17 mars 2018, 16:55:42
    Author     : DGX
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Web-Market de ${userName}</title>
        <link rel="stylesheet" type="text/css" href="resources/css/custom.css">
        <link rel="stylesheet" type="text/css" href="resources/css/materialize.css">
        <link rel="stylesheet" type="text/css" href="resources/css/materialize.min.css">

    </head>
    <body>
        
    <div class="row">
    <div class="col s2 navig z-depth-2">
      <ul class="fixed ">
        <li>
          <div class="user-view">

            <a><img class="circle" src=""></a>


          </div>
        </li>
        <div class="divider"></div>
        <li class="infos">Nom : ${userName}</li>
       
        </li>
        <li class="infos">Mail : ${userEmail}</li>
        <li class="infos">Adresse</li>
        <li>${ListeCodes}</li>
        <li class="infos">
            <form action="LoginController" method="POST"> 
                <input type='submit' name='action' value='logout'>
            </form>
        </li>
      </ul>
    </div>

    <div class="col s10">

      <div class="row">
        <div class="col s12">
          <div class="card blue-grey darken-1">
            <div class="card-content white-text">
              <span class="card-title">Bienvenue ${userName} !</span>
              <p>Nous ésperons que vous trouverez votre bonheur !</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col s12 m6">
          <div class="card blue-grey darken-1">
            <div class="card-content white-text">
              <span class="card-title">Card Title</span>
              <p>I am a very simple card. I am good at containing small bits of information. I am convenient because I require little markup to use effectively.</p>
            </div>
              
             <form method='GET' action="customerController">
                    Code : <input name="code" size="1" maxlength="1" pattern="[A-Z]{1}+" title="Une lettre en MAJUSCULES"><br/>
		    Taux : <input name="taux" type="number" step="0.01" min="0.0" max="99.99" size="5"><br/>
			<input type="hidden" name="action" value="ADD">
			<input type="submit" value="Ajouter">
            </form>
              
              <%--  On montre un éventuel message d'erreur --%>
		<div><h4>${message}</h4></div>
		<%-- On on montre la liste des discount codes --%>
              
            <div>
                <table border="1">
                    <tr><th>Code</th><th>Taux</th><th>Action</th></tr>
                    <c:forEach var="record" items="${codes}">
                        <tr>
                            <td>${record.discountCode}</td>
                            <td><fmt:formatNumber value="${record.rate  / 100}"  type="percent" minFractionDigits="2" minIntegerDigits="2" maxFractionDigits="2" maxIntegerDigits="2"/></td>
                            <td><a href="?action=DELETE&code=${record.discountCode}">delete</a></td>
                        </tr>	  		    
                    </c:forEach>  
                </table>
            </div>

            <div>               
                    <table border="1">
                        <tr><th>Numéro du client</th><th>Numéro de Commande</th><th>Qunatité</th></tr>
                        <c:forEach var="comm" items="${commandes}">
                            <tr>
                                <td>${comm.CUSTOMER_ID}</td>
                                <td>${comm.ORDER_NUM}</td>
                                <td>${comm.QUANTITY}</td>
                                <td><a href="?action=DELETE_COMMANDE&purchaseToDelete=${comm.ORDER_NUM}">delete</a></td>
                                <td><a href="?action=EDIT_COMMANDE&purchaseToEdit=${comm.ORDER_NUM}">Edit</a></td>
                                
                                
                            </tr>	  		    
                        </c:forEach>  
                    </table>
                </form
          </div>
                <div><h4>${message2}</h4></div>
                
                
                
            <form method='GET' action="customerController">
               Num : <input name="num" size="1" maxlength="100" pattern="{1}+" title="Une lettre en MAJUSCULES"><br/> 
               Quantité : <input name="quantite" size="1" maxlength="1000" pattern="[A-Z]{1}+" title="Une lettre en MAJUSCULES"><br/>               
                <input type="hidden" name="action" value="ADD_COMMANDE">
                <input type="submit" value="Ajouter">
            </form>
              
              
          </div>
        </div>
      </div>

    </div>

    </body>
</html>
