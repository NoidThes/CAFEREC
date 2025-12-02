package ph.edu.dlsu.lbycpei.caferecommmendationsystem;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class InventoryController {


    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private TableColumn<InventoryItem, String> colName;
    @FXML private TableColumn<InventoryItem, Integer> colQuantity;
    @FXML private TableColumn<InventoryItem, String> colCategory;


    @FXML private TextField txtName;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtCategory;


    private final ObservableList<InventoryItem> inventoryList = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colQuantity.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        colCategory.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategory()));


        inventoryTable.setItems(inventoryList);
    }


    @FXML
    private void handleAdd() {
        String name = txtName.getText();
        String category = txtCategory.getText();


        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid quantity");
            return;
        }


        inventoryList.add(new InventoryItem(name, quantity, category));
        clearFields();
    }


    @FXML
    private void handleUpdate() {
        InventoryItem item = inventoryTable.getSelectionModel().getSelectedItem();
        if (item == null) { showAlert("Select an item to update."); return; }


        item.setName(txtName.getText());
        item.setCategory(txtCategory.getText());


        try {
            item.setQuantity(Integer.parseInt(txtQuantity.getText()));
        } catch (NumberFormatException e) {
            showAlert("Invalid quantity");
        }


        inventoryTable.refresh();
        clearFields();
    }


    @FXML
    private void handleDelete() {
        InventoryItem item = inventoryTable.getSelectionModel().getSelectedItem();
        if (item == null) { showAlert("Select an item to delete."); return; }
        inventoryList.remove(item);
    }


    private void clearFields() {
        txtName.clear();
        txtQuantity.clear();
        txtCategory.clear();
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cafe-view.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setCafeSystem(this.cafeSystem);

            Stage stage = (Stage) inventoryTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private CafeSystem cafeSystem;

    public void setCafeSystem(CafeSystem system) {
        this.cafeSystem = system;
        inventoryTable.setItems(
                FXCollections.observableArrayList(cafeSystem.getInventory().getItems())
        );
    }

}

