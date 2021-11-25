public interface Explorer {
    void onExplore(Solver state);
    void onPathFound(Node solution);
}
