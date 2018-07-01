package client.test;

import client.Derived;

public class DerivedTestDirClass {

    public void loading_an_src_main_class_from_the_api_module_should_not_crash() {
        new Derived().equals(null);
    }

    public static void main(String[] args) {
        System.out.println("it works");
    }

}
