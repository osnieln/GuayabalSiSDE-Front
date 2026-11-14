package cu.edu.unah.bean;

import cu.edu.unah.entity.Authorities;
import cu.edu.unah.entity.AuthoritiesPK;
import cu.edu.unah.entity.Users;
import cu.edu.unah.rest.RestAuthorities;
import cu.edu.unah.rest.RestUsers;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.*;
import org.primefaces.PrimeFaces;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@Getter
@Setter
@SessionScoped
public class AdminUsersBean implements Serializable{

    private List<Users> listUsers = new ArrayList<Users>();
    private String username ="";
    private String identificacion ="";
    private String nombre ="";
    private String email ="";
    private String password ="";
    private String password1;
    private boolean enable =true;
    private String descripcion ="";

    private Users user = new Users();
    private Users selectedUser;
    private List<String> Roles = new ArrayList<String>();
    private List<String> Roles1 = new ArrayList<String>();
    RestUsers restUsers = new RestUsers();
    RestAuthorities restAuthorities = new RestAuthorities();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void init(){
        cleanVariables();
        listUsers = restUsers.findAllUsers();
        if (listUsers == null) listUsers = new ArrayList<>();
    }

    public String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public void cleanVariables(){
        username="";
        identificacion="";
        nombre="";
        email="";
        password="";
        enable=true;
        descripcion="";
    }

    public String translateEstado(boolean estado) {
        if (!estado) {
            return "Inactivo";
        }
        return "Activo";
    }

    public String translateBooleanToSeverity(boolean element) {
        if (element) {
            return "badge badge-success";
        }
        return "badge badge-danger";
    }

    public void updateSelectedUsers(Users users) {
        this.setSelectedUser(users);
        List<Authorities> authoritiesList = restAuthorities.findAuthorityByUsername(users.getUsername());
        Roles.clear();
        if (authoritiesList != null) {
            for (Authorities authorities : authoritiesList) {
                Roles.add(authorities.getAuthoritiesPK().getAuthority());
            }
        }
    }

    public void updateSelected_Users_toEdit(Users users) {
        selectedUser = users;
        username = users.getUsername();
        nombre = users.getNombre();
        identificacion = users.getIdentificacion();
        descripcion = users.getDescripcion();
        email = users.getEmail();
        enable = users.isEnabled();

        List<Authorities> authoritiesList = restAuthorities.findAuthorityByUsername(users.getUsername());
        Roles.clear();
        Roles1.clear();
        if (authoritiesList != null) {
            for (Authorities authorities : authoritiesList) {
                Roles.add(authorities.getAuthoritiesPK().getAuthority());
                Roles1.add(authorities.getAuthoritiesPK().getAuthority());
            }
        }
    }

    public void updateSelectedUserToDelete(Users users){
        this.selectedUser = users;
    }

    public void addUsers() {
        if (!password.equals(password1)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR: Las contraseñas deben ser coincidentes", ""));
            return;
        }
        Users userToAdd = Users.builder()
                        .username(username)
                        .identificacion(identificacion)
                        .descripcion(descripcion)
                        .email(email)
                        .enabled(enable)
                        .password(passwordEncoder.encode(password))
                        .nombre(nombre)
                .build();
        FacesContext context = FacesContext.getCurrentInstance();

        if(restUsers.createUser(userToAdd)){
            for (String role : Roles) {
                Authorities authorities = new Authorities(username, role);
                restAuthorities.createAuthority(authorities);
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "USUARIO ADICIONADO CORRECTAMENTE", ""));
            init();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-users");
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL CREAR EL USUARIO", ""));
        }
        PrimeFaces.current().executeScript("PF('addusersDialog').hide()");
    }

    public void editUsers() {
        if (!password.equals(password1)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR: Las contraseñas deben ser coincidentes", ""));
            return;
        }
        String tempPassword = "";
        boolean t = false;
        if(password.equals("")){
            tempPassword = selectedUser.getPassword();
            t=true;
        }
        else tempPassword = password;
        Users userToEdit = Users.builder()
                .username(username)
                .identificacion(identificacion)
                .descripcion(descripcion)
                .email(email)
                .enabled(enable)
                .password(t ? tempPassword : passwordEncoder.encode(tempPassword))
                .nombre(nombre)
                .build();

        FacesContext context = FacesContext.getCurrentInstance();
        if(restUsers.updateUser(userToEdit)){
            for (String role : Roles1) {
                AuthoritiesPK authorities = new AuthoritiesPK(username, role);
                restAuthorities.deleteAuthority(authorities);
            }
            for (String role : Roles) {
                Authorities authorities = new Authorities(username, role);
                restAuthorities.createAuthority(authorities);
            }
            init();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "USUARIO EDITADO", ""));
            PrimeFaces.current().ajax().update("form:messages", "form:dt-users");
        }
        else{
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL EDITAR EL USUARIO", ""));
        }
        PrimeFaces.current().executeScript("PF('editusersDialog').hide()");
    }

    public void deleteUsers() {
        FacesContext context = FacesContext.getCurrentInstance();
        if(getCurrentUser().equals(selectedUser.getUsername())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "NO ES POSIBLE ELIMINAR EL USUARIO QUE ACTUALMENTE SE ENCUENTRA AUTENTICADO", ""));
            PrimeFaces.current().ajax().update("form:messages");
        }
        else{
            List<Authorities> authoritiesList = restAuthorities.findAuthorityByUsername(selectedUser.getUsername());
            for (Authorities authorities : authoritiesList) {
                restAuthorities.deleteAuthority(authorities.getAuthoritiesPK());
            }
            if(restUsers.deleteUser(selectedUser.getUsername())){
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "USUARIO ELIMINADO CORRECTAMENTE", ""));
                init();
                PrimeFaces.current().ajax().update("form:messages", "form:dt-users");

            }
            else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR AL ELIMINAR EL USUARIO", ""));
            }
        }
        PrimeFaces.current().executeScript("PF('deletUserDialog').hide()");
    }
}
