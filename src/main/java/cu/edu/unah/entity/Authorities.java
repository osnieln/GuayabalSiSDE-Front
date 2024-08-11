package cu.edu.unah.entity;

import java.io.Serializable;
import jakarta.persistence.*;

import java.io.Serializable;


@Entity
@Table(name = "authorities")
@NamedQueries({
        @NamedQuery(name = "Authorities.findAll", query = "SELECT a FROM Authorities a"),
        @NamedQuery(name = "Authorities.findByUsername", query = "SELECT a FROM Authorities a WHERE a.authoritiesPK.username = :username"),
        @NamedQuery(name = "Authorities.findByAuthority", query = "SELECT a FROM Authorities a WHERE a.authoritiesPK.authority = :authority")})
public class Authorities implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AuthoritiesPK authoritiesPK;
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Users users;

    public Authorities() {
    }

    public Authorities(AuthoritiesPK authoritiesPK) {
        this.authoritiesPK = authoritiesPK;
    }

    public Authorities(String username, String authority) {
        this.authoritiesPK = new AuthoritiesPK(username, authority);
    }

    public AuthoritiesPK getAuthoritiesPK() {
        return authoritiesPK;
    }

    public void setAuthoritiesPK(AuthoritiesPK authoritiesPK) {
        this.authoritiesPK = authoritiesPK;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (authoritiesPK != null ? authoritiesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Authorities)) {
            return false;
        }
        Authorities other = (Authorities) object;
        if ((this.authoritiesPK == null && other.authoritiesPK != null) || (this.authoritiesPK != null && !this.authoritiesPK.equals(other.authoritiesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Authorities{" +
                "authoritiesPK=" + authoritiesPK +
                ", users=" + users +
                '}';
    }
}