# POS Shell扩展

| 学号      | 姓名   |
| --------- | ------ |
| 191220162 | 张乐简 |

[TOC]

## 概述

​		为POS Shell扩展了打印、清空购物车，移除指定物品、移除指定数量物品的功能。另外，为原先有的添加物品功能做了一些改动。

## 实验内容

### 简易手册

| 指令                    | 功能             |
| ----------------------- | ---------------- |
| pc                      | 打印购物车       |
| ec                      | 清空购物车       |
| r --product-id          | 移除某样特定商品 |
| ra --product-id --amout | 减少某样特定商品 |

### 打印购物车

```java
    @ShellMethod(value = "Print cart", key = "pc")
    public String printCart() {
        Cart c=posService.getCart();
        if(c==null){
            return "ERROR:Cart not found";
        }
        return c.toString();
    }
```

​		获取购物车并打印即可。

### 清空购物车

```java
    public boolean clearCart(){
        if (items!=null){
            items.clear();
            return true;
        }
        return false;
    }
```

​		给购物车添加了一个清空商品的接口。

```java
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

```

​		之后简单地调用接口即可。

### 移除商品

```java
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
```

​		首先需要检查购物车中是否有这样商品，有的话再移除它。

### 增添商品改动和减少商品

​		原先添加一样商品时，若已经有同样的商品在购物车中，系统会重新在购物车创建一个同名商品。另外，系统也没有阻止添加负数个商品。

```java
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
```

​		修改后的代码不存在这些问题。接着，实现减少商品的指令。

```java
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
```

