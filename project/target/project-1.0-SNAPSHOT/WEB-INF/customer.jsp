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
                            <div>               
                                <table border="1">
                                    <tr><th>Numéro du client</th><th>Numéro de Commande</th><th>Quantité</th><th>Prix</th><th>Description</th><th>Date</th></tr>
                                            <c:forEach var="comm" items="${commandes}">
                                        <tr class="input-field">
                                        <form method='POST' action="customerController">
                                            <td >
                                                ${comm.CUSTOMER_ID}                       
                                            </td>
                                            <td>
                                                <input hidden name="purchaseToEdit" id="${comm.ORDER_NUM}" type="text" class="validate" value="${comm.ORDER_NUM}">
                                                <p name="purchaseToEdit" value="${comm.ORDER_NUM}">${comm.ORDER_NUM}</p>
                                            </td>
                                            <td >
                                                <input name="quantityToEdit" id="${comm.QUANTITY}" type="text" class="validate" value ="${comm.QUANTITY}">                                   
                                                <input type="hidden" name="action" value="EDIT_COMMANDE">
                                            </td>
                                            <td >
                                                ${comm.SHIPPING_COST}                                                                                  
                                            </td>
                                            <td >
                                                ${comm.DESCRIPTION}                                                                                  
                                            </td>
                                            <td >
                                                ${comm.SHIPPING_DATE}                                                                                  
                                            </td>
                                            <td>
                                                <a href="customerController?action=DELETE_COMMANDE&purchaseToDelete=${comm.ORDER_NUM}">Delete</a>
                                            </td>
                                            <td>
                                                <input type="submit" value="Edit"> 
                                            </td>
                                        </form>                               
                                        </tr>	  		    
                                    </c:forEach>  
                                </table>
                                </form
                            </div>
                            <div><h4>${message}</h4></div>

                            <form method='POST' action="customerController">
                                
                                <select class="listeProduits" name="produit">
                                    <c:forEach var="item" items="${listeProduits}">
                                        <option value="${item}">${item}</option>
                                    </c:forEach>
                                </select>
                                
                                
                                Quantité : <input name="quantite" size="1" maxlength="1000" pattern="[A-Z]{1}+" title="Une lettre en MAJUSCULES"><br/>               
                                <input type="hidden" name="action" value="ADD_COMMANDE">
                                <input type="submit" value="Ajouter">
                            </form>


                        </div>
                    </div>
                </div>

            </div>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0-beta/js/materialize.min.js"></script>
    </body>
</html>
