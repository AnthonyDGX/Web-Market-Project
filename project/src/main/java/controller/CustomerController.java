/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Customer;
import model.DAO;
import model.DataSourceFactory;
import model.DiscountCode;
import model.Product;
import model.PurchaseOrder;

/**
 *
 * @author DGX
 */

@WebServlet(name = "customerController", urlPatterns = {"/customerController"})
public class CustomerController extends HttpServlet{
     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, SQLException {
		// Quelle action a appelé cette servlet ?
                HttpSession session = request.getSession();
                DAO dao = new DAO();
		String action = request.getParameter("action");
		action = (action == null) ? "" : action; // Pour le switch qui n'aime pas les null
                //Pour ajouter des commandes
               // String purchaseToCreate =  request.getParameter("purchaseToCreate");
                String quantite = request.getParameter("quantite");
                ArrayList<String> des = dao.allProduct();
                request.setAttribute("listeProduits", des);
               
                // Pour supprimer des commandes
                String purchaseToDelete = request.getParameter("purchaseToDelete");
                String password = ((String)session.getAttribute("userPassword"));
                //Pour éditer des commandes
                String purchaseToEdit = request.getParameter("purchaseToEdit");
                
                // infos client
                Double solde = dao.soldeClient(Integer.parseInt(password));
                session.setAttribute("solde", solde);
                
                
               
                request.setAttribute("codes", viewCodes(request));
		try {
			
                        Customer c = new Customer();
                        c.setPassword(password);
                        
			session.setAttribute("codes", viewCodes(request));		
			switch (action) {
				
                                        
                                case "ADD_COMMANDE": // Requête d'ajout (vient du formulaire de saisie)
                                    dao.addCommande(Integer.parseInt(password), Integer.parseInt(quantite), dao.numProduct(request.getParameter("produit")));
                                    session.setAttribute("commandes", dao.customerCommandes(c));
                                    solde = dao.soldeClient(Integer.parseInt(password));
                                    session.setAttribute("solde", solde);
                                    request.setAttribute("message", "Commande de "+quantite+" '"+request.getParameter("produit")+"'"+" réalisée.");
                                    request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);
                                    break;
                                
                                case "DELETE_COMMANDE":
                                    try {
                                            dao.deleteCommande(Integer.parseInt(purchaseToDelete));
                                            request.setAttribute("message", "Commande " + purchaseToDelete + " Supprimée");
                                            session.setAttribute("commandes", dao.customerCommandes(c));
                                            request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);
														
					} catch (SQLIntegrityConstraintViolationException e) {
						request.setAttribute("message2", "Impossible de supprimer " +  purchaseToDelete + ", cette commande est utilisée.");
					}
                                    break;
                                    
                                    case "EDIT_COMMANDE":
                                    try {
                                            String quantityToEdit = request.getParameter("quantityToEdit");
                                            dao.editCommande(Integer.parseInt(purchaseToEdit), Integer.parseInt(quantityToEdit), Integer.parseInt(password) );
                                            request.setAttribute("message", "Commande " + purchaseToEdit + " modifiée");
                                            session.setAttribute("commandes", dao.customerCommandes(c));
                                            request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);
														
					} catch (SQLIntegrityConstraintViolationException e) {
						request.setAttribute("message", "Impossible de modifier " +  purchaseToEdit + ", cette commande est utilisée.");
					}
                                    break;
                                    
                                    case "DO_VIREMENT":
                                        try {
                                            int montant = Integer.parseInt(request.getParameter("montant"));
                                            dao.virement(Integer.parseInt(password), montant);
                                            solde = dao.soldeClient(Integer.parseInt(password));
                                            session.setAttribute("solde", solde);
                                            request.setAttribute("message", "Virement de : "+ montant +"$ réalisé sur votre compte.");
                                            request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);
                                        }
                                        catch (SQLIntegrityConstraintViolationException e) {
						
					}
                                        break;
                                        
                                         case "SHOW_PRODUIT":
                                             ArrayList<Product> listeProduit = dao.listProduct();
                                             session.setAttribute("listeProduit", listeProduit);
                                            request.getRequestDispatcher("WEB-INF/produits.jsp").forward(request, response);                                        
                                        break;
                                        
                                         case "SHOW_CLIENT":          
                                            request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);                                       
                                        break;
                                
			}
		} catch (Exception ex) {
			Logger.getLogger("customerController").log(Level.SEVERE, "Action en erreur", ex);
			request.setAttribute("message", ex.getMessage());
		} 
		request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);

		// Est-ce que l'utilisateur est connecté ?
		
	}
     
     @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(CustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
}
     
     
     
   

	

	private String findUserInSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		return (session == null) ? null : (String) session.getAttribute("userName");
	}
        
        private void supprimerCode(String code) throws SQLException {
		DAO dao = new DAO();
                dao.deleteDiscountCode(code);
		
	}
        
         public List<DiscountCode> viewCodes(HttpServletRequest request) throws SQLException {
            List<DiscountCode> result = new LinkedList<>();
            DAO dao= new DAO();
            HttpSession session = request.getSession();
            String password = ((String)session.getAttribute("userPassword"));
            Customer c = new Customer();
            c.setPassword(password);
            result = dao.customerCodes(c);         
            
            return result;
    }
         
       

}
