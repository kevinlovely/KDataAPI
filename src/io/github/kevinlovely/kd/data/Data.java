package io.github.kevinlovely.kd.data;

public class Data{

    private String name = "";
    private DataType dataType;
    private Object defaultData = "";

    public enum DataType {
        STRING, INT
    }

    public Data(DataType dataType) {
        this.dataType = dataType;
    }

    public void setDefaultData(Object defaultData) {
        this.defaultData = defaultData;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultData() {
        return defaultData;
    }

    public DataType getDataType() {
        return dataType;
    }
}
