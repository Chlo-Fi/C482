package ims.view_controller;

import ims.model.Inventory;
import ims.model.Part;
import ims.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static ims.model.Inventory.getPartInventory;

public class AddProductController implements Initializable {

    private ObservableList<Part> currentParts = FXCollections.observableArrayList();
    private String exceptionMessage = new String();
    private int productID;

    @FXML
    private Label lblAddProductIDNumber;
    @FXML
    private TextField txtAddProductName;
    @FXML
    private TextField txtAddProductInv;
    @FXML
    private TextField txtAddProductPrice;
    @FXML
    private TextField txtAddProductMin;
    @FXML
    private TextField txtAddProductMax;
    @FXML
    private TextField txtAddProductSearch;
    @FXML
    private TableView<Part> tvAddProductAdd;
    @FXML
    private TableColumn<Part, Integer> tvAddProductAddIDColumn;
    @FXML
    private TableColumn<Part, String> tvAddProductAddNameColumn;
    @FXML
    private TableColumn<Part, Integer> tvAddProductAddInvColumn;
    @FXML
    private TableColumn<Part, Double> tvAddProductAddPriceColumn;
    @FXML
    private TableView<Part> tvAddProductDelete;
    @FXML
    private TableColumn<Part, Integer> tvAddProductDeleteIDColumn;
    @FXML
    private TableColumn<Part, String> tvAddProductDeleteNameColumn;
    @FXML
    private TableColumn<Part, Integer> tvAddProductDeleteInvColumn;
    @FXML
    private TableColumn<Part, Double> tvAddProductDeletePriceColumn;

    @FXML
    void handleClearSearch(ActionEvent event) {
        updateAddPartTableView();
        txtAddProductSearch.setText("");
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String searchPart = txtAddProductSearch.getText();
        int partIndex = -1;
        if (Inventory.lookupPart(searchPart) == -1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Search Error");
            alert.setHeaderText("Part not found");
            alert.setContentText("The search term entered does not match any known parts.");
            alert.showAndWait();
        }
        else {
            partIndex = Inventory.lookupPart(searchPart);
            Part tempPart = getPartInventory().get(partIndex);
            ObservableList<Part> tempPartList = FXCollections.observableArrayList();
            tempPartList.add(tempPart);
            tvAddProductAdd.setItems(tempPartList);
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        Part part = tvAddProductAdd.getSelectionModel().getSelectedItem();
        currentParts.add(part);
        updateDeletePartTableView();
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Part part = tvAddProductDelete.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Part Deletion");
        alert.setHeaderText("Confirm");
        alert.setContentText("Are you sure you want to delete " + part.getPartName() + " from parts?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println("Part deleted.");
            currentParts.remove(part);
        }
        else {
            System.out.println("You clicked cancel.");
        }
    }

    @FXML
    void handleAddProductSave(ActionEvent event) throws IOException {
        String productName = txtAddProductName.getText();
        String productInv = txtAddProductInv.getText();
        String productPrice = txtAddProductPrice.getText();
        String productMin = txtAddProductMin.getText();
        String productMax = txtAddProductMax.getText();

        try{
            exceptionMessage = Product.isProductValid(productName, Integer.parseInt(productMin), Integer.parseInt(productMax), Integer.parseInt(productInv), Double.parseDouble(productPrice), currentParts, exceptionMessage);
            if (exceptionMessage.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Error Adding Product");
                alert.setContentText(exceptionMessage);
                alert.showAndWait();
                exceptionMessage = "";
            }
            else {
                System.out.println("Product name: " + productName);
                Product newProduct = new Product();
                newProduct.setProductID(productID);
                newProduct.setProductName(productName);
                newProduct.setProductInStock(Integer.parseInt(productInv));
                newProduct.setProductPrice(Double.parseDouble(productPrice));
                newProduct.setProductMin(Integer.parseInt(productMin));
                newProduct.setProductMax(Integer.parseInt(productMax));
                newProduct.setProductParts(currentParts);
                Inventory.addProduct(newProduct);

                Parent addProductSaveParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(addProductSaveParent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Product");
            alert.setContentText("Form contains blank fields.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAddProductCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Are you sure you want to cancel adding a new product?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            Parent addProductCancel = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            Scene scene = new Scene(addProductCancel);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } else {
            System.out.println("You clicked cancel.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tvAddProductAddIDColumn.setCellValueFactory(cellData -> cellData.getValue().partIDproperty().asObject());
        tvAddProductAddNameColumn.setCellValueFactory(cellData -> cellData.getValue().partNameProperty());
        tvAddProductAddInvColumn.setCellValueFactory(cellData -> cellData.getValue().partInvProperty().asObject());
        tvAddProductAddPriceColumn.setCellValueFactory(cellData -> cellData.getValue().partPriceProperty().asObject());
        tvAddProductDeleteIDColumn.setCellValueFactory(cellData -> cellData.getValue().partIDproperty().asObject());
        tvAddProductDeleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().partNameProperty());
        tvAddProductDeleteInvColumn.setCellValueFactory(cellData -> cellData.getValue().partInvProperty().asObject());
        tvAddProductDeletePriceColumn.setCellValueFactory(cellData -> cellData.getValue().partPriceProperty().asObject());
        updateAddPartTableView();
        updateDeletePartTableView();
        productID = Inventory.getProductIDCount();
        lblAddProductIDNumber.setText("Auto-Gen: " + productID);
    }

    public void updateAddPartTableView() {
        tvAddProductAdd.setItems(getPartInventory());
    }

    public void updateDeletePartTableView() {
        tvAddProductDelete.setItems(currentParts);
    }
}
