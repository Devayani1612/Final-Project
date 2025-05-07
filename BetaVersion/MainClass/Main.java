package MainClass;

import javax.swing.SwingUtilities;

import ControllerClasses.MenuController;
import ModelClasses.ScoreManager;
import ModelClasses.SoundManager;
import ViewClasses.MenuView;

public class Main {
    private static MenuView menuView;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScoreManager scoreManager = new ScoreManager();
            SoundManager soundManager = new SoundManager();
            
            menuView = new MenuView();
            new MenuController(menuView, scoreManager, soundManager);
            menuView.show();
        });
    }
    
    public static void showMenuView() {
        menuView.show();
    }
}

