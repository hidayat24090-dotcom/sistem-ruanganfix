package com.sistemruangan.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Custom Dialog Utility with Overlay Effect
 * NO WINDOW SWITCHING - Pure overlay on current window!
 */
public class DialogUtil {
    
    public enum DialogType {
        SUCCESS, ERROR, WARNING, INFO, CONFIRMATION
    }
    
    /**
     * Show simple dialog (auto-close capable)
     */
    public static void showDialog(DialogType type, String title, String message, StackPane rootContainer) {
        if (rootContainer == null) {
            System.err.println("❌ ERROR: rootContainer is NULL! Cannot show dialog.");
            return;
        }
        
        StackPane overlay = createOverlay();
        VBox dialogBox = createDialogBox(type, title, message, overlay, rootContainer, null);
        
        overlay.getChildren().add(dialogBox);
        rootContainer.getChildren().add(overlay);
        
        animateIn(overlay, dialogBox);
    }
    
    /**
     * Show confirmation dialog with callbacks
     */
    public static void showConfirmation(String title, String message, StackPane rootContainer, 
                                       Runnable onConfirm, Runnable onCancel) {
        if (rootContainer == null) {
            System.err.println("❌ ERROR: rootContainer is NULL!");
            return;
        }
        
        StackPane overlay = createOverlay();
        VBox dialogBox = createConfirmationBox(title, message, overlay, rootContainer, onConfirm, onCancel);
        
        overlay.getChildren().add(dialogBox);
        rootContainer.getChildren().add(overlay);
        
        animateIn(overlay, dialogBox);
    }
    
    /**
     * Show input dialog with TextArea
     */
    public static void showInputDialog(String title, String message, String placeholder, 
                                      StackPane rootContainer, InputCallback callback) {
        if (rootContainer == null) {
            System.err.println("❌ ERROR: rootContainer is NULL!");
            return;
        }
        
        StackPane overlay = createOverlay();
        VBox dialogBox = createInputBox(title, message, placeholder, overlay, rootContainer, callback);
        
        overlay.getChildren().add(dialogBox);
        rootContainer.getChildren().add(overlay);
        
        animateIn(overlay, dialogBox);
    }
    
    // ========== PRIVATE METHODS ==========
    
    private static StackPane createOverlay() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        overlay.setAlignment(Pos.CENTER);
        
        // Click outside to close
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                // Clicked on overlay background
                // Don't close automatically - user must click button
            }
        });
        
        return overlay;
    }
    
    private static VBox createDialogBox(DialogType type, String title, String message, 
                                       StackPane overlay, StackPane rootContainer, Runnable callback) {
        VBox dialog = new VBox(0);
        dialog.setMaxWidth(480);
        dialog.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 30, 0, 0, 10);"
        );
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(25, 30, 20, 30));
        header.setStyle("-fx-background-color: " + getHeaderColor(type) + "; -fx-background-radius: 15 15 0 0;");
        
        Label iconLabel = new Label(getIcon(type));
        iconLabel.setStyle("-fx-font-size: 36px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        // Content
        VBox content = new VBox(15);
        content.setPadding(new Insets(25, 30, 25, 30));
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50; -fx-line-spacing: 3px;");
        messageLabel.setMaxWidth(420);
        
        content.getChildren().add(messageLabel);
        
        // Button
        Button btnOk = new Button("OK");
        btnOk.setPrefWidth(120);
        btnOk.setStyle(
            "-fx-background-color: " + getHeaderColor(type) + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 25;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        btnOk.setOnMouseEntered(e -> btnOk.setOpacity(0.85));
        btnOk.setOnMouseExited(e -> btnOk.setOpacity(1.0));
        
        btnOk.setOnAction(e -> {
            if (callback != null) callback.run();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        HBox buttonBox = new HBox(btnOk);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 30, 25, 30));
        
        dialog.getChildren().addAll(header, content, buttonBox);
        
        return dialog;
    }
    
    private static VBox createConfirmationBox(String title, String message, 
                                             StackPane overlay, StackPane rootContainer,
                                             Runnable onConfirm, Runnable onCancel) {
        VBox dialog = new VBox(0);
        dialog.setMaxWidth(500);
        dialog.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 30, 0, 0, 10);"
        );
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(25, 30, 20, 30));
        header.setStyle("-fx-background-color: #f39c12; -fx-background-radius: 15 15 0 0;");
        
        Label iconLabel = new Label("⚠️");
        iconLabel.setStyle("-fx-font-size: 36px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        // Content
        VBox content = new VBox(15);
        content.setPadding(new Insets(25, 30, 25, 30));
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50; -fx-line-spacing: 3px;");
        messageLabel.setMaxWidth(440);
        
        content.getChildren().add(messageLabel);
        
        // Buttons
        Button btnConfirm = new Button("Ya, Lanjutkan");
        btnConfirm.setPrefWidth(150);
        btnConfirm.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        Button btnCancel = new Button("Batal");
        btnCancel.setPrefWidth(120);
        btnCancel.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        btnConfirm.setOnMouseEntered(e -> btnConfirm.setOpacity(0.85));
        btnConfirm.setOnMouseExited(e -> btnConfirm.setOpacity(1.0));
        btnCancel.setOnMouseEntered(e -> btnCancel.setOpacity(0.85));
        btnCancel.setOnMouseExited(e -> btnCancel.setOpacity(1.0));
        
        btnConfirm.setOnAction(e -> {
            if (onConfirm != null) onConfirm.run();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        btnCancel.setOnAction(e -> {
            if (onCancel != null) onCancel.run();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        HBox buttonBox = new HBox(15, btnConfirm, btnCancel);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 30, 25, 30));
        
        dialog.getChildren().addAll(header, content, buttonBox);
        
        return dialog;
    }
    
    private static VBox createInputBox(String title, String message, String placeholder,
                                      StackPane overlay, StackPane rootContainer,
                                      InputCallback callback) {
        VBox dialog = new VBox(0);
        dialog.setMaxWidth(550);
        dialog.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 30, 0, 0, 10);"
        );
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(25, 30, 20, 30));
        header.setStyle("-fx-background-color: #5B9BD5; -fx-background-radius: 15 15 0 0;");
        
        Label iconLabel = new Label("✏️");
        iconLabel.setStyle("-fx-font-size: 36px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        // Content
        VBox content = new VBox(15);
        content.setPadding(new Insets(25, 30, 25, 30));
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50; -fx-line-spacing: 3px;");
        messageLabel.setMaxWidth(490);
        
        TextArea textArea = new TextArea();
        textArea.setPromptText(placeholder);
        textArea.setPrefRowCount(4);
        textArea.setWrapText(true);
        textArea.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #cbd5e0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14px;"
        );
        
        // Focus effect
        textArea.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                textArea.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #5B9BD5;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-padding: 10;" +
                    "-fx-font-size: 14px;"
                );
            } else {
                textArea.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #cbd5e0;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-padding: 10;" +
                    "-fx-font-size: 14px;"
                );
            }
        });
        
        content.getChildren().addAll(messageLabel, textArea);
        
        // Buttons
        Button btnSubmit = new Button("Kirim");
        btnSubmit.setPrefWidth(130);
        btnSubmit.setStyle(
            "-fx-background-color: #2ecc71;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        Button btnCancel = new Button("Batal");
        btnCancel.setPrefWidth(120);
        btnCancel.setStyle(
            "-fx-background-color: #e8edf2;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        btnSubmit.setOnMouseEntered(e -> btnSubmit.setOpacity(0.85));
        btnSubmit.setOnMouseExited(e -> btnSubmit.setOpacity(1.0));
        btnCancel.setOnMouseEntered(e -> btnCancel.setOpacity(0.85));
        btnCancel.setOnMouseExited(e -> btnCancel.setOpacity(1.0));
        
        btnSubmit.setOnAction(e -> {
            String input = textArea.getText().trim();
            if (!input.isEmpty() && callback != null) {
                callback.onInput(input);
            }
            closeDialog(overlay, rootContainer, dialog);
        });
        
        btnCancel.setOnAction(e -> {
            if (callback != null) callback.onCancel();
            closeDialog(overlay, rootContainer, dialog);
        });
        
        HBox buttonBox = new HBox(15, btnSubmit, btnCancel);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 30, 25, 30));
        
        dialog.getChildren().addAll(header, content, buttonBox);
        
        // Auto-focus on TextArea
        javafx.application.Platform.runLater(() -> textArea.requestFocus());
        
        return dialog;
    }
    
    private static void animateIn(StackPane overlay, VBox dialog) {
        overlay.setOpacity(0);
        dialog.setScaleX(0.7);
        dialog.setScaleY(0.7);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), dialog);
        scaleIn.setFromX(0.7);
        scaleIn.setFromY(0.7);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        fadeIn.play();
        scaleIn.play();
    }
    
    private static void closeDialog(StackPane overlay, StackPane rootContainer, VBox dialog) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), dialog);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.7);
        scaleOut.setToY(0.7);
        
        fadeOut.setOnFinished(e -> rootContainer.getChildren().remove(overlay));
        
        fadeOut.play();
        scaleOut.play();
    }
    
    private static String getIcon(DialogType type) {
        switch (type) {
            case SUCCESS: return "✅";
            case ERROR: return "❌";
            case WARNING: return "⚠️";
            case INFO: return "ℹ️";
            case CONFIRMATION: return "❓";
            default: return "ℹ️";
        }
    }
    
    private static String getHeaderColor(DialogType type) {
        switch (type) {
            case SUCCESS: return "#2ecc71";
            case ERROR: return "#e74c3c";
            case WARNING: return "#f39c12";
            case INFO: return "#5B9BD5";
            case CONFIRMATION: return "#f39c12";
            default: return "#5B9BD5";
        }
    }
    
    /**
     * Callback interface for input dialog
     */
    public interface InputCallback {
        void onInput(String input);
        default void onCancel() {}
    }
}