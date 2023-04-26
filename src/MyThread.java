import javax.swing.*;

import static java.lang.Math.cos;

public class MyThread implements Runnable{
    private double FirstColumn;
    private double SecondColumn;
    private double ThirdColumn;
    public double s;
    private JTable table;
    private int i;
    public MyThread(double FirstColumn, double SecondColumn, double ThirdColumn, int i){
        this.FirstColumn = FirstColumn;
        this.SecondColumn = SecondColumn;
        this.ThirdColumn = ThirdColumn;
        this.i = i;
    }
    public void run(){
        double a = FirstColumn;
        double b = SecondColumn;
        double h = ThirdColumn;
        s = 0;
        int j = 0;
        for (double i = 0; i <= (a - b) / h; i++) {
            if (b + (i + 1) * h <= a)
                s += (cos(b * b + j * h) + cos(b * b + (j + 1) * h)) * h / 2;
            else
                s += (cos(b * b + j * h) + cos(a * a)) * h / 2;
            j++;
        }
        System.out.println(s);
        Main.res[i] = s;
    }
}
