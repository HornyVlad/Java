import java.io.Serializable;

public class SaveData implements Serializable {
    private double FirstColumn;
    private double SecondColumn;
    private double ThirdColumn;
    private double FourthColumn;
    public SaveData(double FirstColumn, double SecondColumn, double ThirdColumn, double FourthColumn){
        this.FirstColumn = FirstColumn;
        this.SecondColumn = SecondColumn;
        this.ThirdColumn = ThirdColumn;
        this.FourthColumn = FourthColumn;
    }

    public double getFirstColumn() {
        return FirstColumn;
    }

    public double getSecondColumn() {
        return SecondColumn;
    }

    public double getThirdColumn() {
        return ThirdColumn;
    }

    public double getFourthColumn() {
        return FourthColumn;
    }
}
