package cu.edu.unah.bean;

import cu.edu.unah.rest.RestDashboard;
import cu.edu.unah.util.DashboardResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;

@Named("dashboardBean")
@ViewScoped
@Data
public class DashboardBean implements Serializable {

    private DashboardResponse dashboard;

    @PostConstruct
    public void init() {
        cargarDashboard();
    }

    public void cargarDashboard() {
        RestDashboard restDashboard = new RestDashboard();
        dashboard = restDashboard.getDashboard();
    }
}
