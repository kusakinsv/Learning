package ru.one.learning.patterns.abstractfactory;

public class AbstractFactoryPattern {
    public static void main(String[] args) {

    }

    interface Mouse{
        void click();
        void doubleclick();
        void scroll(int direction);
    }

    interface Keyboard{
        void print();
        void println();
    }

    interface Touchpad{
        void track(int deltaX, int deltaY);
    }

    interface DeviceFactory{
        Mouse getMouse();
        Keyboard getKeyBoard();
        Touchpad getTouchpad();
    }

    

    class RuMouse implements Mouse{
        @Override
        public void click() {
            System.out.println("Кликаем");
        }

        @Override
        public void doubleclick() {
            System.out.println("Двойной клик");
        }

        @Override
        public void scroll(int direction) {
            System.out.println("Сколлим: " + direction);
        }
    }

    class RuKeyboard implements Keyboard{
        @Override
        public void print() {
            System.out.println("Печатаем");
        }

        @Override
        public void println() {
            System.out.println("Печатаем строку");
        }
    }

    class RuTouchpad implements Touchpad{
        @Override
        public void track(int deltaX, int deltaY) {
            System.out.printf("Двигаем на X: %s, Y: %s \n", deltaX, deltaY);
        }
    }

    class EngMouse implements Mouse{
        @Override
        public void click() {
            System.out.println("Click");
        }

        @Override
        public void doubleclick() {
            System.out.println("Double Click");
        }

        @Override
        public void scroll(int direction) {
            System.out.println("Scroll: " + direction);
        }
    }

    class EngKeyboard implements Keyboard{
        @Override
        public void print() {
            System.out.println("Printing");
        }

        @Override
        public void println() {
            System.out.println("Printing line");
        }
    }

    class EngTouchpad implements Touchpad{
        @Override
        public void track(int deltaX, int deltaY) {
            System.out.printf("Move to X: %s, Y: %s \n", deltaX, deltaY);
        }
    }

    class RuDeviceFactory implements DeviceFactory{
        @Override
        public Mouse getMouse() {
            return new RuMouse();
        }

        @Override
        public Keyboard getKeyBoard() {
            return new RuKeyboard();
        }

        @Override
        public Touchpad getTouchpad() {
            return new RuTouchpad();
        }
    }

    class EngDeviceFactory implements DeviceFactory{
        @Override
        public Mouse getMouse() {
            return new EngMouse();
        }

        @Override
        public Keyboard getKeyBoard() {
            return new EngKeyboard();
        }

        @Override
        public Touchpad getTouchpad() {
            return new EngTouchpad();
        }
    }

}



