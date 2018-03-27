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
		//String code = request.getParameter("code");//ce sera pour l'admin controller ajouter des codes ?
		//String taux = request.getParameter("taux");// idem
                //Pour ajouter des commandes
               // String purchaseToCreate =  request.getParameter("purchaseToCreate");
                String quantite = request.getParameter("quantite");
               
                // Pour supprimer des commandes
                String purchaseToDelete = request.getParameter("purchaseToDelete");
                String password = ((String)session.getAttribute("userPassword"));
                //Pour éditer des commandes
                String purchaseToEdit = request.getParameter("purchaseToEdit");
                
                
               
                request.setAttribute("codes", viewCodes(request));
		try {
			
                        Customer c = new Customer();
                        c.setPassword(password);
                        
			request.setAttribute("codes", viewCodes(request));		
			switch (action) {
				/*case "ADD": // Requête d'ajout (vient du formulaire de saisie)
					dao.addDiscountCode(code, Float.valueOf(taux));                                 
					request.setAttribute("message", "Code " + code + " Ajouté");
					request.setAttribute("codes", viewCodes(request));
                                        request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);
					break;
				case "DELETE": // Requête de suppression (vient du lien hypertexte)
					try {
						dao.deleteDiscountCode(code);
						request.setAttribute("message", "Code " + code + " Supprimé");
														
					} catch (SQLIntegrityConstraintViolationException e) {
						request.setAttribute("message", "Impossible de supprimer " + code + ", ce code est utilisé.");
					}
					break;*/
                                        
                                case "ADD_COMMANDE": // Requête d'ajout (vient du formulaire de saisie)
                                    dao.addCommande(Integer.parseInt(password), Integer.parseInt(quantite));
                                    dao.numProduct(request.getParameter("produit"));
                                    
                                    session.setAttribute("commandes", dao.customerCommandes(c));
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
                                            dao.editCommande(Integer.parseInt(purchaseToEdit), Integer.parseInt(quantityToEdit));
                                            request.setAttribute("message", "Commande " + purchaseToEdit + " modifiée");
                                            session.setAttribute("commandes", dao.customerCommandes(c));
                                            request.getRequestDispatcher("WEB-INF/customer.jsp").forward(request, response);
														
					} catch (SQLIntegrityConstraintViolationException e) {
						request.setAttribute("message", "Impossible de modifier " +  purchaseToEdit + ", cette commande est utilisée.");
					}
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
