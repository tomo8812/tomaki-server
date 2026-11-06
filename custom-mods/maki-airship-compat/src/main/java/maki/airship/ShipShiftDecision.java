package maki.airship;

public final class ShipShiftDecision {
    private ShipShiftDecision() {}

    public static boolean send(boolean onShipMount, boolean wasShipMount, boolean shiftDown, boolean wasShiftDown) {
        return onShipMount ? shiftDown != wasShiftDown : wasShipMount;
    }
}
