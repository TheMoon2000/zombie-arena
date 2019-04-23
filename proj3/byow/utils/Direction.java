package byow.utils;

public enum Direction {
    West, East, North, South, Disoriented;

    public static Direction parse(char c) {
        switch (c) {
            case 'W':
                return North;
            case 'S':
                return South;
            case 'D':
                return East;
            case 'A':
                return West;
            default:
                return Disoriented;
        }
    }
}
