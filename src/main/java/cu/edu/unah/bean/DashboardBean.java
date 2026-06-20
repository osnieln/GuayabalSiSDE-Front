package cu.edu.unah.bean;

import cu.edu.unah.rest.RestDashboard;
import cu.edu.unah.util.CultivoDistribucionResponse;
import cu.edu.unah.util.DashboardResponse;
import cu.edu.unah.util.ProduccionMensualResponse;
import cu.edu.unah.util.RiegoMensualResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Data;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named("dashboardBean")
@ViewScoped
@Data
public class DashboardBean implements Serializable {

    private DashboardResponse dashboard;

    private BarChartModel produccionMensualModel;
    private BarChartModel riegosMensualModel;
    private PieChartModel distribucionCultivosModel;

    @PostConstruct
    public void init() {
        cargarDashboard();
    }

    public void cargarDashboard() {
        RestDashboard restDashboard = new RestDashboard();
        dashboard = restDashboard.getDashboard();
        if (dashboard != null) {
            construirGraficas();
        }
    }

    private void construirGraficas() {
        List<ProduccionMensualResponse> produccionMensual = dashboard.getProduccionMensual();
        if (produccionMensual != null) {
            BarChartDataSet dataSet = new BarChartDataSet();
            dataSet.setLabel("Producción (kg)");
            dataSet.setBackgroundColor("rgba(111,66,193,0.7)");
            dataSet.setData(produccionMensual.stream().map(p -> (Number) p.getTotal()).collect(Collectors.toList()));

            ChartData data = new ChartData();
            data.addChartDataSet(dataSet);
            data.setLabels(produccionMensual.stream().map(ProduccionMensualResponse::getMes).collect(Collectors.toList()));

            BarChartModel model = new BarChartModel();
            model.setData(data);
            produccionMensualModel = model;
        }

        List<RiegoMensualResponse> riegosMensual = dashboard.getRiegosMensual();
        if (riegosMensual != null) {
            BarChartDataSet planificadosDataSet = new BarChartDataSet();
            planificadosDataSet.setLabel("Planificados");
            planificadosDataSet.setBackgroundColor("rgba(0,123,255,0.7)");
            planificadosDataSet.setData(riegosMensual.stream().map(r -> (Number) r.getPlanificados()).collect(Collectors.toList()));

            BarChartDataSet ejecutadosDataSet = new BarChartDataSet();
            ejecutadosDataSet.setLabel("Ejecutados");
            ejecutadosDataSet.setBackgroundColor("rgba(253,126,20,0.7)");
            ejecutadosDataSet.setData(riegosMensual.stream().map(r -> (Number) r.getEjecutados()).collect(Collectors.toList()));

            ChartData data = new ChartData();
            data.addChartDataSet(planificadosDataSet);
            data.addChartDataSet(ejecutadosDataSet);
            data.setLabels(riegosMensual.stream().map(RiegoMensualResponse::getMes).collect(Collectors.toList()));

            BarChartModel model = new BarChartModel();
            model.setData(data);
            riegosMensualModel = model;
        }

        List<CultivoDistribucionResponse> distribucionCultivos = dashboard.getDistribucionCultivos();
        if (distribucionCultivos != null) {
            PieChartDataSet dataSet = new PieChartDataSet();
            dataSet.setData(distribucionCultivos.stream().map(c -> (Number) c.getCantidad()).collect(Collectors.toList()));
            dataSet.setBackgroundColor(List.of("#28a745", "#007bff", "#fd7e14", "#6f42c1", "#dc3545", "#17a2b8", "#ffc107"));

            ChartData data = new ChartData();
            data.addChartDataSet(dataSet);
            data.setLabels(distribucionCultivos.stream().map(CultivoDistribucionResponse::getCultivo).collect(Collectors.toList()));

            PieChartModel model = new PieChartModel();
            model.setData(data);
            distribucionCultivosModel = model;
        }
    }
}
