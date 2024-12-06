package salesMaster.service.Impl;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import salesMaster.dao.entities.Client;
import salesMaster.dao.entities.Utilisateur;
import salesMaster.dao.reposetories.IGestionClient;
import salesMaster.service.Iservice.IserviceClient;

@Service
public class ServiceClientImpl implements IserviceClient {
	
	@Autowired
	private IGestionClient clientrepo;
	
	@Override
	public Client addCl(Client c) {
		return clientrepo.save(c);
	}

	@Override
	public void deleteCl(Long id) {
		clientrepo.deleteById(id);
	}

	@Override
	public Client updateCl(Long id, Client updatedClient) {
		 return clientrepo.findById(id)
		            .map(client -> {
		                client.setNom(updatedClient.getNom());
		                client.setAdresse(updatedClient.getAdresse());
		                client.setEmail(updatedClient.getEmail());
		                client.setTelephone(updatedClient.getTelephone());
		                return clientrepo.save(client);
		            }).orElseThrow(() -> new EntityNotFoundException("Client not found"));		
	}

	@Override
	public List<Client> listerCl() {
		return clientrepo.findAll();
	}

	@Override
	public Optional<Client> getClientById(Long id) {
		return clientrepo.findById(id);
	}

	@Override
	public List<Client> getClientsByUtilisateur(Utilisateur utilisateur) {
		return clientrepo.findByUtilisateur(utilisateur);
	}

	
	

	

}
