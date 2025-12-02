package ph.edu.dlsu.lbycpei.caferecommmendationsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainController {

    private CafeSystem cafeSystem;
    private final ObservableList<MenuItem> menuData = FXCollections.observableArrayList();
    private final ObservableList<MenuItem> orderData = FXCollections.observableArrayList();
    private final ObservableList<String> recommendationData = FXCollections.observableArrayList();

    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, String> menuNameColumn;
    @FXML private TableColumn<MenuItem, Double> menuPriceColumn;

    @FXML private TableView<MenuItem> orderTable;
    @FXML private TableColumn<MenuItem, String> orderNameColumn;
    @FXML private TableColumn<MenuItem, Double> orderPriceColumn;
    @FXML private Label totalLabel;
    @FXML private Label userLabel;

    @FXML private ListView<String> recommendationList;
    @FXML private VBox receiptContainer;
    @FXML private TextArea receiptArea;
    @FXML private VBox orderVBox;
    @FXML private Button inventoryButton;

    public void setCafeSystem(CafeSystem cafeSystem) {
        this.cafeSystem = cafeSystem;
        initializeData();

        if (cafeSystem.getCurrentUser() != null) {
            userLabel.setText("Welcome, " + cafeSystem.getCurrentUser().getUsername());
        } else {
            userLabel.setText("Welcome, Guest");
        }
    }

    @FXML
    public void initialize() {
        menuNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        menuPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        menuTable.setItems(menuData);

        orderNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderTable.setItems(orderData);

        recommendationList.setItems(recommendationData);

        receiptContainer.setVisible(false);
        receiptContainer.setManaged(false);
    }

    private void initializeData() {
        menuData.clear();
        menuData.addAll(cafeSystem.getMenuItems());
        cafeSystem.startNewOrder();
        updateOrderUI();
    }

    private void updateOrderUI() {
        Order currentOrder = cafeSystem.getCurrentOrder();
        orderData.clear();
        orderData.addAll(currentOrder.getItems());

        double total = currentOrder.getItems().stream().mapToDouble(MenuItem::getPrice).sum();
        totalLabel.setText(String.format("₱%.2f", total));

        updateRecommendations();
    }

    private void updateRecommendations() {
        recommendationData.clear();
        if (!cafeSystem.getCurrentOrder().getItems().isEmpty()) {
            List<String> recs = cafeSystem.getRecommendations();
            recommendationData.addAll(recs);
        }
    }

    private void refreshOrderTable() {
        orderData.setAll(cafeSystem.getCurrentOrder().getItems());
    }

    private void refreshTotal() {
        double total = cafeSystem.getCurrentOrder()
                .getItems()
                .stream()
                .mapToDouble(MenuItem::getPrice)
                .sum();

        totalLabel.setText(String.format("₱%.2f", total));
    }

    @FXML
    private void handleAddItemToOrder() {
        MenuItem item = menuTable.getSelectionModel().getSelectedItem();
        if (item == null) return;

        boolean success = cafeSystem.addItemToOrder(item.getName());

        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Out of Stock");
            alert.setContentText(item.getName() + " is no longer available.");
            alert.showAndWait();
            return;
        }

        refreshOrderTable();
        refreshTotal();
    }

    @FXML
    private void handleAddRecommendedItem() {
        String selectedRec = recommendationList.getSelectionModel().getSelectedItem();
        if (selectedRec != null) {
            cafeSystem.addRecommendedItem(selectedRec);
            updateOrderUI();
        } else {
            showAlert("Error", "Please select a recommended item to add.");
        }
    }

    @FXML
    private void handlePlaceOrder() {
        if (cafeSystem.getCurrentOrder().getItems().isEmpty()) {
            showAlert("Error", "Your order is empty. Please add items before placing the order.");
            return;
        }

        String receiptText = cafeSystem.finalizeOrder();

        receiptArea.setText(receiptText.trim());
        orderVBox.setVisible(false);
        orderVBox.setManaged(false);
        receiptContainer.setVisible(true);
        receiptContainer.setManaged(true);
    }

    @FXML
    private void handleNewOrder() {
        receiptContainer.setVisible(false);
        receiptContainer.setManaged(false);
        orderVBox.setVisible(true);
        orderVBox.setManaged(true);

        initializeData();
    }

    @FXML
    private void handleLogout() {
        cafeSystem.logoutUser();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ph/edu/dlsu/lbycpei/caferecommmendationsystem/login-view.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setCafeSystem(cafeSystem);

            Stage stage = (Stage) userLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleInventory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventory-view.fxml"));
            Parent root = loader.load();
            
            InventoryController controller = loader.getController();
            controller.setCafeSystem(cafeSystem);

            Stage stage = (Stage) inventoryButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

