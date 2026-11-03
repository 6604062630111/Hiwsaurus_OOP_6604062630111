public interface Navigator {
    void showMenu();

    void showLevelSelect(int chapter);

    void startLevel(LevelConfig config);

    void showResult(LevelConfig config, boolean victory);

    void exitGame();
}
