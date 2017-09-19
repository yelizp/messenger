package com.yeliz.messenger.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yeliz.messenger.database.DatabaseClass;
import com.yeliz.messenger.model.Profile;

public class ProfileService {

	private Map<String,Profile> profiles = DatabaseClass.getProfiles();
	
	public ProfileService() {
		profiles.put("koushik", new Profile(1L, "koushik", "Koushik", "Kothagal"));
		profiles.put("yeliz", new Profile(2L, "yeliz", "Yeliz", "Pehlivanoglu"));
	}	
	
	public List<Profile> getAllProfiles() {
		return new ArrayList<Profile>(profiles.values());		
	}
	
	public Profile getProfile(String profileName) {
		return profiles.get(profileName);
	}
	
	public Profile addProfile(Profile profile) {
		profile.setId(profiles.size() + 1);
		profiles.put(profile.getProfileName(), profile);
		return profile;
	}
	
	public Profile updateProfile(Profile profile) {
		if(profile.getProfileName().isEmpty()) {
			return null;
		}	
		
		profiles.put(profile.getProfileName(), profile);
		return profile;
	}
	
	public Profile removeMessage(String profileName) {
		return profiles.remove(profileName);
	}
}
