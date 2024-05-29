package models;

public class Currency {
    private Long id;
    private String name;
    private String code;
    private String sign;

    public Currency(Long id, String code, String name, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public Currency( String code,String name, String sign) {
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public String getFullName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
