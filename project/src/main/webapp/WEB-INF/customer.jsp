<%--
    Document   : customer
    Created on : 17 mars 2018, 16:55:42
    Author     : DGX
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <link rel="icon" type="image/png" href="assets/img/favicon.ico">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

        <title>Web Market de ${userName}</title>

        <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport' />
        <meta name="viewport" content="width=device-width" />


        <!-- Bootstrap core CSS     -->
        <link href="assets/css/bootstrap.min.css" rel="stylesheet" />

        <!-- Animation library for notifications   -->
        <link href="assets/css/animate.min.css" rel="stylesheet"/>

        <!--  Light Bootstrap Table core CSS    -->
        <link href="assets/css/light-bootstrap-dashboard.css?v=1.4.0" rel="stylesheet"/>


        <!--  CSS for Demo Purpose, don't include it in your project     -->
        <link href="assets/css/demo.css" rel="stylesheet" />
        
         <!--  CSS personnalisé    -->    
        <link href="assets/css/custom.css" rel="stylesheet" />


        <!--     Fonts and icons     -->
        <link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
        <link href='http://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>
        <link href="assets/css/pe-icon-7-stroke.css" rel="stylesheet" />
    </head>
    <body>

        <div class="wrapper">
            <div class="sidebar" data-color="soso"  data-image="assets/img/sidebar-2.jpg">

                <!--   you can change the color of the sidebar using: data-color="blue | azure | green | orange | red | purple" -->


                <div class="sidebar-wrapper">
                    <div class="logo">
                        <a href="http://www.creative-tim.com" class="simple-text">
                            Bienvenue ${userName}!
                        </a>
                    </div>

                    <ul class="nav">

                        <li class="active">
                            <a href="user.html">
                                <i class="pe-7s-user"></i>
                                <p>Votre Profil</p>
                            </a>
                        </li>

                        <li>
                            <a href="typography.html">
                                <i class="pe-7s-news-paper"></i>
                                <p>Liste des Produits</p>
                            </a>
                        </li>

                        <li>
                            <form class="logout" action="LoginController" method="POST">
                                <input class="form-control " type='submit' name='action' value='logout'>
                            </form>
                        </li>



                    </ul>
                </div>
            </div>

            <div class="main-panel">
                <nav class="navbar navbar-default navbar-fixed">
                    <div class="container-fluid">
                        <div class="navbar-header">
                            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navigation-example-2">
                                <span class="sr-only">Toggle navigation</span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                            </button>
                            <a class="navbar-brand" href="#">L'équipe espère que vous allez trouver votre bonheur ${userName} !</a>
                        </div>
                        <div class="collapse navbar-collapse">
                            <ul class="nav navbar-nav navbar-left">
                                <li>
                                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                        <i class="fa fa-dashboard"></i>
                                        <p class="hidden-lg hidden-md">Dashboard</p>
                                    </a>
                                </li>



                            </ul>

                        </div>
                    </div>

                </nav>


                <div class="content">
                    <div class="container-fluid">
                        <div class="row">
                            <div class="col-md-8">
                                <div class="card">
                                    <div class="header">
                                        <h4 class="title">Votre compte</h4>
                                    </div>
                                    <div class="content">
                                        
                                            <div class="row">
                                                <div class="col-md-5">
                                                    <div class="form-group">
                                                        <label>Solde de votre compte : </label>
                                                        <input type="text" class="form-control" disabled placeholder="Company" value="${solde}">
                                                    </div>
                                                </div>
                                                <form method='POST' action="customerController">
                                                    <div class="col-md-3">
                                                        <div class="form-group">
                                                            <label>Somme à verser</label>
                                                             <input type="text" class="form-control" placeholder="montant" name="montant">
                                                             <input type="hidden" name="action" value="DO_VIREMENT">
                                                        </div>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <div class="form-group">
                                                           
                                                            <button type="submit" class="btn btn-info btn-fill pull-right">Effectuer le virement</button>
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>
                                            
                                       
                                    </div>
                                </div>
                                <div class="card">
                                    <div class="header">
                                        <h4 class="title">Effectuer une commande : </h4>
                                    </div>
                                    <div class="content">
                                        <form method='POST' action="customerController">
                                            <div class="row">
                                                <div class="col-md-5">
                                                    <div class="form-group">
                                                        <label>Produit</label>
                                                        <br>
                                                        <select name="produit" class="select-custom selectpicker">
                                                            <c:forEach var="item" items="${listeProduits}">
                                                                <option value="${item}">${item}</option>
                                                            </c:forEach>
                                                        </select>
                                                    </div>
                                                </div>
                                                <div class="col-md-3">
                                                    <div class="form-group">
                                                        <label>Quantité</label>
                                                        <input type="text" class="form-control" placeholder="Quantite" value="" name="quantite">
                                                        <input type="hidden" name="action" value="ADD_COMMANDE">
                                                    </div>
                                                </div>
                                                <div class="col-md-4">
                                                    <div class="form-group">
                                                        <button type="submit" class="btn btn-info btn-fill pull-right">Ajouter</button>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="clearfix"></div>
                                        </form>
                                    </div>
                                </div>
                                <div><h4>${message}</h4></div>
                            </div>

                            <div class="col-md-4">
                                <div class="card card-user">
                                    <div class="image">
                                        <img src="https://ununsplash.imgix.net/photo-1431578500526-4d9613015464?fit=crop&fm=jpg&h=300&q=75&w=400" alt="..."/>
                                    </div>
                                    <div class="content">
                                        <div class="author">
                                            <a href="#">
                                                <img class="avatar border-gray" src="assets/img/faces/Remi.jpg" alt="..."/>
                                                    
                                                <h4 class="title">${userName}<br />
                                                    
                                                    <small>${userEmail}</small>
                                                </h4>
                                            </a>
                                        </div>

                                    </div>
                                    <hr>
                                    <div class="text-center">
                                        <button href="#" class="btn btn-simple"><i class="fa fa-facebook-square"></i></button>
                                        <button href="#" class="btn btn-simple"><i class="fa fa-twitter"></i></button>
                                        <button href="#" class="btn btn-simple"><i class="fa fa-google-plus-square"></i></button>

                                    </div>
                                </div>
                            </div>

                        </div>
                        <div class="row">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="card" >
                                        <div class="header">
                                            <h4 class="title">Striped Table with Hover</h4>
                                            <p class="category">Here is a subtitle for this table</p>
                                        </div>
                                        <div class="content table-responsive table-full-width">
                                            <table class="table table-hover table-striped">
                                                <thead>
                                                <th>ID</th>
                                                <th>Name</th>
                                                <th>Salary</th>
                                                <th>Country</th>
                                                <th>City</th>
                                                <th>Salary</th>
                                                <th>Country</th>
                                                <th>City</th>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="comm" items="${commandes}">
                                                        <tr>
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
                                                            ${comm.COST}
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
                                                </tbody>
                                            </table>

                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>


                <footer class="footer">
                    <div class="container-fluid">
                        <nav class="pull-left">
                            <ul>
                                <li>
                                    <a href="#">
                                        Home
                                    </a>
                                </li>
                                <li>
                                    <a href="#">
                                        Company
                                    </a>
                                </li>
                                <li>
                                    <a href="#">
                                        Portfolio
                                    </a>
                                </li>
                                <li>
                                    <a href="#">
                                        Blog
                                    </a>
                                </li>
                            </ul>
                        </nav>
                        <p class="copyright pull-right">
                            &copy; <script>document.write(new Date().getFullYear())</script> <a href="http://www.creative-tim.com">Creative Tim</a>, made with love for a better web
                        </p>
                    </div>
                </footer>

            </div>
        </div>


    </body>

    <!--   Core JS Files   -->
    <script src="assets/js/jquery.3.2.1.min.js" type="text/javascript"></script>
    <script src="assets/js/bootstrap.min.js" type="text/javascript"></script>

    <!--  Charts Plugin -->
    <script src="assets/js/chartist.min.js"></script>

    <!--  Notifications Plugin    -->
    <script src="assets/js/bootstrap-notify.js"></script>

    <!--  Google Maps Plugin    -->
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=YOUR_KEY_HERE"></script>

    <!-- Light Bootstrap Table Core javascript and methods for Demo purpose -->
    <script src="assets/js/light-bootstrap-dashboard.js?v=1.4.0"></script>

    <!-- Light Bootstrap Table DEMO methods, don't include it in your project! -->
    <script src="assets/js/demo.js"></script>
    
    <script type="text/javascript">
    	$(document).ready(function(){

                    demo.initChartist();

                    $.notify({
                    icon: 'pe-7s-gift',
                    message: "Bienvenue sur votre super Web Market ${userName}! Réalisé par Soso, Gabi et Anthony"

                },{
                    type: 'info',
                    timer: 4000
                });

            });
        </script>

</html>
