package com.example.poshell.cli;

import com.example.poshell.biz.PosService;
import com.example.poshell.model.Cart;
import com.example.poshell.model.Item;
import com.example.poshell.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;
import java.util.Objects;

@ShellComponent
public class PosCommand {

    private PosService posService;

    @Autowired
    public void setPosService(PosService posService) {
        this.posService = posService;
    }

    @ShellMethod(value = "List Products", key = "p")
    public String products() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Product product : posService.products()) {
            stringBuilder.append("\t").append(++i).append("\t").append(product).append("\n");
        }
        return stringBuilder.toString();
    }

    @ShellMethod(value = "New Cart", key = "n")
    public String newCart() {
        return posService.newCart() + " OK";
    }

    @ShellMethod(value = "Add a Product to Cart", key = "a")
    public String addToCart(String productId, int amount) {
        if(amount<=0) {
            return "ERROR:Non-positive amount.";
        }
        List<Item> items=posService.getCart().getItems();
        for (var item:
             items) {
            if(Objects.equals(item.getProduct().getId(), productId)) {
                item.setAmount(item.getAmount() + amount);
                return posService.getCart().toString();
            }
        }
        if (posService.add(productId, amount)) {
            return posService.getCart().toString();
        }
        return "ERROR";
    }

    @ShellMethod(value = "Reduce the amount of a certain product in the Cart", key = "ra")
    public String removeAmoutCart(String productId, int amount) {
        if(amount<=0) {
            return "ERROR:Non-positive amount.";
        }
        List<Item> items=posService.getCart().getItems();
        for (var item:
                items) {
            if(Objects.equals(item.getProduct().getId(), productId)) {
                if(item.getAmount()<amount){
                    return "ERROR:No so many "+productId+" in the Cart.";
                }
                item.setAmount(item.getAmount() - amount);
                return posService.getCart().toString();
            }
        }
        return "ERROR";
    }

    @ShellMethod(value = "Print cart", key = "pc")
    public String printCart() {
        Cart c=posService.getCart();
        if(c==null){
            return "ERROR:Cart not found";
        }
        return c.toString();
    }

    @ShellMethod(value = "Empty cart", key = "ec")
    public String emptyCart() {
        Cart c=posService.getCart();
        if(c==null){
            return "ERROR:No Cart";
        }
        if(c.clearCart()){
            return "Cart cleared.";
        }
        return "Clear failed.";
    }

    @ShellMethod(value = "Remove one certain item in the Cart.", key = "r")
    public String deleteItem(String productId) {
        Cart c=posService.getCart();
        if(c==null){
            return "ERROR:No Cart";
        }
        List<Item> items=c.getItems();
        for (var item:items
             ) {
            if (Objects.equals(item.getProduct().getId(), productId)){
                String ret="Item:\n"+item.toString() +"\nremoved.";
                items.remove(item);
                return ret;
            }
        }
        return "Remove failed.";
    }
}
