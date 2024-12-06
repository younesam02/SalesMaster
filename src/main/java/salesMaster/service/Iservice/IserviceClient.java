package salesMaster.service.Iservice;

import java.util.List;
import java.util.Optional;


import salesMaster.dao.entities.Client;
import salesMaster.dao.entities.Utilisateur;

public interface IserviceClient {
	public Client addCl(Client c) ;
	public Optional<Client> getClientById(Long id);
	public void deleteCl(Long id);
	public Client updateCl(Long id, Client updatedClient);
	public List<Client> listerCl();
	public List<Client> getClientsByUtilisateur(Utilisateur utilisateur);
	}
