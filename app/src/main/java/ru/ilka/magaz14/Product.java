package ru.ilka.magaz14;


public class Product {
    private String name;
    private int price;
    private int image;
    private boolean box;


    Product(String _describe, int _price, int _image, boolean _box) {
        name = _describe;
        price = _price;
        image = _image;
        box = _box;
    }
    Product(){
        name = "Unnamed";
        price = 1;
        image = R.drawable.putin;
        box = false;
    }

    public int getPrice() {return price;}
    public int getImage() {return image;}
    public boolean isInBox() {return box;}
    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setImage(int image) {
        this.image = image;
    }
    public void setBox(boolean box) {
        this.box = box;
    }
}
