package com.gmail.grigorij.backend.entities.recoverylink;

import com.gmail.grigorij.backend.entities.EntityPojo;
import com.gmail.grigorij.utils.ProjectConstants;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@Entity
@Table(name = "recovery_links")
@NamedQueries({
		@NamedQuery(
				name="getRecoveryLinkByToken",
				query="SELECT link FROM RecoveryLink link WHERE link.token = :token_var")
})
public class RecoveryLink extends EntityPojo {

	private String token;

	private String email;


	public RecoveryLink() {}

	public static RecoveryLink generateRecoveryLink() {
		String token = RandomStringUtils.randomAlphabetic(ProjectConstants.RECOVERY_TOKEN_LENGTH);

		RecoveryLink recoveryLink = new RecoveryLink();
		recoveryLink.setToken(token);

		return recoveryLink;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
