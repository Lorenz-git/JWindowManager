import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ManagerController {
	// FRONTEND
	ObservableList<Window> scannedWindows = FXCollections.observableArrayList();
	FilteredList<Window> scannedWindowsFiltered = new FilteredList<Window>(scannedWindows);
	ObservableList<Window> layoutWindows = FXCollections.observableArrayList();

	@FXML
	private ListView<Window> scannedWindowsListView;
	@FXML
	private ListView<Window> layoutWindowsListView;
	@FXML
	private TextField searchText;
	@FXML
	private TextField changeText;

	// BACKEND
	private User32 user32 = User32.INSTANCE;
	private HWND activeWindow = user32.GetActiveWindow();

	public ManagerController() {
	}

	@FXML
	private void initialize() {
		// cell factory scanned list view
		scannedWindowsListView.setCellFactory(param -> new ListCell<Window>() {
			@Override
			protected void updateItem(Window item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null || item.wText == null) {
					setText(null);
				} else {
					setText(item.wText);
				}
			}
		});

		// cell factory layout list view
		layoutWindowsListView.setCellFactory(param -> new ListCell<Window>() {
			@Override
			protected void updateItem(Window item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null || item.wText == null) {
					setText(null);
				} else {
					setText(item.wText);
				}
			}
		});
		// search bar
		searchText.textProperty().addListener((observable, oldValue, newValue) -> {
			scannedWindowsFiltered.setPredicate(window -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every client with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (window.wText.toLowerCase().contains(lowerCaseFilter)) {
					return true; // filter matches first name
				}
				return false; // Does not match
			});
		});

		layoutWindowsListView.getSelectionModel().selectedItemProperty().addListener((ob, ov, nv) -> {
			if (layoutWindowsListView.getSelectionModel().getSelectedItem() != null)
				changeText.setText(layoutWindowsListView.getSelectionModel().getSelectedItem().wText);
			else
				changeText.setText("");

		});

		scannedWindowsListView.setItems(scannedWindowsFiltered);
		layoutWindowsListView.setItems(layoutWindows);
	}

	@FXML
	private void scanWindows(ActionEvent e) {
		scannedWindows.clear();
		user32.EnumWindows(new WNDENUMPROC() {
			public boolean callback(HWND hWnd, Pointer arg1) {
				char[] windowText = new char[512];
				user32.GetWindowText(hWnd, windowText, 512);
				String wText = Native.toString(windowText);

				// get rid of this if block if you want all windows regardless of whether
				// or not they have text
				if (wText.isEmpty()) {
					return true;
				}

				scannedWindows.add(new Window(hWnd, wText));
				return true;
			}
		}, null);
	}

	@FXML
	private void addToLayout(ActionEvent e) {
		Window w = scannedWindowsListView.getSelectionModel().getSelectedItem();
		layoutWindows.add(new Window(w.wText));
	}

	@FXML
	private void removeFromLayout(ActionEvent e) {
		Window w = layoutWindowsListView.getSelectionModel().getSelectedItem();
		if (w != null) {

			layoutWindows.remove(w);
			layoutWindowsListView.getSelectionModel().clearSelection();
		}
	}

	@FXML
	private void saveChange(ActionEvent e) {
		Window w = layoutWindowsListView.getSelectionModel().getSelectedItem();
		if (w != null) {
			w.wText = changeText.getText();
			layoutWindowsListView.getSelectionModel().clearSelection();
		}

	}

	private void readWindowPositions() {
		scanWindows(null);
		for (Window w : layoutWindows) {
			final RECT rect = new RECT();
			user32.GetWindowRect(getWindowHandle(w), rect);
			w.x = rect.left;
			w.y = rect.top;
			w.width = Math.abs(rect.right - rect.left);
			w.height = Math.abs(rect.bottom - rect.top);
			System.out.println(w);
		}
	}

	private HWND getWindowHandle(Window w) {
		for (Window ws : scannedWindows) {
			if (ws.wText.toLowerCase().contains(w.wText.toLowerCase())) {
				return ws.hWnd;
			}
		}
		return null;
	}

	@FXML
	private void saveLayout(ActionEvent e) {
		File file = filePicker(false);
		readWindowPositions();

		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(file, layoutWindows);
			System.out.println("done");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	private void loadLayout(ActionEvent e) {
		File file = filePicker(true);
		ObjectMapper mapper = new ObjectMapper();

		try {
			List<Window> windows = Arrays.asList(mapper.readValue(file, Window[].class));
			layoutWindows.clear();
			for (Window w : windows) {
				layoutWindows.add(w);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@FXML
	private void applyLayout(ActionEvent e) {
		scanWindows(null);
		for (Window w : layoutWindows) {
			HWND hwnd = getWindowHandle(w);
			if (hwnd != null) {
				user32.SetWindowPos(hwnd, activeWindow, w.x, w.y, w.width, w.height, 0);
			}
		}
	}

	private File filePicker(boolean open) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("JSON File", "*.json"));
		if (open) {
			fc.setTitle("Load Layout");
			File f = fc.showOpenDialog(null);
			return f;
		} else {
			fc.setTitle("Save Layout");
			File f = fc.showSaveDialog(null);
			return f;
		}
	}

}
