package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.utils.ProjectConstants;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "recovery_links")
@NamedQueries({
		@NamedQuery(name=RecoveryLink.QUERY_BY_TOKEN,
				query="SELECT link FROM RecoveryLink link WHERE link.token = :" + ProjectConstants.VAR1)
})
public class RecoveryLink extends EntityPojo {

	public static final String QUERY_BY_TOKEN = "get_recovery_link_by_token";

	private String token;
	private User user;


	public RecoveryLink() {
		token = RandomStringUtils.randomAlphabetic(ProjectConstants.RECOVERY_TOKEN_LENGTH);
	}


	public String getToken() {
		return token;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
